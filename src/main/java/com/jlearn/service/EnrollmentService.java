package com.jlearn.service;

import com.jlearn.dao.CourseDao;
import com.jlearn.dao.EnrollmentDao;
import com.jlearn.dao.UserDao;
import com.jlearn.exception.BusinessRuleException;
import com.jlearn.exception.NotFoundException;
import com.jlearn.model.Course;
import com.jlearn.model.EligibilityResult;
import com.jlearn.model.EnrollResult;
import com.jlearn.util.DbUtil;

/** F12 (dang ky khoa hoc) + phan tru xu cua F13. */
public class EnrollmentService {
    private final CourseDao courseDao = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final UserDao userDao = new UserDao();
    private final RoadmapService roadmapService = new RoadmapService();
    private final CoinService coinService = new CoinService();

    /**
     * Mot transaction: khoa ton tai & dang mo -> da dang ky chua -> canEnroll (F13) -> INSERT enrollment (UNIQUE chan trung)
     * -> tru xu (neu co). Hai request cung luc: request den sau gap loi UNIQUE, coi la "da dang ky", KHONG tru xu hai lan.
     * Khong du xu: CoinService nem BusinessRuleException, transaction rollback nen enrollment cung bi huy.
     */
    public EnrollResult enroll(long userId, long courseId) {
        try {
            return DbUtil.tx(c -> {
                if (!userDao.lockForUpdate(c, userId)) throw new NotFoundException("Không tìm thấy tài khoản");   // QUY TAC KHOA: xem UserDao
                Course course = courseDao.findById(c, courseId);
                if (course == null || !course.isPublished()) throw new NotFoundException("Khóa học không tồn tại hoặc chưa mở");

                if (enrollmentDao.exists(c, userId, courseId)) {
                    return EnrollResult.of(EnrollResult.ALREADY_ENROLLED, "Bạn đã đăng ký khóa học này rồi");
                }
                EligibilityResult check = roadmapService.canEnroll(c, userId, course);
                if (!check.isOk()) {
                    return EnrollResult.of(EnrollResult.BLOCKED, check.getReason());
                }
                if (!enrollmentDao.insert(c, userId, courseId)) {          // thua cuoc dua voi request khac
                    return EnrollResult.of(EnrollResult.ALREADY_ENROLLED, "Bạn đã đăng ký khóa học này rồi");
                }
                long cost = course.getCost();
                if (cost > 0) {
                    coinService.spend(c, userId, cost, "course_unlock", "course", courseId,
                            "course_unlock:" + courseId, "Mở khóa học: " + course.getTitle());
                }
                return EnrollResult.of(EnrollResult.ENROLLED, cost > 0
                        ? "Đã mở khóa và đăng ký khóa học (−" + cost + " xu)"
                        : "Đăng ký khóa học thành công");
            });
        } catch (BusinessRuleException e) {
            return EnrollResult.of(EnrollResult.BLOCKED, e.getMessage());   // vi du: khong du xu
        }
    }
}
