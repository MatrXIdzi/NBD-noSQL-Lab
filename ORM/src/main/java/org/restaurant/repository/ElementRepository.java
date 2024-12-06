package org.restaurant.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import org.restaurant.RedisConnection;
import org.restaurant.model.Element;
import org.restaurant.repository.mongo.MongoElementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.UUID;

public class ElementRepository implements Repository<Element> {
    private final MongoElementRepository mongoElementRepository;
    private final JedisPooled pool;
    private final static String HASHPREFIX = "element:";
    private static final Logger logger = LoggerFactory.getLogger(ElementRepository.class);

    public ElementRepository(MongoDatabase database, RedisConnection redisConnection) {
        mongoElementRepository = new MongoElementRepository(database);
        pool = redisConnection.getJedisPooled();
    }

    @Override
    public void create(Element element) {
        //logger.debug("Create element {}", element);
        mongoElementRepository.create(element);
        setElementInRedis(element);
    }

    private void setElementInRedis(Element element) {
        logger.debug("Setting element data in Redis with id {}...", element.getEntityId());
        try {
            pool.jsonSetWithEscape(HASHPREFIX + element.getEntityId(), element);
            pool.expire(HASHPREFIX + element.getEntityId(), 20);
        } catch (JedisConnectionException e) {
            logger.warn("Connection to Redis failed, cannot set element with id {} in cache", element.getEntityId());
        }
    }

    @Override
    public void update(Element element) {
        mongoElementRepository.update(element);
        try {
            pool.jsonDel(HASHPREFIX + element.getEntityId());
        }
        catch (JedisConnectionException e) {
            logger.warn("Connection to Redis failed, cannot invalidate element with id {} in cache", element.getEntityId());
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            pool.jsonDel(HASHPREFIX + id);
        }
        catch (JedisConnectionException e) {
            logger.warn("Connection to Redis failed, cannot delete element with id {} from cache", id);
        }
        mongoElementRepository.delete(id);
    }

    @Override
    public Element read(UUID id) {
        //logger.debug("Reading element with id {}...", id);
        try {
            Element element = pool.jsonGet(HASHPREFIX + id, Element.class);
            if (element == null) throw new IllegalArgumentException();
            logger.debug("Element with id {} exists in Redis, retrieving from cache...", id);
            return element;
        }
        catch (JedisConnectionException e) {
            logger.warn("Connection to Redis failed, reading element with id {} directly from Mongo...", id);
            return mongoElementRepository.read(id);
        }
        catch (IllegalArgumentException e) {
            logger.debug("Element with id {} does not exist in Redis, retrieving from Mongo...", id);
            return mongoElementRepository.read(id);
        }
    }

    @Override
    public List<Element> readAll() {
        return mongoElementRepository.readAll();
    }
}
