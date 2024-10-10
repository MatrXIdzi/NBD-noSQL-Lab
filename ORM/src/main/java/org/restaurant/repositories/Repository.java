package org.restaurant.repositories;

import java.util.List;

public interface Repository <T> {
    void add(T t);
    void remove(T t);
    int count();
    T get(int ID);
}
