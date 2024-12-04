package org.restaurant;

import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisConnection {
    private JedisPooled pool;
    private String host;
    private int port;

    public RedisConnection() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("META-INF/redis-connection.properties")) {
            if (input == null) {
                throw new IOException("Nie można znaleźć pliku redis-connection.properties w katalogu META-INF");
            }
            properties.load(input);

            // Wczytanie wartości
            host = properties.getProperty("host");
            port = Integer.parseInt(properties.getProperty("port"));

        }

        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().socketTimeoutMillis(1000).build();
        pool = new JedisPooled(new HostAndPort(host, port), clientConfig);
    }

    public JedisPooled getJedisPooled() {
        return pool;
    }

    public void clearCache() {
        getJedisPooled().flushAll();
    }
}
