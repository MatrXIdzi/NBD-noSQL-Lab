package org.restaurant.elements;

import jakarta.persistence.*;

@Entity
public class Hall extends Element {
    @Column(name = "base_price", nullable = false)
    private double basePrice;

    @Column(name = "has_dance_floor", nullable = false)
    private boolean hasDanceFloor;

    @Column(name = "has_bar", nullable = false)
    private boolean hasBar;

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
