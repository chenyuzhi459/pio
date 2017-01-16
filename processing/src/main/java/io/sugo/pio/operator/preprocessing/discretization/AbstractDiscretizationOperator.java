package io.sugo.pio.operator.preprocessing.discretization;

import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.preprocessing.PreprocessingOperator;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.util.OperatorService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Simon Fischer
 */
public abstract class AbstractDiscretizationOperator extends PreprocessingOperator {

	public AbstractDiscretizationOperator(String name) {
		super(name);
	}

	@Override
	protected Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd)
			throws UndefinedParameterError {
		AttributeMetaData newAMD = new AttributeMetaData(amd.getName(), Ontology.NOMINAL, amd.getRole());
		return Collections.singletonList(newAMD);
	}

	private static Set<Class<? extends AbstractDiscretizationOperator>> ALL_DISCRETIZATION_OPERATORS = new HashSet<Class<? extends AbstractDiscretizationOperator>>();

	protected static void registerDiscretizationOperator(Class<? extends AbstractDiscretizationOperator> clazz) {
		ALL_DISCRETIZATION_OPERATORS.add(clazz);
	}

	@Override
	public int[] getFilterValueTypes() {
		return new int[] { Ontology.NUMERICAL };
	}
}
