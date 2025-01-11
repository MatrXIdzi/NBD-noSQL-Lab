package org.restaurant;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.InvalidKeyspaceException;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;

import java.net.InetSocketAddress;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.*;

public class CassandraConnector {
    private CqlSession session;

    public void initSession() {
        CqlSessionBuilder builder = prepareSessionBuilder().withKeyspace(CqlIdentifier.fromCql("restaurant"));

        try {
            session = builder.build();
        } catch (InvalidKeyspaceException e) {
            // if "restaurant" keyspace doesn't exist, create it and then re-attempt to create the session
            createRestaurantKeyspace();
            session = builder.build();
            // populate the newly-created keyspace with tables
            createTables();
        }
    }

    public CqlSessionBuilder prepareSessionBuilder() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword");
    }

    public CqlSession getSession() {
        return this.session;
    }

    public void close() {
        session.close();
    }

    private void createRestaurantKeyspace() {
        CqlSession temporaryNoKeyspaceSession = prepareSessionBuilder().build();
        CreateKeyspace keyspace = createKeyspace(CqlIdentifier.fromCql("restaurant"))
                .ifNotExists()
                .withSimpleStrategy(3) // replication factor
                .withDurableWrites(true);
        SimpleStatement createKeyspace = keyspace.build();
        temporaryNoKeyspaceSession.execute(createKeyspace);
    }

    private void createTables() {
        SimpleStatement createElements =
                createTable(CqlIdentifier.fromCql("elements"))
                        .ifNotExists()
                        .withPartitionKey(CqlIdentifier.fromCql("element_type"), DataTypes.TEXT)
                        .withClusteringColumn(CqlIdentifier.fromCql("element_id"), DataTypes.UUID)
                        .withColumn(CqlIdentifier.fromCql("name"), DataTypes.TEXT)
                        .withColumn(CqlIdentifier.fromCql("price_per_person"), DataTypes.DOUBLE)
                        .withColumn(CqlIdentifier.fromCql("max_capacity"), DataTypes.INT)
                        .withColumn(CqlIdentifier.fromCql("premium"), DataTypes.BOOLEAN)
                        .withColumn(CqlIdentifier.fromCql("base_price"), DataTypes.DOUBLE)
                        .withColumn(CqlIdentifier.fromCql("dance_floor"), DataTypes.BOOLEAN)
                        .withColumn(CqlIdentifier.fromCql("bar"), DataTypes.BOOLEAN)
                        .build();

        session.execute(createElements);

        SimpleStatement createReservationsByClient =
                createTable(CqlIdentifier.fromCql("reservations_by_client"))
                        .ifNotExists()
                        .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                        .withClusteringColumn(CqlIdentifier.fromCql("date"), DataTypes.DATE)
                        .withClusteringColumn(CqlIdentifier.fromCql("element_id"), DataTypes.UUID)
                        .withColumn(CqlIdentifier.fromCql("element_name"), DataTypes.TEXT)
                        .build();

        session.execute(createReservationsByClient);

        SimpleStatement createReservationsByDate =
                createTable(CqlIdentifier.fromCql("reservations_by_date"))
                        .ifNotExists()
                        .withPartitionKey(CqlIdentifier.fromCql("date"), DataTypes.DATE)
                        .withClusteringColumn(CqlIdentifier.fromCql("element_id"), DataTypes.UUID)
                        .withColumn(CqlIdentifier.fromCql("element_type"), DataTypes.TEXT)
                        .build();

        session.execute(createReservationsByDate);

        SimpleStatement createClients =
                createTable(CqlIdentifier.fromCql("clients"))
                        .ifNotExists()
                        .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                        .withColumn(CqlIdentifier.fromCql("first_name"), DataTypes.TEXT)
                        .withColumn(CqlIdentifier.fromCql("last_name"), DataTypes.TEXT)
                        .withColumn(CqlIdentifier.fromCql("personal_id"), DataTypes.TEXT)
                        .build();

        session.execute(createClients);
    }
}
