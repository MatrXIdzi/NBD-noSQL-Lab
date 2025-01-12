package org.restaurant.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.servererrors.TruncateException;
import com.datastax.oss.driver.api.core.type.DataTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.CassandraConnector;
import org.restaurant.cassandra.ReservationByClientCassandra;
import org.restaurant.model.Client;
import org.restaurant.model.Element;
import org.restaurant.model.Reservation;
import org.restaurant.model.Table;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationRepositoryTest {
    private static CassandraReservationRepository reservationRepository;
    private static CassandraConnector connector;

    @BeforeAll
    public static void setUp() {
        connector = new CassandraConnector();
        connector.initSession();
        reservationRepository = new CassandraReservationRepository(connector.getSession());
    }

    @AfterAll
    public static void disconnect() {
        connector.close();
    }

    @BeforeEach
    public void clearDatabase() {
        String truncateQuery1 = "TRUNCATE restaurant.reservations_by_client";
        String truncateQuery2 = "TRUNCATE restaurant.reservations_by_date";
        try {
            connector.getSession().execute(SimpleStatement.newInstance(truncateQuery1));
            connector.getSession().execute(SimpleStatement.newInstance(truncateQuery2));
        } catch (TruncateException e) {
            // truncate requires ALL nodes to be up; if it fails, we just drop the tables and re-create them
            String dropTableQuery1 = "DROP TABLE IF EXISTS restaurant.reservations_by_client";
            connector.getSession().execute(dropTableQuery1);

            String dropTableQuery2 = "DROP TABLE IF EXISTS restaurant.reservations_by_date";
            connector.getSession().execute(dropTableQuery2);

            SimpleStatement createReservationsByClient =
                    createTable(CqlIdentifier.fromCql("reservations_by_client"))
                            .ifNotExists()
                            .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                            .withClusteringColumn(CqlIdentifier.fromCql("reservation_date"), DataTypes.DATE)
                            .withClusteringColumn(CqlIdentifier.fromCql("element_id"), DataTypes.UUID)
                            .withColumn(CqlIdentifier.fromCql("element_name"), DataTypes.TEXT)
                            .build();

            connector.getSession().execute(createReservationsByClient);

            SimpleStatement createReservationsByDate =
                    createTable(CqlIdentifier.fromCql("reservations_by_date"))
                            .ifNotExists()
                            .withPartitionKey(CqlIdentifier.fromCql("reservation_date"), DataTypes.DATE)
                            .withClusteringColumn(CqlIdentifier.fromCql("element_id"), DataTypes.UUID)
                            .withColumn(CqlIdentifier.fromCql("element_type"), DataTypes.TEXT)
                            .build();

            connector.getSession().execute(createReservationsByDate);
        }
    }

    @Test
    public void testCreateAndReadReservation() {
        Client client = new Client("John", "Doe", "12345678901");
        Element element = new Table(20.0, 10, "TableName", true);
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);
        reservationRepository.create(reservation);

        ReservationByClientCassandra retrievedReservation = reservationRepository.readAllReservationsByClient(client.getId()).getFirst();
        assertNotNull(retrievedReservation);
        assertEquals(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), retrievedReservation.getReservationDate());
        assertEquals(client.getEntityId(), retrievedReservation.getClientId());
        assertEquals(element.getEntityId(), retrievedReservation.getElementId());
    }

    @Test
    public void testDeleteReservation() {
        Client client = new Client("John", "Doe", "12345678901");
        Element element = new Table(20.0, 10, "TableName", true);
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);

        reservationRepository.create(reservation);

        reservationRepository.delete(reservation);

        assertTrue(reservationRepository.readAllReservationsByClient(client.getId()).isEmpty());
    }

    @Test
    public void testDeleteNonExistentReservation() {
        Client client = new Client("Jane", "Doe", "12345678901");
        Element element = new Table(22.0, 10, "TableName", true);
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);

        assertDoesNotThrow(() -> reservationRepository.delete(reservation));
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

        assertThrows(IllegalStateException.class, () -> reservationRepository.create(reservation2));
    }
}
