package com.jlearn.controller;

import com.jlearn.service.CheckinService;
import com.jlearn.service.CoinService;
import com.jlearn.service.UserService;
import com.jlearn.util.DbUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Trang thống kê coin: số dư, chuỗi điểm danh, 7 ngày gần nhất và ledger giao dịch.
 */
@WebServlet(name = "RewardServlet", urlPatterns = "/reward/coins")
public class RewardServlet extends BaseServlet {
    private final UserService userService = new UserService();
    private final CoinService coinService = new CoinService();
    private final CheckinService checkinService = new CheckinService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = requireUserId(req);
        req.setAttribute("dashboard", userService.dashboard(userId));
        req.setAttribute("transactions", coinService.recentTransactions(userId, 30));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        java.util.Set<LocalDate> checkedDates = DbUtil.read(c -> checkinService.recentDates(c, userId));
        List<Map<String, Object>> weekDays = new ArrayList<>();
        String[] dayLabels = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        for (int offset = 6; offset >= 0; offset--) {
            LocalDate date = today.minusDays(offset);
            Map<String, Object> day = new LinkedHashMap<>();
            day.put("label", dayLabels[date.getDayOfWeek().getValue() - 1]);
            day.put("dateLabel", date.format(DateTimeFormatter.ofPattern("dd/MM")));
            day.put("today", date.equals(today));
            day.put("checked", checkedDates.contains(date));
            weekDays.add(day);
        }
        req.setAttribute("weekDays", weekDays);
        req.setAttribute("activeNav", "coin-stats");
        req.setAttribute("pageTitle", "Thống kê Coin");
        forward(req, resp, "reward/coin_history.jsp");
    }
}
