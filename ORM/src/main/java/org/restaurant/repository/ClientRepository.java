package org.restaurant.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final static String HASHPREFIX = "client:";
    ObjectMapper mapper = new ObjectMapper();

    public ClientRepository(MongoDatabase database, RedisConnection redisConnection) {
        mongoClientRepository = new MongoClientRepository(database);
        this.redisConnection = redisConnection;
        pool = redisConnection.getJedisPooled();
    }

    @Override
    public void create(Client client) {
        mongoClientRepository.create(client);
        setClientInRedis(client);
    }

    private void setClientInRedis(Client client) {
        System.out.println(client.toString());
        pool.jsonSet(HASHPREFIX + client.getEntityId().toString(), client);
        pool.expire(HASHPREFIX + client.getEntityId().toString(), 5);
    }

    @Override
    public void update(Client client) {
        mongoClientRepository.update(client);
        setClientInRedis(client);
    }

    @Override
    public void delete(UUID id) {
        pool.jsonDel(HASHPREFIX + id);
        mongoClientRepository.delete(id);
    }

    @Override
    public Client read(UUID id) {
        if (pool.exists(HASHPREFIX + id.toString())) {
            Client client = pool.jsonGet(HASHPREFIX + id.toString(), Client.class);
            System.out.println(client.toString());
            return client;
        }
        return mongoClientRepository.read(id);
    }

    @Override
    public List<Client> readAll() {
        return mongoClientRepository.readAll();
    }
}
