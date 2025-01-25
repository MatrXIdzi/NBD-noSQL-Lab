package org.restaurant.repository.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.restaurant.ReservationGsonSerializer;
import org.restaurant.model.Reservation;
import org.restaurant.model.Table;
import org.restaurant.repository.Repository;
import org.restaurant.repository.mongo.MongoReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KafkaReservationRepository implements Repository<Reservation> {
    private final Repository<Reservation> reservationRepository;
    private final KafkaProducer<String, String> producer;
    private static final Logger logger = LoggerFactory.getLogger(KafkaReservationRepository.class);

    public KafkaReservationRepository(MongoDatabase database, KafkaProducer<String, String> producer) {
        reservationRepository = new MongoReservationRepository(database);
        this.producer = producer;
    }

    @Override
    public void create(Reservation reservation) {
        reservationRepository.create(reservation);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Reservation.class, new ReservationGsonSerializer());
        Gson gson = builder.create();

        String json = gson.toJson(reservation);

        String key;
        if (reservation.getElement() instanceof Table) {
            if (((Table)reservation.getElement()).isPremium()) key = "table_premium";
            else key = "table_standard";
        } else key = "hall";

        logger.debug("JSON for Kafka: {}", json);

        ProducerRecord<String, String> record = new ProducerRecord<>("reservations", key, json);
        try {
            producer.send(record).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed sending new reservation with id {} to Kafka broker", reservation.getId());
        }
    }

    @Override
    public void update(Reservation reservation) {
        reservationRepository.update(reservation);
    }

    @Override
    public void delete(UUID id) {
        reservationRepository.delete(id);
    }

    @Override
    public Reservation read(UUID id) {
        return reservationRepository.read(id);
    }

    @Override
    public List<Reservation> readAll() {
        return reservationRepository.readAll();
    }
}
