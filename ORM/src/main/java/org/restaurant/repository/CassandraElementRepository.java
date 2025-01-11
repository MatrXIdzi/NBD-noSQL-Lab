package org.restaurant.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import org.restaurant.cassandra.*;
import org.restaurant.model.Client;
import org.restaurant.model.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CassandraElementRepository {
    private final ElementCassandraDao elementDao;

    public CassandraElementRepository(CqlSession session) {
        DaoMapper mapper = new DaoMapperBuilder(session).build();
        elementDao = mapper.getElementDao(CqlIdentifier.fromCql("restaurant"));
    }

    public void create(Element element) {
        elementDao.insertElement(ModelMapper.toElementCassandra(element));
    }

    public void update(Element element) {
        if (!elementDao.updateElement(ModelMapper.toElementCassandra(element))) throw new IllegalArgumentException();
    }

    public void deleteTable(UUID id) {
        elementDao.deleteTableById(id);
    }

    public Element readTable(UUID id) {
        ElementCassandra elementCassandra = elementDao.getTableById(id);
        if (elementCassandra == null) {
            return null;
        }
        return ModelMapper.toElement(elementCassandra);
    }

    public List<Element> readAllTables() {
        PagingIterable<TableCassandra> iterable = elementDao.getAllTables();
        List<Element> elementsList = new ArrayList<>();
        for (ElementCassandra elementCassandra : iterable) {
            elementsList.add(ModelMapper.toElement(elementCassandra));
        }
        return elementsList;
    }

    public void deleteHall(UUID id) {
        elementDao.deleteHallById(id);
    }

    public Element readHall(UUID id) {
        ElementCassandra elementCassandra = elementDao.getHallById(id);
        if (elementCassandra == null) {
            return null;
        }
        return ModelMapper.toElement(elementCassandra);
    }

    public List<Element> readAllHalls() {
        PagingIterable<HallCassandra> iterable = elementDao.getAllHalls();
        List<Element> elementsList = new ArrayList<>();
        for (ElementCassandra elementCassandra : iterable) {
            elementsList.add(ModelMapper.toElement(elementCassandra));
        }
        return elementsList;
    }
}
