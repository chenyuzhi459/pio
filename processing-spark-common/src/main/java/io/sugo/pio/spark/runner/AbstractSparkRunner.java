package io.sugo.pio.spark.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.sugo.pio.spark.transfer.TransferObject;
import io.sugo.pio.spark.transfer.parameter.CommonParameter;
import io.sugo.pio.spark.transfer.parameter.CommonParameter.FileFormat;
import org.apache.hadoop.mapred.FileAlreadyExistsException;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.ml.feature.LabeledPoint;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 */
public abstract class AbstractSparkRunner {
    static ObjectMapper mapper;
    static SparkConf conf;
    static JavaSparkContext sc;
    static SQLContext sqlContext;
    static String inputDirectory;
    static String outputDirectory;
    static FileFormat inputFormat;
    static String fieldSeparator;
    static String nullString;
    static String[] columnNames;
    static Boolean[] isNominal;
    static Boolean[] isFeature;
    static int labelIndex;
    static String[] featureColumns;
    static Map<Integer, List<String>> knownNominalMappings;
    static String labelName;
    static Integer[] featuresIndex;
    static Boolean[] isNominalFeatureIndex;
    static Boolean[] mappingProvided;

    public static void processException(Exception ex, String outputDir, JavaSparkContext ctx) throws SparkException {
        System.out.println("Exception: " + ex.toString());
        String explanation = null;
        if(ctx.version().equals("2.0.1") && ex.getMessage().startsWith("Unable to create database ")) {
            explanation = ". This is a bug identified in Spark 2.0.1, for more details please see: https://issues.apache.org/jira/browse/SPARK-17810. You can override spark.sql.warehouse.dir setting as a workaround or you can update Spark to a newer version.";
        }

        ArrayList exList = new ArrayList(1);
        exList.add(ex.toString() + (explanation == null?"":explanation));

        try {
            ctx.parallelize(exList).coalesce(1).saveAsTextFile(outputDir);
        } catch (Exception e) {
            if(!(e instanceof FileAlreadyExistsException)) {
                throw new SparkException(ex.toString(), ex);
            }
        } finally {
            ctx.cancelAllJobs();
        }

        throw new SparkException(ex.toString(), ex);
    }

    public static String createInputException(String s) {
        return "INPUT" + s;
    }

    protected static void close() {
        sc.stop();
        sc.close();
    }

    protected static void persistModel(TransferObject mto) {
        ArrayList<String> mtoList = new ArrayList(1);
        mtoList.add(mto.toJson());
        sc.parallelize(mtoList).coalesce(1).saveAsTextFile(outputDirectory);
    }

    protected static String[] init(String[] encodedArgs) throws IOException {
        conf = new SparkConf();
        sc = new JavaSparkContext(conf);
        sqlContext = new SQLContext(sc);

        mapper = new ObjectMapper();
        CommonParameter parameter = mapper.readValue(encodedArgs[0], CommonParameter.class);
        inputDirectory = parameter.getInputDir();
        outputDirectory = parameter.getOutputDir();
        inputFormat = parameter.getInputFormat();
        fieldSeparator = parameter.getFieldSeparator();
        nullString = parameter.getNullString();
        columnNames = parameter.getColumnNames();
        isNominal = parameter.getIsNominal();
        isFeature = parameter.getIsFeature();
        labelIndex = parameter.getLabelIndex();
        labelName = columnNames[labelIndex];
        List<String> featureColumnsList = new ArrayList();
        List<Integer> featureIndexesList = new ArrayList();
        List<Boolean> isNominalFeatureIndexList = new ArrayList();

        for(int negativeValues = 0; negativeValues < isFeature.length; ++negativeValues) {
            if(isFeature[negativeValues]) {
                featureColumnsList.add(columnNames[negativeValues]);
                featureIndexesList.add(negativeValues);
                isNominalFeatureIndexList.add(isNominal[negativeValues]);
            }
        }

        featureColumns = featureColumnsList.toArray(new String[0]);
        featuresIndex = featureIndexesList.toArray(new Integer[0]);
        isNominalFeatureIndex = isNominalFeatureIndexList.toArray(new Boolean[0]);
        mappingProvided = new Boolean[isFeature.length];
        Arrays.fill(mappingProvided, false);

        String[] negativeValues = parameter.getNegativeValues();
        String[] positiveValues = parameter.getPositiveValues();

        knownNominalMappings = new HashMap();

        for(int i = 0; i < isFeature.length; ++i) {
            if(isNominal[i] && negativeValues[i] != null && positiveValues[i] != null) {
                List<String> l = Lists.newArrayList(new String[]{negativeValues[i], positiveValues[i]});
                knownNominalMappings.put(i, l);
                mappingProvided[i] = true;
            }
        }

        return encodedArgs;
    }

    protected static Map<Integer, List<String>> discoverLabelMappings(JavaRDD<String[]> splittedInputRDD, boolean cache) {
        if (!knownNominalMappings.containsKey(labelIndex)) {
            knownNominalMappings.putAll(discoverNominalMappings(splittedInputRDD, cache, new int[]{labelIndex}));
        }

        return knownNominalMappings;
    }

    protected static Map<Integer, List<String>> discoverLabelAndNominalFeatureMappings(JavaRDD<String[]> splittedInputRDD, boolean cache, boolean skipDiscoverIfPossible) {
        int[] nominalLabelAndFeatureIndices = new int[1 + featureColumns.length];
        int i = 0;
        if (!knownNominalMappings.containsKey(labelIndex)) {
            nominalLabelAndFeatureIndices[i++] = labelIndex;
        }

        Integer[] discoveredNominalMappings;
        int featureIndex;
        if (skipDiscoverIfPossible) {
            discoveredNominalMappings = featuresIndex;
            int length = discoveredNominalMappings.length;

            for (int j = 0; j < length; ++j) {
                featureIndex = discoveredNominalMappings[j];
                if (isNominal[featureIndex] && !knownNominalMappings.containsKey(featureIndex)) {
                    nominalLabelAndFeatureIndices[i++] = featureIndex;
                }
            }
        } else {
            discoveredNominalMappings = featuresIndex;
            int length = discoveredNominalMappings.length;

            for (int j = 0; j < length; ++j) {
                featureIndex = discoveredNominalMappings[j];
                if (isNominal[featureIndex]) {
                    nominalLabelAndFeatureIndices[i++] = featureIndex;
                }
            }
        }

        if (i > 0) {
            Map<Integer, List<String>> discoveredNominalMapping = discoverNominalMappings(splittedInputRDD, cache, Arrays.copyOf(nominalLabelAndFeatureIndices, i));
            reorderDiscoveredMappings(discoveredNominalMapping);
            knownNominalMappings.putAll(discoveredNominalMapping);
        }

        return knownNominalMappings;
    }

    protected static Map<Integer, List<String>> discoverLabelAndNominalFeatureMappings(Dataset<Row> dataFrame, boolean skipDiscoverIfPossible) {
        return discoverLabelAndNominalFeatureMappings(convertDataFrameToRDD(dataFrame), false, skipDiscoverIfPossible);
    }

    protected static Dataset<Row> getInputAsDataFrame() {
        Dataset df = null;
        if (inputFormat.equals(FileFormat.TEXTFILE)) {
            df = createDataFrame(readTextFile(inputDirectory), columnNames, isNominal);
        } else if (inputFormat.equals(FileFormat.PARQUET)) {
            df = readParquetFile();
        }

        return df;
    }

    protected static JavaRDD<LabeledPoint> getLabeledPointRDD(JavaRDD<String[]> splittedInputRDD, final Map<Integer, List<String>> distinctValues) {
        final int labelIndexFinal = labelIndex;
        final Integer[] featuresIndexFinal = featuresIndex;
        final Boolean[] isNominalFinal = isNominal;
        final String[] columnNamesFinal = columnNames;
        JavaRDD labeledPointRDD = splittedInputRDD.map(new Function<String[], LabeledPoint>() {
            private static final long serialVersionUID = -6927287925476540061L;

            public LabeledPoint call(String[] splittedValuesAll) throws Exception {
                String labelValue = splittedValuesAll[labelIndexFinal];
                String labelName = columnNamesFinal[labelIndexFinal];
                double convertedLabelValue;
                int lp;
                if (isNominalFinal[labelIndexFinal]) {
                    List<String> columns = distinctValues.get(labelIndexFinal);
                    if (columns == null) {
                        throw new SparkException(AbstractSparkRunner.createInputException("The nominal mapping of label attribute: " + labelName + " is undiscovered."));
                    }

                    lp = columns.indexOf(labelValue);
                    if (lp == -1) {
                        throw new SparkException(AbstractSparkRunner.createInputException("The nominal mapping of label attribute: " + labelName + " does not contain member: " + labelValue));
                    }

                    convertedLabelValue = (double) lp;
                } else {
                    if (labelValue == null) {
                        throw new SparkException(AbstractSparkRunner.createInputException("The non-nominal label attribute: " + labelName + " should not contain missing."));
                    }

                    try {
                        convertedLabelValue = Double.valueOf(labelValue);
                    } catch (NumberFormatException var14) {
                        throw new SparkException(AbstractSparkRunner.createInputException("The non-nominal label attribute: " + labelName + " should not contain the non-numeric value: " + labelValue));
                    }
                }

                double[] features = new double[featuresIndexFinal.length];

                for (lp = 0; lp < features.length; ++lp) {
                    int featureIndex = featuresIndexFinal[lp];
                    String featureValue = splittedValuesAll[featureIndex];
                    String featureName = columnNamesFinal[featureIndex];
                    if (isNominalFinal[featureIndex]) {
                        List<String> ex = distinctValues.get(featureIndex);
                        if (ex == null) {
                            throw new SparkException(AbstractSparkRunner.createInputException("The nominal mapping of feature attribute: " + featureName + "is undiscovered."));
                        }

                        int index = ex.indexOf(featureValue);
                        if (index == -1) {
                            if (featureValue == null) {
                                throw new SparkException(AbstractSparkRunner.createInputException("The nominal feature attribute: " + featureName + " should not contain missings as nominal value discovering is skipped."));
                            }

                            throw new SparkException(AbstractSparkRunner.createInputException("The nominal mapping of feature attribute: " + featureName + " does not contain member: " + featureValue));
                        }

                        features[lp] = (double) index;
                    } else {
                        if (featureValue == null) {
                            throw new SparkException(AbstractSparkRunner.createInputException("The non-nominal feature attribute: " + featureName + " should not contain missing."));
                        }

                        try {
                            features[lp] = Double.valueOf(featureValue).doubleValue();
                        } catch (NumberFormatException var13) {
                            throw new SparkException(AbstractSparkRunner.createInputException("The non-nominal feature attribute: " + featureName + " should not contain the non-numeric value: " + featureValue));
                        }
                    }
                }

                LabeledPoint labeledPoint = new LabeledPoint(convertedLabelValue, Vectors.dense(features));
                return labeledPoint;
            }
        });
        return labeledPointRDD;
    }

    protected static JavaRDD<String[]> getInputAsRDD() {
        JavaRDD rdd = null;
        if (inputFormat.equals(FileFormat.TEXTFILE)) {
            rdd = readTextFile(inputDirectory);
        } else if (inputFormat.equals(FileFormat.PARQUET)) {
            rdd = convertDataFrameToRDD(readParquetFile());
        }

        return rdd;
    }

    protected static JavaRDD<String[]> convertDataFrameToRDD(Dataset<Row> dataFrame) {
        dataFrame.javaRDD();
        JavaRDD rdd = dataFrame.toJavaRDD().map(new Function<Row, String[]>() {
            private static final long serialVersionUID = -5580788934085658408L;

            public String[] call(Row row) throws Exception {
                String[] rowArray = new String[row.size()];

                for (int i = 0; i < rowArray.length; ++i) {
                    if (!row.isNullAt(i)) {
                        rowArray[i] = row.get(i).toString();
                    }
                }

                return rowArray;
            }
        });
        return rdd;
    }

    protected static JavaRDD<String[]> checkForNull(JavaRDD<String[]> splittedInputRDD, final int... indexes) throws SparkException {
        final String[] columnNamesFinal = columnNames;
        return splittedInputRDD.map(new Function<String[], String[]>() {
            private static final long serialVersionUID = -533664575373006435L;

            public String[] call(String[] arg0) throws Exception {
                int length = indexes.length;

                for (int i = 0; i < length; ++i) {
                    int index = indexes[i];
                    if (arg0[index] == null) {
                        throw new SparkException(createInputException("The attribute " + columnNamesFinal[index] + " should not contain missings for this algorithm!"));
                    }
                }

                return arg0;
            }
        });
    }

    protected static void checkMissingLabel(Map<Integer, List<String>> distinctValues) throws SparkException {
        checkMissingLabel(null, distinctValues);
    }

    protected static JavaRDD<String[]> checkMissingLabel(JavaRDD<String[]> splittedInputRDD, Map<Integer, List<String>> distinctValues) throws SparkException {
        if (splittedInputRDD != null && mappingProvided[labelIndex]) {
            return checkForNull(splittedInputRDD, new int[]{labelIndex});
        } else if (distinctValues.get(labelIndex) == null) {
            throw new SparkException("Could not find nominal values of the label!");
        } else if ((distinctValues.get(labelIndex)).contains(null)) {
            throw new SparkException(createInputException("Label should not contain missing values!"));
        } else {
            return splittedInputRDD;
        }
    }

    protected static void checkBinominalLabel(Map<Integer, List<String>> distinctValues) throws SparkException {
        if (distinctValues.get(labelIndex) == null) {
            throw new SparkException("Could not find nominal values of the label!");
        } else if ((distinctValues.get(labelIndex)).size() != 2 && ((distinctValues.get(labelIndex)).size() != 3 || !(distinctValues.get(labelIndex)).contains(null))) {
            String exMsg = "This algorithm does not have sufficient capabilities for handling an example set with less or more than 2 different values!";
            throw new SparkException(createInputException(exMsg));
        }
    }

    protected static Map<String, List<String>> convertNominalMappings(Map<Integer, List<String>> knownNominalMappings) {
        HashMap nominalMappings = new HashMap();
        Iterator<Map.Entry<Integer, List<String>>> iterator = knownNominalMappings.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, List<String>> e = iterator.next();
            nominalMappings.put(columnNames[(e.getKey())], e.getValue());
        }

        return nominalMappings;
    }

    private static Map<Integer, List<String>> discoverNominalMappings(JavaRDD<String[]> splittedInputRDD, boolean cache, final int... columnIndices) {
        if (cache) {
            splittedInputRDD.cache();
        }

        return splittedInputRDD.flatMapToPair(new PairFlatMapFunction<String[], Integer, String>() {
            private static final long serialVersionUID = 0L;

            public Iterator<Tuple2<Integer, String>> call(String[] splittedValuesAll) throws Exception {
                ArrayList list = new ArrayList(columnIndices.length);

                for (int i = 0; i < columnIndices.length; ++i) {
                    if (splittedValuesAll == null || splittedValuesAll.length <= columnIndices[i]) {
                        throw new SparkException("Could not find nominal value: attribute index is " + columnIndices[i] + ", values array size is " + (splittedValuesAll == null ? "null" : Integer.valueOf(splittedValuesAll.length)));
                    }

                    list.add(new Tuple2(Integer.valueOf(columnIndices[i]), splittedValuesAll[columnIndices[i]]));
                }

                return list.iterator();
            }
        }).aggregateByKey(new HashSet(), new Function2<Set<String>, String, Set<String>>() {
            private static final long serialVersionUID = 0L;

            public Set<String> call(Set<String> u, String b) throws Exception {
                u.add(b);
                return u;
            }
        }, new Function2<Set<String>, Set<String>, Set<String>>() {
            private static final long serialVersionUID = 0L;

            public Set<String> call(Set<String> u1, Set<String> u2) throws Exception {
                u1.addAll(u2);
                return u1;
            }
        }).mapToPair(new PairFunction<Tuple2<Integer, Set<String>>, Integer, List<String>>() {
            private static final long serialVersionUID = 0L;

            public Tuple2<Integer, List<String>> call(Tuple2<Integer, Set<String>> v1) throws Exception {
                ArrayList l = new ArrayList(v1._2);
                Collections.sort(l, new Comparator<String>() {
                    public int compare(String s1, String s2) {
                        return s1 == null && s2 == null ? 0 : (s1 == null ? 1 : (s2 == null ? -1 : s1.compareTo(s2)));
                    }
                });
                return new Tuple2(v1._1, l);
            }
        }).collectAsMap();
    }

    private static StructType createSchema(String[] columns, Boolean[] isNominal) {
        StructField[] fields = new StructField[columns.length];

        for (int schema = 0; schema < fields.length; ++schema) {
            fields[schema] = new StructField(columns[schema], isNominal[schema] ? DataTypes.StringType : DataTypes.DoubleType, true, Metadata.empty());
        }

        StructType structType = new StructType(fields);
        return structType;
    }


    private static JavaRDD<String[]> readTextFile(String inputDirectory) {
        return splitStringRDD(sc.textFile(inputDirectory), fieldSeparator, nullString);
    }

    private static Dataset<Row> readParquetFile() {
        Dataset dataFrame = sqlContext.parquetFile(new String[]{inputDirectory});
        StructType strucType = dataFrame.schema();
        StructField[] structFields = strucType.fields();

        for (int i = 0; i < structFields.length; ++i) {
            StructField structField = structFields[i];
            if (structField.dataType().equals(DataTypes.BooleanType)) {
                dataFrame = dataFrame.withColumn(structField.name(), dataFrame.col(structField.name()).cast(DataTypes.StringType));
            }
        }

        return dataFrame;
    }

    private static JavaRDD<String[]> splitStringRDD(JavaRDD<String> data, final String fieldSeparator, final String nullString) {
        JavaRDD splitStringRDD = data.map(new Function<String, String[]>() {
            private static final long serialVersionUID = 1L;

            public String[] call(String arg0) throws Exception {
                String[] splittedValues = arg0.split(Pattern.quote(fieldSeparator));

                for (int i = 0; i < splittedValues.length; ++i) {
                    if (splittedValues[i].equals(nullString)) {
                        splittedValues[i] = null;
                    }
                }

                return splittedValues;
            }
        });
        return splitStringRDD;
    }

    private static Dataset<Row> createDataFrame(JavaRDD<String[]> data, final String[] columnNames, final Boolean[] isNominal) {
        JavaRDD rowRDD = data.map(new Function<String[], Row>() {
            private static final long serialVersionUID = 1L;

            public Row call(String[] arg0) throws Exception {
                Object[] columns = new Object[columnNames.length];

                for (int i = 0; i < columnNames.length; ++i) {
                    Object o = null;
                    if (isNominal[i]) {
                        o = arg0[i];
                    } else if (arg0[i] != null) {
                        try {
                            o = Double.valueOf(arg0[i]);
                        } catch (NumberFormatException var6) {
                            throw new SparkException(AbstractSparkRunner.createInputException("The non-nominal attribute: " + columnNames[i] + " should not contain the non-numeric value: " + arg0[i] + " other than the missing placeholder."), var6);
                        }
                    }

                    columns[i] = o;
                }

                return RowFactory.create(columns);
            }
        });
        StructType schema = createSchema(columnNames, isNominal);
        Dataset dataFrame = sqlContext.createDataFrame(rowRDD, schema);
        return dataFrame;
    }

    private static void reorderDiscoveredMappings(Map<Integer, List<String>> discoveredNominalMappings) {
        Iterator<Map.Entry<Integer, List<String>>> iterator = discoveredNominalMappings.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, List<String>> discoveredMapping = iterator.next();
            List<String> known = knownNominalMappings.get(discoveredMapping.getKey());
            List<String> discovered = discoveredMapping.getValue();
            boolean discoveredContainsNull = discovered.contains(null);
            if (known != null) {
                HashSet<String> discoveredSet = new HashSet();
                discoveredSet.addAll(discovered);
                discoveredSet.remove(null);
                HashSet<String> knownSet = new HashSet();
                knownSet.addAll(known);
                knownSet.remove(null);
                if (discoveredSet.equals(knownSet) && discoveredContainsNull && !known.contains(null)) {
                    known.add(null);
                }
            }
        }

    }
}
