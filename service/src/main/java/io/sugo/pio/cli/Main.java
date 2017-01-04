package io.sugo.pio.cli;

import com.google.inject.Injector;
import io.airlift.airline.Cli;
import io.airlift.airline.Help;
import io.airlift.airline.ParseException;
import io.sugo.pio.guice.GuiceInjectors;

/**
 */
public class Main {
    public static void main(String[] args) {
        final Cli.CliBuilder<Runnable> builder = Cli.builder("pio");

        builder.withDescription("Pio command-line runner.")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class, Version.class);

        builder.withGroup("server")
                .withDescription("Run one of the Pio server types.")
                .withDefaultCommand(Help.class)
                .withCommands(
                        CliPio.class
                );

        builder.withGroup("tools")
                .withDescription("Various tools for working with Druid")
                .withDefaultCommand(Help.class)
                .withCommands(
                        PullDependencies.class,
                        CreateTables.class
                );


        final Injector injector = GuiceInjectors.makeStartupInjector();
//        final ExtensionsConfig config = injector.getInstance(ExtensionsConfig.class);
//        final Collection<CliCommandCreator> extensionCommands = Initialization.getFromExtensions(
//                config,
//                CliCommandCreator.class
//        );

//        for (CliCommandCreator creator : extensionCommands) {
//            creator.addCommands(builder);
//        }

        final Cli<Runnable> cli = builder.build();
        try {
            final Runnable command = cli.parse(args);
            if (!(command instanceof Help)) { // Hack to work around Help not liking being injected
                injector.injectMembers(command);
            }
            command.run();
        }
        catch (ParseException e) {
            System.out.println("ERROR!!!!");
            System.out.println(e.getMessage());
            System.out.println("===");
            cli.parse(new String[]{"help"}).run();
        }
    }
}
