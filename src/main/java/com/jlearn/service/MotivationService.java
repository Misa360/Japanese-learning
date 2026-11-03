package com.jlearn.service;

/**
 * F32 (thong diep dong vien). BAN TOI THIEU de dashboard co noi dung; nguoi phu trach F32 thay bang logic that
 * (theo chuoi ngay, tien do, muc tieu F31...).
 */
public class MotivationService {
    public String messageFor(int streakDays, boolean checkedInToday, int completedLessons) {
        if (completedLessons == 0) return "Hãy bắt đầu bài học đầu tiên của bạn hôm nay!";
        if (streakDays >= 7) return "Chuỗi " + streakDays + " ngày liên tiếp – tuyệt vời, hãy giữ vững nhịp học!";
        if (!checkedInToday) return "Đừng quên điểm danh hôm nay để giữ chuỗi ngày học nhé.";
        return "Mỗi ngày một chút, bạn đang tiến gần hơn đến mục tiêu JLPT.";
    }
}
