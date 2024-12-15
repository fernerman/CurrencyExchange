package org.project.cursexchange.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> findById(int id) throws SQLException;
    Optional<T> findByCode(String code) throws SQLException;
    List<T> findAll() throws SQLException;
    Optional<T> save(T t) throws SQLException;
    void update(T t);

}
