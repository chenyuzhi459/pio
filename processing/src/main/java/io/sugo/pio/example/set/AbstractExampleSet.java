package io.sugo.pio.example.set;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.ResultObjectAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 */
public abstract class AbstractExampleSet extends ResultObjectAdapter implements ExampleSet {
    private Map<Double, int[]> idMap = new HashMap<Double, int[]>();

    @Override
    public ExampleSet clone() {
        try {
            Class<? extends AbstractExampleSet> clazz = getClass();
            java.lang.reflect.Constructor cloneConstructor = clazz.getConstructor(new Class[] { clazz });
            AbstractExampleSet result = (AbstractExampleSet) cloneConstructor.newInstance(new Object[] { this });
            result.idMap = this.idMap;
            return result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot clone ExampleSet: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("'" + getClass().getName() + "' does not implement clone constructor!");
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException("Cannot clone " + getClass().getName() + ": " + e + ". Target: "
                    + e.getTargetException() + ". Cause: " + e.getCause() + ".");
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot clone " + getClass().getName() + ": " + e);
        }
    }
}
