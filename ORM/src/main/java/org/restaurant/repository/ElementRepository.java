package org.restaurant.repository;

import com.mongodb.client.MongoDatabase;
import org.restaurant.RedisConnection;
import org.restaurant.model.Element;
import org.restaurant.repository.mongo.MongoElementRepository;
import redis.clients.jedis.JedisPooled;

import java.util.List;
import java.util.UUID;

public class ElementRepository implements Repository<Element> {
    private MongoElementRepository mongoElementRepository;
    private JedisPooled pool;
    private RedisConnection redisConnection;

    public ElementRepository(MongoDatabase database, RedisConnection redisConnection) {
        mongoElementRepository = new MongoElementRepository(database);
        this.redisConnection = redisConnection;
        pool = redisConnection.getPool();
    }


    @Override
    public void create(Element element) {

    }

    @Override
    public void update(Element element) {

    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Element read(UUID id) {
        return null;
    }

    @Override
    public List<Element> readAll() {
        return List.of();
    }
}
