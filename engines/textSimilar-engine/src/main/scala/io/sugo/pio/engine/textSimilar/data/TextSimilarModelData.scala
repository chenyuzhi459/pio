package io.sugo.pio.engine.textSimilar.data

import scala.collection.mutable

/**
  */
case class TextSimilarModelData (mdata: mutable.HashMap[String, Array[(Double, (String, String))]])
