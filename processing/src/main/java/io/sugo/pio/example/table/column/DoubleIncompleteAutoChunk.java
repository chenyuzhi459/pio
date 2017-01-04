package io.sugo.pio.example.table.column;

import java.io.Serializable;

/**
 * Building block of a {@link DoubleIncompleteAutoColumn}.
 * Created by root on 16-12-26.
 */
public abstract class DoubleIncompleteAutoChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * the position of this chunk in {@link DoubleIncompleteAutoColumn#chunks}
     */
    final int id;

    /**
     * the chunk array {@link DoubleIncompleteAutoColumn#chunks}
     */
    final DoubleIncompleteAutoChunk[] chunks;

    DoubleIncompleteAutoChunk(int id, DoubleIncompleteAutoChunk[] chunks) {
        this.id = id;
        this.chunks = chunks;
    }

    /**
     * Ensures that the internal data structure can hold up to {@code size} values.
     *
     * @param size
     *            the size that should be ensured
     */
    abstract void ensure(int size);

    /**
     * Gets the value at the specified row.
     *
     * @param row
     *            the row that should be looked up
     * @return the value at the specified row
     */
    abstract double get(int row);

    /**
     * Sets the value at the specified row to the given value.
     *
     * @param row
     *            the row that should be set
     * @param value
     *            the value that should be set at the row
     */
    abstract void set(int row, double value);
}
