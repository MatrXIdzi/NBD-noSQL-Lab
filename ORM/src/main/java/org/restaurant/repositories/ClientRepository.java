package org.restaurant.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.restaurant.clients.Client;

public class ClientRepository implements Repository<Client> {
    private EntityManager entityManager;

    public ClientRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void add(Client client) {
        entityManager.persist(client);
    }

    @Override
    public void remove(Client client) {
        entityManager.remove(entityManager.contains(client) ? client : entityManager.merge(client));
    }

    @Override
    public int count() {
        return ((Number) entityManager.createQuery("SELECT COUNT(c) FROM Client c").getSingleResult()).intValue();
    }

    @Override
    public Client get(int ID) {
        return entityManager.find(Client.class, ID, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    }
}
