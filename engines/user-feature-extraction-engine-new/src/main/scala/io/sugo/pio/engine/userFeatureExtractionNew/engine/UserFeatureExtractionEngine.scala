package io.sugo.pio.engine.userFeatureExtractionNew.engine

import com.typesafe.config.Config
import io.sugo.pio.engine.training._
import io.sugo.pio.engine.userFeatureExtractionNew.data.{UserFeatureExtractionModelData, UserFeatureExtractionPrepareData, UserFeatureExtractionTrainingData}
import io.sugo.pio.engine.userFeatureExtractionNew.eval.{UserFeatureExtractionEvalActualResult, UserFeatureExtractionEvalIndicators, UserFeatureExtractionEvalQuery}
import io.sugo.pio.engine.userFeatureExtractionNew.module.{UserFeatureExtractionAlgorithm, UserFeatureExtractionDatasource, UserFeatureExtractionModel, UserFeatureExtractionPreparator}

/**
  * Created by penghuan on 2017/4/24.
  */
class UserFeatureExtractionEngine(config: Config, engineParams: EngineParams)
  extends Engine [
    UserFeatureExtractionTrainingData,
    UserFeatureExtractionPrepareData,
    UserFeatureExtractionModelData,
    UserFeatureExtractionEvalQuery,
    UserFeatureExtractionEvalActualResult,
    UserFeatureExtractionEvalIndicators
  ] (engineParams) {

  override protected def createDatasource(params: Params): DataSource[UserFeatureExtractionTrainingData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult] = {
    new UserFeatureExtractionDatasource(config)
  }

  override protected def createPreparator(params: Params): Preparator[UserFeatureExtractionTrainingData, UserFeatureExtractionPrepareData] = {
    new UserFeatureExtractionPreparator
  }

  override protected def createAlgorithm(params: Params): Algorithm[UserFeatureExtractionPrepareData, UserFeatureExtractionModelData] = {
    new UserFeatureExtractionAlgorithm
  }

  override protected def createModel(): Model[UserFeatureExtractionModelData] = {
    new UserFeatureExtractionModel(config)
  }

  override protected def createEval(): Evalution[UserFeatureExtractionModelData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult, UserFeatureExtractionEvalIndicators] = {
    null
  }
}
