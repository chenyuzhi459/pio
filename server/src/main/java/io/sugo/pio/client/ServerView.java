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
    public CallbackAction serverRemoved(PioServer server);

    public CallbackAction serverAdded(PioServer server);

    public CallbackAction serverUpdated(PioServer oldServer, PioServer newServer);

  }
}
