package com.jlearn.service;

import com.jlearn.dao.CheckinDao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * F28 (diem danh). BAN TOI THIEU: chi doc chuoi ngay cho dashboard (F06).
 * TODO F28: checkIn(userId) - ghi checkins + cong xu qua CoinService trong cung transaction, UNIQUE (user_id, checkin_date).
 */
public class CheckinService {
    private final CheckinDao checkinDao = new CheckinDao();

    /** @return {streakDays, checkedInToday(0/1)} */
    public int[] streak(Connection c, long userId) throws SQLException {
        return checkinDao.streak(c, userId);
    }
}
