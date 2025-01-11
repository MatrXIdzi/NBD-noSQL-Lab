package org.restaurant.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class ElementQueryProvider {
    private final CqlSession session;
    private EntityHelper<HallCassandra> hallEntityHelper;
    private EntityHelper<TableCassandra> tableEntityHelper;

    ElementQueryProvider(MapperContext ctx, EntityHelper<HallCassandra> hallEntityHelper,
                           EntityHelper<TableCassandra> tableEntityHelper) {
        this.session = ctx.getSession();
        this.hallEntityHelper = hallEntityHelper;
        this.tableEntityHelper = tableEntityHelper;
    }

    ElementCassandra getTableById(UUID id) {
        Select selectTable = QueryBuilder
                .selectFrom(CqlIdentifier.fromCql("elements"))
                .all()
                .where(Relation.column("element_type").isEqualTo(literal("table")))
                .where(Relation.column("element_id").isEqualTo(literal(id)));
        Row row = session.execute(selectTable.build()).one();

        if (row == null) return null;

        return new TableCassandra(
                "table",
                row.getUuid("element_id"),
                row.getBoolean("premium"),
                row.getDouble("price_per_person"),
                row.getInt("max_capacity"),
                row.getString("name")
                );
    }

    void insertElement(ElementCassandra element) {
        session.execute(
                switch (element.getElementType()) {
                    case "hall" -> {
                        HallCassandra hall = (HallCassandra) element;
                        yield session.prepare(hallEntityHelper.insert().build())
                                .bind()
                                .setString("element_type", element.getElementType())
                                .setUuid("element_id", hall.getId())
                                .setDouble("price_per_person", hall.getPricePerPerson())
                                .setInt("max_capacity", hall.getMaxCapacity())
                                .setString("name", hall.getName())
                                .setBoolean("dance_floor", hall.isDanceFloor())
                                .setBoolean("bar", hall.isBar())
                                .setDouble("base_price", hall.getBasePrice());
                    }
                    case "table" -> {
                        TableCassandra table = (TableCassandra) element;
                        yield session.prepare(tableEntityHelper.insert().build())
                                .bind()
                                .setString("element_type", element.getElementType())
                                .setUuid("element_id", table.getId())
                                .setDouble("price_per_person", table.getPricePerPerson())
                                .setInt("max_capacity", table.getMaxCapacity())
                                .setString("name", table.getName())
                                .setBoolean("premium", table.isPremium());
                    }
                    default -> throw new IllegalArgumentException();
                });
    }

    boolean updateElement(ElementCassandra element) {
        return session.execute(
                switch (element.getElementType()) {
                    case "hall" -> {
                        HallCassandra hall = (HallCassandra) element;
                        yield session.prepare(hallEntityHelper.updateByPrimaryKey().ifExists().build())
                                .bind()
                                .setString("element_type", element.getElementType())
                                .setUuid("element_id", hall.getId())
                                .setDouble("price_per_person", hall.getPricePerPerson())
                                .setInt("max_capacity", hall.getMaxCapacity())
                                .setString("name", hall.getName())
                                .setBoolean("dance_floor", hall.isDanceFloor())
                                .setBoolean("bar", hall.isBar())
                                .setDouble("base_price", hall.getBasePrice());
                    }
                    case "table" -> {
                        TableCassandra table = (TableCassandra) element;
                        yield session.prepare(tableEntityHelper.updateByPrimaryKey().ifExists().build())
                                .bind()
                                .setString("element_type", element.getElementType())
                                .setUuid("element_id", table.getId())
                                .setDouble("price_per_person", table.getPricePerPerson())
                                .setInt("max_capacity", table.getMaxCapacity())
                                .setString("name", table.getName())
                                .setBoolean("premium", table.isPremium());
                    }
                    default -> throw new IllegalArgumentException();
                }).wasApplied();
    }

    void deleteTableById(UUID id) {
        session.execute(session.prepare(tableEntityHelper.deleteByPrimaryKey().build())
                .bind()
                .setString("element_type", "table")
                .setUuid("element_id", id)
        );
    }

    ElementCassandra getHallById(UUID id) {
        Select selectHall = QueryBuilder
                .selectFrom(CqlIdentifier.fromCql("elements"))
                .all()
                .where(Relation.column("element_type").isEqualTo(literal("hall")))
                .where(Relation.column("element_id").isEqualTo(literal(id)));
        Row row = session.execute(selectHall.build()).one();

        if (row == null) return null;

        return new HallCassandra(
                "hall",
                row.getUuid("element_id"),
                row.getDouble("base_price"),
                row.getBoolean("dance_floor"),
                row.getBoolean("bar"),
                row.getDouble("price_per_person"),
                row.getInt("max_capacity"),
                row.getString("name")
        );
    }

    void deleteHallById(UUID id) {
        session.execute(session.prepare(hallEntityHelper.deleteByPrimaryKey().build())
                .bind()
                .setString("element_type", "hall")
                .setUuid("element_id", id)
        );
    }
}
