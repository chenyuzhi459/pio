package io.sugo.pio.engine.search.engine

import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import org.apache.lucene.document._
import java.lang.{Float => jFloat}

import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.search.Constants
import io.sugo.pio.engine.search.data.SearchModelData
import io.sugo.pio.engine.training.Model
import org.ansj.lucene5.AnsjAnalyzer
import org.apache.lucene.analysis.Analyzer

class SearchModel extends Model[SearchModelData] {
  override def save(md: SearchModelData, repository: Repository): Unit = {
    val resItem = md.algData
    resItem.foreachPartition{ res =>
      val analyzer: Analyzer = new AnsjAnalyzer(AnsjAnalyzer.TYPE.index_ansj)
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository), analyzer)
      res.foreach{ itemInf =>
        val doc = new Document
        doc.add( new TextField(Constants.ITEM_ID, itemInf._1, Field.Store.YES))
        doc.add( new TextField(Constants.ITEM_NAME, itemInf._2, Field.Store.YES))
        indexWriter.addDocument(doc)
      }
      indexWriter.close()
    }
    println("save search model data is completed.")
  }
}
