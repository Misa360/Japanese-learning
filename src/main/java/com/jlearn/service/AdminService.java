package com.jlearn.service;

import com.jlearn.util.DbUtil;
import com.jlearn.dao.CourseDao;
import com.jlearn.dao.LevelDao;
import com.jlearn.dao.LessonDao;
import com.jlearn.dao.MaterialDao;
import com.jlearn.dao.QuizDao;
import com.jlearn.dao.UserDao;
import com.jlearn.dao.RoleDao;
import com.jlearn.model.Course;
import com.jlearn.model.Level;
import com.jlearn.model.LessonContext;
import com.jlearn.model.LessonOutlineItem;
import com.jlearn.model.Material;
import com.jlearn.model.Quiz;
import com.jlearn.model.AdminUserRow;
import com.jlearn.exception.NotFoundException;
import com.jlearn.exception.ValidationException;
import com.jlearn.exception.BusinessRuleException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO F33, F34, F37, F38
 * Dashboard va nguoi dung (F33), khoa hoc va chuong (F34), phan thuong (F37), nhat ky (F38). Doi xu phai goi UserDao.lockForUpdate dau transaction roi CoinService.award/spend (xem quy tac khoa trong UserDao).
 * Nghiep vu nam o Service; ham doi nhieu bang chay trong DbUtil.tx(...); moi thao tac ghi them mot dong audit_logs (AuditLogDao) trong cung transaction.
 */
public class AdminService {
    private final CourseDao courseDao = new CourseDao();
    private final LevelDao levelDao = new LevelDao();
    private final LessonDao lessonDao = new LessonDao();
    private final MaterialDao materialDao = new MaterialDao();
    private final QuizDao quizDao = new QuizDao();
    private final UserDao userDao = new UserDao();
    private final RoleDao roleDao = new RoleDao();

    public java.util.List<AdminUserRow> adminUsers(String query,String role,String status,int limit,int offset) {
        return DbUtil.read(c -> userDao.findAdminUsers(c,query,role,status,limit,offset));
    }
    public long adminUserCount(String query,String role,String status) {
        return DbUtil.read(c -> userDao.countAdminUsers(c,query,role,status));
    }
    public void changeUserStatus(long userId,String status,Long actorId) {
        if(!java.util.Arrays.asList("active","locked").contains(status))throw new ValidationException("Trạng thái tài khoản không hợp lệ");
        if(actorId!=null && actorId==userId)throw new BusinessRuleException("Không thể khóa hoặc thay đổi trạng thái tài khoản đang sử dụng");
        DbUtil.tx(c->{
            userDao.lockAdminRole(c);
            if(!userDao.lockForUpdate(c,userId))throw new NotFoundException("Không tìm thấy người dùng");
            com.jlearn.model.User target=userDao.findById(c,userId);
            if("active".equals(target.getStatus()) && target.isAdmin() && "locked".equals(status) && !userDao.hasOtherActiveAdmin(c,userId))throw new BusinessRuleException("Không thể khóa quản trị viên hoạt động cuối cùng");
            if(!status.equals(target.getStatus())){
                userDao.updateAdminStatus(c,userId,status);
                auditUser(c,actorId,"locked".equals(status)?"user.lock":"user.unlock",userId,"status",target.getStatus(),status);
            }
            return null;
        });
    }
    public void changeUserRole(long userId,String role,Long actorId) {
        if(!java.util.Arrays.asList("user","admin").contains(role))throw new ValidationException("Vai trò không hợp lệ");
        if(actorId!=null && actorId==userId)throw new BusinessRuleException("Không thể thay đổi vai trò của tài khoản đang sử dụng");
        DbUtil.tx(c->{
            userDao.lockAdminRole(c);
            if(!userDao.lockForUpdate(c,userId))throw new NotFoundException("Không tìm thấy người dùng");
            com.jlearn.model.User target=userDao.findById(c,userId);
            if(target.isAdmin() && "active".equals(target.getStatus()) && "user".equals(role) && !userDao.hasOtherActiveAdmin(c,userId))throw new BusinessRuleException("Không thể hạ quyền quản trị viên hoạt động cuối cùng");
            if(!role.equals(target.getRoleName())){
                Long roleId=roleDao.idByName(c,role);if(roleId==null)throw new ValidationException("Vai trò không tồn tại");
                userDao.updateAdminRole(c,userId,roleId);
                auditUser(c,actorId,"user.role.update",userId,"role",target.getRoleName(),role);
            }
            return null;
        });
    }

    private void auditUser(java.sql.Connection c,Long actorId,String action,long userId,String field,String before,String after) throws java.sql.SQLException {
        try(java.sql.PreparedStatement ps=c.prepareStatement("INSERT INTO audit_logs(actor_user_id,action,entity_type,entity_id,details) VALUES (?,?,'user',?,JSON_OBJECT('field',?,'from',?,'to',?))")){
            if(actorId==null)ps.setNull(1,java.sql.Types.BIGINT);else ps.setLong(1,actorId);
            ps.setString(2,action);ps.setLong(3,userId);ps.setString(4,field);ps.setString(5,before);ps.setString(6,after);ps.executeUpdate();
        }
    }

    public java.util.List<Course> courses() { return DbUtil.read(courseDao::findAll); }
    public java.util.List<Level> levels() { return DbUtil.read(levelDao::findAll); }
    public Course course(long id) { return DbUtil.read(c -> { Course co = courseDao.findById(c,id); if (co == null) throw new NotFoundException("Không tìm thấy khóa học"); return co; }); }
    public java.util.List<LessonOutlineItem> courseLessons(long courseId) {
        return DbUtil.read(c -> { if (courseDao.findById(c, courseId) == null) throw new NotFoundException("Không tìm thấy khóa học"); return lessonDao.adminLessonsByCourse(c, courseId); });
    }
    public LessonContext lessonContext(long lessonId) {
        return DbUtil.read(c -> { LessonContext lesson = lessonDao.findContext(c, lessonId); if (lesson == null) throw new NotFoundException("Không tìm thấy bài học"); return lesson; });
    }
    public java.util.List<Material> lessonMaterials(long lessonId) { return DbUtil.read(c -> materialDao.findByLesson(c, lessonId)); }
    public java.util.List<Quiz> lessonQuizzes(long lessonId) { return DbUtil.read(c -> quizDao.findByLesson(c, lessonId)); }
    public Quiz quiz(long id) { return DbUtil.read(c -> { Quiz q=quizDao.findById(c,id); if(q==null)throw new NotFoundException("Không tìm thấy bài tập"); return q; }); }
    public void saveQuiz(Quiz quiz,Long actorId) {
        if(quiz.getTitle()==null || quiz.getTitle().trim().isEmpty() || quiz.getTitle().trim().length()>200) throw new ValidationException("Tên bài tập không hợp lệ");
        if(!java.util.Arrays.asList("draft","published","archived").contains(quiz.getStatus())) throw new ValidationException("Trạng thái không hợp lệ");
        quiz.setTitle(quiz.getTitle().trim());
        DbUtil.tx(c->{
            if(quiz.getLessonId()==null || lessonDao.findContext(c,quiz.getLessonId())==null)throw new NotFoundException("Không tìm thấy bài học");
            if(quiz.getId()==0){long id=quizDao.insert(c,quiz);auditEntity(c,actorId,"quiz.create","quiz",id,quiz.getTitle());}
            else {Quiz existing=quizDao.findById(c,quiz.getId());if(existing==null || !quiz.getLessonId().equals(existing.getLessonId()))throw new NotFoundException("Không tìm thấy bài tập");quizDao.update(c,quiz);auditEntity(c,actorId,"quiz.update","quiz",quiz.getId(),quiz.getTitle());}
            return null;
        });
    }
    public Material material(long id) { return DbUtil.read(c -> { Material m=materialDao.findById(c,id); if(m==null) throw new NotFoundException("Không tìm thấy video hoặc tài liệu"); return m; }); }
    public void saveMaterial(Material material, Long actorId) {
        if (material.getTitle()==null || material.getTitle().trim().isEmpty() || material.getTitle().trim().length()>200) throw new ValidationException("Tên nội dung không hợp lệ");
        if (!java.util.Arrays.asList("video","document","text").contains(material.getType())) throw new ValidationException("Loại nội dung không hợp lệ");
        if (material.getSortOrder()<0) throw new ValidationException("Thứ tự phải lớn hơn hoặc bằng 0");
        material.setTitle(material.getTitle().trim());
        if(material.getMediaUrl()!=null && material.getMediaUrl().trim().isEmpty()) material.setMediaUrl(null);
        DbUtil.tx(c -> {
            if (lessonDao.findContext(c,material.getLessonId())==null) throw new NotFoundException("Không tìm thấy bài học");
            if(material.getId()==0) {
                long id=materialDao.insert(c,material); auditEntity(c,actorId,"material.create","material",id,material.getTitle());
            } else {
                if(materialDao.findById(c,material.getId())==null) throw new NotFoundException("Không tìm thấy nội dung");
                materialDao.update(c,material); auditEntity(c,actorId,"material.update","material",material.getId(),material.getTitle());
            }
            return null;
        });
    }

    public void saveCourse(Course co, Long actorId) {
        if (co.getTitle() == null || co.getTitle().trim().isEmpty() || co.getTitle().trim().length() > 200) throw new ValidationException("Tên khóa học không hợp lệ");
        if (co.getSlug() == null || !co.getSlug().matches("[a-z0-9]+(?:-[a-z0-9]+)*") || co.getSlug().length() > 220) throw new ValidationException("Slug chỉ gồm chữ thường, số và dấu gạch ngang");
        if (!java.util.Arrays.asList("draft", "published", "archived").contains(co.getStatus())) throw new ValidationException("Trạng thái không hợp lệ");
        if (co.getUnlockCoins() < 0) throw new ValidationException("Số xu không được âm");
        co.setTitle(co.getTitle().trim());
        if (co.getDescription() != null && co.getDescription().trim().isEmpty()) co.setDescription(null);
        if (co.getThumbnailPath() != null && co.getThumbnailPath().trim().isEmpty()) co.setThumbnailPath(null);
        DbUtil.tx(c -> {
            if (levelDao.findById(c, co.getLevelId()) == null) throw new ValidationException("Cấp độ không hợp lệ");
            if (co.getRequiredCourseId() != null && (co.getId() == co.getRequiredCourseId() || courseDao.findById(c, co.getRequiredCourseId()) == null)) throw new ValidationException("Khóa học tiên quyết không hợp lệ");
            if (co.getId() == 0) {
                long id = courseDao.insert(c, co);
                audit(c, actorId, "course.create", id, co.getTitle());
            } else {
                if (courseDao.findById(c, co.getId()) == null) throw new NotFoundException("Không tìm thấy khóa học");
                courseDao.update(c, co);
                audit(c, actorId, "course.update", co.getId(), co.getTitle());
            }
            return null;
        });
    }

    private void audit(java.sql.Connection c, Long actorId, String action, long id, String title) throws java.sql.SQLException {
        auditEntity(c,actorId,action,"course",id,title);
    }
    private void auditEntity(java.sql.Connection c, Long actorId, String action, String entity, long id, String title) throws java.sql.SQLException {
        try (java.sql.PreparedStatement ps = c.prepareStatement("INSERT INTO audit_logs(actor_user_id,action,entity_type,entity_id,details) VALUES (?,?,?,?,JSON_OBJECT('title',?))")) {
            if (actorId == null) ps.setNull(1, java.sql.Types.BIGINT); else ps.setLong(1, actorId);
            ps.setString(2, action); ps.setString(3,entity); ps.setLong(4, id); ps.setString(5, title); ps.executeUpdate();
        }
    }

    /** F33: so lieu cho trang tong quan admin (1 cau SELECT, chi doc). Khoa: students, courses, publishedCourses, lessons, quizzes, attempts7d, pendingRedemptions. */
    public Map<String, Long> dashboardCounts() {
        final String sql = "SELECT "
                + "(SELECT COUNT(*) FROM users u JOIN roles r ON r.id = u.role_id WHERE r.name = 'user') AS students, "
                + "(SELECT COUNT(*) FROM courses) AS courses, "
                + "(SELECT COUNT(*) FROM courses WHERE status = 'published') AS published_courses, "
                + "(SELECT COUNT(*) FROM lessons) AS lessons, "
                + "(SELECT COUNT(*) FROM quizzes) AS quizzes, "
                + "(SELECT COUNT(*) FROM quiz_attempts WHERE started_at >= (NOW() - INTERVAL 7 DAY)) AS attempts_7d, "
                + "(SELECT COUNT(*) FROM reward_redemptions WHERE status = 'pending') AS pending_redemptions";
        return DbUtil.read(c -> {
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                rs.next();
                Map<String, Long> m = new LinkedHashMap<>();
                m.put("students", rs.getLong("students"));
                m.put("courses", rs.getLong("courses"));
                m.put("publishedCourses", rs.getLong("published_courses"));
                m.put("lessons", rs.getLong("lessons"));
                m.put("quizzes", rs.getLong("quizzes"));
                m.put("attempts7d", rs.getLong("attempts_7d"));
                m.put("pendingRedemptions", rs.getLong("pending_redemptions"));
                return m;
            }
        });
    }
}
