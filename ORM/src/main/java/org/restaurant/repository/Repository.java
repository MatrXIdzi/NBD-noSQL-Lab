package org.restaurant.repository;

import java.util.List;
import java.util.UUID;

public interface Repository<T> {
    void create(T t);
    void update(T t);
    void delete(UUID id);
    T read(UUID id);
    //List<T> readAll();
}
