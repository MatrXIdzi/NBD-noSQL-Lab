package org.restaurant.repository;

import com.mongodb.MongoWriteException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.KafkaConnector;
import org.restaurant.MongoRepository;
import org.restaurant.model.*;
import org.restaurant.repository.kafka.KafkaReservationRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationRepositoryTest {
    private static Repository<Reservation> reservationRepository;
    private static MongoRepository mongoRepository;
    public static KafkaProducer<String, String> producer;

    @BeforeAll
    public static void setUp() throws ExecutionException, InterruptedException {
        KafkaConnector.initProducer();
        producer = KafkaConnector.getProducer();
        mongoRepository = new MongoRepository();
        reservationRepository = new KafkaReservationRepository(mongoRepository.getRestaurantDB(), producer);
    }

    @BeforeEach
    public void clearDatabase() {
        mongoRepository.getRestaurantDB().getCollection("reservations").deleteMany(new Document());
    }

    @AfterAll
    public static void tearDown() {
        producer.close();
    }

    @Test
    public void testCreateAndReadReservation() {
        Client client = new Client("John", "Doe", "12345678901");
        Element element = new Table(20.0, 10, "TableName", true);
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);
        reservationRepository.create(reservation);

        Reservation retrievedReservation = reservationRepository.read(reservationId);
        assertNotNull(retrievedReservation);
        assertEquals(reservationId, retrievedReservation.getEntityId());
        assertEquals(date, retrievedReservation.getReservationDate());
        assertEquals(client.getEntityId(), retrievedReservation.getClient().getEntityId());
        assertEquals(element.getEntityId(), retrievedReservation.getElement().getEntityId());
    }

    @Test
    public void testUpdateReservation() {
        Client client = new Client("John", "Doe", "12345678901");
        Element element = new Hall(25.0, 128, "HallName", false, true, 200.0);
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);
        reservationRepository.create(reservation);

        Date date2 = new GregorianCalendar(2024, Calendar.MARCH, 5).getTime();
        reservation.setReservationDate(date2);
        reservationRepository.update(reservation);

        Reservation updatedReservation = reservationRepository.read(reservationId);
        assertNotNull(updatedReservation);
        assertEquals(client.getEntityId(), updatedReservation.getClient().getEntityId());
        assertEquals(date2, updatedReservation.getReservationDate());
    }

    @Test
    public void testUpdateNonExistentReservation() {
        Client client = new Client("John", "Doe", "12345678901");
        Element element = new Table(20.0, 10, "TableName", true);
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);

        assertThrows(IllegalArgumentException.class, () -> reservationRepository.update(reservation));
    }

    @Test
    public void testDeleteReservation() {
        Client client = new Client("John", "Doe", "12345678901");
        Element element = new Table(20.0, 10, "TableName", false);
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);
        reservationRepository.create(reservation);

        reservationRepository.delete(reservationId);

        Reservation deletedReservation = reservationRepository.read(reservationId);
        assertNull(deletedReservation);
    }

    @Test
    public void testDeleteNonExistentReservation() {
        UUID reservationId = UUID.randomUUID();

        assertDoesNotThrow(() -> reservationRepository.delete(reservationId));
    }

    @Test
    public void testAlreadyReservedReservation() {
        Element element = new Table(20.0, 10, "TableName", true);

        Client client = new Client("John", "Doe", "12345678901");
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);

        Client client2 = new Client("Jane", "Doe", "12345678902");
        UUID reservationId2 = UUID.randomUUID();
        Date sameDate = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation2 = new Reservation(reservationId2, sameDate, client2, element);

        reservationRepository.create(reservation);

        assertThrows(MongoWriteException.class, () -> reservationRepository.create(reservation2));
    }
}
