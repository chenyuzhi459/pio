package io.sugo.pio.cli;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.metamx.common.logger.Logger;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.sugo.pio.guice.JsonConfigProvider;
import io.sugo.pio.metadata.MetadataStorageConnector;
import io.sugo.pio.metadata.MetadataStorageConnectorConfig;

import java.util.List;

@Command(
    name = "metadata-init",
    description = "Initialize Metadata Storage"
)
public class CreateTables extends GuiceRunnable
{
  @Option(name = "--connectURI", description = "Database JDBC connection string", required = true)
  private String connectURI;

  @Option(name = "--user", description = "Database username", required = true)
  private String user;

  @Option(name = "--password", description = "Database password", required = true)
  private String password;

  @Option(name = "--base", description = "Base table name")
  private String base;

  private static final Logger log = new Logger(CreateTables.class);

  public CreateTables()
  {
    super(log);
  }

  @Override
  protected List<? extends Module> getModules()
  {
    return ImmutableList.<Module>of(
        new Module()
        {
          @Override
          public void configure(Binder binder)
          {
            JsonConfigProvider.bindInstance(
                binder, Key.get(MetadataStorageConnectorConfig.class), new MetadataStorageConnectorConfig()
                {
                  @Override
                  public String getConnectURI()
                  {
                    return connectURI;
                  }

                  @Override
                  public String getUser()
                  {
                    return user;
                  }

                  @Override
                  public String getPassword()
                  {
                    return password;
                  }
                }
            );
//            JsonConfigProvider.bindInstance(
//                binder, Key.getFromCache(MetadataStorageTablesConfig.class), MetadataStorageTablesConfig.fromBase(base)
//            );
//            JsonConfigProvider.bindInstance(
//                binder, Key.getFromCache(PioNode.class, Self.class), new PioNode("tools", "localhost", -1)
//            );
          }
        }
    );
  }

  @Override
  public void run()
  {
    final Injector injector = makeInjector();
    MetadataStorageConnector dbConnector = injector.getInstance(MetadataStorageConnector.class);
    dbConnector.createOperatorProcessTable();
//    dbConnector.createDataSourceTable();
//    dbConnector.createPendingSegmentsTable();
//    dbConnector.createSegmentTable();
//    dbConnector.createRulesTable();
//    dbConnector.createConfigTable();
//    dbConnector.createTaskTables();
//    dbConnector.createAuditTable();
//    dbConnector.createSupervisorsTable();
  }
}
