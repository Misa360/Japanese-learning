package com.jlearn.dao;

import com.jlearn.model.Answer;
import java.sql.*;
import java.util.*;

public class AnswerDao {
    public List<Answer> findByQuestion(Connection c,long questionId)throws SQLException{
        List<Answer> out=new ArrayList<>();try(PreparedStatement ps=c.prepareStatement("SELECT id,question_id,content,is_correct,sort_order FROM answers WHERE question_id=? ORDER BY sort_order,id")){ps.setLong(1,questionId);try(ResultSet rs=ps.executeQuery()){while(rs.next()){Answer a=new Answer();a.setId(rs.getLong("id"));a.setQuestionId(rs.getLong("question_id"));a.setContent(rs.getString("content"));a.setCorrect(rs.getBoolean("is_correct"));a.setSortOrder(rs.getInt("sort_order"));out.add(a);}}}return out;
    }
    public void replace(Connection c,long questionId,List<Answer> answers)throws SQLException{
        try(PreparedStatement del=c.prepareStatement("DELETE FROM answers WHERE question_id=?")){del.setLong(1,questionId);del.executeUpdate();}
        String sql="INSERT INTO answers(question_id,content,is_correct,sort_order) VALUES(?,?,?,?)";
        try(PreparedStatement ps=c.prepareStatement(sql)){int order=1;for(Answer a:answers){ps.setLong(1,questionId);ps.setString(2,a.getContent());ps.setBoolean(3,a.isCorrect());ps.setInt(4,order++);ps.addBatch();}ps.executeBatch();}
    }
}
