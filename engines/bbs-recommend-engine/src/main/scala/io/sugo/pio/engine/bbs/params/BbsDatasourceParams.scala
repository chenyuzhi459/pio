package io.sugo.pio.engine.bbs.params

import io.sugo.pio.engine.training.Params

/**
  */
case class BbsDatasourceParams(val sampleNum: Int, val labels: Array[String]) extends Params
