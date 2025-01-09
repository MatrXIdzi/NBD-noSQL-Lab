package org.restaurant.cassandra;

import org.restaurant.model.Client;

public abstract class ModelMapper {
    public static ClientCassandra toClientCassandra(Client client) {
        ClientCassandra clientCassandra =
                new ClientCassandra(client.getEntityId(), client.getFirstName(), client.getLastName(), client.getPersonalID());
        return clientCassandra;
    }

    public static Client toClient(ClientCassandra clientCassandra) {
        Client client =
                new Client(clientCassandra.getId(), clientCassandra.getFirstName(), clientCassandra.getLastName(), clientCassandra.getPersonalID());
        return client;
    }
}
