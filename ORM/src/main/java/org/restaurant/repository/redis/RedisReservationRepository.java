package org.restaurant.repository.redis;

import com.mongodb.client.MongoDatabase;
import org.restaurant.RedisConnection;
import org.restaurant.repository.mongo.ReservationRepository;
import redis.clients.jedis.JedisPooled;

public class RedisReservationRepository extends ReservationRepository {
    private JedisPooled pool;
    private RedisConnection redisConnection;

    public RedisReservationRepository(MongoDatabase database, RedisConnection redisConnection) {
        super(database);
        this.redisConnection = redisConnection;
        pool = redisConnection.getPool();
    }



}
