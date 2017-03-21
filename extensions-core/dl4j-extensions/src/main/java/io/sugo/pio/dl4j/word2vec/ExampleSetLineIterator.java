package io.sugo.pio.dl4j.word2vec;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import java.util.Iterator;

/**
 */
public class ExampleSetLineIterator implements SentenceIterator {
    private final ExampleSet exampleSet;
    private Iterator<Example> exampleSetIterator;
    private SentencePreProcessor preProcessor;
    private String attributeName;

    public ExampleSetLineIterator(ExampleSet exampleSet, String attributeName) {
        this.exampleSet = exampleSet;
        this.attributeName = attributeName;
        exampleSetIterator = exampleSet.iterator();
    }

    @Override
    public String nextSentence() {
        Example example = exampleSetIterator.next();
        for (Attribute attribute: example.getAttributes()) {
            if (attributeName.equals(attribute.getName())) {
                return example.getValueAsString(attribute);
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return exampleSetIterator.hasNext();
    }

    @Override
    public void reset() {
        exampleSetIterator = exampleSet.iterator();
    }

    @Override
    public void finish() {
    }

    @Override
    public SentencePreProcessor getPreProcessor() {
        return preProcessor;
    }

    @Override
    public void setPreProcessor(SentencePreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

}
