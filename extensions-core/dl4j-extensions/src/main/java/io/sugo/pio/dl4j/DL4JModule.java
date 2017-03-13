package io.sugo.pio.dl4j;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.dl4j.layers.DenseLayer;
import io.sugo.pio.dl4j.layers.OutputLayer;
import io.sugo.pio.dl4j.learner.SimpleNeuralNetwork;
import io.sugo.pio.initialization.PioModule;

import java.util.List;

/**
 */
public class DL4JModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(new SimpleModule(DL4JModule.class.getSimpleName())
                .registerSubtypes(
                        new NamedType(SimpleNeuralNetwork.class, "simple_neural_network"),
                        new NamedType(DenseLayer.class, "dense_layer"),
                        new NamedType(OutputLayer.class, "output_layer")
                ));
    }

    @Override
    public void configure(Binder binder) {
    }
}
