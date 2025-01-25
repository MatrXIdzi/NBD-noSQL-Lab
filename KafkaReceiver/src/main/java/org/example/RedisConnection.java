package org.example;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;

public class RedisConnection {
    private final JedisPooled pool;

    public RedisConnection() throws IOException {
        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().socketTimeoutMillis(100).build();
        pool = new JedisPooled(new HostAndPort("localhost", 6380), clientConfig);
        pool.setJsonObjectMapper(new RedisJsonObjectMapper());
    }

    public JedisPooled getJedisPooled() {
        return pool;
    }
}
