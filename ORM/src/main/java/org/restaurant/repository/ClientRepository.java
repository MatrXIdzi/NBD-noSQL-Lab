package org.restaurant.repository;

import com.mongodb.client.MongoDatabase;
import org.restaurant.RedisConnection;
import org.restaurant.model.Client;
import org.restaurant.repository.mongo.MongoClientRepository;
import redis.clients.jedis.JedisPooled;

import java.util.List;
import java.util.UUID;

public class ClientRepository implements Repository<Client> {
    private MongoClientRepository mongoClientRepository;
    private JedisPooled pool;
    private RedisConnection redisConnection;

    public ClientRepository(MongoDatabase database, RedisConnection redisConnection) {
        mongoClientRepository = new MongoClientRepository(database);
        this.redisConnection = redisConnection;
        pool = redisConnection.getPool();
    }

    @Override
    public void create(Client client) {

    }

    @Override
    public void update(Client client) {

    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Client read(UUID id) {
        return null;
    }

    @Override
    public List<Client> readAll() {
        return List.of();
    }
}
