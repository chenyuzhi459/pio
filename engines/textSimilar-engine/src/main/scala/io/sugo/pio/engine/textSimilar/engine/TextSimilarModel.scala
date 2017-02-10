package io.sugo.pio.engine.textSimilar.engine

import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.textSimilar.data.TextSimilarModelData
import io.sugo.pio.engine.training.Model
import org.apache.lucene.document._
import java.lang.{Float => jFloat}

/**
  */
class TextSimilarModel(repository: Repository) extends Model[TextSimilarModelData] with Serializable{
  override def save(md: TextSimilarModelData): Unit = {
    val resItem = md.mdata
    val indexWriter = LuceneUtils.getWriter(new RepositoryDirectory(repository))
    resItem.foreach{ resmap =>
      val itemId = resmap._1
      val simInf = resmap._2
      for ((score,(simId, simName)) <- simInf ){
        val doc = new Document
        doc.add(new StringField(Constants.ITEM_ID, itemId, Field.Store.YES))
        doc.add(new StringField(Constants.RELATED_ITEM_ID, simId, Field.Store.YES))
        doc.add(new StringField(Constants.RELATED_ITEM_NAME, simName, Field.Store.YES))
        doc.add(new FloatField(LucenceConstants.SCORE, score.toFloat, Field.Store.YES))
        doc.add(new NumericDocValuesField(LucenceConstants.SCORE, jFloat.floatToIntBits(score.toFloat)));
        indexWriter.addDocument(doc)
      }
    }
    indexWriter.close()
  }
}
