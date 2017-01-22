package io.sugo.pio.tools.math.similarity.nominal;

/**
 * This calculates the levenshtein distance of two strings. This is not a valid distance measure.
 * 
 * TODO: Extend this to become a valid distance measure
 * 
 */
public class LevenshteinDistance {

	public static int getDistance(String value1, String value2, int substitutionCost) {
		byte[] s = value1.getBytes();
		byte[] t = value2.getBytes();
		int n = s.length + 1;
		int m = t.length + 1;
		int[][] d = new int[n][m];

		for (int i = 0; i < n; i++) {
			d[i][0] = i;
		}
		for (int j = 0; j < m; j++) {
			d[0][j] = j;
		}
		for (int i = 1; i < n; i++) {
			for (int j = 1; j < m; j++) {
				int cost = (s[i - 1] == t[j - 1]) ? 0 : substitutionCost;
				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
			}
		}
		return d[n - 1][m - 1];
	}
}
