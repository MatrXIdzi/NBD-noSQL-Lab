package org.restaurant.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import org.restaurant.cassandra.*;
import org.restaurant.model.Reservation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class CassandraReservationRepository {
    private final ReservationCassandraDao reservationDao;

    public CassandraReservationRepository(CqlSession session) {
        DaoMapper mapper = new DaoMapperBuilder(session).build();
        reservationDao = mapper.getReservationDao(CqlIdentifier.fromCql("restaurant"));
    }

    public void create(Reservation reservation) {
        reservationDao.createReservation(reservation);
    }

    public void delete(Reservation reservation) {
        reservationDao.deleteReservation(
                reservation.getClient().getId(),
                reservation.getReservationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                reservation.getElement().getId());
    }

    public List<ReservationByClientCassandra> readAllReservationsByClient(UUID clientId) {
        return reservationDao.getAllReservationsByClient(clientId).all();
    }

    public List<ReservationByDateCassandra> readAllReservationsByDate(LocalDate reservationDate) {
        return reservationDao.getAllReservationsByDate(reservationDate).all();
    }
}
