package com.jlearn.service;

import com.jlearn.dao.BadgeDao;
import com.jlearn.model.BadgeSummary;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * F30 (huy hieu). BAN TOI THIEU: chi doc huy hieu moi nhat cho dashboard (F06).
 * TODO F30: check(Connection c, long userId) - xet dieu kien va cap huy hieu; F15 se goi ham nay trong transaction hoan thanh bai.
 */
public class BadgeService {
    private final BadgeDao badgeDao = new BadgeDao();

    public BadgeSummary latest(Connection c, long userId) throws SQLException {
        return badgeDao.latest(c, userId);
    }
}
