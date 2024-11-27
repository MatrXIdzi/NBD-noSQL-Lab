package org.restaurant.repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.restaurant.model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class ClientRepository {
    private final MongoCollection<Client> collection;

    public ClientRepository(MongoDatabase database) {
        this.collection = database.getCollection("clients", Client.class);
    }

    public void create(Client client) {
        collection.insertOne(client);
    }

    public Client read(UUID id) {
        return collection.find(eq("_id", id.toString())).first();
    }

    public List<Client> readAll() {
        return collection.find().into(new ArrayList<>());
    }

    public void update(Client client) {
        long modifiedCount = collection.replaceOne(eq("_id", client.getEntityId().toString()), client).getModifiedCount();
        if (modifiedCount == 0) {
            throw new IllegalArgumentException("Client not found");
        }
    }

    public void delete(UUID id) {
        collection.deleteOne(eq("_id", id.toString()));
    }
}