package io.sugo.pio.report;

/**
 */
public interface Tableable {
    public boolean isFirstLineHeader();

    public boolean isFirstColumnHeader();

    public void prepareReporting();

    public void finishReporting();

    public String getColumnName(int index);

    /**
     * @return The number of rows in this {@link Tableable}
     */
    public int getRowNumber();

    /**
     * @return The number of columns in this {@link Tableable}
     */
    public int getColumnNumber();

    public String getCell(int row, int column);
}
