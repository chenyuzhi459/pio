package sugo.io.pio.data.druid.internal;

import java.util.List;

/**
 */
public interface DimensionSelector<T> {
    String getDimensionName();

    boolean isMultiple();

    List<T> getValues();

    T getValue();
}

