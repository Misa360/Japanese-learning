package com.jlearn.controller.admin;

import com.jlearn.model.Answer;
import com.jlearn.model.Question;
import com.jlearn.service.AdminQuizService;
import com.jlearn.util.WebUtil;
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F36 /admin/questions
 * Quan ly cau hoi va dap an cua MOT de (?quiz_id= bat buoc): danh sach, them, sua, doi thu tu, gan vao phan thi (section_id) neu la de thi thu.
 * Luu questions + answers trong cung mot transaction (DbUtil.tx). Cau single_choice phai co dung 1 dap an dung, multiple_choice it nhat 1.
 * GET /admin/questions?quiz_id= ; GET /admin/questions/new?quiz_id= ; GET /admin/questions/{id} ; POST /admin/questions ; POST /admin/questions/{id}
 * Moi thao tac ghi phai ghi audit_logs (F38) va dung co an (status), khong xoa cung khoa hoc/bai hoc/nguoi dung.
 * Hien chi la khung: doGet forward toi JSP "Chua trien khai"; chua co doPost.
 */
@WebServlet(name = "AdminQuestionServlet", urlPatterns = {"/admin/questions", "/admin/questions/*"})
public class AdminQuestionServlet extends AdminBaseServlet {
    private final AdminQuizService adminQuizService=new AdminQuizService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if("practice".equals(req.getParameter("type"))){
            try{
                long quizId=Long.parseLong(req.getParameter("quiz_id"));req.setAttribute("quiz",adminQuizService.practiceQuiz(quizId));String path=req.getPathInfo();
                if(path==null||path.isEmpty()||"/".equals(path)){req.setAttribute("questions",adminQuizService.practiceQuestions(quizId));render(req,resp,"Câu hỏi Luyện đề","admin-practice","admin/question_list.jsp");return;}
                req.setAttribute("sections",adminQuizService.practiceSections(quizId));
                Question q;
                if("/new".equals(path)){q=new Question();q.setQuizId(quizId);q.setQuestionType("single_choice");q.setPoints(BigDecimal.ONE);req.setAttribute("formAction","/admin/questions/new?type=practice&quiz_id="+quizId);}
                else{q=adminQuizService.practiceQuestion(Long.parseLong(path.substring(1)),quizId);req.setAttribute("formAction","/admin/questions/"+q.getId()+"?type=practice&quiz_id="+quizId);}
                req.setAttribute("question",q);render(req,resp,"Biểu mẫu câu hỏi Luyện đề","admin-practice","admin/question_form.jsp");
            }catch(NumberFormatException ex){resp.sendError(HttpServletResponse.SC_BAD_REQUEST);}
            return;
        }
        render(req, resp, isForm(req) ? "Biểu mẫu câu hỏi" : "Danh sách câu hỏi", "admin-questions", isForm(req) ? "admin/question_form.jsp" : "admin/question_list.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        if(!"practice".equals(req.getParameter("type"))){resp.sendError(HttpServletResponse.SC_FORBIDDEN);return;}
        req.setCharacterEncoding("UTF-8");Question q=new Question();
        try{
            String id=req.getParameter("id");if(id!=null&&!id.isEmpty())q.setId(Long.parseLong(id));q.setQuizId(Long.parseLong(req.getParameter("quiz_id")));
            String section=req.getParameter("section_id");q.setSectionId(section==null||section.isEmpty()?null:Long.valueOf(section));q.setQuestionType(req.getParameter("question_type"));q.setContent(req.getParameter("content"));q.setExplanation(req.getParameter("explanation"));q.setAudioUrl(req.getParameter("audio_url"));q.setImageUrl(req.getParameter("image_url"));
            String points=req.getParameter("points");q.setPoints(points==null||points.isEmpty()?null:new BigDecimal(points));String order=req.getParameter("sort_order");q.setSortOrder(order==null||order.isEmpty()?0:Integer.parseInt(order));
            String[] values=req.getParameterValues("answer_text");String[] correctValues=req.getParameterValues("correct_indices");Set<String> correct=correctValues==null?Collections.emptySet():new HashSet<>(Arrays.asList(correctValues));List<Answer> answers=new ArrayList<>();
            if(values!=null)for(int i=0;i<values.length;i++){Answer a=new Answer();a.setContent(values[i]);a.setCorrect(correct.contains(String.valueOf(i)));answers.add(a);}
            adminQuizService.savePracticeQuestion(q,answers,WebUtil.userId(req));resp.sendRedirect(req.getContextPath()+"/admin/quizzes/"+q.getQuizId()+"?type=practice");
        }catch(NumberFormatException ex){resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Dữ liệu số không hợp lệ");}
    }
}
