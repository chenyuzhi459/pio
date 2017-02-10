package io.sugo.pio.cli;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;

/**
 */
@Command(
        name = "deployer",
        description = "Deploy the engine for pio"
)
public class CliDeployer implements Runnable {
    @Arguments(description = "deploySpec.json", required = true)
    public String deploySpec;

    @Override
    public void run() {

        System.out.println(deploySpec);
    }
}
