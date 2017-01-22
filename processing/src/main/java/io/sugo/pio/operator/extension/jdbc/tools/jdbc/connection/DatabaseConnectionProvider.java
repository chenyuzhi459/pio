package io.sugo.pio.operator.extension.jdbc.tools.jdbc.connection;

import java.util.Collection;
import java.util.List;

public interface DatabaseConnectionProvider {
    List<FieldConnectionEntry> readConnectionEntries();

    void writeConnectionEntries(Collection<FieldConnectionEntry> var1);

//    void writeXMLConnectionsEntries(Collection<FieldConnectionEntry> var1, File var2);
}
