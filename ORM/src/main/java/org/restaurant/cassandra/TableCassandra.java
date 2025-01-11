package org.restaurant.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.GetterStyle;

import java.util.UUID;

@Entity(defaultKeyspace = "restaurant")
@CqlName("elements")
@PropertyStrategy(mutable = false, getterStyle = GetterStyle.JAVABEANS)
public class TableCassandra extends ElementCassandra {
    @CqlName("premium")
    private boolean premium;

    public TableCassandra(String elementType, UUID id, boolean premium, double pricePerPerson, int maxCapacity, String name) {
        super("table", id, pricePerPerson, maxCapacity, name);
        this.premium = premium;
    }

    public boolean isPremium() {
        return premium;
    }
}
