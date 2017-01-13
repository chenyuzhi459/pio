package io.sugo.pio.engine.popular.engine

import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.popular.data.PopularModelData
import io.sugo.pio.engine.popular.{Constants, LucenceConstants}
import io.sugo.pio.spark.engine.Model
import io.sugo.pio.spark.engine.data.output.Repository
import org.apache.lucene.document._


class PopularModel extends Model[PopularModelData, QueryableModelData] {
  override def save(md: PopularModelData, repository: Repository): Unit = {
    val resItem = md.itemPopular
    resItem.coalesce(1).foreachPartition(iter => {
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository))
      iter.foreach(re => {
          val doc = new Document
          val score = re._2._1
          doc.add(new StringField(Constants.ITEM_ID, re._1.toString, Field.Store.YES))
          for(tag <- re._2._2)
            doc.add(new StringField(Constants.DETAIL_CATEGORY, tag, Field.Store.YES))
          doc.add(new IntField(LucenceConstants.SCORE, score, Field.Store.YES))
          doc.add(new NumericDocValuesField(LucenceConstants.SCORE, score));

          indexWriter.addDocument(doc)
      })
      indexWriter.close()
    })
  }

  override def load(repository: Repository): QueryableModelData = {
    new QueryableModelData(repository)
  }
}
