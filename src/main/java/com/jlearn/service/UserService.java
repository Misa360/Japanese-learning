package com.jlearn.service;

import com.jlearn.dao.LevelDao;
import com.jlearn.dao.StatsDao;
import com.jlearn.dao.UserDao;
import com.jlearn.dao.StudyLogDao;
import com.jlearn.exception.NotFoundException;
import com.jlearn.model.CourseProgress;
import com.jlearn.model.DashboardView;
import com.jlearn.model.Level;
import com.jlearn.model.MyCourseItem;
import com.jlearn.model.User;
import com.jlearn.util.DbUtil;
import com.jlearn.util.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** F06 dashboard, F07 khoa hoc cua toi, F08 ho so ca nhan. user_id luon tu session. */
public class UserService {
    public static final String FILTER_ALL = "all";

    private final UserDao userDao = new UserDao();
    private final LevelDao levelDao = new LevelDao();
    private final StatsDao statsDao = new StatsDao();
    private final StudyLogDao studyLogDao = new StudyLogDao();
    private final ProgressService progressService = new ProgressService();
    private final CoinService coinService = new CoinService();
    private final CheckinService checkinService = new CheckinService();
    private final BadgeService badgeService = new BadgeService();
    private final MotivationService motivationService = new MotivationService();
    private final AvatarService avatarService = new AvatarService();

    // ---------------------------------------------------------------- F06

    public DashboardView dashboard(long userId) {
        return DbUtil.read(c -> {
            User user = userDao.findById(c, userId);
            if (user == null) throw new NotFoundException("Không tìm thấy tài khoản");

            DashboardView d = new DashboardView();
            d.setUser(user);
            d.setCoinBalance(coinService.getBalance(c, userId));
            int[] streak = checkinService.streak(c, userId);
            d.setStreakDays(streak[0]);
            d.setCheckedInToday(streak[1] == 1);
            d.setCompletedLessons(statsDao.completedLessons(c, userId));
            d.setLatestBadge(badgeService.latest(c, userId));

            List<MyCourseItem> mine = myCourses(c, userId);
            int inProgress = 0, completed = 0;
            for (MyCourseItem it : mine) {
                if (CourseProgress.COURSE_IN_PROGRESS.equals(it.getProgress().getStatus())) inProgress++;
                else if (it.getProgress().isCompleted()) completed++;
            }
            d.setInProgressCourses(inProgress);
            d.setCompletedCourses(completed);

            Long courseId = studyLogDao.lastStudiedCourseId(c, userId);
            if (courseId != null) {
                for (MyCourseItem it : mine) {
                    if (it.getCourseId() == courseId) { d.setContinueCourseId(courseId); d.setContinueCourseTitle(it.getTitle()); }
                }
            }
            if (d.getContinueCourseId() == null) {                       // chua hoc bai nao: goi y khoa dang ky gan nhat chua xong
                for (MyCourseItem it : mine) {
                    if (!it.getProgress().isCompleted()) { d.setContinueCourseId(it.getCourseId()); d.setContinueCourseTitle(it.getTitle()); break; }
                }
            }
            d.setMotivation(motivationService.messageFor(d.getStreakDays(), d.isCheckedInToday(), d.getCompletedLessons()));
            return d;
        });
    }

    // ---------------------------------------------------------------- F07

    /** filter: all | not_started | in_progress | completed (gia tri la la thi coi nhu all). Trang thai tinh dong theo F18. */
    public List<MyCourseItem> myCourses(long userId, String filter) {
        List<MyCourseItem> all = DbUtil.read(c -> myCourses(c, userId));
        String f = normalizeFilter(filter);
        if (FILTER_ALL.equals(f)) return all;
        List<MyCourseItem> out = new ArrayList<>();
        for (MyCourseItem it : all) if (f.equals(it.getProgress().getStatus())) out.add(it);
        return out;
    }

    public static String normalizeFilter(String filter) {
        if (CourseProgress.COURSE_NOT_STARTED.equals(filter) || CourseProgress.COURSE_IN_PROGRESS.equals(filter)
                || CourseProgress.COURSE_COMPLETED.equals(filter)) return filter;
        return FILTER_ALL;
    }

    private List<MyCourseItem> myCourses(java.sql.Connection c, long userId) throws java.sql.SQLException {
        List<MyCourseItem> items = statsDao.enrolledCourses(c, userId);
        if (items.isEmpty()) return items;
        List<Long> ids = new ArrayList<>();
        for (MyCourseItem it : items) ids.add(it.getCourseId());
        Map<Long, CourseProgress> prog = progressService.progressForCourses(c, userId, ids);
        for (MyCourseItem it : items) it.setProgress(prog.get(it.getCourseId()));
        return items;
    }

    // ---------------------------------------------------------------- F08

    public User profile(long userId) {
        return DbUtil.read(c -> {
            User u = userDao.findById(c, userId);
            if (u == null) throw new NotFoundException("Không tìm thấy tài khoản");
            return u;
        });
    }

    public List<Level> levels() {
        return DbUtil.read(levelDao::findAll);
    }

    /**
     * Cap nhat ten hien thi + cap JLPT muc tieu (+ anh). Chi nhan cac truong nay; role/status/xu khong bao gio doi qua day.
     * @param avatar luong du lieu anh (null neu khong chon anh moi)
     * @return map loi theo o (rong = thanh cong)
     */
    public Map<String, String> updateProfile(long userId, String displayName, String targetLevelRaw, InputStream avatar) throws IOException {
        String name = displayName == null ? "" : displayName.trim();
        Validator v = new Validator();
        v.displayName("full_name", name);

        Long levelId = null;
        if (targetLevelRaw != null && !targetLevelRaw.trim().isEmpty()) {
            try { levelId = Long.valueOf(targetLevelRaw.trim()); } catch (NumberFormatException e) { v.error("target_level_id", "Cấp độ không hợp lệ"); }
        }
        if (levelId != null) {
            final Long lv = levelId;
            boolean exists = DbUtil.read(c -> levelDao.findById(c, lv) != null);
            if (!exists) v.error("target_level_id", "Cấp độ không hợp lệ");
        }

        String newAvatar = null;
        if (avatar != null) {
            try {
                newAvatar = avatarService.store(avatar);
            } catch (com.jlearn.exception.ValidationException e) {
                v.error("avatar", e.getMessage());
            }
        }
        if (!v.ok()) {
            avatarService.deleteQuietly(newAvatar);
            return v.errors();
        }

        final String avatarFile = newAvatar;
        final Long levelFinal = levelId;
        String oldAvatar = DbUtil.tx(c -> {
            User u = userDao.findById(c, userId);
            if (u == null) throw new NotFoundException("Không tìm thấy tài khoản");
            userDao.updateProfile(c, userId, name, levelFinal, avatarFile);
            return u.getAvatarPath();
        });
        if (avatarFile != null) avatarService.deleteQuietly(oldAvatar);
        return Collections.<String, String>emptyMap();
    }
}
