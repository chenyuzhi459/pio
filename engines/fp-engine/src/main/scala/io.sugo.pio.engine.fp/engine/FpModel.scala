package io.sugo.pio.engine.fp.engine

import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.fp.{Constants, LucenceConstants}
import io.sugo.pio.engine.fp.data.FpModelData
import org.apache.lucene.document._
import java.lang.{Float => jFloat}

import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.training.Model

class FpModel extends Model[FpModelData] {
  override def save(md: FpModelData, repository: Repository): Unit = {
    val resItem = md.fpData
    resItem.coalesce(1).foreachPartition(iter => {
      val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository))
      iter.foreach(re => {
        val doc = new Document
        val itemId = re._1
        val groupItem = re._2.mkString(Constants.CONSEQUENT_SEP)
        val score = re._3.toFloat
        doc.add(new StringField(Constants.ITEMID, re._1.toString, Field.Store.YES))
        doc.add(new StringField(Constants.CONSEQUENTS, groupItem, Field.Store.YES))
        doc.add(new FloatField(LucenceConstants.SCORE, score, Field.Store.YES))
        doc.add(new NumericDocValuesField(LucenceConstants.SCORE, jFloat.floatToIntBits(score)));

        indexWriter.addDocument(doc)
      })
      indexWriter.close()
    })
  }
}
