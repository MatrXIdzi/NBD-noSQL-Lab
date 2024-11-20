package org.restaurant;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.restaurant.codec.ClientCodec;
import org.restaurant.codec.ElementCodec;
import org.restaurant.codec.ReservationCodec;

import java.util.List;


public class MongoRepository implements AutoCloseable {
    private ConnectionString connectionString = new ConnectionString("mongodb://mongodb1:27017,mongodb2:27018,mongodb3:27019/?replicaSet=replica_set_single");
    private MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());
    private CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());

    private MongoClient mongoClient;
    private MongoDatabase restaurantDB;

    private void initDbConnection() {

        ClientCodec clientCodec = new ClientCodec();
        ElementCodec elementCodec = new ElementCodec();
        ReservationCodec reservationCodec = new ReservationCodec(CodecRegistries.fromCodecs(clientCodec, elementCodec));
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(clientCodec, elementCodec, reservationCodec),
                pojoCodecRegistry);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .credential(credential)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        codecRegistry))
                .build();

        mongoClient = MongoClients.create(settings);
        restaurantDB = mongoClient.getDatabase("restaurant");
    }

    public MongoDatabase getRestaurantDB() {
        initDbConnection();
        return restaurantDB;
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
