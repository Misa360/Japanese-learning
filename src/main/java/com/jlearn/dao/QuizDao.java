package com.jlearn.dao;

import com.jlearn.model.Quiz;
import java.sql.*;
import java.util.*;

public class QuizDao {
    public List<Quiz> findPracticeAdmin(Connection c) throws SQLException {
        String sql="SELECT q.id,q.lesson_id,q.jlpt_level_id,lv.code AS level_code,q.title,q.description,q.quiz_type,q.status,q.time_limit_seconds,q.passing_score,q.max_attempts,q.sort_order,q.show_answer_mode,q.shuffle_questions,COUNT(DISTINCT qu.id) question_count,COUNT(DISTINCT s.id) section_count,(SELECT COUNT(*) FROM quiz_attempts qa WHERE qa.quiz_id=q.id) attempt_count FROM quizzes q LEFT JOIN levels lv ON lv.id=q.jlpt_level_id LEFT JOIN questions qu ON qu.quiz_id=q.id LEFT JOIN quiz_sections s ON s.quiz_id=q.id WHERE q.quiz_type='practice' GROUP BY q.id,q.lesson_id,q.jlpt_level_id,lv.code,q.title,q.description,q.quiz_type,q.status,q.time_limit_seconds,q.passing_score,q.max_attempts,q.sort_order,q.show_answer_mode,q.shuffle_questions ORDER BY lv.sort_order,q.id";
        List<Quiz> out=new ArrayList<>();try(PreparedStatement ps=c.prepareStatement(sql);ResultSet rs=ps.executeQuery()){while(rs.next())out.add(mapFull(rs));}return out;
    }
    public Quiz findPracticeById(Connection c,long id) throws SQLException {
        String sql="SELECT q.id,q.lesson_id,q.jlpt_level_id,lv.code AS level_code,q.title,q.description,q.quiz_type,q.status,q.time_limit_seconds,q.passing_score,q.max_attempts,q.sort_order,q.show_answer_mode,q.shuffle_questions,COUNT(DISTINCT qu.id) question_count,COUNT(DISTINCT s.id) section_count,(SELECT COUNT(*) FROM quiz_attempts qa WHERE qa.quiz_id=q.id) attempt_count FROM quizzes q LEFT JOIN levels lv ON lv.id=q.jlpt_level_id LEFT JOIN questions qu ON qu.quiz_id=q.id LEFT JOIN quiz_sections s ON s.quiz_id=q.id WHERE q.id=? AND q.quiz_type='practice' GROUP BY q.id,q.lesson_id,q.jlpt_level_id,lv.code,q.title,q.description,q.quiz_type,q.status,q.time_limit_seconds,q.passing_score,q.max_attempts,q.sort_order,q.show_answer_mode,q.shuffle_questions";
        try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?mapFull(rs):null;}}
    }
    public long insertPractice(Connection c,Quiz q) throws SQLException {
        String sql="INSERT INTO quizzes(lesson_id,title,description,quiz_type,jlpt_level_id,time_limit_seconds,passing_score,max_attempts,sort_order,show_answer_mode,shuffle_questions,status) VALUES(NULL,?,?,'practice',?,?,?,?,?,?,?,?)";
        try(PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){bindPractice(ps,q);ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next())return rs.getLong(1);}}
        throw new SQLException("Không tạo được đề luyện");
    }
    public void updatePractice(Connection c,Quiz q) throws SQLException {
        String sql="UPDATE quizzes SET title=?,description=?,jlpt_level_id=?,time_limit_seconds=?,passing_score=?,max_attempts=?,sort_order=?,show_answer_mode=?,shuffle_questions=?,status=? WHERE id=? AND quiz_type='practice'";
        try(PreparedStatement ps=c.prepareStatement(sql)){int i=bindPractice(ps,q);ps.setLong(i,q.getId());if(ps.executeUpdate()==0)throw new SQLException("Không tìm thấy đề luyện");}
    }
    private int bindPractice(PreparedStatement ps,Quiz q)throws SQLException{
        ps.setString(1,q.getTitle());ps.setString(2,q.getDescription());ps.setLong(3,q.getJlptLevelId());
        if(q.getTimeLimitSeconds()==null)ps.setNull(4,Types.INTEGER);else ps.setInt(4,q.getTimeLimitSeconds());
        if(q.getPassingScore()==null)ps.setNull(5,Types.DECIMAL);else ps.setBigDecimal(5,q.getPassingScore());
        if(q.getMaxAttempts()==null)ps.setNull(6,Types.SMALLINT);else ps.setInt(6,q.getMaxAttempts());
        ps.setInt(7,q.getSortOrder());ps.setString(8,q.getShowAnswerMode());ps.setBoolean(9,q.isShuffleQuestions());ps.setString(10,q.getStatus());return 11;
    }
    private Quiz mapFull(ResultSet rs)throws SQLException{
        Quiz q=new Quiz();q.setId(rs.getLong("id"));long lesson=rs.getLong("lesson_id");q.setLessonId(rs.wasNull()?null:lesson);long level=rs.getLong("jlpt_level_id");q.setJlptLevelId(rs.wasNull()?null:level);q.setLevelCode(rs.getString("level_code"));q.setTitle(rs.getString("title"));q.setDescription(rs.getString("description"));q.setQuizType(rs.getString("quiz_type"));q.setStatus(rs.getString("status"));int secs=rs.getInt("time_limit_seconds");q.setTimeLimitSeconds(rs.wasNull()?null:secs);q.setPassingScore(rs.getBigDecimal("passing_score"));int max=rs.getInt("max_attempts");q.setMaxAttempts(rs.wasNull()?null:max);q.setSortOrder(rs.getInt("sort_order"));q.setShowAnswerMode(rs.getString("show_answer_mode"));q.setShuffleQuestions(rs.getBoolean("shuffle_questions"));q.setQuestionCount(rs.getInt("question_count"));q.setSectionCount(rs.getInt("section_count"));q.setAttemptCount(rs.getLong("attempt_count"));return q;
    }
    public Quiz findById(Connection c,long id) throws SQLException {
        String sql="SELECT q.id,q.lesson_id,q.title,q.quiz_type,q.status,COUNT(qu.id) AS question_count FROM quizzes q LEFT JOIN questions qu ON qu.quiz_id=q.id WHERE q.id=? GROUP BY q.id,q.lesson_id,q.title,q.quiz_type,q.status";
        try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?map(rs):null;}}
    }
    public long insert(Connection c,Quiz q) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO quizzes(lesson_id,title,quiz_type,status) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            ps.setLong(1,q.getLessonId());ps.setString(2,q.getTitle());ps.setString(3,"lesson");ps.setString(4,q.getStatus());ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next())return rs.getLong(1);}
        } throw new SQLException("Không tạo được bài tập");
    }
    public void update(Connection c,Quiz q) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE quizzes SET title=?,status=? WHERE id=? AND lesson_id=? AND quiz_type='lesson'")){
            ps.setString(1,q.getTitle());ps.setString(2,q.getStatus());ps.setLong(3,q.getId());ps.setLong(4,q.getLessonId());if(ps.executeUpdate()==0)throw new SQLException("Không tìm thấy bài tập");
        }
    }
    private Quiz map(ResultSet rs) throws SQLException { Quiz q=new Quiz(); q.setId(rs.getLong("id"));long lesson=rs.getLong("lesson_id");q.setLessonId(rs.wasNull()?null:lesson);q.setTitle(rs.getString("title"));q.setQuizType(rs.getString("quiz_type"));q.setStatus(rs.getString("status"));q.setQuestionCount(rs.getInt("question_count"));return q; }
    public List<Quiz> findByLesson(Connection c, long lessonId) throws SQLException {
        List<Quiz> out = new ArrayList<>();
        String sql = "SELECT q.id,q.lesson_id,q.title,q.quiz_type,q.status,COUNT(qu.id) AS question_count FROM quizzes q LEFT JOIN questions qu ON qu.quiz_id=q.id WHERE q.lesson_id=? GROUP BY q.id,q.lesson_id,q.title,q.quiz_type,q.status ORDER BY q.id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) {
                out.add(map(rs));
            }}
        }
        return out;
    }
}
