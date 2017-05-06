package io.sugo.pio.engine.demo.http;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.demo.conf.UserExtensionConfig;
import io.sugo.pio.engine.demo.data.UserExtBatchEventHose;
import io.sugo.pio.engine.training.Engine;
import io.sugo.pio.engine.training.EngineFactory;
import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.userExtension.UserExtensionEngineFactory;
import io.sugo.pio.engine.userExtension.UserExtensionEngineParams;
import io.sugo.pio.engine.userExtension.UserExtensionStatus;
import io.sugo.pio.engine.userFeatureExtraction.UserFeatureExtractionEngineFactory;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;

/**
 * Created by penghuan on 2017/4/6.
 */
public class UserExtensionTrainer extends AbstractTraining implements Runnable {
    private final String queryParam;
    private Repository repository;
    private UserExtensionConfig config;

    public UserExtensionTrainer(String queryParam, Repository repository, UserExtensionConfig config) {
        this.queryParam = queryParam;
        this.repository = repository;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            train();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doTrain(JavaSparkContext sc) throws IOException {
        try {
            BatchEventHose eventHose = new UserExtBatchEventHose(queryParam, config.getUrl(), repository);
            PropertyHose propHose = null;
            EngineParams engineParams = new UserExtensionEngineParams();
            EngineFactory engineFactory = new UserExtensionEngineFactory(propHose, eventHose, repository, engineParams);
            Engine engine = engineFactory.createEngine();
            EngineFactory engineFactory2 = new UserFeatureExtractionEngineFactory(propHose, eventHose, repository, engineParams);
            Engine engine2 = engineFactory2.createEngine();
            engine2.train(sc);
            engine.train(sc);
            UserExtensionStatus.setSuccess(repository);
        } catch (UserExtBatchEventHose.CandidateClearEmptyRuntimeException e) {
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.TargetClearEmpty);
        } catch (UserExtBatchEventHose.TargetEmptyRuntimeException e) {
            e.printStackTrace();
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.TargetEmpty);
        } catch (UserExtBatchEventHose.CandidateEmptyRuntimeException e) {
            e.printStackTrace();
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.CandidateEmpty);
        } catch (UserExtBatchEventHose.QueryIORuntimeException e) {
            e.printStackTrace();
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.TimeOut);
        } catch (UserExtBatchEventHose.HasStringRuntimeException e) {
            e.printStackTrace();
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.HasString);
        } catch (Exception e) {
            e.printStackTrace();
            UserExtensionStatus.Info info = UserExtensionStatus.Info.Unkown;
            String msg = e.getMessage();
            if (msg != null && !msg.isEmpty()) {
                info.setMessage(msg);
            }
            UserExtensionStatus.setFailed(repository, info);
        }
    }
}
