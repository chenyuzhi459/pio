package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.LinkedList;
import java.util.List;


/**
 * Creates a new population by a deterministical selection of the best individuals.
 * 
 * @author Ingo Mierswa
 */
public class CutSelection implements PopulationOperator {

	private int popSize;

	public CutSelection(int popSize) {
		this.popSize = popSize;
	}

	public boolean performOperation(int generation) {
		return true;
	}

	@Override
	public void operate(Population population) {
		population.sort();

		List<Individual> newGeneration = new LinkedList<Individual>();
		int counter = 0;
		for (int i = population.getNumberOfIndividuals() - 1; (i >= 0) && (counter < popSize); i--) {
			newGeneration.add(population.get(i));
			counter++;
		}

		population.clear();
		population.addAll(newGeneration);
	}
}
