package org.restaurant.model;

import org.restaurant.AbstractEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Client extends AbstractEntity {

    private String firstName;

    private String lastName;

    private String personalID;

    public Client(String firstName, String lastName, String personalID) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalID = personalID;
    }

    public Client(UUID id, String firstName, String lastName, String personalID) {
        super(id);
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
