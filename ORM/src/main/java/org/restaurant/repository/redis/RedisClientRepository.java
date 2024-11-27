package org.restaurant.repository.redis;

import com.mongodb.client.MongoDatabase;
import org.restaurant.RedisConnection;
import org.restaurant.repository.mongo.ClientRepository;
import redis.clients.jedis.JedisPooled;

public class RedisClientRepository extends ClientRepository {
    private JedisPooled pool;
    private RedisConnection redisConnection;

    public RedisClientRepository(MongoDatabase database, RedisConnection redisConnection) {
        super(database);
        this.redisConnection = redisConnection;
        pool = redisConnection.getPool();
    }
}
