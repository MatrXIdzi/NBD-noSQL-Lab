package org.restaurant.cassandra;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;

import java.util.UUID;

@Dao
public interface ClientCassandraDao {

    @Insert
    void insertClient(ClientCassandra client);

    @Select
    ClientCassandra getClientById(UUID id);

    @Select
    PagingIterable<ClientCassandra> getAllClients();

    @Update
    void updateClient(ClientCassandra client);

    @Delete(entityClass = ClientCassandra.class)
    void deleteClientById(UUID id);
}
