package org.restaurant;

import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisConnection {
    private JedisPooled pool;
    private Jedis jedis;
    private String host;
    private int port;
    private boolean connected;

    public RedisConnection() {
        try {
            Properties properties = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("META-INF/redis-connection.properties")) {
                if (input == null) {
                    throw new IOException("Nie można znaleźć pliku redis-connection.properties w katalogu META-INF");
                }
                properties.load(input);

                // Wczytanie wartości
                host = properties.getProperty("host");
                port = Integer.parseInt(properties.getProperty("port"));

            } catch (IOException e) {
                e.printStackTrace();
            }

            JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().socketTimeoutMillis(1000).build();

            jedis = new Jedis(new HostAndPort(host, port), clientConfig);
            this.pool = new JedisPooled(new HostAndPort(host, port), clientConfig);
            connected = true;
        } catch (JedisConnectionException e) {
            connected = false;
        }

    }

    public JedisPooled getPool() {
        return pool;
    }

    public boolean isConnected() {
        if (!connected) {
            return false;
        }
        else if (jedis == null) {
            return false;
        }
        return jedis.isConnected();
    }

}
