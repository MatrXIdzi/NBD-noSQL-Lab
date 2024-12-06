package org.restaurant.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.bson.Document;
import org.restaurant.MongoRepository;
import org.restaurant.RedisConnection;
import org.restaurant.model.Element;
import org.restaurant.model.Hall;
import org.restaurant.model.Table;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ElementRepositoryTest {
    private static ElementRepository elementRepository;
    private static MongoRepository mongoRepository;
    private static RedisConnection redisConnection;

    @BeforeAll
    public static void setUp() {
        mongoRepository = new MongoRepository();
        try {
            redisConnection = new RedisConnection();
            elementRepository = new ElementRepository(mongoRepository.getRestaurantDB(), redisConnection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void clearDatabase() {
        mongoRepository.getRestaurantDB().getCollection("elements").deleteMany(new Document());
        redisConnection.clearCache();
    }

    @Test
    public void testCreateAndReadElement() {
        UUID elementId = UUID.randomUUID();
        Element element = new Table(elementId, 20.0, 10, "TableName", true);
        elementRepository.create(element);

        Element retrievedElement = elementRepository.read(elementId);
        assertNotNull(retrievedElement);
        assertEquals(elementId, retrievedElement.getEntityId());
        assertEquals("TableName", retrievedElement.getName());
        assertEquals(20.0, retrievedElement.getPricePerPerson());
        assertEquals(10, retrievedElement.getMaxCapacity());
        assertTrue(((Table)retrievedElement).isPremium());
    }

    @Test
    public void testUpdateElement() {
        UUID elementId = UUID.randomUUID();
        Element element = new Hall(elementId, 15.0, 120, "HallName", true, true, 150.0);
        elementRepository.create(element);

        element.setName("UpdatedElementName");
        elementRepository.update(element);

        Element updatedElement = elementRepository.read(elementId);
        assertNotNull(updatedElement);
        assertEquals("UpdatedElementName", updatedElement.getName());
        assertTrue(((Hall)updatedElement).isDanceFloor());
        assertTrue(((Hall)updatedElement).isBar());
    }

    @Test
    public void testUpdateNonExistentElement() {
        UUID elementId = UUID.randomUUID();
        Element element = new Hall(elementId, 10.0, 100, "HallName", true, true, 100.0);

        assertThrows(Exception.class, () -> elementRepository.update(element));
    }

    @Test
    public void testDeleteElement() {
        UUID elementId = UUID.randomUUID();
        Element element = new Table(elementId, 10.0, 10, "TableName", false);
        elementRepository.create(element);

        elementRepository.delete(elementId);

        Element deletedElement = elementRepository.read(elementId);
        assertNull(deletedElement);
    }

    @Test
    public void testDeleteNonExistentElement() {
        UUID elementId = UUID.randomUUID();

        assertDoesNotThrow(() -> elementRepository.delete(elementId));
    }

    @Test
    public void testCreateMultipleElementsAndReadAll() {
        UUID tableId1 = UUID.randomUUID();
        Element table1 = new Table(tableId1, 20.0, 10, "Table1", true);
        elementRepository.create(table1);

        UUID tableId2 = UUID.randomUUID();
        Element table2 = new Table(tableId2, 25.0, 8, "Table2", false);
        elementRepository.create(table2);

        UUID hallId = UUID.randomUUID();
        Element hall = new Hall(hallId, 15.0, 120, "Hall", true, true, 150.0);
        elementRepository.create(hall);

        List<Element> elements = elementRepository.readAll();
        assertNotNull(elements);
        assertEquals(3, elements.size());

        assertTrue(elements.stream().anyMatch(e -> e.getEntityId().equals(tableId1) && e.getName().equals("Table1")));
        assertTrue(elements.stream().anyMatch(e -> e.getEntityId().equals(tableId2) && e.getName().equals("Table2")));
        assertTrue(elements.stream().anyMatch(e -> e.getEntityId().equals(hallId) && e.getName().equals("Hall")));
    }
}