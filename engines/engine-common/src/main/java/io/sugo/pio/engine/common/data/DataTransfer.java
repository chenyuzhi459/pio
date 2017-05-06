package io.sugo.pio.engine.common.data;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by penghuan on 2017/4/26.
 */
public class DataTransfer {
    private DataReader reader;
    private DataWriter writer;
    private Integer threshold = 100000;

    public DataTransfer(DataReader reader, DataWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public void setDataStream(DataReader reader, DataWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public void transfer() throws IOException {
        Integer cnt = 0;
        Iterator<Object> readRepoIter = reader.getRepositoryIterator();
        Iterator<Object> writeRepoIter = writer.getRepositoryIterator();
        Object writeRepo = writeRepoIter.next();
        while (readRepoIter.hasNext()) {
            Iterator<Object> readDataIter = reader.getDataIterator(readRepoIter.next());
            while (readDataIter.hasNext()) {
                Object data = readDataIter.next();
                writer.write(writeRepo, data);
                cnt = cnt + 1;
                if (cnt >= threshold) {
                    writer.flush();
                    writer.close();
                    cnt = 0;
                    if (writeRepoIter.hasNext()) {
                        writeRepo = writeRepoIter.next();
                    } else {
                        return;
                    }
                }
            }
        }
        writer.flush();
        writer.close();
    }

}
