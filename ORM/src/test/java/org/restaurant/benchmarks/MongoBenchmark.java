package org.restaurant.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.restaurant.MongoRepository;
import org.restaurant.model.Client;
import org.restaurant.repository.mongo.MongoClientRepository;
import org.bson.Document;

import java.util.UUID;

public class MongoBenchmark {
    @State(Scope.Benchmark)
    public static class MongoCreateBenchmarkState {
        private static MongoClientRepository clientRepository;
        private static MongoRepository mongoRepository;
        private static Client client;

        @Setup(Level.Invocation)
        public void beforeEach() {
            mongoRepository = new MongoRepository();
            mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
            clientRepository = new MongoClientRepository(mongoRepository.getRestaurantDB());
            client = new Client(UUID.randomUUID(),"John", "Doe", "12345678901");
        }
    }

    @State(Scope.Benchmark)
    public static class MongoReadDeleteBenchmarkState {
        private static MongoClientRepository clientRepository;
        private static MongoRepository mongoRepository;
        private static UUID id;

        @Setup(Level.Invocation)
        public void beforeEach() {
            mongoRepository = new MongoRepository();
            mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
            clientRepository = new MongoClientRepository(mongoRepository.getRestaurantDB());
            id = UUID.randomUUID();
            Client client = new Client(id,"John", "Doe", "12345678901");
            clientRepository.create(client);
        }
    }

    @State(Scope.Benchmark)
    public static class MongoUpdateBenchmarkState {
        private static MongoClientRepository clientRepository;
        private static MongoRepository mongoRepository;
        private static Client client;

        @Setup(Level.Invocation)
        public void beforeEach() {
            mongoRepository = new MongoRepository();
            mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
            clientRepository = new MongoClientRepository(mongoRepository.getRestaurantDB());
            client = new Client(UUID.randomUUID(),"John", "Doe", "12345678901");
            clientRepository.create(client);
            client.setFirstName("Jane");
        }
    }

    @Benchmark
    public void testMongoCreate(MongoCreateBenchmarkState state) {
        state.clientRepository.create(state.client);
    }

    @Benchmark
    public void testMongoRead(MongoReadDeleteBenchmarkState state) {
        state.clientRepository.read(state.id);
    }

    @Benchmark
    public void testMongoDelete(MongoReadDeleteBenchmarkState state) {
        state.clientRepository.delete(state.id);
    }

    @Benchmark
    public void testMongoUpdate(MongoUpdateBenchmarkState state) {
        state.clientRepository.update(state.client);
    }
}

