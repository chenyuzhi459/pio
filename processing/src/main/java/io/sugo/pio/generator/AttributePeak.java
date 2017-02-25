/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 * <p>
 * Complete list of developers available at our web site:
 * <p>
 * http://rapidminer.com
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.generator;

import io.sugo.pio.example.Attribute;


/**
 * Helper class for attributes, their frequencies, and their evidences.
 *
 * @author Ingo Mierswa
 */
public class AttributePeak implements Comparable<AttributePeak> {

    private Attribute attribute;

    private double frequency;

    private double evidence;

    public AttributePeak(Attribute attribute, double frequency, double evidence) {
        this.attribute = attribute;
        this.frequency = frequency;
        this.evidence = evidence;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getEvidence() {
        return evidence;
    }

    @Override
    public int compareTo(AttributePeak p) {
        return (-1) * Double.compare(this.evidence, p.evidence);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AttributePeak)) {
            return false;
        } else {
            AttributePeak peak = (AttributePeak) o;
            return (this.attribute.equals(peak.attribute)) && (this.frequency == peak.frequency);
        }
    }

    @Override
    public int hashCode() {
        return this.attribute.hashCode() ^ Double.valueOf(this.frequency).hashCode();
    }
}
