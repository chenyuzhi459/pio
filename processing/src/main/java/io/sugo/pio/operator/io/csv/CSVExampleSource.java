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
package io.sugo.pio.operator.io.csv;


import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.io.AbstractDataResultSetReader;
import io.sugo.pio.operator.io.AbstractReader;

import java.text.NumberFormat;


/**
 *
 * <p>
 * This operator can be used to load data from .csv files. The user can specify the delimiter and
 * various other parameters.
 * </p>
 *
 * @author Ingo Mierswa, Tobias Malbrecht, Sebastian Loh, Sebastian Land, Simon Fischer
 */
public class CSVExampleSource extends AbstractDataResultSetReader {

	public static final String PARAMETER_CSV_FILE = "csv_file";
	public static final String PARAMETER_TRIM_LINES = "trim_lines";
	public static final String PARAMETER_SKIP_COMMENTS = "skip_comments";
	public static final String PARAMETER_COMMENT_CHARS = "comment_characters";
	public static final String PARAMETER_USE_QUOTES = "use_quotes";
	public static final String PARAMETER_QUOTES_CHARACTER = "quotes_character";
	public static final String PARAMETER_COLUMN_SEPARATORS = "column_separators";
	public static final String PARAMETER_ESCAPE_CHARACTER = "escape_character";

	static {
		AbstractReader.registerReaderDescription(new ReaderDescription("csv", CSVExampleSource.class, PARAMETER_CSV_FILE));
	}

	@Override
	protected NumberFormat getNumberFormat() throws OperatorException {
		return null;
	}

	@Override
	protected String getFileExtension() {
		return "csv";
	}
}
