package org.restaurant.reservations;

import jakarta.persistence.*;
import org.restaurant.clients.Client;
import org.restaurant.elements.Element;
import java.util.Date;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "reservation_date", nullable = false)
    private Date reservationDate;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "element_id", nullable = false)
    private Element element;

    public Reservation() {
    }


    public void setActive(boolean active) {
        this.active = active;
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

    public int getID() {
        return ID;
    }

    public boolean isActive() {
        return active;
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
