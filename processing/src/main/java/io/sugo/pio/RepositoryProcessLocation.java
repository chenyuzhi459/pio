package io.sugo.pio;

import io.sugo.pio.operator.UserData;
import io.sugo.pio.repository.Entry;
import io.sugo.pio.repository.ProcessEntry;
import io.sugo.pio.repository.RepositoryException;
import io.sugo.pio.repository.RepositoryLocation;
import io.sugo.pio.tools.ProgressListener;

import java.io.IOException;


/**
 * @author Simon Fischer
 */
public class RepositoryProcessLocation implements ProcessLocation {

    /**
     * key for custom user property
     */
    public static final String UPDATE_REVISION_ON_SAVE_KEY = "update_revision_on_save";

    /**
     * A simple {@link UserData} object to pass {@link Boolean} values
     */
    public static class SimpleBooleanUserData implements UserData<Object> {

        private boolean value;

        public SimpleBooleanUserData(boolean value) {
            this.value = value;
        }

        @Override
        public UserData<Object> copyUserData(Object newParent) {
            return this;
        }

        public boolean isSet() {
            return value;
        }

    }

    private final RepositoryLocation repositoryLocation;

    public RepositoryProcessLocation(RepositoryLocation location) {
        super();
        this.repositoryLocation = location;
    }

    private ProcessEntry getEntry() throws IOException {
        Entry entry;
        try {
            entry = repositoryLocation.locateEntry();
        } catch (RepositoryException e) {
            throw new IOException("Cannot locate entry '" + repositoryLocation + "': " + e, e);
        }
        if (entry == null) {
            throw new IOException("No such entry: " + repositoryLocation);
        } else if (!(entry instanceof ProcessEntry)) {
            throw new IOException("No process entry: " + repositoryLocation);
        } else {
            return (ProcessEntry) entry;
        }
    }

    @Override
    public OperatorProcess load(ProgressListener listener) throws IOException {
        if (listener != null) {
            listener.setCompleted(60);
        }
        OperatorProcess process = new OperatorProcess("");
        process.setProcessLocation(this);
        if (listener != null) {
            listener.setCompleted(80);
        }
        return process;
    }

    @Override
    public String toHistoryFileString() {
        return "repository " + repositoryLocation.toString();
    }

    public RepositoryLocation getRepositoryLocation() {
        return repositoryLocation;
    }

    @Override
    public String toMenuString() {
        return repositoryLocation.toString();
    }

    @Override
    public String toString() {
        return toMenuString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RepositoryProcessLocation)) {
            return false;
        } else {
            return ((RepositoryProcessLocation) o).repositoryLocation.equals(this.repositoryLocation);
        }
    }

    @Override
    public int hashCode() {
        return repositoryLocation.hashCode();
    }

    @Override
    public String getShortName() {
        return repositoryLocation.getName();
    }
}
