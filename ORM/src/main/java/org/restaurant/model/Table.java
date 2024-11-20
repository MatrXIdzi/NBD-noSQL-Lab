package org.restaurant.model;

import java.util.UUID;

public class Table extends Element {
    private boolean premium;

    public Table(double pricePerPerson, int maxCapacity, String name, boolean premium) {
        super(pricePerPerson, maxCapacity, name);
        this.premium = premium;
    }

    public Table(UUID id, double pricePerPerson, int maxCapacity, String name, boolean premium) {
        super(id, pricePerPerson, maxCapacity, name);
        this.premium = premium;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
