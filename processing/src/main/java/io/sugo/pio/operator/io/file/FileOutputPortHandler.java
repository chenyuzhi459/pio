package io.sugo.pio.operator.io.file;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.nio.file.BufferedFileObject;
import io.sugo.pio.operator.nio.file.FileObject;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeFile;
import io.sugo.pio.parameter.PortProvider;
import io.sugo.pio.parameter.conditions.PortConnectedCondition;
import io.sugo.pio.ports.OutputPort;

import java.io.*;

/**
 * Provides methods for creating and working with a file InputPort. Used by reading operators.
 *
 * @author Dominik Halfkann
 */
public class FileOutputPortHandler {

    private OutputPort fileOutputPort;
    private String fileParameterName;
    private Operator operator;

    public FileOutputPortHandler(Operator operator, OutputPort fileOutputPort, String fileParameterName) {
        this.fileOutputPort = fileOutputPort;
        this.fileParameterName = fileParameterName;
        this.operator = operator;
    }

    /**
     * Returns an OutputStream, depending on whether the {@link #fileOutputPort} is connected or a
     * file name is given.
     */
    public OutputStream openSelectedFile() throws OperatorException {
        if (!fileOutputPort.isConnected()) {
            try {
                return new FileOutputStream(operator.getParameterAsFile(fileParameterName, true));
            } catch (FileNotFoundException e) {
                throw new UserError(operator, e, "pio.error.could_not_write_to_file", operator.getParameterAsFile(fileParameterName), e.getMessage());
            }
        } else {
            return new ByteArrayOutputStream() {

                @Override
                public void close() throws IOException {
                    super.close();
                    fileOutputPort.deliver(new BufferedFileObject(this.toByteArray()));
                }
            };
        }
    }

    /**
     * Returns an OutputStream, depending on whether the {@link #fileOutputPort} is connected, a
     * file name is given and it should be appended to the end of the file.
     */
    public OutputStream openSelectedFile(boolean append) throws OperatorException {
        if (!fileOutputPort.isConnected()) {
            try {
                return new FileOutputStream(operator.getParameterAsFile(fileParameterName, true), append);
            } catch (FileNotFoundException e) {
                throw new UserError(operator, e, "pio.error.could_not_write_to_file", operator.getParameterAsFile(fileParameterName), e.getMessage());
            }
        } else {
            return new ByteArrayOutputStream() {

                @Override
                public void close() throws IOException {
                    super.close();
                    fileOutputPort.deliver(new BufferedFileObject(this.toByteArray()));
                }
            };
        }
    }

    /**
     * Returns either the selected file referenced by the value of the parameter with the name
     * {@link #getFileParameterName()} or the file delivered at {@link #fileOutputPort}. Which of
     * these options is chosen is determined by the parameter {@link #PARAMETER_DESTINATION_TYPE}.
     * */
	/*
	 * public File getSelectedFile() throws OperatorException { if(!fileOutputPort.isConnected()){
	 * return operator.getParameterAsFile(fileParameterName); } else { return
	 * fileOutputPort.getData(FileObject.class).getFile(); } }
	 */

    /**
     * Same as {@link #getSelectedFile()}, but opens the stream.
     * */
	/*
	 * public InputStream openSelectedFile() throws OperatorException, IOException {
	 * if(!fileOutputPort.isConnected()){ return new
	 * FileInputStream(operator.getParameterAsFile(fileParameterName)); } else { return
	 * fileOutputPort.getData(FileObject.class).openStream(); } }
	 */

    /**
     * Same as {@link #getSelectedFile()}, but returns true if file is specified (in the respective
     * way).
     * */
    public boolean isFileSpecified() {
        if (!fileOutputPort.isConnected()) {
            return operator.isParameterSet(fileParameterName);
        } else {
            try {
                return (fileOutputPort.getData(IOObject.class) instanceof FileObject);
            } catch (OperatorException e) {
                return false;
            }
        }

    }

    /**
     * Creates the file parameter named by fileParameterName that depends on whether or not the port
     * returned by the given PortProvider is connected.
     */
    public static ParameterType makeFileParameterType(ParameterHandler parameterHandler, String parameterName,
                                                      String fileExtension, PortProvider portProvider) {
        final ParameterTypeFile fileParam = new ParameterTypeFile(parameterName, "Name of the file to write the data in.",
                true, new String[] { fileExtension });
        fileParam.setExpert(false);
        fileParam.registerDependencyCondition(new PortConnectedCondition(parameterHandler, portProvider, true, false));
        return fileParam;
    }

    /**
     * Creates the file parameter named by fileParameterName that depends on whether or not the port
     * returned by the given PortProvider is connected.
     */
    public static ParameterType makeFileParameterType(ParameterHandler parameterHandler, String parameterName,
                                                      PortProvider portProvider, String... fileExtension) {
        final ParameterTypeFile fileParam = new ParameterTypeFile(parameterName, "Name of the file to write the data in.",
                true, fileExtension);
        fileParam.setExpert(false);
        fileParam.registerDependencyCondition(new PortConnectedCondition(parameterHandler, portProvider, true, false));
        return fileParam;
    }

    /**
     * Adds a new (file-)OutputPortNotConnectedCondition for a given parameter.
     *
     * @param parameter
     * @param parameterHandler
     * @param portProvider
     */
    public static void addFileDependencyCondition(ParameterType parameter, ParameterHandler parameterHandler,
                                                  PortProvider portProvider) {
        parameter.registerDependencyCondition(new PortConnectedCondition(parameterHandler, portProvider, true, false));
    }

    /**
     * Returns the specified filename or "OutputFileObject" if the file OutputPort is connected.
     *
     * @return
     * @throws OperatorException
     */
    public String getSelectedFileDescription() throws OperatorException {
        if (!fileOutputPort.isConnected()) {
            return operator.getParameterAsString(fileParameterName);
        } else {
            return "OutputFileObject";
        }
    }
}