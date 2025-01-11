package org.restaurant.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.Row;
import org.restaurant.CassandraConnector;
import org.restaurant.cassandra.*;
import org.restaurant.model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CassandraClientRepository implements Repository<Client> {
    private final ClientCassandraDao clientDao;

    public CassandraClientRepository(CqlSession session) {
        DaoMapper mapper = new DaoMapperBuilder(session).build();
        clientDao = mapper.getClientDao(CqlIdentifier.fromCql("restaurant"));
    }

    @Override
    public void create(Client client) {
        clientDao.insertClient(ModelMapper.toClientCassandra(client));
    }

    @Override
    public void update(Client client) {
        if (!clientDao.updateClient(ModelMapper.toClientCassandra(client))) throw new IllegalArgumentException();
    }

    @Override
    public void delete(UUID id) {
        clientDao.deleteClientById(id);
    }

    @Override
    public Client read(UUID id) {
        ClientCassandra clientCassandra = clientDao.getClientById(id);
        if (clientCassandra == null) {
            return null;
        }
        return ModelMapper.toClient(clientCassandra);
    }

    public List<Client> readAll() {
        PagingIterable<ClientCassandra> iterable = clientDao.getAllClients();
        List<Client> clientsList = new ArrayList<>();
        for (ClientCassandra clientCassandra : iterable) {
            clientsList.add(ModelMapper.toClient(clientCassandra));
        }
        return clientsList;
    }
}
