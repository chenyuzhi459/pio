package sugo.io.pio.metadata;

/**
 */
public class EntryExistsException extends Exception
{
    private final String entryId;

    public EntryExistsException(String entryId, Throwable t)
    {
        super(String.format("Entry already exists: %s", entryId), t);
        this.entryId = entryId;
    }

    public EntryExistsException(String entryId)
    {
        this(entryId, null);
    }

    public String getEntryId()
    {
        return entryId;
    }
}
