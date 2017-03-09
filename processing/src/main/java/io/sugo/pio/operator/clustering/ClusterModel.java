package io.sugo.pio.operator.clustering;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ConditionedExampleSet;
import io.sugo.pio.example.set.NoMissingAttributeValueCondition;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.operator.AbstractModel;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorProgress;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class is the standard flat cluster model, using the example ids to remember which examples
 * were assigned to which cluster. This information is stored within the single clusters. Since
 * this, the id attribute needs to be unchanged when cluster model is applied onto an example set.
 */
public class ClusterModel extends AbstractModel implements ClusterModelInterface {

    private static final long serialVersionUID = 3780908886210272852L;

    public static final int UNASSIGNABLE = -1;

    private static final int OPERATOR_PROGRESS_STEPS = 50000;

    /**
     * The progress of this operator is split into 3 part-progresses. These values define how many
     * percent are completed after part-progress 1 and part-progress 2.
     */
    private static final int INTERMEDIATE_PROGRESS_1 = 10;
    private static final int INTERMEDIATE_PROGRESS_2 = 30;

    private boolean isAddingAsLabel;
    private boolean isRemovingUnknown;

    @JsonProperty
    private ArrayList<Cluster> clusters;

    public ClusterModel(ExampleSet exampleSet, int k, boolean addClusterAsLabel, boolean removeUnknown) {
        super(exampleSet);
        this.clusters = new ArrayList<Cluster>(k);
        for (int i = 0; i < k; i++) {
            clusters.add(new Cluster(i));
        }
        this.isAddingAsLabel = addClusterAsLabel;
        this.isRemovingUnknown = removeUnknown;
    }

    @Override
    public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {

        OperatorProgress progress = null;
        if (getShowProgress() && getOperator() != null && getOperator().getProgress() != null) {
            progress = getOperator().getProgress();
            progress.setTotal(100);
        }

        Attributes attributes = exampleSet.getAttributes();

        // additional checks
        this.checkCapabilities(exampleSet);

        // creating attribute
        Attribute targetAttribute;
        if (!isAddingAsLabel) {
            targetAttribute = AttributeFactory.createAttribute("cluster", Ontology.NOMINAL);
            exampleSet.getExampleTable().addAttribute(targetAttribute);
            attributes.setCluster(targetAttribute);
        } else {
            targetAttribute = AttributeFactory.createAttribute("label", Ontology.NOMINAL);
            exampleSet.getExampleTable().addAttribute(targetAttribute);
            attributes.setLabel(targetAttribute);
        }

        if (progress != null) {
            progress.setCompleted(INTERMEDIATE_PROGRESS_1);
        }

        // setting values
        int[] clusterIndices = getClusterAssignments(exampleSet);

        if (progress != null) {
            progress.setCompleted(INTERMEDIATE_PROGRESS_2);
        }

        int i = 0;
        for (Example example : exampleSet) {
            if (clusterIndices[i] != ClusterModel.UNASSIGNABLE) {
                example.setValue(targetAttribute, "cluster_" + clusterIndices[i]);
            } else {
                example.setValue(targetAttribute, Double.NaN);
            }
            i++;

            if (progress != null && i % OPERATOR_PROGRESS_STEPS == 0) {
                progress.setCompleted(
                        (int) ((100.0 - INTERMEDIATE_PROGRESS_2) * i / exampleSet.size() + INTERMEDIATE_PROGRESS_2));
            }
        }
        // removing unknown examples if desired
        if (isRemovingUnknown) {
            exampleSet = new ConditionedExampleSet(exampleSet,
                    new NoMissingAttributeValueCondition(exampleSet, targetAttribute.getName()));
        }

        return exampleSet;

    }

    public int getNumberOfClusters() {
        return clusters.size();
    }

    /**
     * This method returns whether this cluster model should add the assignment as a label.
     */
    public boolean isAddingLabel() {
        return isAddingAsLabel;
    }

    /**
     * This method returns whether examples which can't be assigned should be removed from the
     * resulting example set.
     *
     * @return
     */
    public boolean isRemovingUnknownAssignments() {
        return isRemovingUnknown;
    }

    public void setClusterAssignments(int[] clusterId, ExampleSet exampleSet) {
        Attribute id = exampleSet.getAttributes().getId();
        if (id.isNominal()) {
            NominalMapping mapping = id.getMapping();
            int i = 0;
            for (Example example : exampleSet) {
                getCluster(clusterId[i]).assignExample(mapping.mapIndex((int) example.getValue(id)));
                i++;
            }
        } else {
            int i = 0;
            for (Example example : exampleSet) {
                getCluster(clusterId[i]).assignExample(example.getValue(id));
                i++;
            }
        }
    }

    /**
     * This method returns an array with the indices or the cluster for all examples in the set.
     * This will work with new examples, if centroid based clustering has been used before.
     * Otherwise new examples cannot be assigned.
     */
    public int[] getClusterAssignments(ExampleSet exampleSet) {
        int[] clusterAssignments = new int[exampleSet.size()];
        Attribute idAttribute = exampleSet.getAttributes().getId();
        if (idAttribute.isNominal()) {
            int j = 0;
            for (Example example : exampleSet) {
                clusterAssignments[j] = getClusterIndexOfId(example.getValueAsString(idAttribute));
                j++;
            }
        } else {
            int j = 0;
            for (Example example : exampleSet) {
                clusterAssignments[j] = getClusterIndexOfId(example.getValue(idAttribute));
                j++;
            }
        }
        return clusterAssignments;
    }

    /**
     * This method returns the index of the cluster, this Id's example has been assigned to. Please
     * note, that this can only be applied to examples included in the clustering process. New
     * examples might be assigned to clusters using the getClusterAssignments method, if and only if
     * the cluster model supports this. Currently only centroid based cluster models do.
     */
    public int getClusterIndexOfId(Object id) {
        int index = 0;
        for (Cluster cluster : clusters) {
            if (cluster.containsExampleId(id)) {
                return index;
            }
            index++;
        }
        return UNASSIGNABLE;
    }

    public Cluster getCluster(int i) {
        return clusters.get(i);
    }

    public Collection<Cluster> getClusters() {
        return clusters;
    }

    //	@Override
    public String getExtension() {
        return "cm";
    }

    //	@Override
    public String getFileDescription() {
        return "Cluster model";
    }

    public void checkCapabilities(ExampleSet exampleSet) throws OperatorException {
        io.sugo.pio.example.Tools.isIdTagged(exampleSet);
    }

    @Override
    public String getName() {
        return "Cluster Model";
    }

    @JsonProperty
    public String getDescription() {
        return this.toString();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        int sum = 0;
        for (int i = 0; i < getNumberOfClusters(); i++) {
            Cluster cl = getCluster(i);
            int numObjects = cl.getNumberOfExamples();
            result.append("Cluster " + cl.getClusterId() + ": " + numObjects + " items" + Tools.getLineSeparator());
            sum += numObjects;
        }
        result.append("Total number of items: " + sum + Tools.getLineSeparator());
        return result.toString();
    }
}
