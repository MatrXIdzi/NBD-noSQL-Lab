package org.restaurant.model;


import java.util.UUID;

public class Hall extends Element {
    private double basePrice;

    private boolean hasDanceFloor;

    private boolean hasBar;

    public Hall(double pricePerPerson, int maxCapacity, String name, boolean hasDanceFloor, boolean hasBar, double basePrice) {
        super(pricePerPerson, maxCapacity, name);
        this.hasDanceFloor = hasDanceFloor;
        this.hasBar = hasBar;
        this.basePrice = basePrice;
    }
    public Hall(UUID id, double pricePerPerson, int maxCapacity, String name, boolean hasDanceFloor, boolean hasBar, double basePrice) {
        super(id, pricePerPerson, maxCapacity, name);
        this.hasDanceFloor = hasDanceFloor;
        this.hasBar = hasBar;
        this.basePrice = basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setHasDanceFloor(boolean hasDanceFloor) {
        this.hasDanceFloor = hasDanceFloor;
    }

    public void setHasBar(boolean hasBar) {
        this.hasBar = hasBar;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public boolean isDanceFloor() {
        return hasDanceFloor;
    }

    public boolean isBar() {
        return hasBar;
    }
}
