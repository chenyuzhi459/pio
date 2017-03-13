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
package io.sugo.pio.operator.preprocessing.sampling;

import io.sugo.pio.operator.preprocessing.AbstractDataProcessing;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MDInteger;
import io.sugo.pio.ports.metadata.MetaData;


/**
 * Abstract superclass of operators leaving the attribute set and data unchanged but reducing the
 * number of examples.
 *
 */
public abstract class AbstractSamplingOperator extends AbstractDataProcessing {

    public AbstractSamplingOperator() {
        super();
    }

    @Override
    protected MetaData modifyMetaData(ExampleSetMetaData metaData) {
        try {
            metaData.setNumberOfExamples(getSampledSize(metaData));
        } catch (UndefinedParameterError e) {
            metaData.setNumberOfExamples(new MDInteger());
        }
        return metaData;
    }

    /**
     * subclasses must implement this method for exact size meta data.
     */
    protected abstract MDInteger getSampledSize(ExampleSetMetaData emd) throws UndefinedParameterError;

    @Override
    public boolean writesIntoExistingData() {
        return false;
    }
}
