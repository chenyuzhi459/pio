package io.sugo.pio.spark.runner;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.sugo.pio.spark.transfer.model.*;
import io.sugo.pio.spark.transfer.parameter.SparkDecisionTreeParameter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.configuration.Algo;
import org.apache.spark.mllib.tree.configuration.Strategy;
import org.apache.spark.mllib.tree.impurity.Entropy;
import org.apache.spark.mllib.tree.impurity.Gini;
import org.apache.spark.mllib.tree.impurity.Impurity;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.Node;
import org.apache.spark.mllib.tree.model.Split;
import scala.Enumeration;
import scala.collection.JavaConversions;

import java.io.IOException;
import java.util.*;

/**
 */
public class SparkDecisionTreeRunner extends AbstractSparkRunner {
    private static Map<Integer, Integer> categoricalFeaturesInfo;

    public SparkDecisionTreeRunner() {
    }

    public static void main(String[] encodedArgs) throws SparkException {
        try {
            persistModel(learn(init(encodedArgs)));
        } catch (Exception e) {
            processException(e, outputDirectory, sc);
        } finally {
            close();
        }
    }

    public static ModelTransferObject learn(String[] encodedArgs) throws SparkException, IOException {
        SparkDecisionTreeParameter parameter = mapper.readValue(encodedArgs[1], SparkDecisionTreeParameter.class);
        int maxDepth = parameter.getMaxDepth();
        double minGain = parameter.getMinGain();
        int minInstances = parameter.getMinInstances();
        int maxBins = parameter.getMaxBins();
        int maxMemory = parameter.getMaxMemoryInMB();
        double subsamplingRate = parameter.getSubsamplingRate();
        boolean useIdCache = parameter.isUseNodeIdCache();
        boolean skipDiscoverIfPossible = parameter.isSkipDiscover();
        String impurity = parameter.getImpurity();
        if (!impurity.equals("GINI") && !impurity.equals("ENTROPY")) {
            throw new SparkException("Only Gini and Entropy is supported as the impurity measure! Found: " + impurity);
        } else {
            JavaRDD splittedInputRDD = getInputAsRDD();
            Map<Integer, List<String>> distinctValues = discoverLabelAndNominalFeatureMappings(splittedInputRDD, true, skipDiscoverIfPossible);
            splittedInputRDD = checkMissingLabel(splittedInputRDD, distinctValues);
            checkBinominalLabel(distinctValues);
            JavaRDD parsedData = getLabeledPointRDD(splittedInputRDD, distinctValues);
            parsedData.cache();
            categoricalFeaturesInfo = new HashMap();

            for (int v = 0; v < featuresIndex.length; ++v) {
                if (isNominal[featuresIndex[v].intValue()]) {
                    categoricalFeaturesInfo.put(Integer.valueOf(v), Integer.valueOf((distinctValues.get(featuresIndex[v])).size()));
                }
            }

            Enumeration.Value algo = Algo.Classification();
            Impurity imp = null;
            if (impurity.equals("GINI")) {
                imp = Gini.instance();
            } else if (impurity.equals("ENTROPY")) {
                imp = Entropy.instance();
            }

            int numClasses = distinctValues.get(labelIndex).size();
            Strategy strat = new Strategy(algo, imp, maxDepth, numClasses, maxBins, categoricalFeaturesInfo);
            strat.setMinInstancesPerNode(minInstances);
            strat.setMinInfoGain(minGain);
            strat.setMaxMemoryInMB(maxMemory);
            strat.setSubsamplingRate(subsamplingRate);
            strat.setUseNodeIdCache(useIdCache);
            DecisionTree tree = new DecisionTree(strat);
            DecisionTreeModel model = tree.run(parsedData.rdd());
            return convertDecisionTreeModel(model, distinctValues);
        }
    }


    private static ModelTransferObject convertDecisionTreeModel(DecisionTreeModel model, Map<Integer, List<String>> distinctValues) throws SparkException {
        Map<String, List<String>> mappings = convertNominalMappings(distinctValues);
        TreeTO rootTO = convertNode(model.topNode(), (distinctValues.get(labelIndex)).get(1), distinctValues.get(labelIndex).get(0), distinctValues);
        return new TreeModelMTO(rootTO, mappings);
    }

    private static TreeTO convertNode(Node node, String positiveLabel, String negativeLabel, Map<Integer, List<String>> distinctValues) throws SparkException {
        double predictedLabel = node.predict().predict();
        String predictedLabelClass = distinctValues.get(labelIndex).get((int)predictedLabel);
        double predictedLabelProb = node.predict().prob();
        ArrayList children = new ArrayList();
        HashMap counterMap = new HashMap();
        int normalizedCount = (int)(predictedLabelProb * 1000.0D);
        counterMap.put(predictedLabelClass, normalizedCount);
        counterMap.put(predictedLabelClass.equals(positiveLabel)?negativeLabel:positiveLabel, 1000 - normalizedCount);
        if(!node.isLeaf()) {
            if(node.split().isEmpty()) {
                throw new SparkException("Non-leaf node without SplitCondition!");
            }

            Split split = node.split().get();
            int splitOnFeatureIndex = split.feature();
            String splitOnFeature = featureColumns[splitOnFeatureIndex];
            List categoriesIndex = JavaConversions.seqAsJavaList(split.categories());
            SplitConditionTO splitConditionLeft;
            SplitConditionTO splitConditionRight;
            if(categoriesIndex.isEmpty()) {
                splitConditionLeft = new LessEqualsSplitConditionTO(splitOnFeature, split.threshold());
                splitConditionRight = new GreaterSplitConditionTO(splitOnFeature, split.threshold());
            } else {
                int[] complementerCategoriesIndex = getComplementerSet(categoriesIndex, (categoricalFeaturesInfo.get(Integer.valueOf(splitOnFeatureIndex))).intValue());
                List<String> categoriesString = getCategories(categoriesIndex, splitOnFeatureIndex, distinctValues);
                List<String> complementerCategoriesString = getComplementerCategories(complementerCategoriesIndex, splitOnFeatureIndex, distinctValues);
                if(categoriesString.size() == 1 && complementerCategoriesString.size() == 1) {
                    splitConditionLeft = new NominalSplitConditionTO(splitOnFeature, categoriesString.get(0));
                    splitConditionRight = new NominalSplitConditionTO(splitOnFeature, complementerCategoriesString.get(0));
                } else if(categoriesString.size() <= complementerCategoriesString.size()) {
                    splitConditionLeft = new ContainsSplitConditionTO(splitOnFeature, Sets.newHashSet(categoriesString));
                    splitConditionRight = new NotContainsSplitConditionTO(splitOnFeature, Sets.newHashSet(categoriesString));
                } else {
                    splitConditionLeft = new NotContainsSplitConditionTO(splitOnFeature, Sets.newHashSet(complementerCategoriesString));
                    splitConditionRight = new ContainsSplitConditionTO(splitOnFeature, Sets.newHashSet(complementerCategoriesString));
                }
            }

            children.add(new EdgeTO(convertNode(node.leftNode().get(), positiveLabel, negativeLabel, distinctValues), splitConditionLeft));
            children.add(new EdgeTO(convertNode(node.rightNode().get(), positiveLabel, negativeLabel, distinctValues), splitConditionRight));
        }

        return new TreeTO(predictedLabelClass, children, counterMap);
    }

    private static List<String> getCategories(List<Object> categories, final int splitOnFeatureIndex, final Map<Integer, List<String>> distinctValues) {
        return Lists.transform(categories, new Function() {
            public String apply(Object input) {
                return distinctValues.get(featuresIndex[splitOnFeatureIndex]).get((int)Double.parseDouble(input.toString()));
            }
        });
    }

    private static List<String> getComplementerCategories(int[] complementerCategories, final int splitOnFeatureIndex, final Map<Integer, List<String>> distinctValues) {
        return Lists.transform(Arrays.asList(ArrayUtils.toObject(complementerCategories)), new Function<Integer, String>() {
            public String apply(Integer input) {
                return distinctValues.get(featuresIndex[splitOnFeatureIndex]).get(input);
            }
        });
    }

    private static int[] getComplementerSet(List<Object> categories, int fullSize) throws SparkException {
        boolean[] isInCategories = new boolean[fullSize];
        Arrays.fill(isInCategories, false);
        int complementerSize = fullSize - categories.size();

        int j;
        for(Iterator complementerArray = categories.iterator(); complementerArray.hasNext(); isInCategories[j] = true) {
            Object i = complementerArray.next();

            try {
                j = (int)Double.parseDouble(i.toString());
            } catch (NumberFormatException var8) {
                throw new SparkException(var8.toString(), var8);
            }
        }

        int[] complementer = new int[complementerSize];

        for(int i=0, k=0; k < isInCategories.length; ++k) {
            if(!isInCategories[k]) {
                complementer[i++] = k;
            }
        }

        return complementer;
    }
}
