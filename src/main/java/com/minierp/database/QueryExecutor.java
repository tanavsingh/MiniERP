package com.minierp.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryExecutor {

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement ps = prepare(sql, params);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.err.println("[SQL] Query error: " + e.getMessage() + " | SQL: " + sql);
        }
        return results;
    }

    public static int executeUpdate(String sql, Object... params) {
        try (PreparedStatement ps = prepare(sql, params)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[SQL] Update error: " + e.getMessage() + " | SQL: " + sql);
            return -1;
        }
    }

    public static long executeInsert(String sql, Object... params) {
        try (PreparedStatement ps = DBConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, params);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getLong(1);
        } catch (SQLException e) {
            System.err.println("[SQL] Insert error: " + e.getMessage() + " | SQL: " + sql);
        }
        return -1;
    }

    public static Object executeScalar(String sql, Object... params) {
        try (PreparedStatement ps = prepare(sql, params);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getObject(1);
        } catch (SQLException e) {
            System.err.println("[SQL] Scalar error: " + e.getMessage());
        }
        return null;
    }

    private static PreparedStatement prepare(String sql, Object... params) throws SQLException {
        PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql);
        setParams(ps, params);
        return ps;
    }

    private static void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                ps.setNull(i + 1, Types.NULL);
            } else {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}
