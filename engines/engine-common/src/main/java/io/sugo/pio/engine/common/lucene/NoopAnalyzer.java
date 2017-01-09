package io.sugo.pio.engine.common.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharTokenizer;

/**
 */
public class NoopAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        return new TokenStreamComponents(new NoopTokenizer());
    }

    final class NoopTokenizer extends CharTokenizer {

        @Override
        protected boolean isTokenChar(int i) {
            return true;
        }
    }
}