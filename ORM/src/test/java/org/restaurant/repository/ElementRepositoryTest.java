package org.restaurant.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.servererrors.TruncateException;
import com.datastax.oss.driver.api.core.type.DataTypes;
import org.junit.jupiter.api.*;
import org.restaurant.CassandraConnector;
import org.restaurant.model.Element;
import org.restaurant.model.Hall;
import org.restaurant.model.Table;

import java.util.List;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.junit.jupiter.api.Assertions.*;

public class ElementRepositoryTest {
    private static CassandraElementRepository elementRepository;
    private static CassandraConnector connector;

    @BeforeAll
    public static void setUp() {
        connector = new CassandraConnector();
        connector.initSession();
        elementRepository = new CassandraElementRepository(connector.getSession());
    }

    @AfterAll
    public static void disconnect() {
        connector.close();
    }

    @BeforeEach
    public void clearDatabase() {
        String truncateQuery = "TRUNCATE restaurant.elements";
        try {
            connector.getSession().execute(SimpleStatement.newInstance(truncateQuery));
        } catch (TruncateException e) {
            // truncate requires ALL nodes to be up; if it fails, we just drop the table and re-create it
            String dropTableQuery = "DROP TABLE IF EXISTS restaurant.elements";
            connector.getSession().execute(dropTableQuery);

            SimpleStatement createElements =
                    createTable(CqlIdentifier.fromCql("elements"))
                            .ifNotExists()
                            .withPartitionKey(CqlIdentifier.fromCql("element_type"), DataTypes.TEXT)
                            .withClusteringColumn(CqlIdentifier.fromCql("element_id"), DataTypes.UUID)
                            .withColumn(CqlIdentifier.fromCql("name"), DataTypes.TEXT)
                            .withColumn(CqlIdentifier.fromCql("price_per_person"), DataTypes.DOUBLE)
                            .withColumn(CqlIdentifier.fromCql("max_capacity"), DataTypes.INT)
                            .withColumn(CqlIdentifier.fromCql("premium"), DataTypes.BOOLEAN)
                            .withColumn(CqlIdentifier.fromCql("base_price"), DataTypes.DOUBLE)
                            .withColumn(CqlIdentifier.fromCql("dance_floor"), DataTypes.BOOLEAN)
                            .withColumn(CqlIdentifier.fromCql("bar"), DataTypes.BOOLEAN)
                            .build();

            connector.getSession().execute(createElements);
        }
    }

    @Test
    public void testCreateAndReadElements() {
        Element table = new Table(UUID.randomUUID(), 20.0, 10, "TableName", true);
        Element hall = new Hall(UUID.randomUUID(), 15.0, 120, "HallName", true, false, 150.0);

        elementRepository.create(table);
        elementRepository.create(hall);

        Element retrievedTable = elementRepository.readTable(table.getId());
        assertNotNull(retrievedTable);
        assertEquals(table.getId(), retrievedTable.getId());
        assertEquals("TableName", retrievedTable.getName());
        assertEquals(20.0, retrievedTable.getPricePerPerson());
        assertEquals(10, retrievedTable.getMaxCapacity());
        assertTrue(((Table)retrievedTable).isPremium());

        Element retrievedHall = elementRepository.readHall(hall.getId());
        assertNotNull(retrievedHall);
        assertEquals(hall.getId(), retrievedHall.getId());
        assertEquals("HallName", retrievedHall.getName());
        assertEquals(15.0, retrievedHall.getPricePerPerson());
        assertEquals(120, retrievedHall.getMaxCapacity());
        assertTrue(((Hall)retrievedHall).isDanceFloor());
        assertFalse(((Hall)retrievedHall).isBar());
        assertEquals(150.0, ((Hall)retrievedHall).getBasePrice());
    }

    @Test
    public void testUpdateElement() {
        UUID elementId = UUID.randomUUID();
        Element element = new Hall(elementId, 15.0, 120, "HallName", true, true, 150.0);
        elementRepository.create(element);

        element.setName("UpdatedElementName");
        elementRepository.update(element);

        Element updatedElement = elementRepository.readHall(elementId);
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

        elementRepository.deleteTable(elementId);

        Element deletedElement = elementRepository.readTable(elementId);
        assertNull(deletedElement);
    }

    @Test
    public void testDeleteNonExistentElement() {
        UUID elementId = UUID.randomUUID();

        assertDoesNotThrow(() -> elementRepository.deleteTable(elementId));
        assertDoesNotThrow(() -> elementRepository.deleteHall(elementId));
    }

    @Test
    public void testCreateMultipleElementsAndReadAll() {
        UUID tableId1 = UUID.randomUUID();
        Element table1 = new Table(tableId1, 20.0, 10, "Table1", true);
        elementRepository.create(table1);

        UUID tableId2 = UUID.randomUUID();
        Element table2 = new Table(tableId2, 25.0, 8, "Table2", false);
        elementRepository.create(table2);

        UUID hallId1 = UUID.randomUUID();
        Element hall1 = new Hall(hallId1, 15.0, 120, "Hall", true, false, 150.0);
        elementRepository.create(hall1);

        UUID hallId2 = UUID.randomUUID();
        Element hall2 = new Hall(hallId2, 25.0, 128, "Better Hall", true, true, 350.0);
        elementRepository.create(hall2);

        List<Element> tables = elementRepository.readAllTables();
        assertNotNull(tables);
        assertEquals(2, tables.size());

        List<Element> halls = elementRepository.readAllHalls();
        assertNotNull(halls);
        assertEquals(2, halls.size());

        assertTrue(tables.stream().anyMatch(e -> e.getEntityId().equals(tableId1) && e.getName().equals("Table1")));
        assertTrue(tables.stream().anyMatch(e -> e.getEntityId().equals(tableId2) && e.getName().equals("Table2")));

        assertTrue(halls.stream().anyMatch(e -> e.getEntityId().equals(hallId1) && e.getName().equals("Hall")));
        assertTrue(halls.stream().anyMatch(e -> e.getEntityId().equals(hallId2) && e.getName().equals("Better Hall")));
    }
}