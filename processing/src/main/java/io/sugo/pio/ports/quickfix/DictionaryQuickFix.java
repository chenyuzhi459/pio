//package io.sugo.pio.ports.quickfix;
//
//
//import io.sugo.pio.tools.math.similarity.nominal.LevenshteinDistance;
//
//import java.util.Arrays;
//import java.util.Collection;
//
///**
// * @author Sebastian Land
// */
//public abstract class DictionaryQuickFix extends AbstractQuickFix {
//
//	private String description;
//	private String[] options;
//	private int nearestOption;
//
//	public DictionaryQuickFix(String fixWhat, Collection<String> alternativeValues, final String currentValue,
//							  String description) {
//		super(1, true, "replace_by_dictionary", fixWhat);
//		this.options = alternativeValues.toArray(new String[alternativeValues.size()]);
//		this.description = description;
//
//		// searching options for nearest on Hamming Distance
//		Arrays.sort(options);
//		int distance = Integer.MAX_VALUE;
//		int i = 0;
//		for (String string : options) {
//			int currentDistance = LevenshteinDistance.getDistance(string, currentValue, 2);
//			if (currentDistance < distance) {
//				nearestOption = i;
//				distance = currentDistance;
//			}
//			i++;
//		}
//	}
//
//	public DictionaryQuickFix(String arg, Collection<String> alternativeValues, final String currentValue) {
//		this(arg, alternativeValues, currentValue, "Select the appropriate value");
//	}
//
//	@Override
//	public void apply() {
////		Object option = SwingTools.showInputDialog("quickfix.replace_by_dictionary", options, options[nearestOption],
////				description);
////		if (option != null) {
////			insertChosenOption(option.toString());
////		}
//	}
//
//	/**
//	 * This method must be overridden from subclasses to use the chosen option
//	 */
//	public abstract void insertChosenOption(String chosenOption);
//}
