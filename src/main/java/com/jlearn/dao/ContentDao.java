package com.jlearn.dao;

import com.jlearn.model.GrammarPoint;
import com.jlearn.model.Kanji;
import com.jlearn.model.Material;
import com.jlearn.model.MaterialSummary;
import com.jlearn.model.Vocabulary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Noi dung bai hoc: tai lieu, tu vung, ngu phap, kanji. */
public class ContentDao {

    public List<Material> materials(Connection c, long lessonId) throws SQLException {
        String sql = "SELECT id, lesson_id, material_type, title, content_text, media_url, sort_order "
                + "FROM materials WHERE lesson_id = ? ORDER BY sort_order, id";
        List<Material> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Material m = new Material();
                    m.setId(rs.getLong("id"));
                    m.setLessonId(rs.getLong("lesson_id"));
                    m.setType(rs.getString("material_type"));
                    m.setTitle(rs.getString("title"));
                    m.setContentText(rs.getString("content_text"));
                    m.setMediaUrl(rs.getString("media_url"));
                    m.setSortOrder(rs.getInt("sort_order"));
                    out.add(m);
                }
            }
        }
        return out;
    }

    /** Chi tieu de + loai (KHONG lay URL/noi dung) de trang chi tiet khoa hien thi ma khong lo tai lieu cho nguoi chua dang ky. */
    public List<MaterialSummary> summariesByCourse(Connection c, long courseId) throws SQLException {
        String sql = "SELECT m.lesson_id, l.title AS lesson_title, m.material_type, m.title "
                + "FROM materials m JOIN lessons l ON l.id = m.lesson_id AND l.status = 'published' "
                + "JOIN chapters ch ON ch.id = l.chapter_id "
                + "WHERE ch.course_id = ? AND m.material_type <> 'video' "
                + "ORDER BY ch.sort_order, l.sort_order, m.sort_order, m.id";
        List<MaterialSummary> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MaterialSummary s = new MaterialSummary();
                    s.setLessonId(rs.getLong("lesson_id"));
                    s.setLessonTitle(rs.getString("lesson_title"));
                    s.setType(rs.getString("material_type"));
                    s.setTitle(rs.getString("title"));
                    out.add(s);
                }
            }
        }
        return out;
    }

    public List<Vocabulary> vocabulary(Connection c, long lessonId) throws SQLException {
        String sql = "SELECT v.id, v.word, v.reading, v.meaning_vi, v.example_ja, v.example_vi "
                + "FROM lesson_vocabulary lv JOIN vocabulary v ON v.id = lv.vocabulary_id "
                + "WHERE lv.lesson_id = ? ORDER BY lv.sort_order, v.id";
        List<Vocabulary> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vocabulary v = new Vocabulary();
                    v.setId(rs.getLong("id"));
                    v.setWord(rs.getString("word"));
                    v.setReading(rs.getString("reading"));
                    v.setMeaningVi(rs.getString("meaning_vi"));
                    v.setExampleJa(rs.getString("example_ja"));
                    v.setExampleVi(rs.getString("example_vi"));
                    out.add(v);
                }
            }
        }
        return out;
    }

    public List<GrammarPoint> grammar(Connection c, long lessonId) throws SQLException {
        String sql = "SELECT g.id, g.title, g.pattern, g.explanation_vi, g.example_ja, g.example_vi "
                + "FROM lesson_grammar lg JOIN grammar_points g ON g.id = lg.grammar_id "
                + "WHERE lg.lesson_id = ? ORDER BY lg.sort_order, g.id";
        List<GrammarPoint> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GrammarPoint g = new GrammarPoint();
                    g.setId(rs.getLong("id"));
                    g.setTitle(rs.getString("title"));
                    g.setPattern(rs.getString("pattern"));
                    g.setExplanationVi(rs.getString("explanation_vi"));
                    g.setExampleJa(rs.getString("example_ja"));
                    g.setExampleVi(rs.getString("example_vi"));
                    out.add(g);
                }
            }
        }
        return out;
    }

    public List<Kanji> kanji(Connection c, long lessonId) throws SQLException {
        String sql = "SELECT k.id, k.character_kanji, k.onyomi, k.kunyomi, k.meaning_vi, k.stroke_count "
                + "FROM lesson_kanji lk JOIN kanji k ON k.id = lk.kanji_id "
                + "WHERE lk.lesson_id = ? ORDER BY lk.sort_order, k.id";
        List<Kanji> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Kanji k = new Kanji();
                    k.setId(rs.getLong("id"));
                    k.setCharacter(rs.getString("character_kanji"));
                    k.setOnyomi(rs.getString("onyomi"));
                    k.setKunyomi(rs.getString("kunyomi"));
                    k.setMeaningVi(rs.getString("meaning_vi"));
                    int sc = rs.getInt("stroke_count");
                    k.setStrokeCount(rs.wasNull() ? null : Integer.valueOf(sc));
                    out.add(k);
                }
            }
        }
        return out;
    }
}
