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
package io.sugo.pio.operator.nio.model;

import com.metamx.common.logger.Logger;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.nio.file.csv.LineReader;
import io.sugo.pio.operator.nio.model.ParsingError.ErrorCode;
import io.sugo.pio.tools.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Read CSV content from memory rather than file
 *
 * @author Huangama
 */
public class CSVMemoryResultSet implements DataResultSet {

    private static final Logger log = new Logger(CSVMemoryResultSet.class);

    private static final int LINES_FOR_GUESSING = 11;
    private static final int MAX_LOG_COUNT = 100;
    private CSVResultSetConfiguration configuration;
    private LineParser parser;

    private String[] next;
    private String[] current;
    private int currentRow;
    private int maxRow;
    private String[] columnNames;
    private int[] valueTypes;
    private int numColumns = 0;
    private Operator operator;
    private final List<ParsingError> errors = new LinkedList<>();
    private int logCount = 0;
    private long multiplier;
    private long lineCounter = 0;

    /**
     * The csv content in memory
     */
    private List<String> csvContent;

    public CSVMemoryResultSet(CSVResultSetConfiguration configuration, Operator operator) throws OperatorException {
        this.configuration = configuration;
        this.operator = operator;
        open();
    }

    private void open() throws OperatorException {
        getErrors().clear();
        close();
        logCount = 0;

        parser = new LineParser(configuration);

        if (operator != null) {
            csvContent = operator.getParameters().getExternalData();
            maxRow = csvContent.size();
            lineCounter = 0;
            operator.getProgress().setCheckForStop(false);
            operator.getProgress().setTotal(100);

            if (csvContent.isEmpty()) {
                next = null;
                return;
            }
            // Read the first line and parse the column name&type
            String line = csvContent.get(currentRow);

            try {
                next = parser.parse(line);
            } catch (CSVParseException e) {
                ParsingError parsingError = new ParsingError(currentRow, -1, ErrorCode.FILE_SYNTAX_ERROR, line, e);
                getErrors().add(parsingError);
                String warning = "Could not parse line " + currentRow + " in input: " + e.toString();
                if (logCount < MAX_LOG_COUNT) {
                    if (operator != null) {
                        log.warn(warning);
                    } else {
                        log.warn(warning);
                    }
                } else {
                    if (logCount == MAX_LOG_COUNT) {
                        if (operator != null) {
                            log.warn("Maximum number of warnings exceeded. Will display no further warnings.");
                        } else {
                            log.warn("Maximum number of warnings exceeded. Will display no further warnings.");
                        }
                    }
                }
                logCount++;
                next = new String[]{line};
            }

            if (next == null) {
                errors.add(new ParsingError(1, -1, ErrorCode.FILE_SYNTAX_ERROR, "No valid line found."));
                // throw new UserError(operator, 321, configuration.getCsvFile(),
                // "No valid line found.");
                columnNames = new String[0];
                valueTypes = new int[0];
            } else {
            /*operator.getParameters().setParameter(PARAMETER_META_DATA,
                    ParameterTypeList.transformList2String(Collections.singletonList(next)));*/
                numColumns = next.length;
                columnNames = new String[next.length];
                for (int i = 0; i < next.length; i++) {
                    columnNames[i] = "att" + (i + 1);
                }
                valueTypes = new int[next.length];
                Arrays.fill(valueTypes, Ontology.NOMINAL);
                currentRow = -1;
            }
        }
    }

    private void readNext() throws IOException {
        do {
            // Indicates that the next line have reached the end of the CSV list
            if (currentRow >= maxRow-1) {
                next = null;
                return;
            }

            // The next line is always fast one step of the current
            String line = csvContent.get(currentRow + 1);
            try {
                next = parser.parse(line);
                if (next != null) { // no comment read
                    break;
                }
            } catch (CSVParseException e) {
                ParsingError parsingError = new ParsingError(currentRow, -1, ErrorCode.FILE_SYNTAX_ERROR, line, e);
                getErrors().add(parsingError);
                String warning = "Could not parse line " + currentRow + " in input: " + e.toString();
                if (logCount < MAX_LOG_COUNT) {
                    if (operator != null) {
                        log.warn(warning);
                    } else {
                        log.warn(warning);
                    }
                } else {
                    if (logCount == MAX_LOG_COUNT) {
                        if (operator != null) {
                            log.warn("Maximum number of warnings exceeded. Will display no further warnings.");
                        } else {
                            log.warn("Maximum number of warnings exceeded. Will display no further warnings.");
                        }
                    }
                }
                logCount++;
                next = new String[]{line};
            }
        } while (true);
    }

    @Override
    public boolean hasNext() {
//        return next != null;
        return currentRow != maxRow - 1;
    }

    @Override
    public void next() {
        current = next;
        currentRow++;
        try {
            readNext();
        } catch (IOException e) {
            throw new UserError(operator, e, "pio.error.io_error", configuration.getCsvFile(), e.toString());
        }
    }

    @Override
    public int getNumberOfColumns() {
        return numColumns;
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public boolean isMissing(int columnIndex) {
        return columnIndex >= current.length || current[columnIndex] == null || current[columnIndex].isEmpty();
    }

    @Override
    public Number getNumber(int columnIndex) throws ParseException {
        throw new ParseException(
                new ParsingError(currentRow, columnIndex, ErrorCode.UNPARSEABLE_REAL, current[columnIndex]));
    }

    @Override
    public String getString(int columnIndex) throws ParseException {
        if (columnIndex < current.length) {
            return current[columnIndex];
        } else {
            return null;
        }
    }

    @Override
    public Date getDate(int columnIndex) throws ParseException {
        throw new ParseException(
                new ParsingError(currentRow, columnIndex, ErrorCode.UNPARSEABLE_DATE, current[columnIndex]));
    }

    @Override
    public ValueType getNativeValueType(int columnIndex) throws ParseException {
        return ValueType.STRING;
    }

    @Override
    public void close() throws OperatorException {
    }

    //	@Override
    public void reset(ProgressListener listener) throws OperatorException {
        open();
    }

    @Override
    public int[] getValueTypes() {
        return valueTypes;
    }

    @Override
    public int getCurrentRow() {
        return currentRow;
    }

    public List<ParsingError> getErrors() {
        return errors;
    }
}
