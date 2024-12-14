package org.project.cursexchange.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> findById(int id);
    List<T> findAll() throws SQLException;
    void save(T t);
    void update(T t);

}
