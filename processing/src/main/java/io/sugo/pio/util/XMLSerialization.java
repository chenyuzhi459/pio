package io.sugo.pio.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 */
public class XMLSerialization {
    private XMLSerialization() {
    }

    public void writeXML(Object object, OutputStream out) throws IOException {
    }

    /**
     * Returns the singleton instance. We have to return a new instance, since the xStream will
     * remember several mappings and causing a huge memory leak.
     **/
    public static XMLSerialization getXMLSerialization() {
        return new XMLSerialization();
    }
}
