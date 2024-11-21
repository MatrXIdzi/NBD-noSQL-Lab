package org.restaurant.codec;

import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.jupiter.api.Test;
import org.restaurant.model.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodecsTest {
    @Test
    public void testClientEncodeDecode() {
        BsonDocument bsonDocument = new BsonDocument();
        BsonWriter bsonWriter = new BsonDocumentWriter(bsonDocument);

        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId,"John", "Doe", "12345678901");

        Codec<Client> clientCodec = new ClientCodec();

        clientCodec.encode(bsonWriter, client, EncoderContext.builder().build());
        bsonWriter.flush();

        BsonReader bsonReader = new BsonDocumentReader(bsonDocument);
        Client decodedClient = clientCodec.decode(bsonReader, DecoderContext.builder().build());

        assertEquals(client.getEntityId(), decodedClient.getEntityId());
        assertEquals(client.getFirstName(), decodedClient.getFirstName());
        assertEquals(client.getLastName(), decodedClient.getLastName());
        assertEquals(client.getPersonalID(), decodedClient.getPersonalID());
    }

    @Test
    public void testElementEncodeDecode() {
        BsonDocument bsonDocument = new BsonDocument();
        BsonWriter bsonWriter = new BsonDocumentWriter(bsonDocument);

        UUID elementId = UUID.randomUUID();
        Element element = new Hall(elementId, 15.0, 120, "HallName", true, true, 150.0);

        Codec<Element> elementCodec = new ElementCodec();

        elementCodec.encode(bsonWriter, element, EncoderContext.builder().build());
        bsonWriter.flush();

        BsonReader bsonReader = new BsonDocumentReader(bsonDocument);
        Element decodedElement = elementCodec.decode(bsonReader, DecoderContext.builder().build());

        assertEquals(element.getEntityId(), decodedElement.getEntityId());
        assertEquals(element.getName(), decodedElement.getName());
        assertEquals(element.getMaxCapacity(), decodedElement.getMaxCapacity());
        assertEquals(element.getPricePerPerson(), decodedElement.getPricePerPerson());
        assertEquals(((Hall)element).isDanceFloor(), ((Hall)decodedElement).isDanceFloor());
        assertEquals(((Hall)element).isBar(), ((Hall)decodedElement).isBar());
        assertEquals(((Hall)element).getBasePrice(), ((Hall)decodedElement).getBasePrice());
    }

    @Test
    public void testReservationEncodeDecode() {
        BsonDocument bsonDocument = new BsonDocument();
        BsonWriter bsonWriter = new BsonDocumentWriter(bsonDocument);

        Client client = new Client("John", "Doe", "12345678901");
        Element element = new Table(20.0, 10, "TableName", true);
        UUID reservationId = UUID.randomUUID();
        Date date = new GregorianCalendar(2024, Calendar.FEBRUARY, 5).getTime();
        Reservation reservation = new Reservation(reservationId, date, client, element);

        Codec<Element> elementCodec = new ElementCodec();
        Codec<Client> clientCodec = new ClientCodec();
        CodecRegistry codecRegistry = CodecRegistries.fromCodecs(clientCodec, elementCodec);
        Codec<Reservation> reservationCodec = new ReservationCodec(codecRegistry);

        reservationCodec.encode(bsonWriter, reservation, EncoderContext.builder().build());
        bsonWriter.flush();

        BsonReader bsonReader = new BsonDocumentReader(bsonDocument);
        Reservation decodedReservation = reservationCodec.decode(bsonReader, DecoderContext.builder().build());

        assertEquals(reservation.getReservationDate(), decodedReservation.getReservationDate());
        assertEquals(reservation.getEntityId(), decodedReservation.getEntityId());
        assertEquals(reservation.getClient().getEntityId(), decodedReservation.getClient().getEntityId());
        assertEquals(reservation.getElement().getEntityId(), decodedReservation.getElement().getEntityId());
    }
}
