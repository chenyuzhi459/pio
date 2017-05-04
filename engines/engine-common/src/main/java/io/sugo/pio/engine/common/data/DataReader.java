package io.sugo.pio.engine.common.data;

import java.util.Iterator;

/**
 * Created by penghuan on 2017/4/26.
 */
public interface DataReader<RT, DT> {
    Iterator<RT> getRepositoryIterator();
    Iterator<DT> getDataIterator(RT repository);
    void close();
}
