package org.project.cursexchange.dao;

import org.project.cursexchange.model.Currency;

import java.util.List;
import java.util.Optional;


public interface Dao<T> {
    Optional<T> findById(int id);

    T findByCode(String code);

    List<T> findAll();

    void save(T entity);
}
