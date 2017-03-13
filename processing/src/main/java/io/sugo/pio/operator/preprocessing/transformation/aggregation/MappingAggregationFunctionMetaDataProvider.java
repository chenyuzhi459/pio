package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.SimpleMetaDataError;
import io.sugo.pio.tools.Ontology;

import java.util.Map;
import java.util.Set;


/**
 * 
 * This class offers an implementation of {@link AggregationFunctionMetaDataProvider} interface and
 * can be parameterized to work for several {@link AggregationFunction}s on instanciation.
 * 
 * @author Thilo Kamradt
 * 
 */
public class MappingAggregationFunctionMetaDataProvider implements AggregationFunctionMetaDataProvider {

	private String aggregationFunctionName;
	private String functionName;
	private String separatorOpen;
	private String separatorClose;
	private Map<Integer, Integer> ioMap;

	/**
	 * 
	 * @param aggregationFunctionName
	 *            name of the AggregationFunction which will be displayed to the user
	 * @param functionName
	 *            name of the function which will be used for the generated attribute name
	 * @param separatorOpen
	 * @param separatorClose
	 * @param ioMap
	 *            a {@link Map} which maps possible attribute input-types to their output-types
	 */
	public MappingAggregationFunctionMetaDataProvider(String aggregationFunctionName, String functionName,
                                                      String separatorOpen, String separatorClose, Map<Integer, Integer> ioMap) {
		this.aggregationFunctionName = aggregationFunctionName;
		this.functionName = functionName;
		this.separatorClose = separatorClose;
		this.separatorOpen = separatorOpen;
		this.ioMap = ioMap;
		if (ioMap.isEmpty()) {
			throw new IllegalArgumentException(
					"Map of possible Inputvalues to Outputvalues can not be empty. No AttributeType would be accepted.");
		}
	}

	@Override
	public AttributeMetaData getTargetAttributeMetaData(AttributeMetaData sourceAttribute, InputPort port) {
		int bestMatchingType = findBestMatchingType(sourceAttribute.getValueType(), ioMap.keySet());
		if (bestMatchingType != -1) {

			return new AttributeMetaData(functionName + separatorOpen + sourceAttribute.getName() + separatorClose,
					ioMap.get(bestMatchingType));

		} else if (sourceAttribute.getValueType() == Ontology.ATTRIBUTE_VALUE) {

			return new AttributeMetaData(functionName + separatorOpen + sourceAttribute.getName() + separatorClose,
					sourceAttribute.getValueType());

		} else {
			int[] matchingValueTypes = new int[ioMap.size()];
			int index = 0;
			for (Integer i : ioMap.keySet()) {
				matchingValueTypes[index++] = i;
			}
			// not matching type: Return null and register error
			if (matchingValueTypes.length == 1) {
				if (port != null) {
					port.addError(new SimpleMetaDataError(Severity.ERROR, port, "aggregation.incompatible_value_type",
							sourceAttribute.getName(), aggregationFunctionName, Ontology.VALUE_TYPE_NAMES[sourceAttribute
									.getValueType()], Ontology.VALUE_TYPE_NAMES[matchingValueTypes[0]]));
				}
			} else {
				boolean first = true;
				StringBuilder b = new StringBuilder();
				for (int i = 0; i < matchingValueTypes.length - 1; i++) {
					if (first) {
						first = false;
					} else {
						b.append(", ");
					}
					b.append(Ontology.VALUE_TYPE_NAMES[matchingValueTypes[i]]);
				}
				if (port != null) {
					port.addError(new SimpleMetaDataError(Severity.ERROR, port,
							"aggregation.incompatible_value_type_multiple", sourceAttribute.getName(),
							aggregationFunctionName, Ontology.VALUE_TYPE_NAMES[sourceAttribute.getValueType()],
							b.toString(), Ontology.VALUE_TYPE_NAMES[matchingValueTypes[matchingValueTypes.length - 1]]));
				}
			}
			return null;
		}
	}

	/**
	 * Compares the givenType to the allowedTypes and return the best matching Type or -1 if no Type
	 * matches. <br />
	 * NOTE: In this case means best matching the most specific Type
	 * 
	 * @param givenType
	 *            value to classify
	 * @param allowedTypes
	 *            Set of allowed AttributeTypes
	 * @return the value of the best matching Type or -1 if not type matched
	 */
	private int findBestMatchingType(int givenType, Set<Integer> allowedTypes) {
		int toReturn = -1;
		for (Integer type : allowedTypes) {
			if (type == givenType) {
				toReturn = type;
				break;
			}
			if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(givenType, type)) {
				if (toReturn < 0) {
					toReturn = type;
				} else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(type, toReturn)) {
					toReturn = type;
				}
			}
		}

		return toReturn;
	}
}
