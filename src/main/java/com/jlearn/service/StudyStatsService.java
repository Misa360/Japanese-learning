package com.jlearn.service;

import com.jlearn.dao.StudyLogDao;
import com.jlearn.model.HistoryPage;
import com.jlearn.model.Pager;
import com.jlearn.util.DbUtil;

/** F09 - lich su hoc tap. */
public class StudyStatsService {
    public static final int PAGE_SIZE = 20;
    private final StudyLogDao studyLogDao = new StudyLogDao();

    /** Trang vuot qua cuoi thi tra trang cuoi; trang < 1 thi tra trang 1. Chi lich su cua userId (tu session). */
    public HistoryPage history(long userId, int requestedPage) {
        return DbUtil.read(c -> {
            long total = studyLogDao.countHistory(c, userId);
            Pager pager = Pager.of(requestedPage, PAGE_SIZE, total);
            HistoryPage hp = new HistoryPage();
            hp.setPager(pager);
            hp.setItems(studyLogDao.history(c, userId, pager.getOffset(), pager.getSize()));
            return hp;
        });
    }
}
