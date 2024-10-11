package org.restaurant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.restaurant.clients.Client;
import org.restaurant.elements.Hall;
import org.restaurant.elements.Table;
import org.restaurant.reservations.Reservation;
import org.restaurant.repositories.ClientRepository;
import org.restaurant.repositories.ElementRepository;
import org.restaurant.repositories.ReservationRepository;

import java.util.Date;

public class Main {

    public static void main(String[] args) {

        // Ustawienia JPA
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();

        // Inicjalizacja repozytoriów
        ClientRepository clientRepository = new ClientRepository();
        ElementRepository elementRepository = new ElementRepository();
        ReservationRepository reservationRepository = new ReservationRepository();

        // Dodajemy encje do bazy
        em.getTransaction().begin();

        // Tworzymy nowego klienta
        Client client = new Client("Jan", "Kowalski", "1234567890");
        clientRepository.add(client);

        // Tworzymy nową salę (hall)
        Hall hall = new Hall();
        hall.setName("Wielka Sala");
        hall.setMaxCapacity(200);
        hall.setPricePerPerson(50.0);
        hall.setBasePrice(500.0);
        hall.setHasDanceFloor(true);
        hall.setHasBar(false);
        elementRepository.add(hall);

        // Tworzymy nowy stolik (table)
        Table table = new Table();
        table.setName("Stolik nr 1");
        table.setMaxCapacity(4);
        table.setPricePerPerson(10.0);
        elementRepository.add(table);

        // Tworzymy rezerwację
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setElement(hall);
        reservation.setActive(true);
        reservation.setReservationDate(new Date());
        reservationRepository.add(reservation);

        em.getTransaction().commit();

        // Pobieramy dane z bazy
        Client fetchedClient = clientRepository.get(client.getID());
        System.out.println("Pobrany klient: " + fetchedClient.getFirstName() + " " + fetchedClient.getLastName());

        Reservation fetchedReservation = reservationRepository.get(reservation.getID());
        System.out.println("Pobrana rezerwacja: " + fetchedReservation.getClient().getFirstName() +
                " zarezerwował " + fetchedReservation.getElement().getName());

        em.close();
        emf.close();
    }
}