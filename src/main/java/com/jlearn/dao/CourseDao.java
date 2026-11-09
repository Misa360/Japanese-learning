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

    public List<Course> findAll(Connection c) throws SQLException {
        List<Course> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY c.level_id, c.id"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    public long insert(Connection c, Course co) throws SQLException {
        String sql = "INSERT INTO courses (level_id,title,slug,description,thumbnail_path,price,status,is_free,required_course_id,unlock_coins) VALUES (?,?,?,?,?,0,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            bind(ps, co, 1);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getLong(1); }
        }
        throw new SQLException("Không tạo được khóa học");
    }

    public void update(Connection c, Course co) throws SQLException {
        String sql = "UPDATE courses SET level_id=?,title=?,slug=?,description=?,thumbnail_path=?,status=?,is_free=?,required_course_id=?,unlock_coins=? WHERE id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, co, 1); ps.setLong(10, co.getId());
            if (ps.executeUpdate() == 0) throw new SQLException("Không tìm thấy khóa học");
        }
    }

    private void bind(PreparedStatement ps, Course co, int start) throws SQLException {
        ps.setLong(start, co.getLevelId()); ps.setString(start+1, co.getTitle()); ps.setString(start+2, co.getSlug());
        ps.setString(start+3, co.getDescription()); ps.setString(start+4, co.getThumbnailPath());
        ps.setString(start+5, co.getStatus()); ps.setBoolean(start+6, co.isFree());
        if (co.getRequiredCourseId() == null) ps.setNull(start+7, java.sql.Types.BIGINT); else ps.setLong(start+7, co.getRequiredCourseId());
        ps.setLong(start+8, co.getUnlockCoins());
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
