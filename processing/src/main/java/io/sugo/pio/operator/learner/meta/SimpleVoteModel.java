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
package io.sugo.pio.operator.learner.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.operator.Model;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.SimplePredictionModel;
import io.sugo.pio.tools.RandomGenerator;
import io.sugo.pio.tools.Tools;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A simple vote model. For classification problems, the majority class is chosen. For regression
 * problems, the average prediction value is used. This model only supports simple prediction
 * models.
 * 
 */
public class SimpleVoteModel extends SimplePredictionModel implements MetaModel {

	private static final long serialVersionUID = 1089932073805038503L;

	@JsonProperty
	private List<? extends SimplePredictionModel> baseModels;

	@JsonProperty
	private boolean labelIsNominal;

	@JsonProperty
	private List<Double> labelIndices;

	public SimpleVoteModel(ExampleSet exampleSet, List<? extends SimplePredictionModel> baseModels) {
		super(exampleSet, ExampleSetUtilities.SetsCompareOption.EQUAL,
				ExampleSetUtilities.TypesCompareOption.ALLOW_SAME_PARENTS);
		this.baseModels = baseModels;
		labelIsNominal = getLabel().isNominal();
		if (labelIsNominal) {
			labelIndices = new LinkedList<>();
			NominalMapping mapping = getLabel().getMapping();
			List<String> mappingValues = mapping.getValues();
			for (String value : mappingValues) {
				double index = mapping.getIndex(value);
				labelIndices.add(index);
			}
		}
	}

	@Override
	public double predict(Example example) throws OperatorException {
		if (labelIsNominal) {
			Map<Double, AtomicInteger> classVotes = new TreeMap<>();
			Iterator<? extends SimplePredictionModel> iterator = baseModels.iterator();
			while (iterator.hasNext()) {
				double prediction = iterator.next().predict(example);
				AtomicInteger counter = classVotes.get(prediction);
				if (counter == null) {
					classVotes.put(prediction, new AtomicInteger(1));
				} else {
					counter.incrementAndGet();
				}
			}

			List<Double> bestClasses = new LinkedList<>();
			int bestClassesVotes = -1;
			for (double currentClass : labelIndices) {
				AtomicInteger votes = classVotes.get(currentClass);
				if (votes != null) {
					int currentVotes = votes.intValue();
					if (currentVotes > bestClassesVotes) {
						bestClasses.clear();
						bestClasses.add(currentClass);
						bestClassesVotes = currentVotes;
					}
					if (currentVotes == bestClassesVotes) {
						bestClasses.add(currentClass);
					}
					example.setConfidence(getLabel().getMapping().mapIndex((int) currentClass), (double) currentVotes
							/ (double) baseModels.size());
				} else {
					example.setConfidence(getLabel().getMapping().mapIndex((int) currentClass), 0.00);
				}
			}
			if (bestClasses.size() == 1) {
				return bestClasses.get(0);
			} else {
				return bestClasses.get(RandomGenerator.getGlobalRandomGenerator().nextInt(bestClasses.size()));
			}
		} else {
			double sum = 0.0d;
			Iterator<? extends SimplePredictionModel> iterator = baseModels.iterator();
			while (iterator.hasNext()) {
				sum += iterator.next().predict(example);
			}
			return sum / baseModels.size();
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		for (SimplePredictionModel model : baseModels) {
			buffer.append("Model " + i + ":" + Tools.getLineSeparator());
			buffer.append("---" + Tools.getLineSeparator());
			buffer.append(model.toString());
			buffer.append(Tools.getLineSeparators(2));
			i++;
		}
		return buffer.toString();
	}

	@Override
	public List<String> getModelNames() {
		List<String> names = new LinkedList<>();
		for (int i = 0; i < this.baseModels.size(); i++) {
			names.add("Model " + (i + 1));
		}
		return names;
	}

	@Override
	public List<? extends Model> getModels() {
		return baseModels;
	}
}