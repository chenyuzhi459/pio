package io.sugo.pio.engine.userHistory.engine

import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import org.apache.lucene.document._
import java.lang.{Float => jFloat}

import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.training.Model
import io.sugo.pio.engine.userHistory.{Constants, LucenceConstants}
import io.sugo.pio.engine.userHistory.data.UserHistoryModelData
import org.apache.spark.api.java.JavaSparkContext


class UserHistoryModel(val repository: Repository) extends Model[UserHistoryModelData] with Serializable {
  override def save(sc: JavaSparkContext,md: UserHistoryModelData): Unit = {
    val resItem = md.algData
    resItem.foreachPartition{ res =>
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository), null)
      res.foreach{ actInf =>
        val doc = new Document
        doc.add( new StringField(Constants.USER_ID, actInf._1, Field.Store.YES))
        doc.add( new StringField(Constants.ITEM_ID, actInf._2, Field.Store.YES))
        doc.add(new NumericDocValuesField(LucenceConstants.SCORE, actInf._3));
        indexWriter.addDocument(doc)
      }
      indexWriter.close()
    }
  }
}
