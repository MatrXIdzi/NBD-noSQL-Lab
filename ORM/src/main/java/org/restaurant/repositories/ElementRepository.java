package org.restaurant.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.restaurant.clients.Client;
import org.restaurant.elements.Element;

import java.util.ArrayList;
import java.util.List;

public class ElementRepository implements Repository <Element> {
    @PersistenceContext
    private EntityManager entityManager;

    public ElementRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void add(Element element) {
        entityManager.persist(element);
    }

    @Override
    public void remove(Element element) {
        entityManager.remove(entityManager.contains(element) ? element : entityManager.merge(element));
    }

    @Override
    public int count() {
        return ((Number) entityManager.createQuery("SELECT COUNT(c) FROM Client c").getSingleResult()).intValue();
    }

    @Override
    public Element get(int ID) {
        return entityManager.find(Element.class, ID);
    }
}
