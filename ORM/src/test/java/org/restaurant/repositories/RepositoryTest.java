package org.restaurant.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.restaurant.clients.Client;
import org.restaurant.elements.Hall;
import org.restaurant.elements.Table;
import org.restaurant.reservations.Reservation;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepositoryTest {

    @Test
    public void addClientTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        ClientRepository clientRepository = new ClientRepository(em);

        Client client = new Client("Jan", "Kowalski", "1234567890");

        em.getTransaction().begin();
        clientRepository.add(client);
        em.getTransaction().commit();

        Client fetchedClient = clientRepository.get(client.getID());

        assertEquals(fetchedClient.getFirstName(), client.getFirstName());
        assertEquals(fetchedClient.getLastName(), client.getLastName());
        assertEquals(fetchedClient.getPersonalID(), client.getPersonalID());
        assertEquals(fetchedClient.getID(), client.getID());

        em.close();
        emf.close();
    }

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

        em.getTransaction().begin();
        elementRepository.add(hall);
        elementRepository.add(table);
        em.getTransaction().commit();

        Table fetchedTable = (Table) elementRepository.get(table.getID());
        Hall fetchedHall = (Hall) elementRepository.get(hall.getID());

        assertEquals(fetchedTable.getMaxCapacity(), table.getMaxCapacity());
        assertEquals(fetchedTable.getPricePerPerson(), table.getPricePerPerson());

        assertEquals(fetchedHall.getMaxCapacity(), hall.getMaxCapacity());
        assertEquals(fetchedHall.getPricePerPerson(), hall.getPricePerPerson());
        assertEquals(fetchedHall.isDanceFloor(), hall.isDanceFloor());

        em.close();
        emf.close();
    }

    @Test
    public void addReservationTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        // initialize repositories, inject entity manager
        ClientRepository clientRepository = new ClientRepository(em);
        ElementRepository elementRepository = new ElementRepository(em);
        ReservationRepository reservationRepository = new ReservationRepository(em);

        Client client = new Client("Jan", "Kowalski", "1234567890");

        Hall hall = new Hall();
        hall.setName("Wielka Sala");
        hall.setMaxCapacity(200);
        hall.setPricePerPerson(50.0);
        hall.setBasePrice(500.0);
        hall.setHasDanceFloor(true);
        hall.setHasBar(false);

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setElement(hall);
        reservation.setActive(true);
        reservation.setReservationDate(new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime());

        em.getTransaction().begin();
        clientRepository.add(client);
        elementRepository.add(hall);
        reservationRepository.add(reservation);
        em.getTransaction().commit();

        Reservation fetchedReservation = reservationRepository.get(reservation.getID());

        assertEquals(fetchedReservation.getClient(), reservation.getClient());
        assertEquals(fetchedReservation.getElement(), reservation.getElement());
        assertEquals(fetchedReservation.getReservationDate(), reservation.getReservationDate());

        em.close();
        emf.close();
    }
}