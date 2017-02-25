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

import com.metamx.common.logger.Logger;


/**
 * This class has two numerical input attributes and one output attribute. Calculates the power of
 * the input attribute to the second.
 *
 * @author Ingo Mierswa
 */
public class PowerGenerator extends BinaryNumericalGenerator {

    private static final Logger log = new Logger(PowerGenerator.class);

    public PowerGenerator() {
    }

    @Override
    public FeatureGenerator newInstance() {
        return new PowerGenerator();
    }

    @Override
    public boolean isCommutative() {
        return false;
    }

    @Override
    public boolean isSelfApplicable() {
        return true;
    }

    @Override
    public double calculateValue(double value1, double value2) {
        return Math.pow(value1, value2);
    }

    @Override
    public void setFunction(String name) {
        if (!name.equals("^")) {
            // LogService.getGlobal().log("Illegal function name '" + name + "' for " +
            // getClass().getName() + ".", LogService.ERROR);
            log.error("io.sugo.pio.generator.PowerGenerator.illegal_function_name",
                    new Object[]{name, getClass().getName()});
        }
    }

    @Override
    public String getFunction() {
        return "^";
    }
}
