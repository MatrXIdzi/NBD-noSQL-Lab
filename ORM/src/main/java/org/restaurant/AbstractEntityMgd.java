package org.restaurant;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.UUID;

public class AbstractEntityMgd implements Serializable {

    @BsonProperty("_id")
    private final UUID id; //kwapi ma tutaj swoj uuid

    public UUID getEntityId() {
        return id;
    }

    public AbstractEntityMgd() {
        this.id = UUID.randomUUID();
    }

    public AbstractEntityMgd(UUID id) {
        this.id = id;
    }
}
