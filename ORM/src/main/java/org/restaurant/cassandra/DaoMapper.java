package org.restaurant.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface DaoMapper {
    @DaoFactory
    ClientCassandraDao getClientDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ElementCassandraDao getElementDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    ReservationCassandraDao getReservationDao(@DaoKeyspace CqlIdentifier keyspace);
}
