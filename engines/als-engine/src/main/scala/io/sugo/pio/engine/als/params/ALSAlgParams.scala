package io.sugo.pio.engine.als.params

import io.sugo.pio.engine.training.Params

/**
  */
case class ALSAlgParams (val rank: Int, val numIter: Int) extends Params
