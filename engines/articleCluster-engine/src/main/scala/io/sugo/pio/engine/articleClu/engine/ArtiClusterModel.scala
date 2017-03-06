package io.sugo.pio.engine.articleClu.engine

import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.articleClu.data.ArtiClusterModelData
import io.sugo.pio.engine.training.Model
import org.apache.spark.api.java.JavaSparkContext
/**
  */

class ArtiClusterModel(clf_repository: Repository, w2v_repository: Repository, map_repository: Repository) extends Model[ArtiClusterModelData] with Serializable{
  override def save(sc: JavaSparkContext, md: ArtiClusterModelData): Unit = {
    val mapfileName = Constants.LABEL_FILE
    val clfModel = md.clfModel
    val w2vModel = md.w2vModel
    val clfPath = clf_repository.getPath
    val w2vPath = w2v_repository.getPath
    val mapPath = map_repository.getPath
    clfModel.save(sc, clfPath)
    w2vModel.save(sc, w2vPath)

    val wr = map_repository.openOutput(mapfileName)
    md.labelMap.foreach{ case (actLabel, quantLabel) =>
      val line = actLabel ++ Constants.LABEL_SEP ++ quantLabel.toString ++ "\n"
      wr.write(line.getBytes)
      wr.flush()
    }
    wr.close()
  }

}
