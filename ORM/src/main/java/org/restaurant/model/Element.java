package org.restaurant.model;

import org.restaurant.AbstractEntity;

import java.util.UUID;

public abstract class Element extends AbstractEntity {

    private double pricePerPerson;

    private int maxCapacity;

    private String name;

    public Element(double pricePerPerson, int maxCapacity, String name) {
        super();
        this.pricePerPerson = pricePerPerson;
        this.maxCapacity = maxCapacity;
        this.name = name;
    }

    public Element(UUID id, double pricePerPerson, int maxCapacity, String name) {
        super(id);
        this.pricePerPerson = pricePerPerson;
        this.maxCapacity = maxCapacity;
        this.name = name;
    }

    public double getPricePerPerson() {
        return pricePerPerson;
    }

    public void setPricePerPerson(double pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
