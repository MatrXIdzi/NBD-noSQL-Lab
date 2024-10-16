package org.restaurant.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.restaurant.elements.Element;

public class ElementRepository implements Repository <Element> {
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
        return ((Number) entityManager.createQuery("SELECT COUNT(c) FROM Element c").getSingleResult()).intValue();
    }

    @Override
    public Element get(int ID) {
        return entityManager.find(Element.class, ID, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    }
}
