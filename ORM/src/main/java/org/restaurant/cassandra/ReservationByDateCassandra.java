package org.restaurant.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.GetterStyle;
import java.time.LocalDate;
import java.util.UUID;

@Entity(defaultKeyspace = "restaurant")
@CqlName("reservations_by_date")
@PropertyStrategy(mutable = false, getterStyle = GetterStyle.JAVABEANS)
public class ReservationByDateCassandra {
    @PartitionKey
    @CqlName("reservation_date")
    private LocalDate reservationDate;

    @ClusteringColumn
    @CqlName("element_id")
    private UUID elementId;

    @CqlName("element_type")
    private String elementType;

    public ReservationByDateCassandra(LocalDate reservationDate, UUID elementId, String elementType) {
        this.reservationDate = reservationDate;
        this.elementId = elementId;
        this.elementType = elementType;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public UUID getElementId() {
        return elementId;
    }

    public String getElementType() {
        return elementType;
    }
}
