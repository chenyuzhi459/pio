package io.sugo.engine.server.http;

import io.sugo.engine.server.conf.UserExtensionConfig;
import io.sugo.engine.server.conf.UserExtensionRepository;
import io.sugo.engine.server.data.UserExtensionEventReader;
import io.sugo.pio.engine.common.data.DataTransfer;
import io.sugo.pio.engine.common.data.LocalDataWriter;
import io.sugo.pio.engine.common.spark.SparkJobServer;
import io.sugo.pio.engine.userExtension.UserExtensionStatus;
import io.sugo.pio.engine.userFeatureExtractionNew.tools.UserFeatureExtractionProperty;

/**
 * Created by penghuan on 2017/5/2.
 */
public class UserExtensionEngineServer implements Runnable {
    private final String queryParam;
    private UserExtensionRepository repository;
    private UserExtensionConfig config;

    public UserExtensionEngineServer(String queryParam, UserExtensionRepository repository, UserExtensionConfig config) {
        this.queryParam = queryParam;
        this.config = config;
        this.repository = repository;
    }

    @Override
    public void run() {
        doTrain();
    }

    private void doTrain() {
        try {
            LocalDataWriter localDataWriter = new LocalDataWriter(repository.getRepository(UserExtensionRepository.RepositoryName.Event));
            UserExtensionEventReader userExtensionEventReader = new UserExtensionEventReader(queryParam, config.getUrl());
            DataTransfer dataTransfer = new DataTransfer(userExtensionEventReader, localDataWriter);
            dataTransfer.transfer();

            String argsStr = String.format("%s=%s,%s=%s,%s=%s,%s=%s",
                    UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.Input),
                    repository.getRepository(UserExtensionRepository.RepositoryName.Event),
                    UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputModel),
                    repository.getRepository(UserExtensionRepository.RepositoryName.FeatureModel),
                    UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputFeatureWeight),
                    repository.getRepository(UserExtensionRepository.RepositoryName.FeatureWeight),
                    UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputFeatureDesign),
                    repository.getRepository(UserExtensionRepository.RepositoryName.FeatureDesign)
            );
            System.out.println(String.format("spark job server info, args [%s]", argsStr));

            SparkJobServer sparkJobServer = new SparkJobServer(config.getSparkJobServerHost(), config.getSparkJobServerPort());
            String context = config.getSparkJobServerContext();
            String appName = config.getSparkJobServerAppName();
            String classPath = config.getSparkJobServerClassPath();
            if (!sparkJobServer.isContextExists(context)) {
                throw new Exception(String.format("spark job server error, context[%s] not found", context));
            }
            if (!sparkJobServer.isAppNameExists(appName)) {
                throw new Exception(String.format("spark job server error, appName[%s] not found", appName));
            }
            String jobId = sparkJobServer.createJob(context, appName, classPath, argsStr);
            for (int i=0; i<100; i++) {
                String jobStatus = sparkJobServer.getJob(jobId);
                System.out.println(String.format("spark job server job info [%s:%s]", jobId, jobStatus));
                if (jobStatus.equals(SparkJobServer.JobStatus.Finished.toString())) {
                    UserExtensionStatus.setSuccess(repository);
                    return;
                }
                if (jobStatus.equals(SparkJobServer.JobStatus.Error.toString())) {
                    UserExtensionStatus.setFailed(repository);
                    return;
                }
                if (!jobStatus.equals(SparkJobServer.JobStatus.Finished.toString()) &&
                        !jobStatus.equals(SparkJobServer.JobStatus.Error.toString())) {
                    UserExtensionStatus.setFailed(repository);
                    return;
                }
                Thread.sleep(10000);
            }
            UserExtensionStatus.setFailed(repository);
            sparkJobServer.deleteJob(jobId);
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
