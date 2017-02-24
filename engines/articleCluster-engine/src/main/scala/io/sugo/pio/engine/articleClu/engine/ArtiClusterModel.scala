package io.sugo.pio.engine.articleClu.engine

import io.sugo.pio.engine.common.lucene.RepositoryDirectory
import io.sugo.pio.engine.common.utils.LuceneUtils
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.articleClu.data.ArtiClusterModelData
import io.sugo.pio.engine.training.Model
import org.apache.lucene.document._
import java.lang.{Float => jFloat}

/**
  */
class ArtiClusterModel(repository: Repository) extends Model[ArtiClusterModelData] with Serializable{
  override def save(md: ArtiClusterModelData): Unit = {
    null
  }
}
