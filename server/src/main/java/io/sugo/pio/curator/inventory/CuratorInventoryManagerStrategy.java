package io.sugo.pio.curator.inventory;

/**
 */
public interface CuratorInventoryManagerStrategy<ContainerClass>
{
  public ContainerClass deserializeContainer(byte[] bytes);

  public void newContainer(ContainerClass newContainer);
  public void deadContainer(ContainerClass deadContainer);
  public ContainerClass updateContainer(ContainerClass oldContainer, ContainerClass newContainer);
  public void inventoryInitialized();
}
