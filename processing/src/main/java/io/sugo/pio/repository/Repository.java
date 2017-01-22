package io.sugo.pio.repository;

/**
 * @author Simon Fischer
 */
public interface Repository extends Folder {

    void addRepositoryListener(RepositoryListener l);

    void removeRepositoryListener(RepositoryListener l);

    /**
     * This will return the entry if existing or null if it can't be found.
     */
    Entry locate(String string) throws RepositoryException;

    /**
     * Returns some user readable information about the state of this repository.
     */
    String getState();

    /**
     * Returns the icon name for the repository.
     */
    String getIconName();

    boolean shouldSave();

    /**
     * Called after the repository is added. Currently unused, but may be useful. Was once used to
     * fetch JDBC connection entries from remote server.
     */
    void postInstall();

    void preRemove();

    /**
     * Returns true if the repository is configurable. In that case,
     */
    boolean isConfigurable();
}
