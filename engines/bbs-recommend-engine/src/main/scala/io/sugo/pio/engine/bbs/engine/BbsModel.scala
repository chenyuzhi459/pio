package io.sugo.pio.engine.bbs.engine

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.bbs.data.BbsModelData
import io.sugo.pio.engine.training.Model
import org.apache.hadoop.fs.Path
import org.apache.spark.api.java.JavaSparkContext
/**
  */

class BbsModel(repository: Repository) extends Model[BbsModelData] with Serializable{
  override def save(sc: JavaSparkContext, md: BbsModelData): Unit = {
    val data: LSAQueryEngine = md.md
    val path = repository.getPath
    val wholePath = new Path(path, Constants.FILE_NAME).toUri.toString
    serialize[LSAQueryEngine](data, wholePath)
  }

  def serialize[T](o: T, path: String) {
    val bos = new FileOutputStream(path)
    val oos = new ObjectOutputStream(bos)
    oos.writeObject(o)
    oos.close()
  }

  def deserialize[T](path: String): T = {
    val bis = new FileInputStream(path)
    val ois = new ObjectInputStream(bis)
    ois.readObject.asInstanceOf[T]
  }

}
