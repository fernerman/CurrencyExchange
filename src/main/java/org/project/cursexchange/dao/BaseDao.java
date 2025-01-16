package org.project.cursexchange.dao;

import org.project.cursexchange.Util;
import org.project.cursexchange.config.DatabaseConnection;
import org.project.cursexchange.mapper.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract  class BaseDao<T> {

    public List<T> findByFieldWithJoin(
            String tableName,
            String fieldName,
            Object fieldValue,
            String joinTable,
            String joinCondition,
            RowMapper<T> rowMapper) throws SQLException {

        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName +
                " JOIN " + joinTable +
                " ON " + joinCondition +
                " WHERE " + joinTable + "." + fieldName + " = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setObject(1, fieldValue);

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    results.add(rowMapper.mapRow(resultSet));
                }
            }
        }
        return results;
    }

    public Optional<T> findByField(String fieldName,
                                   Object fieldValue,
                                   String tableName,
                                   RowMapper<T> rowMapper) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " WHERE " + fieldName + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, fieldValue);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(rowMapper.mapRow(resultSet));
                }
            }
        }
        return Optional.empty();
    }
    public Optional<T> findByFields(String[] fieldNames, Object[] fieldValues, String tableName, RowMapper<T> rowMapper) throws SQLException {
        if (fieldNames.length != fieldValues.length) {
            throw new IllegalArgumentException("Field names and values must have the same length");
        }

        StringBuilder whereClause = new StringBuilder();
        for (int i = 0; i < fieldNames.length; i++) {
            if (i > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(fieldNames[i]).append(" = ?");
        }

        String sql = "SELECT * FROM " + tableName + " WHERE " + whereClause;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < fieldValues.length; i++) {
                stmt.setObject(i + 1, fieldValues[i]);
            }
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(rowMapper.mapRow(resultSet));
                }
            }
        }
        return Optional.empty();
    }
    public boolean updateField(String fieldName, String fieldValue, int id, String tableName) throws SQLException {
        String sql = "UPDATE " + tableName + " SET " + fieldName + " = ? WHERE " + "id" + " = ?";
        try (Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, fieldValue);
            preparedStatement.setObject(2, id);
            var d=preparedStatement.executeUpdate();
            if(preparedStatement.executeUpdate()==1){
                return true;
            }
        }
        return false;
    }

    public List<T> findAll(String tableName, RowMapper<T> rowMapper) throws SQLException {
        List<T> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    entities.add(rowMapper.mapRow(resultSet));
                }
            }
        }
        return entities;
    }

    public boolean save(String tableName, String[] columns, Object[] values) throws SQLException {
        String sqlRequest = "INSERT INTO " + tableName + " (" + String.join(", ", columns) + ") VALUES (" + "?, ".repeat(columns.length - 1) + "?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest)) {
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 1, values[i]);
            }
            return preparedStatement.executeUpdate() > 0;

        }
    }
}
