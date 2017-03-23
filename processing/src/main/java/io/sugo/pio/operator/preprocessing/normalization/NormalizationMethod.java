/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 * <p>
 * Complete list of developers available at our web site:
 * <p>
 * http://rapidminer.com
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
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
