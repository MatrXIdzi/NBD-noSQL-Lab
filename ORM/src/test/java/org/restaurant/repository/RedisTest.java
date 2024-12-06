package org.restaurant.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.restaurant.RedisConnection;
import org.restaurant.model.Client;
import org.restaurant.model.Element;
import org.restaurant.model.Table;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RedisTest {
    private static JedisPooled pool;
    private static RedisConnection redisConnection;

    @BeforeAll
    public static void setUp() {
        try {
            redisConnection = new RedisConnection();
            pool = redisConnection.getJedisPooled();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateReadClearClientCache() {
        // create
        Client client = new Client(UUID.randomUUID(),"John", "Doe", "12345678901");
        pool.jsonSetWithEscape("clients:" + client.getEntityId(), client);

        // read
        assertFalse(pool.keys("*").isEmpty());
        Client client2 = pool.jsonGet("clients:" + client.getEntityId(), Client.class);
        assertEquals(client.getEntityId(), client2.getEntityId());
        assertEquals(client.getPersonalID(), client2.getPersonalID());
        assertEquals(client.getFirstName(), client2.getFirstName());
        assertEquals(client.getLastName(), client2.getLastName());

        // clear
        redisConnection.clearCache();
        assertTrue(pool.keys("*").isEmpty());
    }

    @Test
    public void testCreateReadClearElementCache() {
        // create
        Element element = new Table(UUID.randomUUID(), 20.0, 10, "TableName", true);
        pool.jsonSetWithEscape("elements:" + element.getEntityId(), element);

        // read
        assertFalse(pool.keys("*").isEmpty());
        Element element2 = pool.jsonGet("elements:" + element.getEntityId(), Element.class);
        assertEquals(element.getEntityId(), element2.getEntityId());
        assertEquals(element.getName(), element2.getName());
        assertEquals(element.getPricePerPerson(), element2.getPricePerPerson());
        assertEquals(element.getMaxCapacity(), element2.getMaxCapacity());
        assertTrue(((Table)element2).isPremium());

        // clear
        redisConnection.clearCache();
        assertTrue(pool.keys("*").isEmpty());
    }
}
