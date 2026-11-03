package com.jlearn.service;

import com.jlearn.dao.ContentDao;
import com.jlearn.dao.CourseDao;
import com.jlearn.dao.EnrollmentDao;
import com.jlearn.dao.LessonDao;
import com.jlearn.dao.ProgressDao;
import com.jlearn.dao.StudyLogDao;
import com.jlearn.exception.ForbiddenException;
import com.jlearn.exception.NotFoundException;
import com.jlearn.exception.UnauthorizedException;
import com.jlearn.model.Course;
import com.jlearn.model.CourseProgress;
import com.jlearn.model.LessonContext;
import com.jlearn.model.LessonOutlineItem;
import com.jlearn.model.LessonPage;
import com.jlearn.model.Material;
import com.jlearn.model.NextLesson;
import com.jlearn.util.DbUtil;
import com.jlearn.util.VideoEmbed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/** F14 (xem bai), F16 (luu vi tri), F17 (nextLesson). Kiem tra quyen o day, khong chi o route (chong IDOR). */
public class LessonService {
    private final LessonDao lessonDao = new LessonDao();
    private final ContentDao contentDao = new ContentDao();
    private final CourseDao courseDao = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final ProgressDao progressDao = new ProgressDao();
    private final StudyLogDao studyLogDao = new StudyLogDao();

    // ---------------------------------------------------------------- F14

    public LessonPage loadLessonPage(Long userId, boolean admin, long lessonId) {
        if (userId == null) throw new UnauthorizedException();
        final long uid = userId;
        return DbUtil.tx(c -> {
            LessonContext lc = requireViewable(c, lessonId, admin);
            boolean enrolled = enrollmentDao.exists(c, uid, lc.getCourseId());
            requireAccess(enrolled, lc, admin);

            LessonPage page = new LessonPage();
            page.setContext(lc);
            page.setEnrolled(enrolled);

            List<LessonOutlineItem> outline = lessonDao.outline(c, uid, lc.getCourseId());
            page.setOutline(outline);
            int done = 0;
            int idx = -1;
            for (int i = 0; i < outline.size(); i++) {
                LessonOutlineItem it = outline.get(i);
                if (it.isCompleted()) done++;
                if (it.getId() == lessonId) idx = i;
            }
            page.setCourseProgress(CourseProgress.of(outline.size(), done));
            if (idx >= 0) {
                LessonOutlineItem me = outline.get(idx);
                page.setLessonProgressStatus(me.getStatus());
                int pos = me.getLastPositionSeconds();
                // dang o gan cuoi bai thi cho xem lai tu dau thay vi dung o giay cuoi
                if (lc.getDurationSeconds() > 0 && pos >= lc.getDurationSeconds() - 5) pos = 0;
                page.setResumeSeconds(pos);
                if (idx > 0) page.setPrevLessonId(outline.get(idx - 1).getId());
                if (idx < outline.size() - 1) page.setNextLessonId(outline.get(idx + 1).getId());
            } else {
                page.setLessonProgressStatus(CourseProgress.LESSON_NOT_STARTED);
            }

            for (Material m : contentDao.materials(c, lessonId)) classify(page, m);
            page.setVocabulary(contentDao.vocabulary(c, lessonId));
            page.setGrammar(contentDao.grammar(c, lessonId));
            page.setKanji(contentDao.kanji(c, lessonId));

            studyLogDao.insertIfNotRecent(c, uid, lessonId, "lesson_view", 10);          // ghi study_logs luc mo bai
            if (enrolled) enrollmentDao.updateLastLesson(c, uid, lc.getCourseId(), lessonId);
            return page;
        });
    }

    /** Phan loai tai lieu. Video chi duoc phep khi qua VideoEmbed (domain cho phep); link tai lieu chi http/https. */
    private void classify(LessonPage page, Material m) {
        String type = m.getType() == null ? "" : m.getType().toLowerCase(Locale.ROOT);
        switch (type) {
            case "video":
                m.setYoutubeId(VideoEmbed.youtubeId(m.getMediaUrl()));
                m.setDirectVideoUrl(m.getYoutubeId() == null ? VideoEmbed.directVideoUrl(m.getMediaUrl()) : null);
                m.setMediaUrl(null);                                          // khong dua URL tho ra view
                page.getVideos().add(m);
                break;
            case "transcript":
                page.getTranscripts().add(m);
                break;
            case "text":
                page.getTexts().add(m);
                break;
            default:
                m.setSafeUrl(VideoEmbed.safeHttpUrl(m.getMediaUrl()));
                page.getDocuments().add(m);
        }
    }

    // ---------------------------------------------------------------- F16

    /** Luu giay dang xem (0..thoi luong). Khong cong xu. Tra ve so giay da luu. */
    public int savePosition(Long userId, boolean admin, long lessonId, int seconds) {
        if (userId == null) throw new UnauthorizedException();
        final long uid = userId;
        return DbUtil.tx(c -> {
            LessonContext lc = requireViewable(c, lessonId, admin);
            boolean enrolled = enrollmentDao.exists(c, uid, lc.getCourseId());
            requireAccess(enrolled, lc, admin);

            int s = Math.max(0, seconds);
            if (lc.getDurationSeconds() > 0) s = Math.min(s, lc.getDurationSeconds());
            progressDao.upsertPosition(c, uid, lessonId, s);
            if (enrolled) enrollmentDao.updateLastLesson(c, uid, lc.getCourseId(), lessonId);
            return s;
        });
    }

    // ---------------------------------------------------------------- F17

    /**
     * Chon bai de "Tiep tuc hoc":
     *  1) enrollments.last_lesson_id neu bai do CHUA hoan thanh;
     *  2) neu khong: bai chua hoan thanh dau tien sau bai gan nhat, roi toi bai chua hoan thanh dau tien cua khoa;
     *  3) hoan thanh het: ALL_DONE kem bai dau tien.
     * (Khac chu "uu tien last_lesson_id" cua dac ta mot chut: khi bai gan nhat da xong thi sang bai ke tiep, khong quay lai bai da xong.)
     */
    public NextLesson nextLesson(long userId, long courseId) {
        return DbUtil.read(c -> {
            Course course = courseDao.findById(c, courseId);
            if (course == null || !course.isPublished()) throw new NotFoundException("Khóa học không tồn tại");
            if (!enrollmentDao.exists(c, userId, courseId)) return NextLesson.of(NextLesson.NOT_ENROLLED, null);

            List<LessonOutlineItem> outline = lessonDao.outline(c, userId, courseId);
            if (outline.isEmpty()) return NextLesson.of(NextLesson.NO_LESSONS, null);

            Long last = enrollmentDao.lastLessonId(c, userId, courseId);
            int lastIdx = -1;
            if (last != null) {
                for (int i = 0; i < outline.size(); i++) {
                    if (outline.get(i).getId() == last) { lastIdx = i; break; }
                }
            }
            if (lastIdx >= 0 && !outline.get(lastIdx).isCompleted()) {
                return NextLesson.of(NextLesson.CONTINUE, outline.get(lastIdx).getId());
            }
            for (int i = lastIdx + 1; i < outline.size(); i++) {
                if (!outline.get(i).isCompleted()) return NextLesson.of(NextLesson.CONTINUE, outline.get(i).getId());
            }
            for (int i = 0; i < outline.size(); i++) {
                if (!outline.get(i).isCompleted()) return NextLesson.of(NextLesson.CONTINUE, outline.get(i).getId());
            }
            return NextLesson.of(NextLesson.ALL_DONE, outline.get(0).getId());
        });
    }

    // ---------------------------------------------------------------- chung

    private LessonContext requireViewable(Connection c, long lessonId, boolean admin) throws SQLException {
        LessonContext lc = lessonDao.findContext(c, lessonId);
        if (lc == null || (!lc.isPublished() && !admin)) throw new NotFoundException("Không tìm thấy bài học");
        return lc;
    }

    /** Vao duoc khi: da dang ky khoa chua bai, hoac bai xem thu (is_preview), hoac admin. */
    private void requireAccess(boolean enrolled, LessonContext lc, boolean admin) {
        if (!enrolled && !lc.isPreview() && !admin) {
            throw new ForbiddenException("Bạn cần đăng ký khóa học để xem bài này");
        }
    }
}
