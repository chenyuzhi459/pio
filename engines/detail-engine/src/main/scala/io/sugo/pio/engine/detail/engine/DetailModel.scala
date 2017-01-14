package io.sugo.pio.engine.detail.engine

import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.detail.Constants
import io.sugo.pio.engine.detail.data.DetailModelData
import io.sugo.pio.engine.training.Model
import org.apache.lucene.document._

class DetailModel extends Model[DetailModelData] with Serializable {

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
}
