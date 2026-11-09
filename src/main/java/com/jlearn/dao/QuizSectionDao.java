package com.jlearn.dao;

import com.jlearn.model.QuizSection;
import java.sql.*;
import java.util.*;

public class QuizSectionDao {
    public List<QuizSection> findPracticeSections(Connection c,long quizId)throws SQLException{
        String sql="SELECT s.id,s.quiz_id,s.title,s.section_type,s.time_limit_seconds,s.max_score,s.passing_score,s.sort_order,COUNT(q.id) question_count FROM quiz_sections s LEFT JOIN questions q ON q.section_id=s.id WHERE s.quiz_id=? GROUP BY s.id,s.quiz_id,s.title,s.section_type,s.time_limit_seconds,s.max_score,s.passing_score,s.sort_order ORDER BY s.sort_order,s.id";
        List<QuizSection> out=new ArrayList<>();try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,quizId);try(ResultSet rs=ps.executeQuery()){while(rs.next())out.add(map(rs));}}return out;
    }
    public QuizSection findPracticeSection(Connection c,long id,long quizId)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT s.id,s.quiz_id,s.title,s.section_type,s.time_limit_seconds,s.max_score,s.passing_score,s.sort_order,COUNT(q.id) question_count FROM quiz_sections s LEFT JOIN questions q ON q.section_id=s.id WHERE s.id=? AND s.quiz_id=? GROUP BY s.id,s.quiz_id,s.title,s.section_type,s.time_limit_seconds,s.max_score,s.passing_score,s.sort_order")){ps.setLong(1,id);ps.setLong(2,quizId);try(ResultSet rs=ps.executeQuery()){return rs.next()?map(rs):null;}}
    }
    public long insert(Connection c,QuizSection s)throws SQLException{
        String sql="INSERT INTO quiz_sections(quiz_id,title,section_type,time_limit_seconds,max_score,passing_score,sort_order) VALUES(?,?,?,?,?,?,?)";
        try(PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){bind(ps,s);ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next())return rs.getLong(1);}}throw new SQLException("Không tạo được phần luyện đề");
    }
    public void update(Connection c,QuizSection s)throws SQLException{
        String sql="UPDATE quiz_sections SET title=?,section_type=?,time_limit_seconds=?,max_score=?,passing_score=?,sort_order=? WHERE id=? AND quiz_id=?";
        try(PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,s.getTitle());ps.setString(2,s.getSectionType());setInt(ps,3,s.getTimeLimitSeconds());setDecimal(ps,4,s.getMaxScore());setDecimal(ps,5,s.getPassingScore());ps.setInt(6,s.getSortOrder());ps.setLong(7,s.getId());ps.setLong(8,s.getQuizId());if(ps.executeUpdate()==0)throw new SQLException("Không tìm thấy phần luyện đề");}
    }
    private void bind(PreparedStatement ps,QuizSection s)throws SQLException{ps.setLong(1,s.getQuizId());ps.setString(2,s.getTitle());ps.setString(3,s.getSectionType());setInt(ps,4,s.getTimeLimitSeconds());setDecimal(ps,5,s.getMaxScore());setDecimal(ps,6,s.getPassingScore());ps.setInt(7,s.getSortOrder());}
    private void setInt(PreparedStatement ps,int i,Integer n)throws SQLException{if(n==null)ps.setNull(i,Types.INTEGER);else ps.setInt(i,n);}
    private void setDecimal(PreparedStatement ps,int i,java.math.BigDecimal n)throws SQLException{if(n==null)ps.setNull(i,Types.DECIMAL);else ps.setBigDecimal(i,n);}
    private QuizSection map(ResultSet rs)throws SQLException{QuizSection s=new QuizSection();s.setId(rs.getLong("id"));s.setQuizId(rs.getLong("quiz_id"));s.setTitle(rs.getString("title"));s.setSectionType(rs.getString("section_type"));int time=rs.getInt("time_limit_seconds");s.setTimeLimitSeconds(rs.wasNull()?null:time);s.setMaxScore(rs.getBigDecimal("max_score"));s.setPassingScore(rs.getBigDecimal("passing_score"));s.setSortOrder(rs.getInt("sort_order"));s.setQuestionCount(rs.getInt("question_count"));return s;}
}
