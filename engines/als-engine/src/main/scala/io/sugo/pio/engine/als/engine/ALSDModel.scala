package io.sugo.pio.engine.als.engine

import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import org.apache.lucene.document._
import java.lang.{Float => jFloat}
import io.sugo.pio.engine.als.{Constants, LucenceConstants}
import io.sugo.pio.engine.als.data.ALSModelData
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.training.Model

class ALSDModel(val repository: Repository) extends Model[ALSModelData] with Serializable {

  override def save(md: ALSModelData): Unit = {
    val modelData = md.model
    modelData.coalesce(1).foreachPartition(iter => {
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository))
      iter.foreach(data => {
          data._2.foreach({
            rating =>
              val doc = new Document
              doc.add(new StringField(Constants.USER_ID, rating.user.toString, Field.Store.YES))
              doc.add(new StringField(Constants.ITEM_ID, rating.product.toString, Field.Store.YES))
              doc.add(new FloatField(LucenceConstants.SCORE, rating.rating.toFloat, Field.Store.YES))
              doc.add(new NumericDocValuesField(LucenceConstants.SCORE, jFloat.floatToIntBits(rating.rating.toFloat)));
              indexWriter.addDocument(doc)
          })
      })
      indexWriter.close()
    })
  }
}
