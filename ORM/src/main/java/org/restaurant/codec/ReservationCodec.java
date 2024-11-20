package org.restaurant.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.restaurant.model.Client;
import org.restaurant.model.Element;
import org.restaurant.model.Reservation;

import java.util.Date;
import java.util.UUID;

public class ReservationCodec implements Codec<Reservation> {

    private final CodecRegistry codecRegistry;

    public ReservationCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public void encode(BsonWriter writer, Reservation reservation, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_id", reservation.getEntityId().toString());
        writer.writeDateTime("reservationDate", reservation.getReservationDate().getTime());
        writer.writeName("client");
        codecRegistry.get(Client.class).encode(writer, reservation.getClient(), encoderContext);
        writer.writeName("element");
        codecRegistry.get(Element.class).encode(writer, reservation.getElement(), encoderContext);
        writer.writeEndDocument();
    }

    @Override
    public Reservation decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        UUID id = UUID.fromString(reader.readString("_id"));
        Date reservationDate = new Date(reader.readDateTime("reservationDate"));
        reader.readName("client");
        Client client = codecRegistry.get(Client.class).decode(reader, decoderContext);
        reader.readName("element");
        Element element = codecRegistry.get(Element.class).decode(reader, decoderContext);
        reader.readEndDocument();
        return new Reservation(id, reservationDate, client, element);
    }

    @Override
    public Class<Reservation> getEncoderClass() {
        return Reservation.class;
    }
}
