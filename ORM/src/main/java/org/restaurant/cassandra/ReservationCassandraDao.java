package org.restaurant.cassandra;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import org.restaurant.model.Reservation;

import java.time.LocalDate;
import java.util.UUID;

@Dao
public interface ReservationCassandraDao {
    @QueryProvider(providerClass = ReservationQueryProvider.class,
            entityHelpers = {ReservationByDateCassandra.class, ReservationByClientCassandra.class})
    void createReservation(Reservation reservation);

    @Select
    PagingIterable<ReservationByClientCassandra> getAllReservationsByClient(UUID clientId);

    @Select
    PagingIterable<ReservationByDateCassandra> getAllReservationsByDate(LocalDate reservationDate);

    @QueryProvider(providerClass = ReservationQueryProvider.class,
            entityHelpers = {ReservationByDateCassandra.class, ReservationByClientCassandra.class})
    void deleteReservation(UUID clientId, LocalDate reservationDate, UUID elementId);
}
