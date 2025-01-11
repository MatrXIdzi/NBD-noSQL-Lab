package org.restaurant.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.GetterStyle;

import java.util.UUID;

@Entity(defaultKeyspace = "restaurant")
@CqlName("elements")
@PropertyStrategy(mutable = false, getterStyle = GetterStyle.JAVABEANS)
public class HallCassandra extends ElementCassandra {
    @CqlName("base_price")
    private double basePrice;

    @CqlName("dance_floor")
    private boolean hasDanceFloor;

    @CqlName("bar")
    private boolean hasBar;

    public HallCassandra(String elementType, UUID id, double basePrice, boolean danceFloor, boolean bar, double pricePerPerson, int maxCapacity, String name) {
        super("hall", id, pricePerPerson, maxCapacity, name);
        this.basePrice = basePrice;
        this.hasDanceFloor = danceFloor;
        this.hasBar = bar;
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
