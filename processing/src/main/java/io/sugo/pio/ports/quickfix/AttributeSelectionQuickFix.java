//package io.sugo.pio.ports.quickfix;
//
//
//import io.sugo.pio.parameter.ParameterHandler;
//import io.sugo.pio.ports.metadata.ExampleSetMetaData;
//import io.sugo.pio.tools.Ontology;
//
///**
// * @author Simon Fischer
// */
//public class AttributeSelectionQuickFix extends DictionaryQuickFix {
//
//	private final ParameterHandler handler;
//	private final String parameterName;
//
//	public AttributeSelectionQuickFix(ExampleSetMetaData metaData, String parameterName, ParameterHandler handler,
//									  String currentValue) {
//		this(metaData, parameterName, handler, currentValue, Ontology.VALUE_TYPE);
//	}
//
//	public AttributeSelectionQuickFix(ExampleSetMetaData metaData, String parameterName, ParameterHandler handler,
//									  String currentValue, int mustBeOfType) {
//		super(parameterName, metaData.getAttributeNamesByType(mustBeOfType), currentValue, handler.getParameters()
//				.getParameterType(parameterName).getDescription());
//		this.handler = handler;
//		this.parameterName = parameterName;
//	}
//
//	@Override
//	public void insertChosenOption(String chosenOption) {
//		handler.setParameter(parameterName, chosenOption);
//	}
//}
