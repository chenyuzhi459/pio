package io.sugo.pio.operator.io.csv;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.SimpleExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.io.AbstractDataResultSetReader;
import io.sugo.pio.operator.io.DataResultSetFactory;
import io.sugo.pio.ports.OutputPort;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
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
//@JsonTypeName("csv_reader")
public class CSVExampleSource extends AbstractDataResultSetReader implements Serializable {
	public static final String TYPE = "csv_reader";

	public static final String PARAMETER_CSV_FILE = "csv_file";
	public static final String PARAMETER_TRIM_LINES = "trim_lines";
	public static final String PARAMETER_SKIP_COMMENTS = "skip_comments";
	public static final String PARAMETER_COMMENT_CHARS = "comment_characters";
	public static final String PARAMETER_USE_QUOTES = "use_quotes";
	public static final String PARAMETER_QUOTES_CHARACTER = "quotes_character";
	public static final String PARAMETER_COLUMN_SEPARATORS = "column_separators";
	public static final String PARAMETER_ESCAPE_CHARACTER = "escape_character";


//	private InputPort inputPort = new InputPortImpl("file");

	@JsonProperty
	private String file = "/work/win7/druid.csv";

//	@Override
//	public String getType() {
//		return TYPE;
//	}

//	@JsonProperty
//	public String getInputPort() {
//		return inputPort.getName();
////	}

	@JsonCreator
	public CSVExampleSource(
			@JsonProperty("file") String file,
			@JsonProperty("name") String name,
			@JsonProperty("output") OutputPort outputPort
	) {
		super(SimpleExampleSet.class, name, outputPort);
		this.file = file;
	}

	@Override
	protected NumberFormat getNumberFormat() throws OperatorException {
		return null;
	}

	@Override
	public ExampleSet read() {
		try {
			System.out.println("CSVExampleSource read");
			Thread.sleep(13000);
			System.out.println("CSVExampleSource read finished");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return super.read();
	}

	public String getFile() {
		return file;
	}

	@Override
	protected DataResultSetFactory getDataResultSetFactory() throws OperatorException {
		CSVResultSetConfiguration csvConfig = new CSVResultSetConfiguration();
		csvConfig.setCsvFile(file);
		csvConfig.setTrimLines(true);
		csvConfig.setSkipComments(true);
		csvConfig.setColumnSeparators(",");
		csvConfig.setUseQuotes(true);
		csvConfig.setQuoteCharacter('"');
		csvConfig.setEscapeCharacter('\\');
		csvConfig.setCommentCharacters("#");
		csvConfig.setEncoding(StandardCharsets.UTF_8);

		return csvConfig;
	}

	@Override
	protected String getFileExtension() {
		return "csv";
	}
}
