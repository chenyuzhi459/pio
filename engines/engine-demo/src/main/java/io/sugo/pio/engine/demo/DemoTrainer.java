package io.sugo.pio.engine.demo;

import io.airlift.airline.Command;
import io.sugo.pio.engine.demo.http.*;

import java.io.IOException;

/**
 */
@Command(
        name = "demo-trainer",
        description = "Runs a demo-trainer"
)
public class DemoTrainer implements Runnable {
    @Override
    public void run() {
        try {
            train();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void train() throws IOException {
//        PopularTraining popularTraining = new PopularTraining();
//        popularTraining.train();
//        ALSTraining alsTraining = new ALSTraining();
//        alsTraining.train();
//        DetailTraining detailTraining = new DetailTraining();
//        detailTraining.train();
//        FpTraining fpTraining = new FpTraining();
//        fpTraining.train();
//        SearchTraining searchTraining = new SearchTraining();
//        searchTraining.train();
//        UserHistoryTraining userHistoryTraining = new UserHistoryTraining();
//        userHistoryTraining.train();
//        TextSimilarTraining textSimilarTraining = new TextSimilarTraining();
//        textSimilarTraining.train();
        ArtiClusterTraining artiClusterTraining = new ArtiClusterTraining();
        artiClusterTraining.train();
    }
}