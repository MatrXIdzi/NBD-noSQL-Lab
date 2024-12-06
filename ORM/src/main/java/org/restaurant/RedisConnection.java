package org.restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisConnection {
    private final JedisPooled pool;
    private static final Logger logger = LoggerFactory.getLogger(RedisConnection.class);

    public RedisConnection() throws IOException {
        Properties properties = new Properties();
        String host;
        int port;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("META-INF/redis-connection.properties")) {
            if (input == null) {
                throw new IOException("Cannot find file redis-connection.properties in META-INF");
            }

            properties.load(input);

            // load values from properties
            host = properties.getProperty("host");
            port = Integer.parseInt(properties.getProperty("port"));
        }

        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().socketTimeoutMillis(100).build();
        pool = new JedisPooled(new HostAndPort(host, port), clientConfig);
        pool.setJsonObjectMapper(new RedisJsonObjectMapper());
    }

    public JedisPooled getJedisPooled() {
        return pool;
    }

    public void clearCache() {
        try {
            getJedisPooled().flushAll();
        }
        catch (JedisConnectionException e) {
            logger.warn("Connection to Redis failed, cannot clear cache");
        }
    }
}
