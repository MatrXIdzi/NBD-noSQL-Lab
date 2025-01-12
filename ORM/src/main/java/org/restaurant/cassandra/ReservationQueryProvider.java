package org.restaurant.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.DefaultBatchType;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import org.restaurant.model.Reservation;
import org.restaurant.model.Table;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

public class ReservationQueryProvider {
    private final CqlSession session;
    private EntityHelper<ReservationByDateCassandra> reservationByDateEntityHelper;
    private EntityHelper<ReservationByClientCassandra> reservationByClientEntityHelper;

    ReservationQueryProvider(MapperContext ctx, EntityHelper<ReservationByDateCassandra> reservationByDateEntityHelper,
                         EntityHelper<ReservationByClientCassandra> reservationByClientEntityHelper) {
        this.session = ctx.getSession();
        this.reservationByDateEntityHelper = reservationByDateEntityHelper;
        this.reservationByClientEntityHelper = reservationByClientEntityHelper;
    }

    void createReservation(Reservation reservation) {
        BoundStatement addReservationByDateStatement = session.prepare(reservationByDateEntityHelper.insert().ifNotExists().build())
                .bind()
                .setLocalDate("reservation_date", reservation.getReservationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .setUuid("element_id", reservation.getElement().getId())
                .setString("element_type", (reservation.getElement() instanceof Table) ? "table" : "hall");

        /* if adding reservation to "reservations_by_date" table failed (probably due to an already existing reservation),
        then throw an exception and DO NOT proceed to adding the reservation to "reservations_by_client" table
         */
        if (!session.execute(addReservationByDateStatement).wasApplied()) throw new IllegalStateException();

        BoundStatement addReservationByClientStatement = session.prepare(reservationByClientEntityHelper.insert().ifNotExists().build())
                .bind()
                .setUuid("client_id", reservation.getClient().getId())
                .setLocalDate("reservation_date", reservation.getReservationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .setUuid("element_id", reservation.getElement().getId())
                .setString("element_name", reservation.getElement().getName());

        if (!session.execute(addReservationByClientStatement).wasApplied()) throw new IllegalStateException();
    }

    void deleteReservation(UUID clientId, LocalDate reservationDate, UUID elementId) {
        BoundStatement deleteReservationByDateStatement = session.prepare(reservationByDateEntityHelper.deleteByPrimaryKey().build())
                .bind()
                .setLocalDate("reservation_date", reservationDate)
                .setUuid("element_id", elementId);

        BoundStatement deleteReservationByClientStatement = session.prepare(reservationByClientEntityHelper.deleteByPrimaryKey().build())
                .bind()
                .setUuid("client_id", clientId)
                .setLocalDate("reservation_date", reservationDate)
                .setUuid("element_id", elementId);

        BatchStatement batch =
                BatchStatement.newInstance(
                        DefaultBatchType.LOGGED,
                        deleteReservationByDateStatement,
                        deleteReservationByClientStatement);

        session.execute(batch);
    }
}
