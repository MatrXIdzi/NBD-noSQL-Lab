package org.restaurant.reservations;

import jakarta.persistence.*;
import org.restaurant.clients.Client;
import org.restaurant.elements.Element;
import java.util.Date;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;

    @Version
    private int version;

    @Column(name = "reservation_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date reservationDate;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "element_id", nullable = false)
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
