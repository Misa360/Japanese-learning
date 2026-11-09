package com.jlearn.dao;

import com.jlearn.model.Question;
import java.sql.*;
import java.util.*;

public class QuestionDao {
    public List<Question> findPracticeQuestions(Connection c,long quizId)throws SQLException{
        List<Question> out=new ArrayList<>();String sql="SELECT q.id,q.quiz_id,q.section_id,s.title section_title,q.question_type,q.content,q.explanation,q.audio_url,q.image_url,q.points,q.sort_order FROM questions q LEFT JOIN quiz_sections s ON s.id=q.section_id WHERE q.quiz_id=? ORDER BY q.sort_order,q.id";
        try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,quizId);try(ResultSet rs=ps.executeQuery()){while(rs.next())out.add(map(rs));}}return out;
    }
    public Question findPracticeQuestion(Connection c,long id,long quizId)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT q.id,q.quiz_id,q.section_id,s.title section_title,q.question_type,q.content,q.explanation,q.audio_url,q.image_url,q.points,q.sort_order FROM questions q LEFT JOIN quiz_sections s ON s.id=q.section_id WHERE q.id=? AND q.quiz_id=?")){ps.setLong(1,id);ps.setLong(2,quizId);try(ResultSet rs=ps.executeQuery()){return rs.next()?map(rs):null;}}
    }
    public long insert(Connection c,Question q)throws SQLException{
        String sql="INSERT INTO questions(quiz_id,section_id,question_type,content,explanation,audio_url,image_url,points,sort_order) VALUES(?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){bind(ps,q);ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next())return rs.getLong(1);}}throw new SQLException("Không tạo được câu hỏi");
    }
    public void update(Connection c,Question q)throws SQLException{
        String sql="UPDATE questions SET section_id=?,question_type=?,content=?,explanation=?,audio_url=?,image_url=?,points=?,sort_order=? WHERE id=? AND quiz_id=?";
        try(PreparedStatement ps=c.prepareStatement(sql)){setLong(ps,1,q.getSectionId());ps.setString(2,q.getQuestionType());ps.setString(3,q.getContent());ps.setString(4,q.getExplanation());ps.setString(5,q.getAudioUrl());ps.setString(6,q.getImageUrl());ps.setBigDecimal(7,q.getPoints());ps.setInt(8,q.getSortOrder());ps.setLong(9,q.getId());ps.setLong(10,q.getQuizId());if(ps.executeUpdate()==0)throw new SQLException("Không tìm thấy câu hỏi");}
    }
    private void bind(PreparedStatement ps,Question q)throws SQLException{ps.setLong(1,q.getQuizId());setLong(ps,2,q.getSectionId());ps.setString(3,q.getQuestionType());ps.setString(4,q.getContent());ps.setString(5,q.getExplanation());ps.setString(6,q.getAudioUrl());ps.setString(7,q.getImageUrl());ps.setBigDecimal(8,q.getPoints());ps.setInt(9,q.getSortOrder());}
    private void setLong(PreparedStatement ps,int i,Long n)throws SQLException{if(n==null)ps.setNull(i,Types.BIGINT);else ps.setLong(i,n);}
    private Question map(ResultSet rs) throws SQLException {Question q=new Question();q.setId(rs.getLong("id"));q.setQuizId(rs.getLong("quiz_id"));long sec=rs.getLong("section_id");q.setSectionId(rs.wasNull()?null:sec);q.setSectionTitle(rs.getString("section_title"));q.setQuestionType(rs.getString("question_type"));q.setContent(rs.getString("content"));q.setExplanation(rs.getString("explanation"));q.setAudioUrl(rs.getString("audio_url"));q.setImageUrl(rs.getString("image_url"));q.setPoints(rs.getBigDecimal("points"));q.setSortOrder(rs.getInt("sort_order"));return q;}
}
