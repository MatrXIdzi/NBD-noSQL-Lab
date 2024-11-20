package org.restaurant.model;

import org.restaurant.AbstractEntity;

import java.util.Date;
import java.util.UUID;

public class Reservation extends AbstractEntity {

    private Date reservationDate;

    private Client client;

    private Element element;

    public Reservation(Date reservationDate, Client client, Element element) {
        super();
        this.reservationDate = reservationDate;
        this.client = client;
        this.element = element;
    }

    public Reservation(UUID id, Date reservationDate, Client client, Element element) {
        super(id);
        this.reservationDate = reservationDate;
        this.client = client;
        this.element = element;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setElement(Element element) {
        this.element = element;
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
