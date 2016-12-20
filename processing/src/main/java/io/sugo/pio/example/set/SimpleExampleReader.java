package io.sugo.pio.example.set;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.DataRowReader;

/**
 */
public class SimpleExampleReader extends AbstractExampleReader {

    /** The parent example reader. */
    private DataRowReader dataRowReader;

    /** The current example set. */
    private ExampleSet exampleSet;

    /** Creates a simple example reader. */
    public SimpleExampleReader(DataRowReader drr, ExampleSet exampleSet) {
        this.dataRowReader = drr;
        this.exampleSet = exampleSet;
    }

    /** Returns true if there are more data rows. */
    @Override
    public boolean hasNext() {
        return dataRowReader.hasNext();
    }

    /** Returns a new example based on the current data row. */
    @Override
    public Example next() {
        if (!hasNext()) {
            return null;
        }
        DataRow data = dataRowReader.next();
        if (data == null) {
            return null;
        }
        return new Example(data, exampleSet);
    }
}
