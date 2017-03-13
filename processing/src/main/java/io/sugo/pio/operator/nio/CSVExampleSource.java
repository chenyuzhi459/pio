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
package io.sugo.pio.operator.nio;

import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.io.AbstractReader;
import io.sugo.pio.operator.nio.model.AbstractDataResultSetReader;
import io.sugo.pio.operator.nio.model.CSVResultSetConfiguration;
import io.sugo.pio.operator.nio.model.DataResultSetFactory;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.tools.DateParser;
import io.sugo.pio.tools.LineParser;
import io.sugo.pio.tools.StrictDecimalFormat;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;


/**
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

	/*static {
        AbstractReader.registerReaderDescription(new ReaderDescription("csv", CSVExampleSource.class, PARAMETER_CSV_FILE));
	}*/

	public CSVExampleSource() {
        super();
    }

    @Override
    protected DataResultSetFactory getDataResultSetFactory() throws OperatorException {
        return new CSVResultSetConfiguration(this);
    }

    @Override
    protected NumberFormat getNumberFormat() throws OperatorException {
        return StrictDecimalFormat.getInstance(this, true);
    }

    @Override
    protected boolean supportsEncoding() {
        return true;
    }

    //	@Override
    protected String getFileParameterName() {
        return PARAMETER_CSV_FILE;
    }

    @Override
    protected String getFileExtension() {
        return "csv";
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.CSVExampleSource.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.CSVExampleSource.description");
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        LinkedList<ParameterType> types = new LinkedList<>();
        /*ParameterType type = new ParameterTypeConfiguration(CSVExampleSourceConfigurationWizardCreator.class, this);
		type.setExpert(false);
		types.add(type);
		types.add(makeFileParameterType());*/
        ParameterType type = new ParameterTypeFile(PARAMETER_CSV_FILE,
                I18N.getMessage("pio.CSVExampleSource.csv_file"),
                "csv", false);
        types.add(type);

        // Separator
        types.add(new ParameterTypeString(PARAMETER_COLUMN_SEPARATORS,
                I18N.getMessage("pio.CSVExampleSource.column_separators"),
//				"Column separators for data files (regular expression)",
                ";", false));
        types.add(new ParameterTypeBoolean(
                PARAMETER_TRIM_LINES,
                I18N.getMessage("pio.CSVExampleSource.trim_lines"),
//				"Indicates if lines should be trimmed (empty spaces are removed at the beginning and the end) before the column split is performed. This option might be problematic if TABs are used as a seperator.",
                false));
        // Quotes
        types.add(new ParameterTypeBoolean(PARAMETER_USE_QUOTES,
                I18N.getMessage("pio.CSVExampleSource.use_quotes"),
//				"Indicates if quotes should be regarded.",
                true, false));
        type = new ParameterTypeChar(PARAMETER_QUOTES_CHARACTER,
                I18N.getMessage("pio.CSVExampleSource.quotes_character"),
//				"The quotes character.",
                LineParser.DEFAULT_QUOTE_CHARACTER, false);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_QUOTES, false, true));
        types.add(type);
        type = new ParameterTypeChar(PARAMETER_ESCAPE_CHARACTER,
                I18N.getMessage("pio.CSVExampleSource.escape_character"),
//				"The character that is used to escape quotes and column seperators",
                LineParser.DEFAULT_QUOTE_ESCAPE_CHARACTER, true);
        types.add(type);

        // Comments
        types.add(new ParameterTypeBoolean(PARAMETER_SKIP_COMMENTS,
                I18N.getMessage("pio.CSVExampleSource.skip_comments"),
//				"Indicates if a comment character should be used.",
                false, false));
        type = new ParameterTypeString(PARAMETER_COMMENT_CHARS,
                I18N.getMessage("pio.CSVExampleSource.comment_characters"),
//				"Lines beginning with these characters are ignored.", "#",
                false);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_SKIP_COMMENTS, false, true));
        types.add(type);

        // Numberformats
        types.addAll(StrictDecimalFormat.getParameterTypes(this, true));
        types.addAll(DateParser.getParameterTypes(this));

        types.addAll(super.getParameterTypes());
        return types;
    }

}