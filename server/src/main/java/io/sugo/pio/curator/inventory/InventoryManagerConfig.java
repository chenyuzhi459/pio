package io.sugo.pio.curator.inventory;

/**
 */
public interface InventoryManagerConfig
{
  /**
   * The ContainerPath is the path where the InventoryManager should look for new containers of inventory.
   *
   * Because ZK does not allow for children under ephemeral nodes, the common interaction for registering Inventory
   * that might be ephemeral is to
   *
   * 1) Create a permanent node underneath the InventoryPath
   * 2) Create an ephemeral node under the ContainerPath with the same name as the permanent node under InventoryPath
   * 3) For each piece of "inventory", create an ephemeral node as a child of the node created in step (1)
   *
   * @return the containerPath
   */
  public String getContainerPath();
}
