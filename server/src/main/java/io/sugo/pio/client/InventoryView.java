package io.sugo.pio.client;

/**
 */
public interface InventoryView
{
  public PioDataServer getInventoryValue(String string);
  public Iterable<PioDataServer> getInventory();
}
