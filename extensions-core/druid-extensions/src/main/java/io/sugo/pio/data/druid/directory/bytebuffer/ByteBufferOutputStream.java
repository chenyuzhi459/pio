package io.sugo.pio.data.druid.directory.bytebuffer;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.store.BufferedChecksum;
import org.apache.lucene.store.IndexOutput;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * A memory-resident {@link IndexOutput} implementation.
 * 
 * <p>For Lucene internal use</p>
 * @version $Id: RAMOutputStream.java 941125 2010-05-05 00:44:15Z mikemccand $
 */

public class ByteBufferOutputStream extends IndexOutput {
  static final int BUFFER_SIZE = 1024;

  private ByteBufferFile file;

  private ByteBuffer currentBuffer;
  private int currentBufferIndex;

  private int bufferPosition;
  private long bufferStart;
  private int bufferLength;

  private final Checksum crc;

  public ByteBufferOutputStream(String name, ByteBufferFile f, boolean checksum) {
    super("ByteBufferOutputStream(name=" + name + "\")");
    file = f;

    // make sure that we switch to the
    // first needed buffer lazily
    currentBufferIndex = -1;
    currentBuffer = null;
    if (checksum) {
      crc = new BufferedChecksum(new CRC32());
    } else {
      crc = null;
    }
  }

  public void close() throws IOException {
    flush();
  }

  public long length() {
    return file.length;
  }

  public void writeByte(byte b) throws IOException {
    if (bufferPosition == bufferLength) {
      currentBufferIndex++;
      switchCurrentBuffer();
    }
    if (crc != null) {
      crc.update(b);
    }
    currentBuffer.position(bufferPosition++);
    currentBuffer.put(b);
  }

  public void writeBytes(byte[] b, int offset, int len) throws IOException {
    assert b != null;
    if (crc != null) {
      crc.update(b, offset, len);
    }
    while (len > 0) {
      if (bufferPosition ==  bufferLength) {
        currentBufferIndex++;
        switchCurrentBuffer();
      }

      int remainInBuffer = currentBuffer.capacity() - bufferPosition;
      int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;

      currentBuffer.position(bufferPosition);
      currentBuffer.put(b, offset, bytesToCopy);

      offset += bytesToCopy;
      len -= bytesToCopy;
      bufferPosition += bytesToCopy;
      currentBuffer.position(bufferPosition);
    }
  }

  private void switchCurrentBuffer() throws IOException {
    if (currentBufferIndex == file.numBuffers()) {
      currentBuffer = file.addBuffer(BUFFER_SIZE);
    } else {
      currentBuffer = file.getBuffer(currentBufferIndex);
    }
    bufferPosition = 0;
    currentBuffer.position(bufferPosition);
    bufferStart = (long) BUFFER_SIZE * (long) currentBufferIndex;
    bufferLength = currentBuffer.capacity();
  }

  private void setFileLength() {
    long pointer = bufferStart + bufferPosition;
    if (pointer > file.length) {
      file.setLength(pointer);
    }
  }

  public void flush() throws IOException {
    setFileLength();
  }

  public long getFilePointer() {
    return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
  }

  @Override
  public long getChecksum() throws IOException {
    if (crc == null) {
      throw new IllegalStateException("internal RAMOutputStream created with checksum disabled");
    } else {
      return crc.getValue();
    }
  }
}