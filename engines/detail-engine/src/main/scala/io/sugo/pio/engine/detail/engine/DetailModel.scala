package io.sugo.pio.engine.detail.engine

import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.detail.{Constants, LucenceConstants}
import io.sugo.pio.engine.detail.data.DetailModelData
import io.sugo.pio.engine.training.Model
import org.apache.lucene.document._
import java.lang.{Float => jFloat}

import org.apache.spark.api.java.JavaSparkContext

class DetailModel(val repository: Repository) extends Model[DetailModelData] with Serializable {

  override def save(sc: JavaSparkContext,md: DetailModelData): Unit = {
    val modelData = md.model
    modelData.coalesce(1).foreachPartition(iter => {
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository))
      iter.foreach(data => {
          data._2.foreach({
            item =>
              val doc = new Document
              doc.add(new StringField(Constants.ITEM_ID, data._1, Field.Store.YES))
              doc.add(new StringField(Constants.RELATED_ITEM_ID, item._1, Field.Store.YES))
              doc.add(new FloatField(LucenceConstants.SCORE, item._2.toFloat, Field.Store.YES))
              doc.add(new NumericDocValuesField(LucenceConstants.SCORE, jFloat.floatToIntBits(item._2.toFloat)));
              indexWriter.addDocument(doc)
          })
      })
      indexWriter.close()
    })
  }
}

