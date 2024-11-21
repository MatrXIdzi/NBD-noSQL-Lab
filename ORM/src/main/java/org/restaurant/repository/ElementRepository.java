package org.restaurant.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.restaurant.model.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class ElementRepository {
    private final MongoCollection<Element> collection;

    public ElementRepository(MongoDatabase database) {
        this.collection = database.getCollection("elements", Element.class);
    }

    public void create(Element element) {
        collection.insertOne(element);
    }

    public Element read(UUID id) {
        return collection.find(eq("_id", id.toString())).first();
    }

    public List<Element> readAll() {
        return collection.find().into(new ArrayList<>());
    }

    public void update(Element element) {
        long modifiedCount = collection.replaceOne(eq("_id", element.getEntityId().toString()), element).getModifiedCount();
        if (modifiedCount == 0) {
            throw new IllegalArgumentException("Element not found");
        }
    }

    public void delete(UUID id) {
        collection.deleteOne(eq("_id", id.toString()));
    }
}
