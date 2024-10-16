package org.restaurant.elements;

import jakarta.persistence.*;

@Entity
@jakarta.persistence.Table(name = "TableElement")
public class Table extends Element {
    @Column(name = "premium", nullable = false)
    private boolean premium;

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
