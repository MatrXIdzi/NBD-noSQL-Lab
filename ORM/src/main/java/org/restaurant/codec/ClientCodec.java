package org.restaurant.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.restaurant.model.Client;

import java.util.UUID;

public class ClientCodec implements Codec<Client> {

    @Override
    public void encode(BsonWriter writer, Client client, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_id", client.getEntityId().toString());
        writer.writeString("name", client.getFirstName());
        writer.writeString("surname", client.getLastName());
        writer.writeString("personalID", client.getPersonalID());
        writer.writeEndDocument();
    }

    @Override
    public Client decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        UUID id = UUID.fromString(reader.readString("_id"));
        String firstName = reader.readString("name");
        String lastName = reader.readString("surname");
        String personalID = reader.readString("personalID");
        reader.readEndDocument();
        return new Client(id, firstName, lastName, personalID);
    }

    @Override
    public Class<Client> getEncoderClass() {
        return Client.class;
    }
}
