package sugo.io.pio.data.druid.directory.bytebuffer;

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

import org.apache.lucene.store.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 */
public class ByteBufferDirectory extends BaseDirectory  {

  HashMap fileMap = new HashMap();
  long sizeInBytes;

  /** Constructs an empty {@link Directory}. */
  public ByteBufferDirectory() {
    this(new SingleInstanceLockFactory());
  }

  /** Constructs an empty {@link Directory} with the given {@link LockFactory}. */
  public ByteBufferDirectory(LockFactory lockFactory) {
    super(lockFactory);
  }

  public ByteBufferDirectory(Directory dir, IOContext context) throws IOException {
    this(dir, false, context);
  }

  private ByteBufferDirectory(Directory dir, boolean closeDir, IOContext context) throws IOException {
    this();
    for (String file : dir.listAll()) {
        copyFrom(dir, file, file, context);
    }
    if (closeDir) {
      dir.close();
    }
  }

  public String[] listAll() {
    ensureOpen();
    Set fileNames = fileMap.keySet();
    String[] result = new String[fileNames.size()];
    int i = 0;
    Iterator it = fileNames.iterator();
    while (it.hasNext())
      result[i++] = (String)it.next();
    return result;
  }

  /** Returns the length in bytes of a file in the directory.
   * @throws IOException if the file does not exist
   */
  public long fileLength(String name) throws IOException {
    ensureOpen();
    ByteBufferFile file;
    file = (ByteBufferFile)fileMap.get(name);
    if (file==null)
      throw new FileNotFoundException(name);
    return file.getLength();
  }

  /** Removes an existing file in the directory.
   * @throws IOException if the file does not exist
   */
  public void deleteFile(String name) throws IOException {
    ensureOpen();
    ByteBufferFile file = (ByteBufferFile)fileMap.get(name);
    if (file!=null) {
        fileMap.remove(name);
        file.directory = null;
        sizeInBytes -= file.sizeInBytes;
    } else
      throw new FileNotFoundException(name);
  }

  /** Renames an existing file in the directory.
   * @throws FileNotFoundException if from does not exist
   * @deprecated
   */
  public void renameFile(String from, String to) throws IOException {
    ensureOpen();
    ByteBufferFile fromFile = (ByteBufferFile)fileMap.get(from);
    if (fromFile==null)
      throw new FileNotFoundException(from);
    ByteBufferFile toFile = (ByteBufferFile)fileMap.get(to);
    if (toFile!=null) {
      sizeInBytes -= toFile.sizeInBytes;       // updates to ByteBufferFile.sizeInBytes synchronized on directory
      toFile.directory = null;
    }
    fileMap.remove(from);
    fileMap.put(to, fromFile);
  }

  /** Creates a new, empty file in the directory with the given name. Returns a stream writing this file. */
  public IndexOutput createOutput(String name, IOContext context) throws IOException {
    ensureOpen();
    ByteBufferFile file = new ByteBufferFile(this);
    ByteBufferFile existing = (ByteBufferFile)fileMap.get(name);
    if (existing!=null) {
      sizeInBytes -= existing.sizeInBytes;
      existing.directory = null;
    }
    fileMap.put(name, file);
    return new ByteBufferOutputStream(name, file, true);
  }

  @Override
  public void sync(Collection<String> names) throws IOException {

  }

  /** Returns a stream reading an existing file. */
  @Override
  public IndexInput openInput(String name, IOContext context) throws IOException {
    ensureOpen();
    ByteBufferFile file;
      file = (ByteBufferFile)fileMap.get(name);
    if (file == null) {
      throw new FileNotFoundException(name);
    }
    return new ByteBufferInputStream(name, file);
  }

  /** Closes the store to future operations, releasing associated memory. */
  public void close() {
    isOpen = false;
    fileMap.clear();
  }
}
