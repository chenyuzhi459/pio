package io.sugo.pio.io.process;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;

/**
 */
public class XMLTools {
    private final static DocumentBuilderFactory BUILDER_FACTORY;

    static {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        BUILDER_FACTORY = domFactory;
    }

    /**
     * Creates a new {@link DocumentBuilder} instance.
     *
     * Needed because DocumentBuilder is not thread-safe and crashes when different threads try to
     * parse at the same time.
     *
     * @return
     * @throws IOException
     *             if it fails to create a {@link DocumentBuilder}
     */
    private static DocumentBuilder createDocumentBuilder() throws IOException {
        try {
            synchronized (BUILDER_FACTORY) {
                return BUILDER_FACTORY.newDocumentBuilder();
            }
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    public static Document parse(String string) throws SAXException, IOException {
        return createDocumentBuilder().parse(new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8"))));
    }

    public static Document parse(InputStream in) throws SAXException, IOException {
        return createDocumentBuilder().parse(in);
    }

    public static Document parse(File file) throws SAXException, IOException {
        return createDocumentBuilder().parse(file);
    }

    /**
     * As {@link #getTagContents(Element, String, boolean)}, but never throws an exception. Returns
     * null if can't retrieve string.
     */
    public static String getTagContents(Element element, String tag) {
        return getTagContents(element, tag, false);
    }

    public static String getTagContents(Element element, String tag, String deflt) {
        String result = getTagContents(element, tag);
        if (result == null) {
            return deflt;
        } else {
            return result;
        }
    }

    /**
     * For a tag <parent> <tagName>content</tagName> <something>else</something> ... </parent>
     *
     * returns "content". This will return the content of the first occurring child element with
     * name tagName. If no such tag exists null is returned.
     */
    public static String getTagContents(Element parent, String tagName, boolean throwExceptionOnError)  {
        NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element && ((Element) node).getTagName().equals(tagName)) {
                Element child = (Element) node;
                return child.getTextContent();
            }
        }
        return null;
    }

}
