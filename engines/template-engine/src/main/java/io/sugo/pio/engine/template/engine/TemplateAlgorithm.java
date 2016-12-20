package io.sugo.pio.engine.template.engine;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import io.sugo.pio.engine.Algorithm;
import io.sugo.pio.engine.template.data.TemplateModelData;
import io.sugo.pio.engine.template.data.TemplatePreparedData;

/**
 */
public class TemplateAlgorithm implements Algorithm<TemplatePreparedData, TemplateModelData> {
    @Override
    public TemplateModelData train(JavaSparkContext sc, TemplatePreparedData pd) {
        Dataset pdData= pd.getData();

        ALS als = new ALS()
                .setMaxIter(5)
                .setRegParam(0.01)
                .setUserCol("userId")
                .setItemCol("movieId")
                .setRatingCol("rating");

        ALSModel model = als.fit(pdData);
        return new TemplateModelData(model);
    }
}
