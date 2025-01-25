package org.restaurant;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public abstract class KafkaConnector {
    private static KafkaProducer<String, String> producer;

    public static void initProducer() throws ExecutionException, InterruptedException {
        if (producer != null) producer.close();

        prepareTopic();

        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, "local");
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka1:9192,kafka2:9292,kafka3:9392");
        producerConfig.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 45000); // 45s
        producerConfig.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        producer = new KafkaProducer<>(producerConfig);
    }

    private static void prepareTopic() throws InterruptedException, ExecutionException {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka1:9192,kafka2:9292,kafka3:9392");

        try (Admin admin = Admin.create(properties)) {
            String topicName = "reservations";
            int partitions = 3;
            short replicationFactor = 3;

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            CreateTopicsOptions options = new CreateTopicsOptions()
                    .timeoutMs(1000)
                    .validateOnly(false)
                    .retryOnQuotaViolation(true);

            CreateTopicsResult result = admin.createTopics(List.of(newTopic), options);

            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();
        }
        catch (ExecutionException e) {
            // if topic already exists, that's fine; otherwise, re-throw the exception
            if (!(e.getCause() instanceof TopicExistsException)) throw e;
        }
    }

    public static KafkaProducer<String, String> getProducer() {
        return producer;
    }
}
