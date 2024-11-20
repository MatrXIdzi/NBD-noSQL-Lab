package org.restaurant;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractEntity implements Serializable {

    @BsonProperty("_id")
    private final UUID id;

    public UUID getEntityId() {
        return id;
    }

    public AbstractEntity() {
        this.id = UUID.randomUUID();
    }

    public AbstractEntity(UUID id) {
        this.id = id;
    }
}
