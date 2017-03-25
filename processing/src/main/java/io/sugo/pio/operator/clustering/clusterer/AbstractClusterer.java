package io.sugo.pio.operator.clustering.clusterer;

import com.metamx.common.logger.Logger;
import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.clustering.ClusterModel;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.*;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.metadata.MetaDataTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Abstract superclass of clusterers which defines the I/O behavior.
 */
public abstract class AbstractClusterer extends Operator {

    private static final Logger logger = new Logger(AbstractClusterer.class);

    private InputPort exampleSetInput = getInputPorts().createPort(PortConstant.EXAMPLE_SET, PortConstant.EXAMPLE_SET_DESC);
    private OutputPort modelOutput = getOutputPorts().createPort(PortConstant.CLUSTER_MODEL, PortConstant.CLUSTER_MODEL_DESC);
    private OutputPort exampleSetOutput = getOutputPorts().createPort(PortConstant.CLUSTER_SET, PortConstant.CLUSTER_SET_DESC);

    public AbstractClusterer() {
        super();
        exampleSetInput.addPrecondition(new SimplePrecondition(exampleSetInput, new ExampleSetMetaData()));
        getTransformer().addRule(new GenerateNewMDRule(modelOutput, new MetaData(getClusterModelClass())));
        getTransformer().addRule(new ExampleSetPassThroughRule(exampleSetInput, exampleSetOutput, SetRelation.EQUAL) {

            @Override
            public ExampleSetMetaData modifyExampleSet(ExampleSetMetaData metaData) {
                if (addsClusterAttribute()) {
                    if (addsClusterAttribute()) {
                        metaData.addAttribute(new AttributeMetaData(Attributes.CLUSTER_NAME, Ontology.NOMINAL,
                                Attributes.CLUSTER_NAME));
                    }
                    if (addsIdAttribute()) {
                        MetaDataTools.checkAndCreateIds(metaData);
                    }
                    metaData.addAllAttributes(getAdditionalAttributes());
                }
                return metaData;
            }
        });
    }

    /**
     * Generates a cluster model from an example set. Called by {@link #apply()}.
     */
    public abstract ClusterModel generateClusterModel(ExampleSet exampleSet) throws OperatorException;

    /**
     * Indicates whether {@link #doWork()} will add a cluster attribute to the example set.
     */
    protected abstract boolean addsClusterAttribute();

    /**
     * Indicates whether {@link #doWork()} will add an id attribute to the example set.
     */
    protected abstract boolean addsIdAttribute();

    /**
     * Subclasses might override this method in order to add additional attributes to the
     * metaDataSet
     */
    protected Collection<AttributeMetaData> getAdditionalAttributes() {
        return new LinkedList<AttributeMetaData>();
    }

    @Override
    public IOContainer getResult() {
        List<IOObject> ioObjects = new ArrayList<>();
        ioObjects.add(modelOutput.getAnyDataOrNull());
        ioObjects.add(exampleSetOutput.getAnyDataOrNull());
        return new IOContainer(ioObjects);
    }

    @Override
    public void doWork() throws OperatorException {
        ExampleSet input = exampleSetInput.getData(ExampleSet.class);
        ClusterModel clusterModel = generateClusterModel(input);

        // registering visualizer
//		ObjectVisualizerService.addObjectVisualizer(clusterModel, new ExampleVisualizer((ExampleSet) input.clone()));

        modelOutput.deliver(clusterModel);
        exampleSetOutput.deliver(input); // generateClusterModel() may have added cluster attribute

        logger.info("AbstractClusterer generate clusters and deliver to the next operator successfully.");
    }

    /**
     * Subclasses might overwrite this method in order to return the appropriate class of their
     * model if postprocessing is needed.
     */
    public Class<? extends ClusterModel> getClusterModelClass() {
        return ClusterModel.class;
    }

    @Override
    public boolean shouldAutoConnect(OutputPort outputPort) {
        if (outputPort == exampleSetOutput) {
            // TODO: Remove in later versions
            return addsClusterAttribute();
        } else {
            return super.shouldAutoConnect(outputPort);
        }
    }

    public InputPort getExampleSetInputPort() {
        return exampleSetInput;
    }
}
