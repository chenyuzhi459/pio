package io.sugo.pio.engine.flow;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.flow.data.BackpackRes;
import io.sugo.pio.engine.flow.engine.Backpack;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

/**
 */
public class FlowModelFactory implements ModelFactory<FlowResult> {
    private final Repository repository;

    @JsonCreator
    public FlowModelFactory(@JsonProperty("repository") Repository repository) {
        this.repository = repository;
    }

    @Override
    public PredictionModel<FlowResult> loadModel() {
        return new FlowPredictionModel();
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    static class FlowPredictionModel implements PredictionModel<FlowResult> {

        public FlowResult predict(PredictionQueryObject query) {
            FlowQuery flowQuery = (FlowQuery) query;
            double totalFlow = flowQuery.getTotalFlow();
            double[] flowPackage = flowQuery.getFlowPackage();
            double[] price = flowQuery.getPrice();
            Backpack backpack = new Backpack(totalFlow, flowPackage, price);
            BackpackRes backpackRes = backpack.calcuateFast();
            return new FlowResult(backpackRes.totalPrice(), backpackRes.groups());
        }
    }
}
