package org.restaurant.elements;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Hall")
public class Hall extends Element {

    @Column(name = "base_price")
    private double basePrice;

    @Column(name = "has_dance_floor")
    private boolean hasDanceFloor;

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setHasDanceFloor(boolean hasDanceFloor) {
        this.hasDanceFloor = hasDanceFloor;
    }

    public void setHasBar(boolean hasBar) {
        this.hasBar = hasBar;
    }

    @Column(name = "has_bar")
    private boolean hasBar;


    // Getters and setters...
}
