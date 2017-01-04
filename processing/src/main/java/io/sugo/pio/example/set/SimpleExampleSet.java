/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.example.set;

import io.sugo.pio.example.*;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.ExampleTable;

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
 * @author Ingo Mierswa, Simon Fischer Exp $
 */
public class SimpleExampleSet extends AbstractExampleSet {

	private static final long serialVersionUID = 9163340881176421801L;

	/** The table used for reading the examples from. */
	private ExampleTable exampleTable;

	/** Holds all information about the attributes. */
	private Attributes attributes = new SimpleAttributes();

	/**
	 * Constructs a new SimpleExampleSet backed by the given example table. The example set
	 * initially does not have any special attributes but all attributes from the given table will
	 * be used as regular attributes.
	 *
	 * If you are constructing the example set from a {@link MemoryExampleTable}, you should use the
	 * method {@link MemoryExampleTable#createExampleSet()} instead unless you are absolutely sure
	 * what you are doing.
	 */
	public SimpleExampleSet(ExampleTable exampleTable) {
		this(exampleTable, null, null);
	}

	/**
	 * Constructs a new SimpleExampleSet backed by the given example table. The example set
	 * initially does not have any special attributes but all attributes from the given table will
	 * be used as regular attributes.
	 *
	 * If you are constructing the example set from a {@link MemoryExampleTable}, you should use the
	 * method {@link MemoryExampleTable#createExampleSet()} instead unless you are absolutely sure
	 * what you are doing.
	 */
	public SimpleExampleSet(ExampleTable exampleTable, List<Attribute> regularAttributes) {
		this(exampleTable, regularAttributes, null);
	}

	/**
	 * Constructs a new SimpleExampleSet backed by the given example table. All attributes in the
	 * table apart from the special attributes become normal (regular) attributes. The special
	 * attributes are specified by the given map. The ordering of the attributes is defined by the
	 * iteration order of the map.
	 *
	 * If you are constructing the example set from a {@link MemoryExampleTable}, you should use the
	 * method {@link MemoryExampleTable#createExampleSet(Map)} instead unless you are absolutely
	 * sure what you are doing.
	 */
	public SimpleExampleSet(ExampleTable exampleTable, Map<Attribute, String> specialAttributes) {
		this(exampleTable, null, specialAttributes);
	}

	/**
	 * Constructs a new SimpleExampleSet backed by the given example table. All attributes in the
	 * table defined in the regular attribute list apart from those (also) defined the special
	 * attributes become normal (regular) attributes. The special attributes are specified by the
	 * given map. The ordering of the attributes is defined by the iteration order of the map.
	 *
	 * If you are constructing the example set from a {@link MemoryExampleTable}, you should use the
	 * method {@link MemoryExampleTable#createExampleSet(Map)} instead unless you are absolutely
	 * sure what you are doing.
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
	public int size() {
		return exampleTable.size();
	}


	@Override
	public Iterator<Example> iterator() {
		return new SimpleExampleReader(getExampleTable().getDataRowReader(), this);
	}
}
