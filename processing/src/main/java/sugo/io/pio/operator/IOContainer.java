package sugo.io.pio.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
public class IOContainer {
    private final List<IOObject> ioObjects;

    public IOContainer(Collection<? extends IOObject> objectCollection) {
        ioObjects = new ArrayList<>(objectCollection.size());
        ioObjects.addAll(objectCollection);
    }
}
