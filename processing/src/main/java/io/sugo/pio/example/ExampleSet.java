package io.sugo.pio.example;

import io.sugo.pio.operator.ResultObject;

/**
 */
public interface ExampleSet extends ResultObject, Cloneable, Iterable<Example> {
    /**
     * Returns the data structure holding all attributes. NOTE! if you intend to iterate over all
     * Attributes of this ExampleSet then you need to create an Iterator by calling
     * {@link ExampleSet#getAttributes()#getAttributes()} and use it instead.
     */
    public Attributes getAttributes();
}
