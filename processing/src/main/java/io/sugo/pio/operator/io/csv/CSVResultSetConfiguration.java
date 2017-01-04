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


import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.io.AbstractDataResultSetReader;
import io.sugo.pio.operator.io.DataResultSet;
import io.sugo.pio.operator.io.DataResultSetFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * A class holding information about syntactical configuration for parsing CSV files
 *
 * @author Simon Fischer
 */
public class CSVResultSetConfiguration implements DataResultSetFactory {

	private String csvFile;

	private boolean skipComments = true;
	private boolean useQuotes = true;
	private boolean skipUTF8BOM = false;
	private boolean trimLines = false;
	private boolean hasHeaderRow = true;

	private String columnSeparators = ";";

	private char quoteCharacter = '"';
	private char escapeCharacter = '\\';
	private char decimalCharacter = '.';
	private String commentCharacters = "#";
	private int startingRow = 0;
	private int headerRow = 0;

	private Charset encoding = StandardCharsets.UTF_8;

	/**
	 * This will create a completely empty result set configuration
	 */
	public CSVResultSetConfiguration() {}

	@Override
	public void setParameters(AbstractDataResultSetReader source) {
		source.setParameter(CSVExampleSource.PARAMETER_CSV_FILE, getCsvFile());
		source.setParameter(CSVExampleSource.PARAMETER_SKIP_COMMENTS, String.valueOf(isSkipComments()));
		source.setParameter(CSVExampleSource.PARAMETER_USE_QUOTES, String.valueOf(isUseQuotes()));
		// source.setParameter(CSVExampleSource.PARAMETER_USE_FIRST_ROW_AS_ATTRIBUTE_NAMES,
		// String.valueOf(isFirstRowAsAttributeNames()));
		source.setParameter(CSVExampleSource.PARAMETER_COLUMN_SEPARATORS, getColumnSeparators());
		source.setParameter(CSVExampleSource.PARAMETER_TRIM_LINES, String.valueOf(isTrimLines()));
		source.setParameter(CSVExampleSource.PARAMETER_QUOTES_CHARACTER, String.valueOf(getQuoteCharacter()));
		source.setParameter(CSVExampleSource.PARAMETER_ESCAPE_CHARACTER, String.valueOf(getEscapeCharacter()));
		source.setParameter(CSVExampleSource.PARAMETER_COMMENT_CHARS, getCommentCharacters());
	}

	@Override
	public DataResultSet makeDataResultSet(Operator operator) {
		return new CSVResultSet(this, operator);
	}

	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}

	public String getCsvFile() {
		return csvFile;
	}

	public File getCsvFileAsFile() {
		return csvFile == null ? null : new File(csvFile);
	}

	public void setUseQuotes(boolean useQuotes) {
		this.useQuotes = useQuotes;
	}

	public boolean isUseQuotes() {
		return useQuotes;
	}

	public boolean hasHeaderRow() {
		return hasHeaderRow;
	}

	public void setHasHeaderRow(boolean hasHeaderRow) {
		this.hasHeaderRow = hasHeaderRow;
	}

	public void setSkipComments(boolean skipComments) {
		this.skipComments = skipComments;
	}

	public boolean isSkipComments() {
		return skipComments;
	}

	public void setColumnSeparators(String columnSeparators) {
		this.columnSeparators = columnSeparators;
	}

	public String getColumnSeparators() {
		return columnSeparators;
	}

	public void setCommentCharacters(String commentCharacters) {
		this.commentCharacters = commentCharacters;
	}

	public String getCommentCharacters() {
		return commentCharacters;
	}

	public void setEscapeCharacter(char escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	public char getEscapeCharacter() {
		return escapeCharacter;
	}

	public void setQuoteCharacter(char quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}

	public char getQuoteCharacter() {
		return quoteCharacter;
	}

	public void setDecimalCharacter(char decimalCharacter) {
		this.decimalCharacter = decimalCharacter;
	}

	public char getDecimalCharacter() {
		return decimalCharacter;
	}

	public void setTrimLines(boolean trimLines) {
		this.trimLines = trimLines;
	}

	public boolean isTrimLines() {
		return trimLines;
	}

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	public Charset getEncoding() {
		return encoding;
	}

	public boolean isSkippingUTF8BOM() {
		return skipUTF8BOM;
	}

	public void setSkipUTF8BOM(boolean skipUTF8BOM) {
		this.skipUTF8BOM = skipUTF8BOM;
	}

	public int getStartingRow() {
		return startingRow;
	}

	public void setStartingRow(int startingRow) {
		this.startingRow = startingRow;
	}

	public int getHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(int headerRow) {
		this.headerRow = headerRow;
	}

	@Override
	public String getResourceName() {
		return getCsvFile();
	}

	@Override
	public void close() {}

	/**
	 * @return a map containing all fieldNames and their values
	 */
	public Map<String, String> getParameterMap() {
		Map<String, String> parameterMap = new HashMap<>();
		parameterMap.put("csvFile", getCsvFile());
		parameterMap.put("useQuotes", String.valueOf(isUseQuotes()));
		parameterMap.put("hasHeaderRow", String.valueOf(hasHeaderRow()));
		parameterMap.put("headerRow", String.valueOf(getHeaderRow()));
		parameterMap.put("decimalCharacter", String.valueOf(getDecimalCharacter()));
		parameterMap.put("startingRow", String.valueOf(getStartingRow()));
		parameterMap.put("skipComments", String.valueOf(isSkipComments()));
		parameterMap.put("columnSeparators", getColumnSeparators());
		parameterMap.put("commentCharacters", getCommentCharacters());
		parameterMap.put("escapeCharacter", String.valueOf(getEscapeCharacter()));
		parameterMap.put("quoteCharacter", String.valueOf(getQuoteCharacter()));
		parameterMap.put("trimLines", String.valueOf(isTrimLines()));
		parameterMap.put("encoding", String.valueOf(getEncoding()));
		parameterMap.put("skipUTF8BOM", String.valueOf(isSkippingUTF8BOM()));
		return parameterMap;
	}
}
