package io.sugo.pio.engine.common.data;

import java.util.Iterator;

/**
 * Created by penghuan on 2017/4/26.
 */
public interface DataWriter<RT, DT> {
    Iterator<RT> getRepositoryIterator();
    void write(RT repository, DT data);
    void flush();
    void close();
}
