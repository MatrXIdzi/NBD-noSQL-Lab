package org.restaurant.elements;

import jakarta.persistence.*;
import jakarta.persistence.Table;

@Entity
@Table(name = "elements")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Element {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;

    @Version
    private int version;

    @Column(name = "price_per_person", nullable = false)
    private double pricePerPerson;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @Column(name = "name", nullable = false)
    private String name;

    public int getID() {
        return ID;
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
