package io.sugo.pio.operator.extension.jdbc.tools.jdbc.connection;


import io.sugo.pio.io.process.XMLTools;
import io.sugo.pio.tools.FileSystemService;
import io.sugo.pio.tools.Tools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FileDatabaseConnectionProvider implements DatabaseConnectionProvider {
    public FileDatabaseConnectionProvider() {
    }

    public List<FieldConnectionEntry> readConnectionEntries() {
        LinkedList entries = new LinkedList();
        File connectionsFile = this.getOldConnectionsFile();
        File xmlConnectionsFile = this.getXMLConnectionsFile();
        if (!xmlConnectionsFile.exists() && !connectionsFile.exists()) {
            try {
                xmlConnectionsFile.createNewFile();
//                this.writeXMLConnectionsEntries(DatabaseConnectionService.getConnectionEntries(), xmlConnectionsFile);
            } catch (IOException var8) {
                ;
            }
        } else if (!xmlConnectionsFile.exists() && connectionsFile.exists()) {
            entries.addAll(DatabaseConnectionService.readConnectionEntries(connectionsFile));
//            this.writeXMLConnectionsEntries(DatabaseConnectionService.getConnectionEntries(), xmlConnectionsFile);
            connectionsFile.delete();
        } else {
            FileReader reader = null;

            try {
                reader = new FileReader(xmlConnectionsFile);
                if (!"".equals(Tools.readTextFile(reader))) {
                    Document e = XMLTools.parse(xmlConnectionsFile);
                    Element jdbcElement = e.getDocumentElement();
//                    entries.addAll(DatabaseConnectionService.parseEntries(jdbcElement));
                }
            } catch (Exception var7) {
//                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.connection.DatabaseConnectionService.reading_database_error", new Object[]{var7}), var7);
            }
        }

        return entries;
    }

    public void writeConnectionEntries(Collection<FieldConnectionEntry> connectionEntries) {
        File connectionEntriesFile = this.getXMLConnectionsFile();
//        this.writeXMLConnectionsEntries(connectionEntries, connectionEntriesFile);
    }

    private File getOldConnectionsFile() {
        return FileSystemService.getUserConfigFile("connections");
    }

    private File getXMLConnectionsFile() {
        return FileSystemService.getUserConfigFile("connections.xml");
    }
}
