package org.restaurant;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.restaurant.cassandra.*;
import org.restaurant.model.Client;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//@Disabled
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

        // Execute the query
        Row row = connector.getSession().execute(select.build()).one();

        assertNotNull(row);
    }

    @Test
    public void createTablesTest() {
        connector.createTables();

        // List of table names to check
        List<String> tablesList = List.of("clients", "elements", "reservations_by_date", "reservations_by_client");

        for (String tableName : tablesList) {
            Select select = QueryBuilder.selectFrom("system_schema", "tables")
                    .column("table_name")
                    .whereColumn("keyspace_name").isEqualTo(QueryBuilder.literal("restaurant"))
                    .whereColumn("table_name").isEqualTo(QueryBuilder.literal(tableName));

            // Execute the query
            Row row = connector.getSession().execute(select.build()).one();

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
}
