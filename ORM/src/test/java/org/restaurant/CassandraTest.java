package org.restaurant;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.restaurant.cassandra.*;
import org.restaurant.model.Client;
import org.restaurant.model.Element;
import org.restaurant.model.Reservation;
import org.restaurant.model.Table;

import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CassandraTest {
    private static CassandraConnector connector;

    @BeforeAll
    public static void connect() {
        connector = new CassandraConnector();
        connector.initSession();
    }

    @AfterAll
    public static void disconnect() {
        connector.close();
    }

    @Test
    public void keyspaceExistsTest() {
        // Prepare query to check if the "restaurant" keyspace exists
        Select select = QueryBuilder.selectFrom("system_schema", "keyspaces")
                .column("keyspace_name")
                .whereColumn("keyspace_name").isEqualTo(QueryBuilder.literal("restaurant"));

        // Execute the query (with consistency level ONE, because the system_schema table isn't replicated)
        Row row = connector.getSession().execute(select.build().setConsistencyLevel(ConsistencyLevel.ONE)).one();

        assertNotNull(row);
    }

    @Test
    public void tablesExistTest() {
        // List of table names to check
        List<String> tablesList = List.of("clients", "elements", "reservations_by_date", "reservations_by_client");

        for (String tableName : tablesList) {
            Select select = QueryBuilder.selectFrom("system_schema", "tables")
                    .column("table_name")
                    .whereColumn("keyspace_name").isEqualTo(QueryBuilder.literal("restaurant"))
                    .whereColumn("table_name").isEqualTo(QueryBuilder.literal(tableName));

            // Execute the query (with consistency level ONE, because the system_schema table isn't replicated)
            Row row = connector.getSession().execute(select.build().setConsistencyLevel(ConsistencyLevel.ONE)).one();

            assertNotNull(row);
        }
    }

    @Test
    public void CRUDClientTest() {
        DaoMapper mapper = new DaoMapperBuilder(connector.getSession()).build();
        ClientCassandraDao clientDao = mapper.getClientDao(CqlIdentifier.fromCql("restaurant"));

        // Create and read
        Client client = new Client(UUID.randomUUID(), "John", "Doe", "123");

        clientDao.insertClient(ModelMapper.toClientCassandra(client));

        Client client2 = ModelMapper.toClient(clientDao.getClientById(client.getId()));

        assertEquals(client.getId(), client2.getId());
        assertEquals(client.getFirstName(), client2.getFirstName());

        // Update
        client2.setFirstName("Jane");
        clientDao.updateClient(ModelMapper.toClientCassandra(client2));

        Client client3 = ModelMapper.toClient(clientDao.getClientById(client2.getId()));

        assertEquals(client2.getFirstName(), client3.getFirstName());

        // Delete
        clientDao.deleteClientById(client3.getEntityId());

        assertNull(clientDao.getClientById(client.getId()));
    }

    @Test
    public void CRUDTableTest() {
        DaoMapper mapper = new DaoMapperBuilder(connector.getSession()).build();
        ElementCassandraDao elementDao = mapper.getElementDao(CqlIdentifier.fromCql("restaurant"));

        // Create and read
        Element table = new Table(UUID.randomUUID(), 20.0, 10, "Table1", true);

        elementDao.insertElement(ModelMapper.toElementCassandra(table));

        Element table2 = ModelMapper.toElement(elementDao.getTableById(table.getId()));

        assertEquals(table.getId(), table2.getId());
        assertEquals(table.getName(), table2.getName());

        // Update
        table2.setName("Table updated");
        ((Table) table2).setPremium(false);
        elementDao.updateElement(ModelMapper.toElementCassandra(table2));

        Element table3 = ModelMapper.toElement(elementDao.getTableById(table2.getId()));

        assertEquals(table2.getName(), table3.getName());
        assertEquals(((Table) table2).isPremium(), ((Table) table3).isPremium());

        // Delete
        elementDao.deleteTableById(table3.getEntityId());

        assertNull(elementDao.getTableById(table.getId()));
    }

    @Test
    public void CRDReservationTest() {
        DaoMapper mapper = new DaoMapperBuilder(connector.getSession()).build();
        ReservationCassandraDao reservationDao = mapper.getReservationDao(CqlIdentifier.fromCql("restaurant"));

        // Create and read
        Client client = new Client("John", "Doe", "12345678901");
        Element element = new Table(20.0, 10, "TableName", true);
        UUID reservationId = UUID.randomUUID();

        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 10).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);

        reservationDao.createReservation(reservation);

        ReservationByClientCassandra retrievedReservationByClient = reservationDao.getAllReservationsByClient(client.getId()).one();
        ReservationByDateCassandra retrievedReservationByDate =
                reservationDao.getAllReservationsByDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).one();

        assertNotNull(retrievedReservationByClient);
        assertNotNull(retrievedReservationByDate);

        assertEquals(client.getId(), retrievedReservationByClient.getClientId());
        assertEquals(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), retrievedReservationByClient.getReservationDate());
        assertEquals(element.getId(), retrievedReservationByClient.getElementId());
        assertEquals(element.getName(), retrievedReservationByClient.getElementName());

        assertEquals(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), retrievedReservationByDate.getReservationDate());
        assertEquals(element.getEntityId(), retrievedReservationByDate.getElementId());
        assertEquals("table", retrievedReservationByDate.getElementType());

        // Delete
        reservationDao.deleteReservation(client.getId(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), element.getId());

        assertTrue(reservationDao.getAllReservationsByClient(client.getId()).all().isEmpty());
        assertTrue(reservationDao.getAllReservationsByDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).all().isEmpty());
    }
}
