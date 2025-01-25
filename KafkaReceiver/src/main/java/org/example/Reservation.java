package org.example;

import java.util.Date;
import java.util.UUID;

public class Reservation {
    private final UUID id;

    private final Date reservationDate;

    private final UUID clientId;

    private final UUID elementId;

    private final String restaurantName;

    public Reservation(UUID id, Date reservationDate, UUID clientId, UUID elementId, String restaurantName) {
        this.id = id;
        this.reservationDate = reservationDate;
        this.clientId = clientId;
        this.elementId = elementId;
        this.restaurantName = restaurantName;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public UUID getClientId() {
        return clientId;
    }

    public UUID getElementId() {
        return elementId;
    }

    public UUID getId() {
        return id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
}
