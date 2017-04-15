package io.sugo.pio.operator.io;

import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.io.file.FileOutputPortHandler;
import io.sugo.pio.operator.nio.file.FileObject;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeFile;
import io.sugo.pio.parameter.PortProvider;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.Port;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Abstract super type of stream writing operators.
 *
 * @author Dominik Halfkann
 */
public abstract class AbstractStreamWriter extends AbstractWriter<ExampleSet> {

	protected OutputPort fileOutputPort = getOutputPorts().createPort(PortConstant.FILE, PortConstant.FILE_DESC);
	private FileOutputPortHandler filePortHandler = new FileOutputPortHandler(this, fileOutputPort, getFileParameterName());

	protected boolean shouldAppend() {
		return false;
	}

	public AbstractStreamWriter() {
		super(ExampleSet.class);
		getTransformer().addGenerationRule(fileOutputPort, FileObject.class);
	}

	@Override
	public boolean shouldAutoConnect(OutputPort outputPort) {
		if (outputPort == fileOutputPort) {
			return false;
		} else {
			return super.shouldAutoConnect(outputPort);
		}
	}

	@Override
	public ExampleSet write(ExampleSet exampleSet) throws OperatorException {

		OutputStream outputStream = null;
		try {
			if (shouldAppend()) {
				outputStream = filePortHandler.openSelectedFile(true);
			} else {
				outputStream = filePortHandler.openSelectedFile();
			}
			writeStream(exampleSet, outputStream);

		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				if (outputStream instanceof FileOutputStream) {
					throw new UserError(this, e, "pio.error.error_write_to", getParameterAsFile(getFileParameterName()), "");
				} else if (outputStream instanceof ByteArrayOutputStream) {
					throw new UserError(this, e, "pio.error.error_write_to", "output stream", "");
				} else {
					throw new UserError(this, e, "pio.error.error_write_to", "unknown file or stream", "");
				}
			}
		}

		return exampleSet;
	}

	/**
	 * Creates (but does not add) the file parameter named by {@link #getFileParameterName()} that
	 * depends on whether or not {@link #fileOutputPort} is connected.
	 */
	protected ParameterType makeFileParameterType() {
		return FileOutputPortHandler.makeFileParameterType(this, getFileParameterName(), new PortProvider() {

			@Override
			public Port getPort() {
				return fileOutputPort;
			}
		}, getFileExtensions());
	}

	/**
	 * Writes data to an OutputStream in a format which is defined in the subclass.
	 */
	protected abstract void writeStream(ExampleSet exampleSet, OutputStream outputStream) throws OperatorException;

	/**
	 * Returns the name of the {@link ParameterTypeFile} to be added through which the user can
	 * specify the file name.
	 */
	protected abstract String getFileParameterName();

	/** Returns the allowed file extension. */
	protected abstract String[] getFileExtensions();

}
