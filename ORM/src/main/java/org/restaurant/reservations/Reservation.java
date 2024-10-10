package org.restaurant.reservations;

import org.restaurant.clients.Client;
import org.restaurant.elements.Element;

import java.util.Date;

public class Reservation {
    private int ID;
    private boolean active;
    private Date reservationDate;
    private Client client;
    private Element element;
}
