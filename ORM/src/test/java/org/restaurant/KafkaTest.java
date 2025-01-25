package org.restaurant;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class KafkaTest {
    public static KafkaProducer<String, String> producer;

    @BeforeAll
    public static void setup() {
        try {
            KafkaConnector.initProducer();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        producer = KafkaConnector.getProducer();
    }

    @AfterAll
    public static void tearDown() {
        producer.close();
    }

    @Test
    @Disabled
    public void publishRecordTest() {
        System.out.println("Sending message...");
        ProducerRecord<String, String> record = new ProducerRecord<>("reservations", "Hello, world!");

        assertDoesNotThrow(() -> producer.send(record).get());
    }
}
