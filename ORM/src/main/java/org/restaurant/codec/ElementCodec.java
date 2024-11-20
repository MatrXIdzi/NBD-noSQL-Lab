package org.restaurant.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.restaurant.model.Element;
import org.restaurant.model.Hall;
import org.restaurant.model.Table;

import java.util.UUID;

public class ElementCodec implements Codec<Element> {

    @Override
    public void encode(BsonWriter writer, Element element, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_id", element.getEntityId().toString());
        writer.writeString("name", element.getName());
        writer.writeDouble("pricePerPerson", element.getPricePerPerson());
        writer.writeInt32("maxCapacity", element.getMaxCapacity());

        if (element instanceof Hall hall) {
            writer.writeString("type", "Hall");
            writer.writeDouble("basePrice", hall.getBasePrice());
            writer.writeBoolean("hasDanceFloor", hall.isDanceFloor());
            writer.writeBoolean("hasBar", hall.isBar());
        } else if (element instanceof Table table) {
            writer.writeString("type", "Table");
            writer.writeBoolean("premium", table.isPremium());
            // Add other Table-specific fields
        }

        writer.writeEndDocument();
    }

    @Override
    public Element decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        UUID id = UUID.fromString(reader.readString("_id"));
        String name = reader.readString("name");
        double pricePerPerson = reader.readDouble("pricePerPerson");
        int maxCapacity = reader.readInt32("maxCapacity");
        String type = reader.readString("type");

        Element element = null;
        if ("Hall".equals(type)) {
            double basePrice = reader.readDouble("basePrice");
            boolean hasDanceFloor = reader.readBoolean("hasDanceFloor");
            boolean hasBar = reader.readBoolean("hasBar");
            // Read other Hall-specific fields
            element = new Hall(id, pricePerPerson, maxCapacity, name, hasDanceFloor, hasBar, basePrice);
        } else if ("Table".equals(type)) {
            boolean premium = reader.readBoolean("premium");
            // Read other Table-specific fields
            element = new Table(id, pricePerPerson, maxCapacity, name, premium);
        }

        reader.readEndDocument();
        return element;
    }

    @Override
    public Class<Element> getEncoderClass() {
        return Element.class;
    }
}
