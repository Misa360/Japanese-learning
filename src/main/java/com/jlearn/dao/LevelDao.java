package com.jlearn.dao;

import com.jlearn.model.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LevelDao {
    public List<Level> findAll(Connection c) throws SQLException {
        String sql = "SELECT id, code, name, sort_order, description FROM levels ORDER BY sort_order";
        List<Level> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    public Level findById(Connection c, long id) throws SQLException {
        String sql = "SELECT id, code, name, sort_order, description FROM levels WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private Level map(ResultSet rs) throws SQLException {
        Level l = new Level();
        l.setId(rs.getLong("id"));
        l.setCode(rs.getString("code"));
        l.setName(rs.getString("name"));
        l.setSortOrder(rs.getInt("sort_order"));
        l.setDescription(rs.getString("description"));
        return l;
    }
}
