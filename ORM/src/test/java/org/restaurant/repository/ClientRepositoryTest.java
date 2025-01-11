package org.restaurant.repository;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.servererrors.TruncateException;
import com.datastax.oss.driver.api.core.type.DataTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.CassandraConnector;
import org.restaurant.model.Client;

import java.io.IOException;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.junit.jupiter.api.Assertions.*;

public class ClientRepositoryTest {
    private static Repository<Client> clientRepository;
    private static CassandraConnector connector;

    @BeforeAll
    public static void setUp() {
        connector = new CassandraConnector();
        connector.initSession();
        clientRepository = new CassandraClientRepository(connector.getSession());
    }

    @AfterAll
    public static void disconnect() {
        connector.close();
    }

    @BeforeEach
    public void clearDatabase() {
        String truncateQuery = "TRUNCATE restaurant.clients";
        try {
            connector.getSession().execute(SimpleStatement.newInstance(truncateQuery));
        } catch (TruncateException e) {
            // truncate requires ALL nodes to be up; if it fails, we just drop the table and re-create it
            String dropTableQuery = "DROP TABLE IF EXISTS restaurant.clients";
            connector.getSession().execute(dropTableQuery);

            SimpleStatement createClients =
                    createTable(CqlIdentifier.fromCql("clients"))
                            .ifNotExists()
                            .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                            .withColumn(CqlIdentifier.fromCql("first_name"), DataTypes.TEXT)
                            .withColumn(CqlIdentifier.fromCql("last_name"), DataTypes.TEXT)
                            .withColumn(CqlIdentifier.fromCql("personal_id"), DataTypes.TEXT)
                            .build();

            connector.getSession().execute(createClients);
        }
    }

    @Test
    public void testCreateAndReadClient() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId,"John", "Doe", "12345678901");
        clientRepository.create(client);

        Client retrievedClient = clientRepository.read(clientId);
        assertNotNull(retrievedClient);
        assertEquals(clientId, retrievedClient.getEntityId());
        assertEquals("John", retrievedClient.getFirstName());
        assertEquals("Doe", retrievedClient.getLastName());
        assertEquals("12345678901", retrievedClient.getPersonalID());
    }

    @Test
    public void testUpdateClient() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId, "Jane", "Doe", "98765432101");
        clientRepository.create(client);

        client.setFirstName("Janet");
        clientRepository.update(client);

        Client updatedClient = clientRepository.read(clientId);
        assertNotNull(updatedClient);
        assertEquals("Janet", updatedClient.getFirstName());
        assertEquals("Doe", updatedClient.getLastName());
    }

    @Test
    public void testUpdateNonExistentClient() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId, "Non", "Existent", "00000000000");

        assertThrows(IllegalArgumentException.class, () -> clientRepository.update(client));
    }

    @Test
    public void testDeleteClient() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId, "Mark", "Smith", "11223344556");
        clientRepository.create(client);

        clientRepository.delete(clientId);

        Client deletedClient = clientRepository.read(clientId);
        assertNull(deletedClient);
    }

    @Test
    public void testDeleteNonExistentClient() {
        UUID clientId = UUID.randomUUID();

        assertDoesNotThrow(() -> clientRepository.delete(clientId));
    }
}
