package org.restaurant.benchmarks;

import org.bson.Document;
import org.openjdk.jmh.annotations.*;
import org.restaurant.MongoRepository;
import org.restaurant.repository.mongo.MongoClientRepository;

public class MongoBenchmark {
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private static MongoClientRepository clientRepository;
        private static MongoRepository mongoRepository;

        @Setup(Level.Invocation)
        public void beforeAll() {
            mongoRepository = new MongoRepository();
            clientRepository = new MongoClientRepository(mongoRepository.getRestaurantDB());
        }

        @Setup(Level.Trial)
        public void beforeEach() {
            mongoRepository.getRestaurantDB().getCollection("clients").deleteMany(new Document());
        }
    }

    @Benchmark
    public void testMongoCreate(BenchmarkState state) {

    }
}

