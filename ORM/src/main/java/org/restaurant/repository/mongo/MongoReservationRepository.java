package org.restaurant.repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.restaurant.model.Client;
import org.restaurant.model.Reservation;
import org.restaurant.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.ascending;

public class MongoReservationRepository implements Repository<Reservation> {
    private final MongoCollection<Reservation> collection;

    public MongoReservationRepository(MongoDatabase database) {
        this.collection = database.getCollection("reservations", Reservation.class);
        createUniqueIndex();
    }

    private void createUniqueIndex() {
        collection.createIndex(ascending("element._id", "reservationDate"), new IndexOptions().unique(true));
    }

    public void create(Reservation reservation) {
        collection.insertOne(reservation);
    }

    public Reservation read(UUID id) {
        return collection.find(eq("_id", id.toString())).first();
    }

    public List<Reservation> readAll() {
        return collection.find().into(new ArrayList<>());
    }

    public void update(Reservation reservation) {
        long modifiedCount = collection.replaceOne(eq("_id", reservation.getEntityId().toString()), reservation).getModifiedCount();
        if (modifiedCount == 0) {
            throw new IllegalArgumentException("Reservation not found");
        }
    }

    public void delete(UUID id) {
        collection.deleteOne(eq("_id", id.toString()));
    }
}