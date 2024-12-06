package org.restaurant.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.restaurant.MongoRepository;
import org.restaurant.RedisConnection;
import org.restaurant.model.Client;
import org.restaurant.repository.ClientRepository;
import org.bson.Document;

import java.io.IOException;
import java.util.UUID;

public class RedisBenchmark {
    @State(Scope.Benchmark)
    public static class RedisCreateBenchmarkState {
        private static ClientRepository clientRepository;
        private static MongoRepository mongoRepository;
        private static RedisConnection redisConnection;
        private static Client client;

        @Setup(Level.Invocation)
        public void beforeEach() {
            mongoRepository = new MongoRepository();
            mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
            try {
                redisConnection = new RedisConnection();
                redisConnection.clearCache();
                clientRepository = new ClientRepository(mongoRepository.getRestaurantDB(), redisConnection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            client = new Client(UUID.randomUUID(),"John", "Doe", "12345678901");
        }
    }

    @State(Scope.Benchmark)
    public static class RedisReadDeleteBenchmarkState {
        private static ClientRepository clientRepository;
        private static MongoRepository mongoRepository;
        private static RedisConnection redisConnection;
        private static UUID id;

        @Setup(Level.Invocation)
        public void beforeEach() {
            mongoRepository = new MongoRepository();
            mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
            try {
                redisConnection = new RedisConnection();
                redisConnection.clearCache();
                clientRepository = new ClientRepository(mongoRepository.getRestaurantDB(), redisConnection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            id = UUID.randomUUID();
            Client client = new Client(id,"John", "Doe", "12345678901");
            clientRepository.create(client);
        }
    }

    @State(Scope.Benchmark)
    public static class RedisMissReadBenchmarkState {
        private static ClientRepository clientRepository;
        private static MongoRepository mongoRepository;
        private static RedisConnection redisConnection;
        private static UUID id;

        @Setup(Level.Invocation)
        public void beforeEach() {
            mongoRepository = new MongoRepository();
            mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
            try {
                redisConnection = new RedisConnection();
                redisConnection.clearCache();
                clientRepository = new ClientRepository(mongoRepository.getRestaurantDB(), redisConnection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            id = UUID.randomUUID();
            Client client = new Client(id,"John", "Doe", "12345678901");
            clientRepository.create(client);
            redisConnection.clearCache();
        }
    }

    @State(Scope.Benchmark)
    public static class RedisUpdateBenchmarkState {
        private static ClientRepository clientRepository;
        private static MongoRepository mongoRepository;
        private static RedisConnection redisConnection;
        private static Client client;

        @Setup(Level.Invocation)
        public void beforeEach() {
            mongoRepository = new MongoRepository();
            mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
            try {
                redisConnection = new RedisConnection();
                redisConnection.clearCache();
                clientRepository = new ClientRepository(mongoRepository.getRestaurantDB(), redisConnection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            client = new Client(UUID.randomUUID(),"John", "Doe", "12345678901");
            clientRepository.create(client);
            client.setFirstName("Jane");
        }
    }

    @Benchmark
    public void testRedisCreate(RedisCreateBenchmarkState state) {
        state.clientRepository.create(state.client);
    }

    @Benchmark
    public void testRedisHitRead(RedisReadDeleteBenchmarkState state) {
        state.clientRepository.read(state.id);
    }

    @Benchmark
    public void testRedisMissRead(RedisMissReadBenchmarkState state) {
        state.clientRepository.read(state.id);
    }

    @Benchmark
    public void testRedisDelete(RedisReadDeleteBenchmarkState state) {
        state.clientRepository.delete(state.id);
    }

    @Benchmark
    public void testRedisUpdate(RedisUpdateBenchmarkState state) {
        state.clientRepository.update(state.client);
    }
}

