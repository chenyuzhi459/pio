package io.sugo.pio.engine.common.lucene;

import io.sugo.pio.spark.engine.data.output.LocalFileRepository;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 */
public class RepositoryDirectoryTest {
    @Test
    public void testWriteAndSearch() throws IOException {
        String path = "/tmp/index_" + System.currentTimeMillis();
        LocalFileRepository repository = new LocalFileRepository(path);
        RepositoryDirectory directory = new RepositoryDirectory(repository);
        IndexWriterConfig iwc = new IndexWriterConfig(new NoopAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, iwc);
        for(int i=0;i<100;i++) {
            Document doc = new Document();
            doc.add(new IntField("index", i, Field.Store.NO));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(new MatchAllDocsQuery(), 10);
        FileUtils.deleteDirectory(new File(path));
    }
}
