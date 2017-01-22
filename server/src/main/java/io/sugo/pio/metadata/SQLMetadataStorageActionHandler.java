/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.sugo.pio.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.metamx.common.Pair;
import com.metamx.common.logger.Logger;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.exceptions.CallbackFailedException;
import org.skife.jdbi.v2.exceptions.StatementException;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.util.ByteArrayMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLMetadataStorageActionHandler<EntryType, StatusType>
    implements MetadataStorageActionHandler<EntryType, StatusType>
{
  private static final Logger log = new Logger(SQLMetadataStorageActionHandler.class);

  private final SQLMetadataConnector connector;
  private final ObjectMapper jsonMapper;
  private final TypeReference entryType;
  private final TypeReference statusType;

  private final String entryTypeName;
  private final String entryTable;

  public SQLMetadataStorageActionHandler(
      final SQLMetadataConnector connector,
      final ObjectMapper jsonMapper,
      final MetadataStorageActionHandlerTypes<EntryType, StatusType> types,
      final String entryTypeName,
      final String entryTable
  )
  {
    this.connector = connector;
    this.jsonMapper = jsonMapper;
    this.entryType = types.getEntryType();
    this.statusType = types.getStatusType();
    this.entryTypeName = entryTypeName;
    this.entryTable = entryTable;
  }

  public void insert(
      final String id,
      final DateTime timestamp,
      final EntryType entry,
      final boolean active,
      final StatusType status
  ) throws EntryExistsException
  {
    try {
      connector.retryWithHandle(
          new HandleCallback<Void>()
          {
            @Override
            public Void withHandle(Handle handle) throws Exception
            {
              handle.createStatement(
                  String.format(
                      "INSERT INTO %s (id, created_date, payload, active, status_payload) VALUES (:id, :created_date, :payload, :active, :status_payload)",
                      entryTable
                  )
              )
                    .bind("id", id)
                    .bind("created_date", timestamp.toString())
                    .bind("payload", jsonMapper.writeValueAsBytes(entry))
                    .bind("active", active)
                    .bind("status_payload", jsonMapper.writeValueAsBytes(status))
                    .execute();
              return null;
            }
          },
          new Predicate<Throwable>()
          {
            @Override
            public boolean apply(Throwable e)
            {
              final boolean isStatementException = e instanceof StatementException ||
                                                   (e instanceof CallbackFailedException
                                                    && e.getCause() instanceof StatementException);
              return connector.isTransientException(e) && !(isStatementException && getEntry(id).isPresent());
            }
          }
      );
    }
    catch (Exception e) {
      final boolean isStatementException = e instanceof StatementException ||
                                           (e instanceof CallbackFailedException
                                            && e.getCause() instanceof StatementException);
      if (isStatementException && getEntry(id).isPresent()) {
        throw new EntryExistsException(id, e);
      } else {
        throw Throwables.propagate(e);
      }
    }
  }

  public boolean setStatus(final String entryId, final boolean active, final StatusType status)
  {
    return connector.retryWithHandle(
        new HandleCallback<Boolean>()
        {
          @Override
          public Boolean withHandle(Handle handle) throws Exception
          {
            return handle.createStatement(
                String.format(
                    "UPDATE %s SET active = :active, status_payload = :status_payload WHERE id = :id AND active = TRUE",
                    entryTable
                )
            )
                         .bind("id", entryId)
                         .bind("active", active)
                         .bind("status_payload", jsonMapper.writeValueAsBytes(status))
                         .execute() == 1;
          }
        }
    );
  }

  public Optional<EntryType> getEntry(final String entryId)
  {
    return connector.retryWithHandle(
        new HandleCallback<Optional<EntryType>>()
        {
          @Override
          public Optional<EntryType> withHandle(Handle handle) throws Exception
          {
            byte[] res = handle.createQuery(
                String.format("SELECT payload FROM %s WHERE id = :id", entryTable)
            )
                               .bind("id", entryId)
                               .map(ByteArrayMapper.FIRST)
                               .first();

            return Optional.fromNullable(
                res == null ? null : jsonMapper.<EntryType>readValue(res, entryType)
            );
          }
        }
    );

  }

  public Optional<StatusType> getStatus(final String entryId)
  {
    return connector.retryWithHandle(
        new HandleCallback<Optional<StatusType>>()
        {
          @Override
          public Optional<StatusType> withHandle(Handle handle) throws Exception
          {
            byte[] res = handle.createQuery(
                String.format("SELECT status_payload FROM %s WHERE id = :id", entryTable)
            )
                               .bind("id", entryId)
                               .map(ByteArrayMapper.FIRST)
                               .first();

            return Optional.fromNullable(
                res == null ? null : jsonMapper.<StatusType>readValue(res, statusType)
            );
          }
        }
    );
  }

  public List<Pair<EntryType, StatusType>> getActiveEntriesWithStatus()
  {
    return connector.retryWithHandle(
        new HandleCallback<List<Pair<EntryType, StatusType>>>()
        {
          @Override
          public List<Pair<EntryType, StatusType>> withHandle(Handle handle) throws Exception
          {
            return handle
                .createQuery(
                    String.format(
                        "SELECT id, payload, status_payload FROM %s WHERE active = TRUE ORDER BY created_date",
                        entryTable
                    )
                )
                .map(
                    new ResultSetMapper<Pair<EntryType, StatusType>>()
                    {
                      @Override
                      public Pair<EntryType, StatusType> map(int index, ResultSet r, StatementContext ctx)
                          throws SQLException
                      {
                        try {
                          return Pair.of(
                              jsonMapper.<EntryType>readValue(
                                  r.getBytes("payload"),
                                  entryType
                              ),
                              jsonMapper.<StatusType>readValue(
                                  r.getBytes("status_payload"),
                                  statusType
                              )
                          );
                        }
                        catch (IOException e) {
                          log.error(e, "Failed to parse entry payload");
                          throw new SQLException(e);
                        }
                      }
                    }
                ).list();
          }
        }
    );

  }

  public List<StatusType> getInactiveStatusesSince(final DateTime timestamp)
  {
    return connector.retryWithHandle(
        new HandleCallback<List<StatusType>>()
        {
          @Override
          public List<StatusType> withHandle(Handle handle) throws Exception
          {
            return handle
                .createQuery(
                    String.format(
                        "SELECT id, status_payload FROM %s WHERE active = FALSE AND created_date >= :start ORDER BY created_date DESC",
                        entryTable
                    )
                ).bind("start", timestamp.toString())
                .map(
                    new ResultSetMapper<StatusType>()
                    {
                      @Override
                      public StatusType map(int index, ResultSet r, StatementContext ctx) throws SQLException
                      {
                        try {
                          return jsonMapper.readValue(
                              r.getBytes("status_payload"),
                              statusType
                          );
                        }
                        catch (IOException e) {
                          log.error(e, "Failed to parse status payload");
                          throw new SQLException(e);
                        }
                      }
                    }
                ).list();
          }
        }
    );
  }
}
