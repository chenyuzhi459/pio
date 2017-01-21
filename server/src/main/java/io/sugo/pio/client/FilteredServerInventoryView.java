package io.sugo.pio.client;


import java.util.concurrent.Executor;

public interface FilteredServerInventoryView extends InventoryView
{
  public void registerServerCallback(Executor exec, ServerView.ServerCallback callback);
}
