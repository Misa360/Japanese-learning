package com.jlearn.controller.admin;

import com.jlearn.model.QuizSection;
import com.jlearn.service.AdminQuizService;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F36 /admin/quiz-sections - Phan thi cua mot de thi thu JLPT (?quiz_id= bat buoc, bang quiz_sections).
 * Vi du N5: "Tu vung - Chu Han", "Ngu phap - Doc hieu", "Nghe hieu". Truong: title, section_type (vocab_kanji/grammar/reading/listening/other),
 * time_limit_seconds, max_score, passing_score, sort_order. Cau hoi gan vao phan thi bang questions.section_id.
 * GET /admin/quiz-sections?quiz_id= ; GET /admin/quiz-sections/new?quiz_id= ; GET /admin/quiz-sections/{id} ; POST /admin/quiz-sections ; POST /admin/quiz-sections/{id}
 * De luyen de va bai tap khong dung bang nay. Moi thao tac ghi phai ghi audit_logs (F38). Hien chi la khung.
 */
@WebServlet(name = "AdminQuizSectionServlet", urlPatterns = {"/admin/quiz-sections", "/admin/quiz-sections/*"})
public class AdminQuizSectionServlet extends AdminBaseServlet {
    private final AdminQuizService adminQuizService=new AdminQuizService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if("practice".equals(req.getParameter("type"))){
            try{
                long quizId=Long.parseLong(req.getParameter("quiz_id"));req.setAttribute("quiz",adminQuizService.practiceQuiz(quizId));String path=req.getPathInfo();
                if(path==null||path.isEmpty()||"/".equals(path)){resp.sendRedirect(req.getContextPath()+"/admin/quizzes/"+quizId+"?type=practice");return;}
                if("/new".equals(path)){QuizSection s=new QuizSection();s.setQuizId(quizId);s.setSectionType("vocab_kanji");s.setSortOrder(adminQuizService.practiceSections(quizId).size()+1);req.setAttribute("section",s);req.setAttribute("formAction","/admin/quiz-sections/new?type=practice&quiz_id="+quizId);}
                else{long id=Long.parseLong(path.substring(1));req.setAttribute("section",adminQuizService.practiceSection(id,quizId));req.setAttribute("formAction","/admin/quiz-sections/"+id+"?type=practice&quiz_id="+quizId);}
                render(req,resp,"Phần luyện đề","admin-practice","admin/quiz_section_form.jsp");
            }catch(NumberFormatException ex){resp.sendError(HttpServletResponse.SC_BAD_REQUEST);}
            return;
        }
        render(req, resp, isForm(req) ? "Biểu mẫu phần thi" : "Phần thi của đề thi thử", "admin-jlpt",
                isForm(req) ? "admin/quiz_section_form.jsp" : "admin/quiz_section_list.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        if(!"practice".equals(req.getParameter("type"))){resp.sendError(HttpServletResponse.SC_FORBIDDEN);return;}
        req.setCharacterEncoding("UTF-8");QuizSection s=new QuizSection();
        try{
            String id=req.getParameter("id");if(id!=null&&!id.isEmpty())s.setId(Long.parseLong(id));s.setQuizId(Long.parseLong(req.getParameter("quiz_id")));s.setTitle(req.getParameter("title"));s.setSectionType(req.getParameter("section_type"));
            String time=req.getParameter("time_limit_seconds");s.setTimeLimitSeconds(time==null||time.isEmpty()?null:Integer.valueOf(time));String order=req.getParameter("sort_order");s.setSortOrder(order==null||order.isEmpty()?0:Integer.parseInt(order));
            adminQuizService.savePracticeSection(s,WebUtil.userId(req));resp.sendRedirect(req.getContextPath()+"/admin/quizzes/"+s.getQuizId()+"?type=practice");
        }catch(NumberFormatException ex){resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Dữ liệu số không hợp lệ");}
    }
}
