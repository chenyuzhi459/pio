package io.sugo.pio.operator.io;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.io.AbstractReader;
import io.sugo.pio.operator.io.DataResultSet;
import io.sugo.pio.operator.io.DataResultSetFactory;
import io.sugo.pio.operator.io.DataResultSetTranslationConfiguration;
import io.sugo.pio.operator.io.csv.CSVResultSetConfiguration;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * This class uses DataResultSets to load data from file and then delivers the data as an example
 * set.
 *
 * @author Sebastian Land
 */
public abstract class AbstractDataResultSetReader extends AbstractReader<ExampleSet> {

	private InputPort fileInputPort = getInputPorts().createPort("file");

	public InputPort getFileInputPort() {
		return fileInputPort;
	}

	@Override
	public ExampleSet read() throws OperatorException {
		// loading data result set
		final ExampleSet exampleSet;
		try (DataResultSetFactory dataResultSetFactory = getDataResultSetFactory();
			 DataResultSet dataResultSet = dataResultSetFactory.makeDataResultSet(this)) {
			exampleSet = transformDataResultSet(dataResultSet);
		}
		return exampleSet;
	}

	protected DataResultSetFactory getDataResultSetFactory() {
		return new CSVResultSetConfiguration();
	}

	/**
	 * Returns the configured number format or null if a default number format should be used.
	 */
	protected abstract NumberFormat getNumberFormat() throws OperatorException;

	/** Returns the allowed file extension. */
	protected abstract String getFileExtension();

	/** Returns the allowed file extensions. */
	protected String[] getFileExtensions() {
		return new String[] { getFileExtension() };
	}

	/**
	 *
	 * Transforms the provided {@link DataResultSet} into an example set.
	 *
	 * @param dataResultSet
	 *            the data result set to transform into an example set
	 * @return the generated example set
	 * @throws OperatorException
	 *             in case something goes wrong
	 */
	protected ExampleSet transformDataResultSet(DataResultSet dataResultSet) throws OperatorException {

		// loading configuration
		DataResultSetTranslationConfiguration configuration = new DataResultSetTranslationConfiguration(this);
		final boolean configComplete = !configuration.isComplete();
		if (configComplete) {
			configuration.reconfigure(dataResultSet);
		}

		// now use translator to read, translate and return example set
		DataResultSetTranslator translator = new DataResultSetTranslator(this);
		NumberFormat numberFormat = getNumberFormat();
		if (numberFormat != null) {
			configuration.setNumberFormat(numberFormat);
		}

		if (configComplete) {
			translator.guessValueTypes(configuration, dataResultSet, 1000);
		}
		return translator.read(dataResultSet, configuration, false);
	}
}
