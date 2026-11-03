package com.jlearn.service;

import com.jlearn.dao.EnrollmentDao;
import com.jlearn.dao.LessonDao;
import com.jlearn.dao.ProgressDao;
import com.jlearn.dao.UserDao;
import com.jlearn.exception.ForbiddenException;
import com.jlearn.exception.NotFoundException;
import com.jlearn.model.CompletionResult;
import com.jlearn.model.CourseProgress;
import com.jlearn.model.LessonContext;
import com.jlearn.util.DbUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** F18 (tien do tinh dong) va F15 (hoan thanh bai). Cong thuc nam o CourseProgress.percent()/courseStatus(). */
public class ProgressService {
    /** So xu thuong khi hoan thanh bai lan dau. Chinh theo luat thuong cua nhom. */
    public static final long LESSON_COMPLETE_COINS = 10L;

    private final ProgressDao progressDao = new ProgressDao();
    private final LessonDao lessonDao = new LessonDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final UserDao userDao = new UserDao();
    private final CoinService coinService = new CoinService();

    // ---------------------------------------------------------------- F18

    /** Tien do nhieu khoa cung luc (1 truy van). userId = 0 cho khach. */
    public Map<Long, CourseProgress> progressForCourses(Connection c, long userId, Collection<Long> courseIds)
            throws SQLException {
        Map<Long, int[]> totals = progressDao.totalsByCourse(c, userId, courseIds);
        Map<Long, CourseProgress> out = new HashMap<>();
        for (Long id : courseIds) {
            int[] t = totals.get(id);
            out.put(id, t == null ? CourseProgress.of(0, 0) : CourseProgress.of(t[0], t[1]));
        }
        return out;
    }

    public CourseProgress courseProgress(Connection c, long userId, long courseId) throws SQLException {
        return progressForCourses(c, userId, Collections.singletonList(courseId)).get(courseId);
    }

    public CourseProgress courseProgress(long userId, long courseId) {
        return DbUtil.read(c -> courseProgress(c, userId, courseId));
    }

    // ---------------------------------------------------------------- F15

    /**
     * Danh dau hoan thanh bai trong MOT transaction: kiem tra dang ky -> khoa dong tien do ->
     * (neu chua hoan thanh) ghi tien do + cong xu -> cap nhat moc hoan thanh khoa.
     * Goi lai nhieu lan / hai request cung luc chi cong xu mot lan (khoa dong + idempotency_key).
     */
    public CompletionResult completeLesson(long userId, long lessonId) {
        return DbUtil.tx(c -> {
            if (!userDao.lockForUpdate(c, userId)) throw new NotFoundException("Không tìm thấy tài khoản");   // QUY TAC KHOA: xem UserDao
            LessonContext lc = lessonDao.findContext(c, lessonId);
            if (lc == null || !lc.isPublished()) throw new NotFoundException("Không tìm thấy bài học");
            if (!enrollmentDao.exists(c, userId, lc.getCourseId())) {
                throw new ForbiddenException("Bạn cần đăng ký khóa học trước khi hoàn thành bài này");
            }

            progressDao.ensureRow(c, userId, lessonId);
            String status = progressDao.lockStatus(c, userId, lessonId);

            CompletionResult res = new CompletionResult();
            if (CourseProgress.LESSON_COMPLETED.equals(status)) {
                res.setAlreadyCompleted(true);
                res.setCoinsAwarded(0);
                res.setProgress(courseProgress(c, userId, lc.getCourseId()));
                return res;
            }

            progressDao.markCompleted(c, userId, lessonId);
            long coins = coinService.award(c, userId, LESSON_COMPLETE_COINS, "lesson_complete", "lesson", lessonId,
                    "lesson_complete:" + lessonId, "Hoàn thành bài học");

            CourseProgress cp = courseProgress(c, userId, lc.getCourseId());
            if (cp.isCompleted()) enrollmentDao.markCompletedIfNeeded(c, userId, lc.getCourseId());

            // F30: tai day goi BadgeService.check(c, userId) khi nguoi phu trach F30 xong (dung chung Connection c).

            res.setAlreadyCompleted(false);
            res.setCoinsAwarded(coins);
            res.setProgress(cp);
            return res;
        });
    }
}
