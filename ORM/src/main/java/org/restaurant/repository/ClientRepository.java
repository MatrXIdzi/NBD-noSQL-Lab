package org.restaurant.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import org.restaurant.RedisConnection;
import org.restaurant.model.Client;
import org.restaurant.repository.mongo.MongoClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.UUID;

public class ClientRepository implements Repository<Client> {
    private final MongoClientRepository mongoClientRepository;
    private final JedisPooled pool;
    private final static String HASHPREFIX = "client:";
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ClientRepository.class);

    public ClientRepository(MongoDatabase database, RedisConnection redisConnection) {
        mongoClientRepository = new MongoClientRepository(database);
        pool = redisConnection.getJedisPooled();
    }

    @Override
    public void create(Client client) {
        logger.debug("Create client {}", client);
        mongoClientRepository.create(client);
        setClientInRedis(client);
    }

    private void setClientInRedis(Client client) {
        logger.debug("Setting/updating client data in Redis with id {}...", client.getEntityId());
        try {
            String clientJSON = mapper.writeValueAsString(client);
            pool.jsonSet(HASHPREFIX + client.getEntityId(), clientJSON);
            pool.expire(HASHPREFIX + client.getEntityId(), 20);
        } catch (JedisConnectionException e) {
            logger.warn("Connection to Redis failed, cannot set/update client with id {} in cache", client.getEntityId());
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Client client) {
        mongoClientRepository.update(client);
        pool.jsonDel(HASHPREFIX + client.getEntityId());
    }

    @Override
    public void delete(UUID id) {
        try {
            pool.jsonDel(HASHPREFIX + id);
        }
        catch (JedisConnectionException e) {
            logger.warn("Connection to Redis failed, cannot delete client with id {} from cache", id);
        }
        mongoClientRepository.delete(id);
    }

    @Override
    public Client read(UUID id) {
        logger.debug("Reading client with id {}...", id);
        try {
            if (pool.exists(HASHPREFIX + id)) {
                logger.debug("Client with id {} exists in Redis, retrieving from cache...", id);
                return pool.jsonGet(HASHPREFIX + id, Client.class);
            }
        }
        catch (JedisConnectionException e) {
            logger.warn("Connection to Redis failed, reading client with id {} directly from Mongo...", id);
            return mongoClientRepository.read(id);
        }
        logger.debug("Client with id {} does not exist in Redis, retrieving from Mongo...", id);
        return mongoClientRepository.read(id);
    }

    @Override
    public List<Client> readAll() {
        return mongoClientRepository.readAll();
    }
}
