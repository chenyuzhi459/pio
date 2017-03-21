package io.sugo.pio.dl4j.word2vec;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.InputStream;

/**
 */
public class ChineseTokenizerFactory implements TokenizerFactory {
    private TokenPreProcess preProcess;

    @Override
    public Tokenizer create(String toTokenize) {
        Tokenizer t = new ChineseTokenizer(toTokenize);
        t.setTokenPreProcessor(preProcess);
        return t;
    }

    @Override
    public Tokenizer create(InputStream toTokenize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTokenPreProcessor(TokenPreProcess preProcessor) {
        this.preProcess = preProcessor;
    }

    @Override
    public TokenPreProcess getTokenPreProcessor() {
        return preProcess;
    }
}
