package io.sugo.pio.client;

/**
 */
public interface InventoryView
{
  public PioServer getInventoryValue(String string);
  public Iterable<PioServer> getInventory();
}
