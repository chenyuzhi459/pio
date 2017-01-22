package io.sugo.pio.metadata;

import com.google.common.base.Optional;
import com.metamx.common.Pair;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface MetadataStorageActionHandler<EntryType, StatusType>
{
  /**
   * Creates a new entry.
   * 
   * @param id entry id
   * @param timestamp timestamp this entry was created
   * @param entry object representing this entry
   * @param active active or inactive flag
   * @param status status object associated wit this object, can be null
   * @throws EntryExistsException
   */
  public void insert(
          @NotNull String id,
          @NotNull DateTime timestamp,
          @NotNull EntryType entry,
          boolean active,
          @Nullable StatusType status
  ) throws EntryExistsException;


  /**
   * Sets or updates the status for any active entry with the given id.
   * Once an entry has been set inactive, its status cannot be updated anymore.
   *
   * @param entryId entry id
   * @param active active
   * @param status status
   * @return true if the status was updated, false if the entry did not exist of if the entry was inactive
   */
  public boolean setStatus(String entryId, boolean active, StatusType status);

  /**
   * Retrieves the entry with the given id.
   *
   * @param entryId entry id
   * @return optional entry, absent if the given id does not exist
   */
  public Optional<EntryType> getEntry(String entryId);

  /**
   * Retrieve the status for the entry with the given id.
   *
   * @param entryId entry id
   * @return optional status, absent if entry does not exist or status is not set
   */
  public Optional<StatusType> getStatus(String entryId);

  /**
   * Return all active entries with their respective status
   *
   * @return list of (entry, status) pairs
   */
  public List<Pair<EntryType, StatusType>> getActiveEntriesWithStatus();

  /**
   * Return all statuses for inactive entries created on or later than the given timestamp
   *
   * @param timestamp timestamp
   * @return list of statuses
   */
  public List<StatusType> getInactiveStatusesSince(DateTime timestamp);
}
