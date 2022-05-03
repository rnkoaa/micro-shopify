package org.richard.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, U> {

    boolean update(T value);

    T save(T value);

    List<T> save(List<T> values);

    int count();

    List<T> findAll();

    Optional<T> findById(U id);

    boolean delete(U id);

    boolean deleteAll(boolean testing);
}
