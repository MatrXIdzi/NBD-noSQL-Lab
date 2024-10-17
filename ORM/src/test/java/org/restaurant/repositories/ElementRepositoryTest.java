package org.restaurant.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.restaurant.elements.Hall;
import org.restaurant.elements.Table;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElementRepositoryTest {
    @Test
    public void addElementsTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        ElementRepository elementRepository = new ElementRepository(em);

        Hall hall = new Hall();
        hall.setName("Wielka Sala");
        hall.setMaxCapacity(200);
        hall.setPricePerPerson(50.0);
        hall.setBasePrice(500.0);
        hall.setHasDanceFloor(true);
        hall.setHasBar(false);

        Table table = new Table();
        table.setName("Stolik nr 1");
        table.setMaxCapacity(4);
        table.setPricePerPerson(10.0);
        table.setPremium(true);

        try {
            em.getTransaction().begin();
            elementRepository.add(hall);
            elementRepository.add(table);
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

        ElementRepository elementRepository2 = new ElementRepository(em2);

        try {
            em2.getTransaction().begin();

            Table fetchedTable = (Table) elementRepository2.get(table.getID());
            Hall fetchedHall = (Hall) elementRepository2.get(hall.getID());

            em2.getTransaction().commit();

            assertEquals(fetchedTable.getMaxCapacity(), table.getMaxCapacity());
            assertEquals(fetchedTable.getPricePerPerson(), table.getPricePerPerson());
            assertEquals(fetchedTable.isPremium(), table.isPremium());

            assertEquals(fetchedHall.getMaxCapacity(), hall.getMaxCapacity());
            assertEquals(fetchedHall.getPricePerPerson(), hall.getPricePerPerson());
            assertEquals(fetchedHall.isDanceFloor(), hall.isDanceFloor());
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
