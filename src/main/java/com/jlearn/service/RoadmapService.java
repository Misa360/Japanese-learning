package com.jlearn.service;

import com.jlearn.dao.CourseDao;
import com.jlearn.dao.EnrollmentDao;
import com.jlearn.dao.LevelDao;
import com.jlearn.model.Course;
import com.jlearn.model.CourseCard;
import com.jlearn.model.CourseProgress;
import com.jlearn.model.EligibilityResult;
import com.jlearn.model.Level;
import com.jlearn.model.LevelBlock;
import com.jlearn.util.DbUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** F10 (lo trinh N5 -> N1) va F13 (dieu kien mo khoa: canEnroll). */
public class RoadmapService {
    private final LevelDao levelDao = new LevelDao();
    private final CourseDao courseDao = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final ProgressService progressService = new ProgressService();
    private final CoinService coinService = new CoinService();

    /** F10. userId = null (khach) thi khong co trang thai dang ky/khoa, chi hien khoa hoc va dieu kien. */
    public List<LevelBlock> buildRoadmap(Long userId) {
        return DbUtil.read(c -> {
            boolean logged = userId != null;
            long uid = logged ? userId : 0L;

            List<Level> levels = levelDao.findAll(c);
            List<Course> courses = courseDao.findPublished(c);
            Set<Long> enrolled = logged ? enrollmentDao.enrolledCourseIds(c, uid) : Collections.<Long>emptySet();
            long balance = logged ? coinService.getBalance(c, uid) : 0L;

            Set<Long> ids = new LinkedHashSet<>();           // khoa hien thi + khoa tien quyet -> 1 truy van tien do
            for (Course co : courses) {
                ids.add(co.getId());
                if (co.getRequiredCourseId() != null) ids.add(co.getRequiredCourseId());
            }
            Map<Long, CourseProgress> progress = progressService.progressForCourses(c, uid, ids);

            Map<Long, LevelBlock> byLevel = new HashMap<>();
            List<LevelBlock> blocks = new ArrayList<>();
            for (Level lv : levels) {
                LevelBlock b = new LevelBlock();
                b.setLevel(lv);
                byLevel.put(lv.getId(), b);
                blocks.add(b);
            }
            for (Course co : courses) {
                LevelBlock b = byLevel.get(co.getLevelId());
                if (b == null) continue;
                CourseCard card = new CourseCard();
                card.setCourse(co);
                card.setProgress(progress.get(co.getId()));
                if (logged) {
                    boolean isEnrolled = enrolled.contains(co.getId());
                    card.setEnrolled(isEnrolled);
                    boolean requiredDone = requiredDone(co, progress);
                    card.setEligibility(evaluate(co, isEnrolled, requiredDone, balance));
                }
                b.getCourses().add(card);
            }
            return blocks;
        });
    }

    private static boolean requiredDone(Course co, Map<Long, CourseProgress> progress) {
        if (co.getRequiredCourseId() == null) return true;
        CourseProgress p = progress.get(co.getRequiredCourseId());
        return p != null && p.isCompleted();
    }

    /** F13: canEnroll(user, course) -> {ok, reason}. Dung trong transaction cua F12 (cung Connection). */
    public EligibilityResult canEnroll(Connection c, long userId, Course course) throws SQLException {
        boolean enrolled = enrollmentDao.exists(c, userId, course.getId());
        boolean requiredDone = course.getRequiredCourseId() == null
                || progressService.courseProgress(c, userId, course.getRequiredCourseId()).isCompleted();
        long balance = course.getCost() > 0 ? coinService.getBalance(c, userId) : 0L;
        return evaluate(course, enrolled, requiredDone, balance);
    }

    /** Ban khong can DB cua canEnroll (de test don vi). Thu tu: mo cua -> da dang ky -> tien quyet -> xu. */
    public static EligibilityResult evaluate(Course course, boolean enrolled, boolean requiredDone, long balance) {
        long cost = course.getCost();
        if (!course.isPublished()) {
            return EligibilityResult.of(false, EligibilityResult.NOT_AVAILABLE, "Khóa học chưa mở", cost);
        }
        if (enrolled) {
            return EligibilityResult.of(false, EligibilityResult.ALREADY_ENROLLED, "Bạn đã đăng ký khóa học này", cost);
        }
        if (!requiredDone) {
            String name = course.getRequiredCourseTitle() == null ? "tiên quyết" : "«" + course.getRequiredCourseTitle() + "»";
            return EligibilityResult.of(false, EligibilityResult.REQUIRED_COURSE,
                    "Cần hoàn thành khóa " + name + " trước", cost);
        }
        if (cost > 0 && balance < cost) {
            return EligibilityResult.of(false, EligibilityResult.NOT_ENOUGH_COINS,
                    "Cần " + cost + " xu để mở khóa (bạn đang có " + balance + " xu)", cost);
        }
        return EligibilityResult.of(true, EligibilityResult.OK, "", cost);
    }
}
