package io.sugo.pio.engine.userExtension.data

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

case class UserExtensionPreparaData(userProfileTarget: RDD[(String, Vector)], userProfileCandidate: RDD[(String, Vector)]) {}
