package org.restaurant.elements;


public class Hall extends Element {
    private double basePrice;

    private boolean hasDanceFloor;

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
