package com.jlearn.service;

import com.jlearn.dao.ContentDao;
import com.jlearn.dao.CourseDao;
import com.jlearn.dao.EnrollmentDao;
import com.jlearn.dao.LessonDao;
import com.jlearn.dao.LevelDao;
import com.jlearn.exception.NotFoundException;
import com.jlearn.model.Chapter;
import com.jlearn.model.Course;
import com.jlearn.model.CourseDetail;
import com.jlearn.model.CourseProgress;
import com.jlearn.model.LessonOutlineItem;
import com.jlearn.util.DbUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** F11 - chi tiet khoa hoc. */
public class CourseService {
    private final CourseDao courseDao = new CourseDao();
    private final LevelDao levelDao = new LevelDao();
    private final LessonDao lessonDao = new LessonDao();
    private final ContentDao contentDao = new ContentDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final RoadmapService roadmapService = new RoadmapService();

    /**
     * Khoa khong ton tai hoac chua mo (status != published) -> 404, tru admin.
     * Khach xem duoc mo ta + muc luc; chi dang nhap + dang ky moi co dau hoan thanh.
     * Tai lieu chi tra tieu de/loai (khong URL) - noi dung that nam o /learn/{id} va duoc kiem tra quyen o do.
     */
    public CourseDetail getDetail(Long userId, boolean admin, long courseId) {
        return DbUtil.read(c -> {
            Course course = courseDao.findById(c, courseId);
            if (course == null || (!course.isPublished() && !admin)) throw new NotFoundException("Khóa học không tồn tại");

            boolean logged = userId != null;
            long uid = logged ? userId : 0L;

            CourseDetail d = new CourseDetail();
            d.setCourse(course);
            d.setLevel(levelDao.findById(c, course.getLevelId()));
            d.setLoggedIn(logged);

            List<Chapter> chapters = lessonDao.chapters(c, courseId);
            Map<Long, Chapter> byId = new HashMap<>();
            for (Chapter ch : chapters) byId.put(ch.getId(), ch);
            List<LessonOutlineItem> outline = lessonDao.outline(c, uid, courseId);
            int done = 0;
            for (LessonOutlineItem it : outline) {
                Chapter ch = byId.get(it.getChapterId());
                if (ch != null) ch.getLessons().add(it);
                if (it.isCompleted()) done++;
            }
            d.setChapters(chapters);
            d.setLessonCount(outline.size());
            d.setProgress(CourseProgress.of(outline.size(), done));
            d.setMaterials(contentDao.summariesByCourse(c, courseId));

            if (logged) {
                d.setEnrolled(enrollmentDao.exists(c, uid, courseId));
                d.setEligibility(roadmapService.canEnroll(c, uid, course));
            }
            return d;
        });
    }
}
