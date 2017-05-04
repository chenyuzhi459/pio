package io.sugo.engine.server.http;

import com.typesafe.config.ConfigFactory;
import io.sugo.engine.server.conf.UserExtensionConfig;
import io.sugo.engine.server.conf.UserExtensionRepository;
import io.sugo.engine.server.data.UserExtensionEventReader;
import io.sugo.pio.engine.common.data.DataTransfer;
import io.sugo.pio.engine.common.data.LocalDataWriter;
import io.sugo.pio.engine.userExtension.UserExtensionStatus;
import io.sugo.pio.engine.userFeatureExtractionNew.main.UserFeatureExtractionLocal;
import io.sugo.pio.engine.userFeatureExtractionNew.tools.UserFeatureExtractionProperty;

import java.io.IOException;

/**
 * Created by penghuan on 2017/4/25.
 */
public class UserExtensionEngineStarter implements Runnable {
    private final String queryParam;
    private UserExtensionRepository repository;
    private UserExtensionConfig config;

    public UserExtensionEngineStarter(String queryParam, UserExtensionRepository repository, UserExtensionConfig config) {
        this.queryParam = queryParam;
        this.config = config;
        this.repository = repository;
    }

    @Override
    public void run() {
        try {
            doTrain();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doTrain() throws IOException {
        try {
            LocalDataWriter localDataWriter = new LocalDataWriter(repository.getRepository(UserExtensionRepository.RepositoryName.Event));
            UserExtensionEventReader userExtensionEventReader = new UserExtensionEventReader(queryParam, config.getUrl());
            DataTransfer dataTransfer = new DataTransfer(userExtensionEventReader, localDataWriter);
            dataTransfer.transfer();

            String configStr = String.format("%s=%s\n%s=%s\n%s=%s\n%s=%s",
                    UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.Input),
                    repository.getRepository(UserExtensionRepository.RepositoryName.Event),
                    UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputModel),
                    repository.getRepository(UserExtensionRepository.RepositoryName.FeatureModel),
                    UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputFeatureWeight),
                    repository.getRepository(UserExtensionRepository.RepositoryName.FeatureWeight),
                    UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputFeatureDesign),
                    repository.getRepository(UserExtensionRepository.RepositoryName.FeatureDesign)
            );
            UserFeatureExtractionLocal userFeatureExtractionLocal = new UserFeatureExtractionLocal();
            userFeatureExtractionLocal.run(ConfigFactory.parseString(configStr));
            UserExtensionStatus.setSuccess(repository);
        } catch (UserExtensionEventReader.CandidateClearEmptyRuntimeException e) {
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.TargetClearEmpty);
        } catch (UserExtensionEventReader.TargetEmptyRuntimeException e) {
            e.printStackTrace();
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.TargetEmpty);
        } catch (UserExtensionEventReader.CandidateEmptyRuntimeException e) {
            e.printStackTrace();
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.CandidateEmpty);
        } catch (UserExtensionEventReader.QueryIORuntimeException e) {
            e.printStackTrace();
            UserExtensionStatus.setFailed(repository, UserExtensionStatus.Info.TimeOut);
        } catch (UserExtensionEventReader.HasStringRuntimeException e) {
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
