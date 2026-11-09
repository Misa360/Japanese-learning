package com.jlearn.dao;

import com.jlearn.model.Material;
import java.sql.*;
import java.util.*;

public class MaterialDao {
    public Material findById(Connection c, long id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT id,lesson_id,material_type,title,content_text,media_url,sort_order FROM materials WHERE id=?")) {
            ps.setLong(1,id); try (ResultSet rs=ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    public long insert(Connection c, Material m) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement("INSERT INTO materials(lesson_id,material_type,title,content_text,media_url,sort_order) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            bind(ps,m); ps.executeUpdate(); try(ResultSet rs=ps.getGeneratedKeys()){ if(rs.next()) return rs.getLong(1); }
        }
        throw new SQLException("Không tạo được nội dung bài học");
    }
    public void update(Connection c, Material m) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement("UPDATE materials SET material_type=?,title=?,content_text=?,media_url=?,sort_order=? WHERE id=? AND lesson_id=?")) {
            ps.setString(1,m.getType()); ps.setString(2,m.getTitle()); ps.setString(3,m.getContentText()); ps.setString(4,m.getMediaUrl()); ps.setInt(5,m.getSortOrder()); ps.setLong(6,m.getId()); ps.setLong(7,m.getLessonId());
            if(ps.executeUpdate()==0) throw new SQLException("Không tìm thấy nội dung");
        }
    }
    private void bind(PreparedStatement ps, Material m) throws SQLException {
        ps.setLong(1,m.getLessonId()); ps.setString(2,m.getType()); ps.setString(3,m.getTitle()); ps.setString(4,m.getContentText()); ps.setString(5,m.getMediaUrl()); ps.setInt(6,m.getSortOrder());
    }
    private Material map(ResultSet rs) throws SQLException {
        Material m=new Material(); m.setId(rs.getLong("id")); m.setLessonId(rs.getLong("lesson_id")); m.setType(rs.getString("material_type")); m.setTitle(rs.getString("title")); m.setContentText(rs.getString("content_text")); m.setMediaUrl(rs.getString("media_url")); m.setSortOrder(rs.getInt("sort_order")); return m;
    }
    public List<Material> findByLesson(Connection c, long lessonId) throws SQLException {
        List<Material> out = new ArrayList<>();
        String sql = "SELECT id,lesson_id,material_type,title,content_text,media_url,sort_order FROM materials WHERE lesson_id=? ORDER BY sort_order,id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) {
                out.add(map(rs));
            }}
        }
        return out;
    }
}
