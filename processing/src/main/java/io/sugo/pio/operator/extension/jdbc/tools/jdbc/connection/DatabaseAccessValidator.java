package io.sugo.pio.operator.extension.jdbc.tools.jdbc.connection;


import io.sugo.pio.repository.RepositoryAccessor;

public interface DatabaseAccessValidator {
    boolean canAccessDatabaseConnection(String var1, RepositoryAccessor var2);
}
