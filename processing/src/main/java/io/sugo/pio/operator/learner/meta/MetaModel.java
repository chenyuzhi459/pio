/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.operator.learner.meta;

import io.sugo.pio.operator.Model;

import java.util.List;


/**
 * This interface provides methods for accessing the different models encapsulated in this model for
 * graphical representation. Models implementing this interface might be rendered using the the
 * MetaModelRenderer.
 * 
 */
public interface MetaModel {

	public List<? extends Model> getModels();

	public List<String> getModelNames();

}