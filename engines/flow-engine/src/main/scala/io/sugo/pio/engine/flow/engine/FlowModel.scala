package io.sugo.pio.engine.flow.engine

import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.flow.data.FlowModelData
import io.sugo.pio.engine.training.Model
import org.apache.spark.api.java.JavaSparkContext

/**
  */
class FlowModel (val repository: Repository) extends Model[FlowModelData] with Serializable{
  override def save(sc: JavaSparkContext,md: FlowModelData): Unit = {
  }
}
