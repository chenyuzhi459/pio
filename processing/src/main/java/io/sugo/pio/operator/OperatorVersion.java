//package io.sugo.pio.operator;
//
//
//import io.sugo.pio.tools.VersionNumber;
//
///**
// * Operators can change their behaviour from one version to another. Hence, their version is stored
// * in the process XML file. If the behaviour of an operator changes from, say, version 5.0.003 to
// * 5.0.004, we can notify the user in version 5.0.004. To that end, the method
// *  will return 5.0.003, whereas
// *  will return [5.0.003] (or a superset thereof) in
// * version [5.0.004], so that we can detect that the behavior changed.
// *
// *
// * <strong>Note:</strong> The version numbers always refer to the plugin the operator is loaded
// * from. If it is not loaded from a plugin, it refers to the RapidMiner version.
// *
// * @author Simon Fischer
// *
// */
//public class OperatorVersion extends VersionNumber {
//
//	/**
//	 * Parses a version string of the form x.xx.xxx
//	 *
//	 * @throws IllegalArgumentException
//	 *             for malformed strings.
//	 */
//	public OperatorVersion(String versionString) {
//		super(versionString);
//	}
//
//	public OperatorVersion(int major, int minor, int buildNumber) {
//		super(major, minor, buildNumber);
//	}
//
//	public static OperatorVersion getLatestVersion() {
//		try {
////			Plugin plugin = desc.getProvider();
////			if (plugin == null) {
////				return new OperatorVersion(RapidMiner.getLongVersion());
////			} else {
////				return new OperatorVersion(plugin.getVersion());
////			}
//			return new OperatorVersion("OperatorVersionV1");
//		} catch (IllegalArgumentException e) {
//			// returning current version
//			return new OperatorVersion(5, 0, 0);
//		}
//	}
//}
