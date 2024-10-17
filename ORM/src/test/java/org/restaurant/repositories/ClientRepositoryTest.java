package org.restaurant.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.restaurant.clients.Client;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientRepositoryTest {
    @Test
    public void addClientTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        ClientRepository clientRepository = new ClientRepository(em);

        Client client = new Client("Jan", "Kowalski", "1234567890");
        try {
            em.getTransaction().begin();
            clientRepository.add(client);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }

        EntityManager em2 = emf.createEntityManager();

        ClientRepository clientRepository2 = new ClientRepository(em2);

        try {
            em2.getTransaction().begin();
            Client fetchedClient = clientRepository2.get(client.getID());
            em2.getTransaction().commit();
            assertEquals(fetchedClient.getFirstName(), client.getFirstName());
            assertEquals(fetchedClient.getLastName(), client.getLastName());
            assertEquals(fetchedClient.getPersonalID(), client.getPersonalID());
            assertEquals(fetchedClient.getID(), client.getID());
        } catch (Exception e) {
            if (em2.getTransaction().isActive()) {
                em2.getTransaction().rollback();
            }
            throw e;
        } finally {
            em2.close();
        }
        emf.close();
    }
}
