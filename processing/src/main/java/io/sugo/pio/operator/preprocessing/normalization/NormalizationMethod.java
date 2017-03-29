package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;

import java.util.Collection;
import java.util.List;


/**
 * This is an interface for all Normalization methods. Each normalization method needs to have an
 * empty constructor.
 *
 */
public interface NormalizationMethod {

    /**
     * This modifies the meta data of the given attribute and returns a collection of all derived
     * attributes. In normal cases this is simply one single attribute.
     *
     * @param parameterHandler
     *            TODO
     */
    public Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd,
                                                                 InputPort exampleSetInputPort, ParameterHandler parameterHandler) throws UndefinedParameterError;

    /**
     * This method can be used to clear all member types right before the normalization model is
     * retrieved.
     */
    public void init();

    /**
     * This method will be called to build the normalization model from the given {@link ExampleSet}
     * . It will be called directly after init() is called.
     *
     * @param operator
     *            TODO
     * @throws UserError
     */
    public AbstractNormalizationModel getNormalizationModel(ExampleSet exampleSet, Operator operator) throws UserError;

    /**
     * If this method needs additional parameter types, they can be returned here.
     */
    public List<ParameterType> getParameterTypes(ParameterHandler handler);

    /**
     * This just returns the name of the method.
     */
    public String getName();

    public String getDisplayName();
}
