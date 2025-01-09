package org.restaurant.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.entity.naming.GetterStyle;

import java.util.UUID;

@Entity(defaultKeyspace = "restaurant")
@CqlName("clients")
@PropertyStrategy(mutable = false, getterStyle = GetterStyle.JAVABEANS)
public class ClientCassandra {
    @PartitionKey
    @CqlName("client_id")
    private UUID id;

    @CqlName("first_name")
    private String firstName;

    @CqlName("last_name")
    private String lastName;

    @CqlName("personal_id")
    private String personalID;

    public ClientCassandra(UUID id, String firstName, String lastName, String personalID) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalID = personalID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPersonalID() {
        return personalID;
    }

    public UUID getId() {
        return id;
    }
}
