package io.sugo.pio.operator.extension.jdbc.tools.jdbc.connection;

import io.sugo.pio.repository.RepositoryAccessor;

public class TrueAccessValidator implements DatabaseAccessValidator {
    public TrueAccessValidator() {
    }

    public boolean canAccessDatabaseConnection(String entryName, RepositoryAccessor accessor) {
        return true;
    }
}
