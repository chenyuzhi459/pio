package io.sugo.pio.engine.flow;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

public class FlowQuery implements PredictionQueryObject {
    @JsonProperty
    private double totalFlow;

    @JsonProperty
    private double[] flowPackage;

    @JsonProperty
    private double[] price;

    @JsonCreator
    public FlowQuery(
            @JsonProperty("totalFlow") double totalFlow,
            @JsonProperty("flowPackage") double[] flowPackage,
            @JsonProperty("price") double[] price
    ) {
        this.totalFlow = totalFlow;
        this.flowPackage = flowPackage;
        this.price = price;
    }

    public double getTotalFlow(){
        return totalFlow;
    }

    public double[] getFlowPackage(){
        return flowPackage;
    }

    public double[] getPrice(){
        return price;
    }

    @Override
    public String getType() {
        return "flow";
    }
}