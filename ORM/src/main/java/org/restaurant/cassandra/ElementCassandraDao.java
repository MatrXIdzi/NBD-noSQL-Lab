package org.restaurant.cassandra;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;

import java.util.UUID;

@Dao
public interface ElementCassandraDao {
    @QueryProvider(providerClass = ElementQueryProvider.class,
            entityHelpers = {HallCassandra.class, TableCassandra.class})
    void insertElement(ElementCassandra element);

    @QueryProvider(providerClass = ElementQueryProvider.class,
            entityHelpers = {HallCassandra.class, TableCassandra.class})
    boolean updateElement(ElementCassandra element);

    @QueryProvider(providerClass = ElementQueryProvider.class,
            entityHelpers = {HallCassandra.class, TableCassandra.class})
    ElementCassandra getTableById(UUID id);

    @QueryProvider(providerClass = ElementQueryProvider.class,
            entityHelpers = {HallCassandra.class, TableCassandra.class})
    void deleteTableById(UUID id);

    @Select(customWhereClause = "element_type = 'table'")
    PagingIterable<TableCassandra> getAllTables();

    @QueryProvider(providerClass = ElementQueryProvider.class,
            entityHelpers = {HallCassandra.class, TableCassandra.class})
    ElementCassandra getHallById(UUID id);

    @QueryProvider(providerClass = ElementQueryProvider.class,
            entityHelpers = {HallCassandra.class, TableCassandra.class})
    void deleteHallById(UUID id);

    @Select(customWhereClause = "element_type = 'hall'")
    PagingIterable<HallCassandra> getAllHalls();
}
