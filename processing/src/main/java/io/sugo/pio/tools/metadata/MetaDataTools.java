package io.sugo.pio.tools.metadata;

import io.sugo.pio.example.Attributes;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.SetRelation;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Range;


/**
 * Tools class for handling meta data. Should contain analogs to the Tools class for handling
 * exampleSet.
 * 
 */
public class MetaDataTools {

	/**
	 * This method is the analogon to the checkAndCreateIds of the Tools class for meta data. It
	 * creates an integer id attribute with as much informations as available.
	 */
	public static void checkAndCreateIds(ExampleSetMetaData emd) {
		if (emd.getSpecial(Attributes.ID_NAME) == null) {
			AttributeMetaData idMD = new AttributeMetaData(Attributes.ID_NAME, Ontology.INTEGER, Attributes.ID_NAME);
			if (emd.getNumberOfExamples().isKnown()) {
				if (emd.getNumberOfExamples().getValue().doubleValue() > 1) {
					idMD.setValueRange(new Range(0, emd.getNumberOfExamples().getValue().doubleValue() - 1),
							SetRelation.EQUAL);
				} else {
					idMD.setValueRange(new Range(), SetRelation.EQUAL);
				}
			}
			emd.addAttribute(idMD);
		}
	}
}
