package io.sugo.pio.engine.search.engine

import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.search.Constants
import io.sugo.pio.engine.search.data.SearchModelData
import io.sugo.pio.engine.training.Model
import org.ansj.lucene5.AnsjAnalyzer
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document._

class SearchModel(val repository: Repository) extends Model[SearchModelData] with Serializable {
  override def save(md: SearchModelData): Unit = {
    val resItem = md.algData
    resItem.foreachPartition{ res =>
      val analyzer: Analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.index_ansj)
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository), analyzer)
      res.foreach{ itemInf =>
        val doc = new Document
        doc.add( new StringField(Constants.ITEM_ID, itemInf._1, Field.Store.YES))
        doc.add( new TextField(Constants.ITEM_NAME, itemInf._2, Field.Store.YES))
        indexWriter.addDocument(doc)
      }
      indexWriter.close()
    }
  }
}
