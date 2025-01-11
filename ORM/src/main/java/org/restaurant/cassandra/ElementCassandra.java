package org.restaurant.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.naming.GetterStyle;

import java.util.UUID;

@Entity(defaultKeyspace = "restaurant")
@CqlName("elements")
@PropertyStrategy(mutable = false, getterStyle = GetterStyle.JAVABEANS)
public class ElementCassandra {
    @PartitionKey
    @CqlName("element_type")
    private String elementType;

    @ClusteringColumn
    @CqlName("element_id")
    private UUID id;

    @CqlName("price_per_person")
    private double pricePerPerson;

    @CqlName("max_capacity")
    private int maxCapacity;

    @CqlName("name")
    private String name;

    public ElementCassandra(String elementType, UUID id, double pricePerPerson, int maxCapacity, String name) {
        this.elementType = elementType;
        this.id = id;
        this.pricePerPerson = pricePerPerson;
        this.maxCapacity = maxCapacity;
        this.name = name;
    }

    public double getPricePerPerson() {
        return pricePerPerson;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public String getName() {
        return name;
    }

    public String getElementType() {
        return elementType;
    }

    public UUID getId() {
        return id;
    }
}
