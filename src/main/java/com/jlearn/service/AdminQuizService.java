package com.jlearn.service;

import com.jlearn.dao.*;
import com.jlearn.exception.NotFoundException;
import com.jlearn.exception.ValidationException;
import com.jlearn.model.*;
import com.jlearn.util.DbUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.*;

/** Nghiep vu quan tri de luyen, phan thi, cau hoi va dap an. */
public class AdminQuizService {
    private final QuizDao quizDao=new QuizDao();
    private final QuizSectionDao sectionDao=new QuizSectionDao();
    private final QuestionDao questionDao=new QuestionDao();
    private final AnswerDao answerDao=new AnswerDao();
    private final LevelDao levelDao=new LevelDao();

    public List<Quiz> practiceQuizzes(){return DbUtil.read(quizDao::findPracticeAdmin);}
    public Quiz practiceQuiz(long id){return DbUtil.read(c->{Quiz q=quizDao.findPracticeById(c,id);if(q==null)throw new NotFoundException("Không tìm thấy đề luyện");return q;});}
    public List<Level> levels(){return DbUtil.read(levelDao::findAll);}
    public List<QuizSection> practiceSections(long quizId){return DbUtil.read(c->{requirePractice(c,quizId);return sectionDao.findPracticeSections(c,quizId);});}
    public QuizSection practiceSection(long id,long quizId){return DbUtil.read(c->{requirePractice(c,quizId);QuizSection s=sectionDao.findPracticeSection(c,id,quizId);if(s==null)throw new NotFoundException("Không tìm thấy phần luyện đề");return s;});}
    public List<Question> practiceQuestions(long quizId){return DbUtil.read(c->{requirePractice(c,quizId);List<Question> qs=questionDao.findPracticeQuestions(c,quizId);for(Question q:qs)q.setAnswers(answerDao.findByQuestion(c,q.getId()));return qs;});}
    public Question practiceQuestion(long id,long quizId){return DbUtil.read(c->{requirePractice(c,quizId);Question q=questionDao.findPracticeQuestion(c,id,quizId);if(q==null)throw new NotFoundException("Không tìm thấy câu hỏi");q.setAnswers(answerDao.findByQuestion(c,id));return q;});}

    public void savePracticeQuiz(Quiz q,Long actorId){
        if(q.getTitle()==null||q.getTitle().trim().isEmpty()||q.getTitle().trim().length()>200)throw new ValidationException("Tên đề luyện không hợp lệ");
        if(q.getJlptLevelId()==null)throw new ValidationException("Hãy chọn cấp độ JLPT");
        if(!Arrays.asList("draft","published","archived").contains(q.getStatus()))throw new ValidationException("Trạng thái không hợp lệ");
        if(q.getTimeLimitSeconds()!=null&&q.getTimeLimitSeconds()<=0)throw new ValidationException("Thời gian phải lớn hơn 0");
        if(q.getMaxAttempts()!=null&&q.getMaxAttempts()<=0)throw new ValidationException("Số lượt làm phải lớn hơn 0");
        if(q.getPassingScore()!=null&&(q.getPassingScore().compareTo(BigDecimal.ZERO)<0||q.getPassingScore().compareTo(new BigDecimal("100"))>0))throw new ValidationException("Điểm đạt phải từ 0 đến 100");
        q.setTitle(q.getTitle().trim());if(q.getDescription()!=null&&q.getDescription().trim().isEmpty())q.setDescription(null);q.setShowAnswerMode("immediate");q.setQuizType("practice");
        if(q.getId()==0&&"published".equals(q.getStatus()))throw new ValidationException("Hãy lưu đề ở trạng thái bản nháp, thêm câu hỏi rồi mới xuất bản");
        DbUtil.tx(c->{if(levelDao.findById(c,q.getJlptLevelId())==null)throw new ValidationException("Cấp độ không hợp lệ");
            if(q.getId()==0){long id=quizDao.insertPractice(c,q);q.setId(id);audit(c,actorId,"practice.create","quiz",id,q.getTitle());}
            else{Quiz old=requirePractice(c,q.getId());if("published".equals(q.getStatus())&&old.getQuestionCount()==0)throw new ValidationException("Không thể xuất bản đề chưa có câu hỏi");quizDao.updatePractice(c,q);audit(c,actorId,"practice.update","quiz",q.getId(),q.getTitle());}return null;});
    }

    public void savePracticeSection(QuizSection s,Long actorId){
        if(s.getTitle()==null||s.getTitle().trim().isEmpty()||s.getTitle().trim().length()>200)throw new ValidationException("Tên phần thi không hợp lệ");
        if(!Arrays.asList("vocabulary","kanji","grammar","reading","listening","other","vocab_kanji").contains(s.getSectionType()))throw new ValidationException("Loại phần thi không hợp lệ");
        if(s.getSortOrder()<0)throw new ValidationException("Thứ tự không hợp lệ");if(s.getTimeLimitSeconds()!=null&&s.getTimeLimitSeconds()<=0)throw new ValidationException("Thời gian phải lớn hơn 0");
        s.setTitle(s.getTitle().trim());
        // Giữ tương thích database cũ: vocabulary/kanji vẫn có nhãn riêng ở title, type dùng giá trị đã có.
        if("vocabulary".equals(s.getSectionType())||"kanji".equals(s.getSectionType()))s.setSectionType("vocab_kanji");
        DbUtil.tx(c->{requirePractice(c,s.getQuizId());if(s.getId()==0){long id=sectionDao.insert(c,s);audit(c,actorId,"practice.section.create","quiz_section",id,s.getTitle());}else{if(sectionDao.findPracticeSection(c,s.getId(),s.getQuizId())==null)throw new NotFoundException("Không tìm thấy phần luyện đề");sectionDao.update(c,s);audit(c,actorId,"practice.section.update","quiz_section",s.getId(),s.getTitle());}return null;});
    }

    public void savePracticeQuestion(Question q,List<Answer> answers,Long actorId){
        if(q.getContent()==null||q.getContent().trim().isEmpty())throw new ValidationException("Nội dung câu hỏi không được để trống");
        if(!Arrays.asList("single_choice","multiple_choice","text_input").contains(q.getQuestionType()))throw new ValidationException("Dạng câu hỏi không hợp lệ");
        if(q.getPoints()==null||q.getPoints().compareTo(BigDecimal.ZERO)<=0)throw new ValidationException("Điểm phải lớn hơn 0");if(q.getSortOrder()<0)throw new ValidationException("Thứ tự không hợp lệ");
        List<Answer> valid=new ArrayList<>();for(Answer a:answers)if(a.getContent()!=null&&!a.getContent().trim().isEmpty()){a.setContent(a.getContent().trim());valid.add(a);}
        long correct=valid.stream().filter(Answer::isCorrect).count();
        if("text_input".equals(q.getQuestionType())){if(valid.isEmpty()||correct!=1)throw new ValidationException("Câu hỏi điền đáp án cần một đáp án đúng");}
        else if(valid.size()<2)throw new ValidationException("Cần ít nhất hai phương án trả lời");
        if("single_choice".equals(q.getQuestionType())&&correct!=1)throw new ValidationException("Câu hỏi một đáp án phải chọn đúng một phương án đúng");
        if("multiple_choice".equals(q.getQuestionType())&&correct<1)throw new ValidationException("Câu hỏi nhiều đáp án cần ít nhất một phương án đúng");
        q.setContent(q.getContent().trim());if(q.getExplanation()!=null&&q.getExplanation().trim().isEmpty())q.setExplanation(null);
        DbUtil.tx(c->{Quiz quiz=requirePractice(c,q.getQuizId());if(q.getSectionId()==null||sectionDao.findPracticeSection(c,q.getSectionId(),q.getQuizId())==null)throw new ValidationException("Hãy chọn phần thi thuộc đề luyện này");
            if(q.getId()==0){long id=questionDao.insert(c,q);answerDao.replace(c,id,valid);audit(c,actorId,"practice.question.create","question",id,q.getContent());}
            else{if(questionDao.findPracticeQuestion(c,q.getId(),q.getQuizId())==null)throw new NotFoundException("Không tìm thấy câu hỏi");if(quiz.getAttemptCount()>0)throw new ValidationException("Đề đã có lượt làm; không thể sửa câu hỏi để giữ nguyên lịch sử kết quả");questionDao.update(c,q);answerDao.replace(c,q.getId(),valid);audit(c,actorId,"practice.question.update","question",q.getId(),q.getContent());}return null;});
    }

    private Quiz requirePractice(Connection c,long id)throws java.sql.SQLException{Quiz q=quizDao.findPracticeById(c,id);if(q==null)throw new NotFoundException("Không tìm thấy đề luyện");return q;}
    private void audit(Connection c,Long actor,String action,String entity,long id,String label)throws java.sql.SQLException{
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO audit_logs(actor_user_id,action,entity_type,entity_id,details) VALUES(?,?,?,?,JSON_OBJECT('title',?))")){if(actor==null)ps.setNull(1,Types.BIGINT);else ps.setLong(1,actor);ps.setString(2,action);ps.setString(3,entity);ps.setLong(4,id);ps.setString(5,label);ps.executeUpdate();}
    }
}
