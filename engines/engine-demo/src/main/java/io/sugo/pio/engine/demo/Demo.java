package io.sugo.pio.engine.demo;

import io.airlift.airline.Cli;
import io.airlift.airline.Help;
import io.airlift.airline.ParseException;
import io.sugo.pio.engine.demo.http.ALSEval;

import java.io.IOException;

/**
 */
public class Demo {
    public static void main(String[] args) throws Exception {
        final Cli.CliBuilder<Runnable> builder = Cli.builder("pio");

        builder.withGroup("demo")
                .withDescription("Pio Demo command-line runner.")
                .withDefaultCommand(Help.class)
                .withCommands(DemoTrainer.class, DemoServer.class);

        final Cli<Runnable> cli = builder.build();
        try {
            final Runnable command = cli.parse(args);
            command.run();
        }
        catch (ParseException e) {
            System.out.println("ERROR!!!!");
            System.out.println(e.getMessage());
            System.out.println("===");
        }
    }

    private static void eval() throws IOException {
        ALSEval alsEval = new ALSEval();
        alsEval.eval();
    }


}
