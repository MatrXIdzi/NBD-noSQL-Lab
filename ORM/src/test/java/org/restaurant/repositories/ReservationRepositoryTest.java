package org.restaurant.repositories;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.restaurant.clients.Client;
import org.restaurant.elements.Hall;
import org.restaurant.reservations.Reservation;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationRepositoryTest {
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

        try {
            em.getTransaction().begin();
            clientRepository.add(client);
            elementRepository.add(hall);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
        Reservation reservation = new Reservation();
        try {
            em.getTransaction().begin();

            Client client2 = em.find(Client.class, client.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            Hall hall2 = em.find(Hall.class, hall.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            reservation.setClient(client2);
            reservation.setElement(hall2);
            reservation.setActive(true);
            reservation.setReservationDate(new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime());

            reservationRepository.add(reservation);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }

        try {
            em.getTransaction().begin();
            Reservation fetchedReservation = reservationRepository.get(reservation.getID());
            em.getTransaction().commit();

            assertEquals(fetchedReservation.getClient(), reservation.getClient());
            assertEquals(fetchedReservation.getElement(), reservation.getElement());
            assertEquals(fetchedReservation.getReservationDate(), reservation.getReservationDate());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    public void reserveElementAlreadyReservedTest() {
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

        try {
            em.getTransaction().begin();
            clientRepository.add(client);
            elementRepository.add(hall);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }

        Reservation reservation = new Reservation();
        try {
            em.getTransaction().begin();

            Client client2 = em.find(Client.class, client.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            Hall hall2 = em.find(Hall.class, hall.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            reservation.setClient(client2);
            reservation.setElement(hall2);
            reservation.setActive(true);
            reservation.setReservationDate(new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime());

            reservationRepository.add(reservation);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }

        // Attempt to reserve the same table for the same date
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                em.getTransaction().begin();

                Client client3 = em.find(Client.class, client.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                Hall hall3 = em.find(Hall.class, hall.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                Reservation duplicateReservation = new Reservation();
                duplicateReservation.setClient(client3);
                duplicateReservation.setElement(hall3);
                duplicateReservation.setActive(true);
                duplicateReservation.setReservationDate(new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime());

                reservationRepository.add(duplicateReservation);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        });

        em.close();
        emf.close();
    }

    @Test
    public void reserveElementByTwoClientsRaceTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        // initialize repositories, inject entity manager
        ClientRepository clientRepository1 = new ClientRepository(em1);
        ElementRepository elementRepository1 = new ElementRepository(em1);
        ReservationRepository reservationRepository1 = new ReservationRepository(em1);
        ReservationRepository reservationRepository2 = new ReservationRepository(em2);

        Client client1 = new Client("Jan", "Kowalski", "1234567890");
        Client client2 = new Client("Anna", "Nowak", "0987654321");

        Hall hall = new Hall();
        hall.setName("Wielka Sala");
        hall.setMaxCapacity(200);
        hall.setPricePerPerson(50.0);
        hall.setBasePrice(500.0);
        hall.setHasDanceFloor(true);
        hall.setHasBar(false);

        try {
            em1.getTransaction().begin();
            clientRepository1.add(client1);
            clientRepository1.add(client2);
            elementRepository1.add(hall);
            em1.getTransaction().commit();
        } catch (Exception e) {
            if (em1.getTransaction().isActive()) {
                em1.getTransaction().rollback();
            }
            throw e;
        }

        Reservation reservation1 = new Reservation();

        // Attempt to reserve the same table for the same date by another client
        assertThrows(RollbackException.class, () -> {
            try {
                em1.getTransaction().begin();
                em2.getTransaction().begin();

                Client managedClient2 = em2.find(Client.class, client2.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                Hall managedHall2 = em2.find(Hall.class, hall.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                Client managedClient1 = em1.find(Client.class, client1.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                Hall managedHall1 = em1.find(Hall.class, hall.getID(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                reservation1.setClient(managedClient1);
                reservation1.setElement(managedHall1);
                reservation1.setActive(true);
                reservation1.setReservationDate(new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime());

                reservationRepository1.add(reservation1);
                Reservation reservation2 = new Reservation();
                reservation2.setClient(managedClient2);
                reservation2.setElement(managedHall2);
                reservation2.setActive(true);
                reservation2.setReservationDate(new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime());

                reservationRepository2.add(reservation2);

                assertDoesNotThrow(() -> em1.getTransaction().commit());
                em2.getTransaction().commit();
            } catch (Exception e) {
                if (em1.getTransaction().isActive()) {
                    em1.getTransaction().rollback();
                }
                if (em2.getTransaction().isActive()) {
                    em2.getTransaction().rollback();
                }
                throw e;
            }
        });

        em1.close();
        em2.close();
        emf.close();
    }
}