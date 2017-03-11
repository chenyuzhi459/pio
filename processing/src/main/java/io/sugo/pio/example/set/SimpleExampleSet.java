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
package io.sugo.pio.example.set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.*;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.example.table.column.ColumnarExampleTable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * A simple implementation of ExampleSet containing a list of attributes and a special attribute
 * map. The data is queried from an example table which contains the data (example sets actually are
 * only views on this table and does not keep any data). This simple example set implementation
 * usually is the basic example set of the multi-layered data view.
 *
 */
public class SimpleExampleSet extends AbstractExampleSet {

    private static final long serialVersionUID = 9163340881176421801L;

    /** The table used for reading the examples from. */
    @JsonProperty
    private ExampleTable exampleTable;

    /** Holds all information about the attributes. */
    private Attributes attributes = new SimpleAttributes();

    /**
     */
    public SimpleExampleSet(ExampleTable exampleTable) {
        this(exampleTable, null, null);
    }

    /**
     */
    public SimpleExampleSet(ExampleTable exampleTable, List<Attribute> regularAttributes) {
        this(exampleTable, regularAttributes, null);
    }

    /**
     */
    public SimpleExampleSet(ExampleTable exampleTable, Map<Attribute, String> specialAttributes) {
        this(exampleTable, null, specialAttributes);
    }

    /**
     */
    public SimpleExampleSet(ExampleTable exampleTable, List<Attribute> regularAttributes,
                            Map<Attribute, String> specialAttributes) {
        this.exampleTable = exampleTable;
        List<Attribute> regularList = regularAttributes;
        if (regularList == null) {
            regularList = new LinkedList<>();
            for (int a = 0; a < exampleTable.getNumberOfAttributes(); a++) {
                Attribute attribute = exampleTable.getAttribute(a);
                if (attribute != null) {
                    regularList.add(attribute);
                }
            }
        }

        for (Attribute attribute : regularList) {
            if ((specialAttributes == null) || (specialAttributes.get(attribute) == null)) {
                getAttributes().add(new AttributeRole((Attribute) attribute.clone()));
            }
        }
    }

    /**
     * Clone constructor. The example table is copied by reference, the attributes are copied by a
     * deep clone.
     *
     * Don't use this method directly but use the clone method instead.
     */
    public SimpleExampleSet(SimpleExampleSet exampleSet) {
        this.exampleTable = exampleSet.exampleTable;
        this.attributes = (Attributes) exampleSet.getAttributes().clone();
    }

    // --- attributes ---

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    // --- examples ---

    @Override
    public ExampleTable getExampleTable() {
        return exampleTable;
    }

    @Override
    public Example getExample(int index) {
        DataRow dataRow = getExampleTable().getDataRow(index);
        if (dataRow == null) {
            return null;
        } else {
            return new Example(dataRow, this);
        }
    }

    @Override
    public int size() {
        return exampleTable.size();
    }


    @Override
    public Iterator<Example> iterator() {
        return new SimpleExampleReader(getExampleTable().getDataRowReader(), this);
    }

    @Override
    public void cleanup() {
        if (exampleTable instanceof ColumnarExampleTable) {
            ColumnarExampleTable table = (ColumnarExampleTable) exampleTable;
            this.exampleTable = table.columnCleanupClone(attributes);
        }
    }
}
