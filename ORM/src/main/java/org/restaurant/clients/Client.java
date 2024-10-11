package org.restaurant.clients;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.restaurant.reservations.Reservation;

import java.util.ArrayList;
import java.util.List;


public class Client {

    private int ID;
    private String firstName;
    private String lastName;
    private String personalID;

    private List<Reservation> Reservations = new ArrayList<>();

    public Client(String firstName, String lastName, String personalID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalID = personalID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonalID() {
        return personalID;
    }

    public void setPersonalID(String personalID) {
        this.personalID = personalID;
    }


}
