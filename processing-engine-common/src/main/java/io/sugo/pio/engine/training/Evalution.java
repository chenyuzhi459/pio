package io.sugo.pio.engine.training;

/**
 */
public interface Evalution<MD, EQ, EAR, IND> {
    IND predict(MD md, EQ qd, EAR ar);
}
