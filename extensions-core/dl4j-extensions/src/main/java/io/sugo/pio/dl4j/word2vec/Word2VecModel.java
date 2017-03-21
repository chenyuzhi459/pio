package io.sugo.pio.dl4j.word2vec;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.AbstractModel;
import io.sugo.pio.operator.OperatorException;
import org.deeplearning4j.models.word2vec.Word2Vec;

public class Word2VecModel extends AbstractModel {

    private Word2Vec vec;
    private ExampleSet resultTable;

    protected Word2VecModel(ExampleSet exampleSet) {
        super(exampleSet);
        // TODO Auto-generated constructor stub
    }

    public void setModel(Word2Vec vec){
        this.vec = vec;
    }

    public void setResultTable(ExampleSet result){
        this.resultTable = result;
    }

    public Word2Vec getModel(){
        return vec;
    }

    public ExampleSet getResult(){
        return resultTable;
    }

    @Override
    public ExampleSet apply(ExampleSet testSet) throws OperatorException {
        // TODO Auto-generated method stub
        return null;
    }

}