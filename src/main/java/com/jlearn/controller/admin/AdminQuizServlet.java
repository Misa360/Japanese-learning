package com.jlearn.controller.admin;

import com.jlearn.model.Quiz;
import com.jlearn.service.AdminService;
import com.jlearn.service.AdminQuizService;
import com.jlearn.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO F36 /admin/quizzes - Quan ly DE (bang quizzes). Mot servlet cho 3 loai, phan biet bang ?type= (quizzes.quiz_type):
 *   type=lesson     bai tap cua bai hoc (?lesson_id= bat buoc, di tu Khoa hoc > Bai hoc > Bai tap)
 *   type=practice   luyen de (khong gan bai hoc)
 *   type=jlpt_mock  thi thu JLPT (co the chia phan thi qua /admin/quiz-sections)
 * Truong form: title, description, quiz_type, lesson_id (chi type=lesson), jlpt_level_id, time_limit_seconds, passing_score, max_attempts,
 * show_answer_mode (immediate cho luyen de / after_submit cho thi thu), shuffle_questions, sort_order, status (draft/published/archived).
 * Moi de co lien ket "Cau hoi" -> /admin/questions?quiz_id= ; de thi thu co them "Phan thi" -> /admin/quiz-sections?quiz_id=.
 * GET /admin/quizzes?type= ; GET /admin/quizzes/new?type= ; GET /admin/quizzes/{id} ; POST /admin/quizzes ; POST /admin/quizzes/{id} ; POST /admin/quizzes/{id}/status
 * Khong cho publish de chua co cau hoi nao. Moi thao tac ghi phai ghi audit_logs (F38). Hien chi la khung.
 */
@WebServlet(name = "AdminQuizServlet", urlPatterns = {"/admin/quizzes", "/admin/quizzes/*"})
public class AdminQuizServlet extends AdminBaseServlet {
    private final AdminService adminService = new AdminService();
    private final AdminQuizService adminQuizService = new AdminQuizService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        String nav;
        String title;
        if ("jlpt_mock".equals(type)) { nav = "admin-jlpt"; title = "Đề thi thử JLPT"; }
        else if ("practice".equals(type)) { nav = "admin-practice"; title = "Đề luyện đề"; }
        else { nav = "admin-courses"; title = "Bài tập của bài học"; }
        if ("practice".equals(type)) {
            String path=req.getPathInfo();
            try {
                if(path==null||path.isEmpty()||"/".equals(path)){
                    req.setAttribute("quizzes",adminQuizService.practiceQuizzes());
                    render(req,resp,title,nav,"admin/quiz_list.jsp");return;
                }
                req.setAttribute("levels",adminQuizService.levels());
                if("/new".equals(path)){
                    Quiz q=new Quiz();q.setQuizType("practice");q.setStatus("draft");q.setShowAnswerMode("immediate");
                    req.setAttribute("quiz",q);req.setAttribute("formAction","/admin/quizzes/new?type=practice");
                    render(req,resp,"Tạo đề luyện",nav,"admin/practice_quiz_form.jsp");return;
                }
                String[] pathParts=path.substring(1).split("/");long id=Long.parseLong(pathParts[0]);
                Quiz q=adminQuizService.practiceQuiz(id);req.setAttribute("quiz",q);
                if(pathParts.length==2&&"edit".equals(pathParts[1])){
                    req.setAttribute("formAction","/admin/quizzes/"+id+"/edit?type=practice");
                    render(req,resp,"Sửa đề luyện",nav,"admin/practice_quiz_form.jsp");return;
                }
                if(pathParts.length!=1){resp.sendError(HttpServletResponse.SC_NOT_FOUND);return;}
                req.setAttribute("sections",adminQuizService.practiceSections(id));
                req.setAttribute("questions",adminQuizService.practiceQuestions(id));
                render(req,resp,q.getTitle(),nav,"admin/practice_quiz_detail.jsp");return;
            }catch(NumberFormatException ex){resp.sendError(HttpServletResponse.SC_NOT_FOUND);return;}
        }
        if ("lesson".equals(type)) {
            try {
                long lessonId=Long.parseLong(req.getParameter("lesson_id"));
                req.setAttribute("lesson",adminService.lessonContext(lessonId));
                String path=req.getPathInfo();
                if(path!=null && path.startsWith("/")) {
                    if("/new".equals(path)) { Quiz q=new Quiz(); q.setLessonId(lessonId); q.setStatus("draft"); req.setAttribute("quiz",q); req.setAttribute("formAction","/admin/quizzes/new?type=lesson&lesson_id="+lessonId); }
                    else {
                        Quiz q=adminService.quiz(Long.parseLong(path.substring(1)));
                        if(q.getLessonId()==null || q.getLessonId()!=lessonId) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                        req.setAttribute("quiz",q); req.setAttribute("formAction","/admin/quizzes/"+q.getId()+"?type=lesson&lesson_id="+lessonId);
                    }
                    render(req,resp,"Biểu mẫu bài tập",nav,"admin/quiz_form.jsp");
                    return;
                }
                req.setAttribute("quizzes",adminService.lessonQuizzes(lessonId));
                render(req,resp,title,nav,"admin/quiz_list.jsp");
            } catch(NumberFormatException ex) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); }
            return;
        }
        if (isForm(req)) {
            render(req, resp, "Biểu mẫu đề", nav, "admin/quiz_form.jsp");
        } else {
            render(req, resp, title, nav, "admin/quiz_list.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");
        if("practice".equals(req.getParameter("type"))){
            Quiz q=new Quiz();try{
                String id=req.getParameter("id");if(id!=null&&!id.isEmpty())q.setId(Long.parseLong(id));
                q.setTitle(req.getParameter("title"));q.setDescription(req.getParameter("description"));q.setJlptLevelId(Long.parseLong(req.getParameter("jlpt_level_id")));
                String time=req.getParameter("time_limit_seconds");q.setTimeLimitSeconds(time==null||time.isEmpty()?null:Integer.valueOf(time));
                String passing=req.getParameter("passing_score");q.setPassingScore(passing==null||passing.isEmpty()?null:new java.math.BigDecimal(passing));
                String attempts=req.getParameter("max_attempts");q.setMaxAttempts(attempts==null||attempts.isEmpty()?null:Integer.valueOf(attempts));
                String order=req.getParameter("sort_order");q.setSortOrder(order==null||order.isEmpty()?0:Integer.parseInt(order));q.setShuffleQuestions("true".equals(req.getParameter("shuffle_questions")));q.setStatus(req.getParameter("status"));
                adminQuizService.savePracticeQuiz(q,WebUtil.userId(req));
                resp.sendRedirect(req.getContextPath()+"/admin/quizzes/"+q.getId()+"?type=practice");
            }catch(NumberFormatException ex){resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Vui lòng nhập đúng dữ liệu số");}return;
        }
        Quiz q=new Quiz();
        try {
            String id=req.getParameter("id"); if(id!=null && !id.isEmpty()) q.setId(Long.parseLong(id));
            q.setLessonId(Long.parseLong(req.getParameter("lesson_id"))); q.setTitle(req.getParameter("title")); q.setStatus(req.getParameter("status"));
            adminService.saveQuiz(q,WebUtil.userId(req));
            resp.sendRedirect(req.getContextPath()+"/admin/lessons/"+q.getLessonId());
        } catch(NumberFormatException ex) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Dữ liệu không hợp lệ"); }
    }
}
