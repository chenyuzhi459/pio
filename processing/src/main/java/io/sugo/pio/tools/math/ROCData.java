package io.sugo.pio.tools.math;

import io.sugo.pio.tools.Tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This container holds all ROC data points for a single ROC curve.
 * 
 */
public class ROCData implements Iterable<ROCPoint>, Serializable {

	private static final long serialVersionUID = 1L;

	private final List<ROCPoint> points = new ArrayList<ROCPoint>();

	private double sumPos;

	private double sumNeg;

	private double bestIsometricsTP;

	public void addPoint(ROCPoint point) {
		points.add(point);
	}

	public void removePoint(ROCPoint point) {
		points.remove(point);
	}

	public int getNumberOfPoints() {
		return points.size();
	}

	public ROCPoint getPoint(int index) {
		return points.get(index);
	}

	public double getInterpolatedTruePositives(double d) {
		if (Tools.isZero(d)) {
			return 0.0d;
		}

		if (Tools.isGreaterEqual(d, getTotalPositives())) {
			return getTotalPositives();
		}

		// if (points.size() == 2) {
		// if (Tools.isLess(d,1.0d)) {
		// return (sumPos % 2 == 0) ? (sumPos / 2) : (sumPos / 2 + 1);
		// } else {
		// return sumPos;
		// }
		// }

		ROCPoint last = null;
		double lastFpDivN = 0;
		for (ROCPoint p : this) {
			double fpDivN = p.getFalsePositives() / getTotalNegatives();
			if (Tools.isGreater(fpDivN, d)) {
				if (last == null) {
					return 0;
				} else {
					double alpha = (d - lastFpDivN) / (fpDivN - lastFpDivN);
					return last.getTruePositives() + alpha * (p.getTruePositives() - last.getTruePositives());
					// return last.getTruePositives();
				}
			}
			last = p;
			lastFpDivN = fpDivN;
		}
		return getTotalPositives();
	}

	public double getInterpolatedThreshold(double d) {
		if (Tools.isZero(d)) {
			return 1.0d;
		}

		if (Tools.isGreaterEqual(d, getTotalPositives())) {
			return 0.0d;
		}

		// if (points.size() == 2) {
		// if (Tools.isLess(d, 1.0d)) {
		// return points.get(1).getConfidence();
		// } else {
		// return 0.0d;
		// }
		// }

		ROCPoint last = null;
		for (ROCPoint p : this) {
			double fpDivN = p.getFalsePositives() / getTotalNegatives();
			if (Tools.isGreater(fpDivN, d)) {
				if (last == null) {
					return 1.0d;
				} else {
					return last.getConfidence();
				}
			}
			last = p;
		}
		if (last == null) {
			return 0d;
		} else {
			return last.getConfidence();
		}
	}

	@Override
	public Iterator<ROCPoint> iterator() {
		return points.iterator();
	}

	public void setTotalPositives(double sumPos) {
		this.sumPos = sumPos;
	}

	public double getTotalPositives() {
		return this.sumPos;
	}

	public void setTotalNegatives(double sumNeg) {
		this.sumNeg = sumNeg;
	}

	public double getTotalNegatives() {
		return this.sumNeg;
	}

	public void setBestIsometricsTPValue(double value) {
		this.bestIsometricsTP = value;
	}

	public double getBestIsometricsTPValue() {
		return this.bestIsometricsTP;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("ROC Points" + Tools.getLineSeparator());
		for (ROCPoint p : points) {
			result.append(p + Tools.getLineSeparator());
		}
		return result.toString();
	}
}
