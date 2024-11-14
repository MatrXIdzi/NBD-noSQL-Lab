package org.restaurant.reservations;

import org.restaurant.clients.Client;
import org.restaurant.elements.Element;
import java.util.Date;

public class Reservation {
    private int ID;

    private Date reservationDate;

    private Client client;

    private Element element;

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public int getID() {
        return ID;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public Client getClient() {
        return client;
    }

    public Element getElement() {
        return element;
    }
}
