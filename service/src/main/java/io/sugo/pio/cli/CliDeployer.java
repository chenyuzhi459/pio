package io.sugo.pio.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.sugo.pio.client.task.TaskServiceClient;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.guice.LazySingleton;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 */
@Command(
        name = "deployer",
        description = "Deploy the engine for pio"
)
public class CliDeployer extends GuiceRunnable {
    private static final Logger log = new Logger(CliDeployer.class);

    @Arguments(description = "deploymentSpec.json", required = true)
    private String deploymentSpec;

    @Inject
    private ObjectMapper objectMapper;

    public CliDeployer() {
        super(log);
    }

    @Override
    public void run() {
        Injector injector = makeInjector();
        try {
            File file = new File(deploymentSpec);
            if (!file.exists()) {
                log.error("Could not find the json file");
                System.exit(1);
            }

            String deploySpecJson = FileUtils.readFileToString(file);
            Task task = objectMapper.readValue(deploySpecJson, Task.class);

            TaskServiceClient client = injector.getInstance(TaskServiceClient.class);
            final Lifecycle lifecycle = initLifecycle(injector);
            final Thread hook = new Thread(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            log.info("Running shutdown hook");
                            lifecycle.stop();
                        }
                    }
            );
            Runtime.getRuntime().addShutdownHook(hook);
            client.submitTask(task);
            lifecycle.stop();
        } catch (Throwable t) {
            log.error(t, "Error when starting up.  Failing.");
            System.exit(1);
        }
    }

    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.<Module>of(new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bind(TaskServiceClient.class).in(LazySingleton.class);
            }
        });
    }
}
