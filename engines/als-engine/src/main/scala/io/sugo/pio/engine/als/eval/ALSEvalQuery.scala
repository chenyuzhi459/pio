package io.sugo.pio.engine.als.eval

import org.apache.spark.rdd.RDD

/**
  */
case class ALSEvalQuery (qd: RDD[(Int, Int)])