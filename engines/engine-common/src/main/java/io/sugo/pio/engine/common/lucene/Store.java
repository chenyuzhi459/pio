//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.sugo.pio.engine.common.lucene;

/**
 * @lucene.experimental
 */
public interface Store {

    byte[] takeBuffer(int bufferSize);

    void putBuffer(byte[] buffer);

}