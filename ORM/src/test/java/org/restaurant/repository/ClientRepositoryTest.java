package org.restaurant.repository;

import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.MongoRepository;
import org.restaurant.model.Client;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientRepositoryTest {
    private static ClientRepository clientRepository;
    private static MongoRepository mongoRepository;

    @BeforeAll
    public static void setUp() {
        mongoRepository = new MongoRepository();
        clientRepository = new ClientRepository(mongoRepository.getRestaurantDB());
    }

    @BeforeEach
    public void clearDatabase() {
        mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
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
    }

    @Test
    public void testUpdateNonExistentClient() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId, "Non", "Existent", "00000000000");

        assertThrows(Exception.class, () -> clientRepository.update(client));
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
