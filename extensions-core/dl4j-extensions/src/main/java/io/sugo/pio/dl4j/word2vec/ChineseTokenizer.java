package io.sugo.pio.dl4j.word2vec;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;

import java.util.Iterator;
import java.util.List;

/**
 */
public class ChineseTokenizer implements Tokenizer {
    private Result result;
    private Iterator<Term> terms;
    private TokenPreProcess tokenPreProcess;

    public ChineseTokenizer(String tokens) {
        result = ToAnalysis.parse(tokens);
        terms = result.iterator();
    }

    @Override
    public boolean hasMoreTokens() {
        return terms.hasNext();
    }

    @Override
    public int countTokens() {
        return result.size();
    }

    @Override
    public String nextToken() {
        String base = terms.next().getName();
        if(tokenPreProcess != null)
            base = tokenPreProcess.preProcess(base);
        return base;
    }

    @Override
    public List<String> getTokens() {
        return Lists.transform(
                result.getTerms(),
                new Function<Term, String>()
                {
                    @Override
                    public String apply(Term term)
                    {
                        return term.getName();
                    }
                }
        );
    }

    @Override
    public void setTokenPreProcessor(TokenPreProcess tokenPreProcessor) {
        this.tokenPreProcess = tokenPreProcessor;
    }
}
