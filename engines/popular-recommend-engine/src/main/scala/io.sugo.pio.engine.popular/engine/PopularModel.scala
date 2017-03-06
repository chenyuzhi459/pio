package io.sugo.pio.engine.popular.engine

import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.popular.data.PopularModelData
import io.sugo.pio.engine.popular.{Constants, LucenceConstants}
import io.sugo.pio.engine.training.Model
import org.apache.lucene.document._
import org.apache.spark.api.java.JavaSparkContext


class PopularModel(val repository: Repository) extends Model[PopularModelData] with Serializable {

  override def save(sc: JavaSparkContext,md: PopularModelData): Unit = {
    val resItem = md.itemPopular
    resItem.coalesce(1).foreachPartition(iter => {
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository))
      iter.foreach(re => {
          val doc = new Document
          val score = re._2._1
          doc.add(new StringField(Constants.ITEM_ID, re._1.toString, Field.Store.YES))
          for(tag <- re._2._2)
            doc.add(new StringField(Constants.CATEGORY, tag, Field.Store.YES))
          doc.add(new IntField(LucenceConstants.SCORE, score, Field.Store.YES))
          doc.add(new NumericDocValuesField(LucenceConstants.SCORE, score));

          indexWriter.addDocument(doc)
      })
      indexWriter.close()
    })
  }
}

