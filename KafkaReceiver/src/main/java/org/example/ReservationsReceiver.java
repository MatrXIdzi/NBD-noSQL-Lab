package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReservationsReceiver {
    private KafkaConsumer<String, String> consumer;
    private final AtomicBoolean receiving = new AtomicBoolean(false);
    private ConsumerThread thread;
    private final String loggerSuffix;

    public ReservationsReceiver(String loggerSuffix) {
        this.loggerSuffix = loggerSuffix;
    }

    public void stopConsumer() {
        receiving.set(false);
    }

    public void createConsumer() {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        consumer = new KafkaConsumer<>(consumerConfig);
        consumer.subscribe(List.of("reservations"));
    }

    public void startConsumer() {
        if (consumer == null) throw new IllegalStateException("Consumer not initialized");
        if (thread != null && thread.isAlive()) throw new IllegalStateException("Consumer thread is already running");
        thread = new ConsumerThread();
        thread.start();
    }

    private class ConsumerThread extends Thread {
        private static final String HASHPREFIX = "reservation:";

        public void run() {
            Logger logger = LoggerFactory.getLogger("ConsumerThread" + loggerSuffix);

            receiving.set(true);

            logger.info("Consumer started");

            RedisConnection redisConnection = null;
            try {
                redisConnection = new RedisConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JedisPooled pool = redisConnection.getJedisPooled();
            RedisJsonObjectMapper jsonObjectMapper = new RedisJsonObjectMapper();

            //String receivedJson = "{\"reservationId\":\"7d6d82df-3855-4531-a934-dd9fb1e0ec60\",\"reservationDate\":1707087600000,\"clientId\":\"4f356833-fa2d-4605-af24-338ec6ffd1c4\",\"elementId\":\"2018f652-329f-45dd-b4df-ece140b85da7\",\"restaurantName\":\"Kafka Na Wynos\"}";

            Map<Integer, Long> offsets = new HashMap<>();
            Duration timeout = Duration.of(100, ChronoUnit.MILLIS);
            MessageFormat formatter = new MessageFormat( "Temat {0}, partycja {1}, offset {2, number, integer}, klucz {3}, wartość {4}");

            while (receiving.get()) {
                ConsumerRecords<String, String> records = consumer.poll(timeout);
                for (ConsumerRecord<String, String> record : records) {
                    String result = formatter.format(new Object[]{record.topic(), record.partition(),
                            record.offset(), record.key(), record.value()});
                    logger.info(result);

                    Reservation reservation = jsonObjectMapper.fromJson(record.value(), Reservation.class);

                    //String newJson = jsonObjectMapper.toJson(reservation);
                    //System.out.println(newJson);

                    pool.jsonSetWithEscape(HASHPREFIX + reservation.getId(), reservation);

                    offsets.put(record.partition(), record.offset());
                }
                consumer.commitSync();
            }

            pool.close();
            logger.info("Consumer stopped");
            logger.info("Offsets: {}", offsets);
        }
    }
}
