package org.restaurant;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractEntity implements Serializable {
    private final UUID id;

    public UUID getEntityId() {
        return id;
    }

    public UUID getId() {
        return getEntityId();
    }

    public AbstractEntity() {
        this.id = UUID.randomUUID();
    }

    public AbstractEntity(UUID id) {
        this.id = id;
    }
}
