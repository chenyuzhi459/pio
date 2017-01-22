package io.sugo.pio.client;

import java.util.concurrent.Executor;

/**
 */
public interface ServerView
{
  public void registerServerCallback(Executor exec, ServerCallback callback);

  public enum CallbackAction
  {
    CONTINUE,
    UNREGISTER,
  }

  public static interface ServerCallback
  {
    public CallbackAction serverRemoved(PioDataServer server);

    public CallbackAction serverAdded(PioDataServer server);

    public CallbackAction serverUpdated(PioDataServer oldServer, PioDataServer newServer);

  }
}
