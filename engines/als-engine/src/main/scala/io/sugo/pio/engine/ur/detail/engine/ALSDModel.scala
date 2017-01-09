package io.sugo.pio.engine.ur.detail.engine

import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.ur.detail.{Constants, LucenceConstants}
import io.sugo.pio.engine.ur.detail.data.ALSModelData
import io.sugo.pio.spark.engine.Model
import io.sugo.pio.spark.engine.data.output.Repository
import org.apache.lucene.document._
import java.lang.{Float => jFloat}

class ALSDModel extends Model[ALSModelData, QueryableModelData] with Serializable {

  override def save(md: ALSModelData, repository: Repository): Unit = {
    val modelData = md.model
    modelData.foreachPartition(iter => {
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository))
      iter.foreach(data => {
        data._2.foreach(
          item => {
            val doc = new Document
            doc.add(new StringField(Constants.USER_ID, data._1.toString, Field.Store.YES))
            for(tag <- item._3)
              doc.add(new StringField(Constants.DETAIL_CATEGORY, tag, Field.Store.YES))
            doc.add(new StringField(Constants.ITEM_ID, item._1.toString, Field.Store.YES))
            doc.add(new FloatField(LucenceConstants.SCORE, item._2, Field.Store.YES))
            doc.add(new NumericDocValuesField(LucenceConstants.SCORE, jFloat.floatToIntBits(item._2)));
            indexWriter.addDocument(doc)
          }
        )
      })
      indexWriter.close()
    })
  }

  override def load(repository: Repository): QueryableModelData = {
   new QueryableModelData(repository)
  }
}
