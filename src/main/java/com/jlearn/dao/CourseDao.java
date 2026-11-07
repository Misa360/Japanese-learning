package com.jlearn.dao;

import com.jlearn.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {
    private static final String SELECT =
            "SELECT c.id, c.level_id, lv.code AS level_code, c.title, c.slug, c.description, c.thumbnail_path, c.status, "
          + "c.is_free, c.required_course_id, c.unlock_coins, rc.title AS required_title "
          + "FROM courses c LEFT JOIN courses rc ON rc.id = c.required_course_id "
          + "LEFT JOIN levels lv ON lv.id = c.level_id ";

    public Course findById(Connection c, long id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE c.id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Khoa dang mo (status = published), sap theo cap do roi theo id. */
    public List<Course> findPublished(Connection c) throws SQLException {
        List<Course> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE c.status = 'published' ORDER BY c.level_id, c.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    private Course map(ResultSet rs) throws SQLException {
        Course co = new Course();
        co.setId(rs.getLong("id"));
        co.setLevelId(rs.getLong("level_id"));
        co.setLevelCode(rs.getString("level_code"));
        co.setTitle(rs.getString("title"));
        co.setSlug(rs.getString("slug"));
        co.setDescription(rs.getString("description"));
        co.setThumbnailPath(rs.getString("thumbnail_path"));
        co.setStatus(rs.getString("status"));
        co.setFree(rs.getBoolean("is_free"));
        long req = rs.getLong("required_course_id");
        co.setRequiredCourseId(rs.wasNull() ? null : Long.valueOf(req));
        co.setUnlockCoins(rs.getLong("unlock_coins"));
        co.setRequiredCourseTitle(rs.getString("required_title"));
        return co;
    }
}
