package io.sugo.pio.engine.ur.detail.engine

import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.ur.detail.{Constants, LucenceConstants}
import io.sugo.pio.engine.ur.detail.data.DetailModelData
import io.sugo.pio.spark.engine.Model
import io.sugo.pio.spark.engine.data.output.Repository
import org.apache.lucene.document._
import java.lang.{Float => jFloat}

class DetailModel extends Model[DetailModelData, QueryableModelData] with Serializable {

  override def save(md: DetailModelData, repository: Repository): Unit = {
    val modelData = md.model
    modelData.coalesce(1).foreachPartition(iter => {
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository))
      iter.foreach(data => {
          data._2.foreach({
            item =>
              val doc = new Document
              doc.add(new StringField(Constants.ITEM_ID, data._1, Field.Store.YES))
              doc.add(new StringField(Constants.RELATED_ITEM_ID, item, Field.Store.YES))
              indexWriter.addDocument(doc)
          })
      })
      indexWriter.close()
    })
  }

  override def load(repository: Repository): QueryableModelData = {
   new QueryableModelData(repository)
  }
}
