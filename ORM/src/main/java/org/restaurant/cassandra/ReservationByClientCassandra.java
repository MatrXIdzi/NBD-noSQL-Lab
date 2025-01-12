package org.restaurant.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.GetterStyle;

import java.time.LocalDate;
import java.util.UUID;

@Entity(defaultKeyspace = "restaurant")
@CqlName("reservations_by_client")
@PropertyStrategy(mutable = false, getterStyle = GetterStyle.JAVABEANS)
public class ReservationByClientCassandra {
    @PartitionKey
    @CqlName("client_id")
    private UUID clientId;

    @ClusteringColumn(1)
    @CqlName("reservation_date")
    private LocalDate reservationDate;

    @ClusteringColumn(2)
    @CqlName("element_id")
    private UUID elementId;

    @CqlName("element_name")
    private String elementName;

    public ReservationByClientCassandra(UUID clientId, LocalDate reservationDate, UUID elementId, String elementName) {
        this.clientId = clientId;
        this.reservationDate = reservationDate;
        this.elementId = elementId;
        this.elementName = elementName;
    }

    public UUID getClientId() {
        return clientId;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public UUID getElementId() {
        return elementId;
    }

    public String getElementName() {
        return elementName;
    }
}
