package io.sugo.pio.spark.connections;

import io.sugo.pio.io.process.XMLTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.*;

/**
 */
public class ConfigurationMapBuilder {
    private String lastVisitedFile = "undefined";
    private List<String> skippedXmls = new ArrayList();

    public ConfigurationMapBuilder.ConfigurationMap readProperties(List<String> paths, String pattern) throws IOException {
        ConfigurationMapBuilder.ConfigurationMap conf = new ConfigurationMapBuilder.ConfigurationMap();
        Collection<File> xmlFiles = listFiles(paths, pattern);
        File xmlFile;
        Iterator<File> iterator = xmlFiles.iterator();

        while(iterator.hasNext()) {
            xmlFile = iterator.next();
            if(xmlFile.getName().matches(pattern)) {
                FileInputStream xmlStream2 = new FileInputStream(xmlFile);

                try {
                    lastVisitedFile = xmlFile.getAbsolutePath();
                    addPropertiesFromStream(xmlStream2, conf);
                } catch (Throwable t) {
                    throw t;
                } finally {
                    if(xmlStream2 != null) {
                        xmlStream2.close();
                    }
                }
            }
        }

        return conf;
    }

    public ConfigurationMapBuilder.ConfigurationMap readHadoopProperties(List<String> files) throws IOException {
        return readProperties(files, ".*(?<!hive)-site\\.xml$");
    }

    public void addPropertiesFromStream(InputStream xmlStream, ConfigurationMapBuilder.ConfigurationMap conf) throws IOException {
        try {
            Document e = XMLTools.parse(new ConfigurationMapBuilder.MyInputStream(xmlStream));
            addPropertiesFromSiteXml(e, conf);
        } catch (SAXException var4) {
            skippedXmls.add(lastVisitedFile);
        }

    }

    private static Collection<File> listFiles(Collection<String> paths, String suffix) throws IOException {
        ArrayList result = new ArrayList();
        IOFileFilter suffixFilter = FileFilterUtils.suffixFileFilter(suffix);
        Iterator<String> iterator = paths.iterator();

        while(iterator.hasNext()) {
            String path = iterator.next();
            File file = new File(path);
            if(file.isFile()) {
                if(suffixFilter.accept(file)) {
                    result.add(file);
                }
            } else {
                if(!file.isDirectory()) {
                    throw new NoSuchFileException(path, null, "No such file or directory.");
                }

                result.addAll(FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(suffix), FileFilterUtils.trueFileFilter()));
            }
        }

        return result;
    }

    private static void addPropertiesFromSiteXml(Document document, ConfigurationMapBuilder.ConfigurationMap result) {
        Element configElement = document.getDocumentElement();
        NodeList all_properties = configElement.getElementsByTagName("property");

        for(int i = 0; i < all_properties.getLength(); ++i) {
            Element property = (Element)all_properties.item(i);
            String name = XMLTools.getTagContents(property, "name");
            String value = XMLTools.getTagContents(property, "value");
            if(name != null && value != null) {
                result.put(name, value);
            }
        }
    }

    public static class MyInputStream extends BufferedInputStream {
        public MyInputStream(InputStream in) {
            super(in);
        }

        public void close() {
        }
    }

    public static class ConfigurationMap extends HashMap<String, String> {
        private static final long serialVersionUID = 8254734412189554675L;

        public ConfigurationMap() {
        }

        public String put(String key, String value) {
            if(containsKey(key)) {
                if(!(get(key)).equals(value)) {
                    throw new RuntimeException("Different values for the same property: " + key + ". (" + get(key) + " != " + value + ")");
                }
            } else {
                super.put(key, value);
            }

            return null;
        }
    }
}
