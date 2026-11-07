package com.jlearn;

import com.jlearn.exception.AppException;
import com.jlearn.exception.ForbiddenException;
import com.jlearn.filter.AdminFilter;
import com.jlearn.filter.AuthenticationFilter;
import com.jlearn.filter.CsrfFilter;
import com.jlearn.filter.GuestFilter;
import com.jlearn.model.*;
import com.jlearn.service.*;
import com.jlearn.util.CsrfTokens;
import com.jlearn.util.DbUtil;
import com.jlearn.util.SessionKeys;
import com.jlearn.util.VideoEmbed;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.*;

/**
 * Kiem tra nhanh khong can JUnit/Tomcat: java -cp ... com.jlearn.SmokeCheck
 * Can DB da nap 01..10 + 11 + 12 (xem sql/) va bien moi truong JLEARN_DB_URL / USER / PASSWORD.
 * CANH BAO: xoa va tao lai du lieu enrollments/lesson_progress/coin_transactions cua user 2 va 3.
 */
public class SmokeCheck {
    static int pass = 0, fail = 0;

    static void check(String name, boolean cond) {
        if (cond) pass++; else { fail++; System.out.println("FAIL: " + name); }
    }

    static long scalar(String sql) throws Exception {
        try (Connection c = DbUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getLong(1) : -1;
        }
    }

    static void exec(String... sqls) throws Exception {
        try (Connection c = DbUtil.getConnection(); Statement st = c.createStatement()) {
            for (String s : sqls) st.execute(s);
        }
    }

    // ------------------------------------------------------------ mock servlet objects
    static class Rec { String redirect; Integer error; Integer status; StringBuilder body = new StringBuilder(); boolean passed; }

    static HttpSession session(Map<String, Object> attrs) {
        return (HttpSession) Proxy.newProxyInstance(SmokeCheck.class.getClassLoader(), new Class[]{HttpSession.class}, (p, m, a) -> {
            switch (m.getName()) {
                case "getAttribute": return attrs.get(a[0]);
                case "setAttribute": attrs.put((String) a[0], a[1]); return null;
                case "removeAttribute": attrs.remove(a[0]); return null;
                default: return null;
            }
        });
    }

    static HttpServletRequest req(String method, String servletPath, String pathInfo, Map<String, Object> sess,
                                  Map<String, String> params, Map<String, String> headers, boolean hasSession, Map<String, Object> reqAttrs) {
        final HttpSession[] holder = {hasSession ? session(sess) : null};
        return (HttpServletRequest) Proxy.newProxyInstance(SmokeCheck.class.getClassLoader(), new Class[]{HttpServletRequest.class}, (p, m, a) -> {
            switch (m.getName()) {
                case "getMethod": return method;
                case "getServletPath": return servletPath;
                case "getPathInfo": return pathInfo;
                case "getContextPath": return "/jlearn";
                case "getQueryString": return null;
                case "getCharacterEncoding": return "UTF-8";
                case "getParameter": return params.get(a[0]);
                case "getHeader": return headers.get(a[0]);
                case "getSession":
                    if (a.length == 1 && Boolean.FALSE.equals(a[0])) return holder[0];
                    if (holder[0] == null) holder[0] = session(sess);
                    return holder[0];
                case "setAttribute": reqAttrs.put((String) a[0], a[1]); return null;
                case "getAttribute": return reqAttrs.get(a[0]);
                default: return null;
            }
        });
    }

    static HttpServletResponse resp(Rec r) {
        final java.io.StringWriter sw = new java.io.StringWriter();
        final java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        return (HttpServletResponse) Proxy.newProxyInstance(SmokeCheck.class.getClassLoader(), new Class[]{HttpServletResponse.class}, (p, m, a) -> {
            switch (m.getName()) {
                case "sendRedirect": r.redirect = (String) a[0]; return null;
                case "sendError": r.error = (Integer) a[0]; return null;
                case "setStatus": r.status = (Integer) a[0]; return null;
                case "getWriter": return pw;
                default: return null;
            }
        });
    }

    static Rec run(Filter f, String method, String servletPath, String pathInfo, Map<String, Object> sess,
                   Map<String, String> params, Map<String, String> headers, boolean hasSession, Map<String, Object> reqAttrs) throws Exception {
        Rec r = new Rec();
        FilterChain chain = (rq, rs) -> r.passed = true;
        f.doFilter(req(method, servletPath, pathInfo, sess, params, headers, hasSession, reqAttrs), resp(r), chain);
        return r;
    }

    static Rec run(Filter f, String method, String path, Map<String, Object> sess, Map<String, String> params,
                   Map<String, String> headers, boolean hasSession) throws Exception {
        return run(f, method, path, null, sess, params, headers, hasSession, new HashMap<>());
    }

    static Map<String, Object> user(long id, String role) {
        Map<String, Object> m = new HashMap<>();
        m.put(SessionKeys.USER_ID, id);
        m.put(SessionKeys.ROLE, role);
        return m;
    }

    // ------------------------------------------------------------ tests
    public static void main(String[] args) throws Exception {
        filters();
        pure();
        db();
        System.out.println("PASS=" + pass + " FAIL=" + fail);
        DbUtil.shutdown();
        System.exit(fail == 0 ? 0 : 1);
    }

    static void filters() throws Exception {
        Map<String, String> none = new HashMap<>();
        AuthenticationFilter auth = new AuthenticationFilter();

        Rec r = run(auth, "GET", "/dashboard", new HashMap<>(), none, none, false);
        check("guest /dashboard -> redirect /login", "/jlearn/login".equals(r.redirect) && !r.passed);
        Map<String, Object> s = new HashMap<>();
        r = run(auth, "GET", "/learn", "/5", s, none, none, false, new HashMap<>());
        check("nextUrl duoc luu la duong dan noi bo", "/learn/5".equals(s.get(SessionKeys.NEXT_URL)));
        check("guest /roadmap public", run(auth, "GET", "/roadmap", new HashMap<>(), none, none, false).passed);
        check("guest / public", run(auth, "GET", "/", new HashMap<>(), none, none, false).passed);
        check("guest /assets/css public", run(auth, "GET", "/assets/css/a.css", new HashMap<>(), none, none, false).passed);
        check("guest GET /courses/7 public", run(auth, "GET", "/courses", "/7", new HashMap<>(), none, none, false, new HashMap<>()).passed);
        check("guest POST /courses/7/enroll bi chan", !run(auth, "POST", "/courses", "/7/enroll", new HashMap<>(), none, none, false, new HashMap<>()).passed);
        check("guest GET /courses/7/continue bi chan", !run(auth, "GET", "/courses", "/7/continue", new HashMap<>(), none, none, false, new HashMap<>()).passed);
        check("guest /admin bi chan", !run(auth, "GET", "/admin", new HashMap<>(), none, none, false).passed);
        check("guest /courses/7/../admin khong lot", !run(auth, "GET", "/courses", "/7x", new HashMap<>(), none, none, false, new HashMap<>()).passed);
        Map<String, String> ajax = new HashMap<>();
        ajax.put("X-Requested-With", "XMLHttpRequest");
        r = run(auth, "POST", "/learn", "/5/position", new HashMap<>(), none, ajax, false, new HashMap<>());
        check("AJAX chua dang nhap -> 401 JSON, khong redirect", Integer.valueOf(401).equals(r.status) && r.redirect == null);
        check("user /dashboard di tiep", run(auth, "GET", "/dashboard", user(2, "user"), none, none, true).passed);

        GuestFilter guest = new GuestFilter();
        check("guest vao /login di tiep", run(guest, "GET", "/login", new HashMap<>(), none, none, false).passed);
        r = run(guest, "GET", "/login", user(2, "user"), none, none, true);
        check("user da login vao /login -> /dashboard", "/jlearn/dashboard".equals(r.redirect));
        r = run(guest, "GET", "/register", user(1, "ADMIN"), none, none, true);
        check("admin da login vao /register -> /admin", "/jlearn/admin".equals(r.redirect));

        AdminFilter admin = new AdminFilter();
        r = run(admin, "GET", "/admin", user(2, "user"), none, none, true);
        check("user thuong vao /admin -> 403", Integer.valueOf(403).equals(r.error) && !r.passed);
        check("admin vao /admin di tiep", run(admin, "GET", "/admin", user(1, "admin"), none, none, true).passed);
        check("role ADMIN viet hoa van la admin", run(admin, "GET", "/admin", user(1, "ADMIN"), none, none, true).passed);
        r = run(admin, "GET", "/admin", new HashMap<>(), none, none, false);
        check("khach vao /admin -> /login", "/jlearn/login".equals(r.redirect));

        CsrfFilter csrf = new CsrfFilter();
        Map<String, Object> ses = user(2, "user");
        Map<String, Object> attrs = new HashMap<>();
        r = run(csrf, "GET", "/profile", null, ses, none, none, true, attrs);
        String token = (String) ses.get(SessionKeys.CSRF);
        check("GET tao token va dat request attribute", r.passed && token != null && token.equals(attrs.get("csrfToken")));
        r = run(csrf, "POST", "/profile", null, ses, none, none, true, new HashMap<>());
        check("POST khong token -> 403", Integer.valueOf(403).equals(r.error) && !r.passed);
        Map<String, String> bad = new HashMap<>();
        bad.put(CsrfTokens.PARAM, token + "x");
        check("POST token sai -> 403", Integer.valueOf(403).equals(run(csrf, "POST", "/profile", null, ses, bad, none, true, new HashMap<>()).error));
        Map<String, String> good = new HashMap<>();
        good.put(CsrfTokens.PARAM, token);
        check("POST token dung (param) di tiep", run(csrf, "POST", "/profile", null, ses, good, none, true, new HashMap<>()).passed);
        Map<String, String> hdr = new HashMap<>();
        hdr.put(CsrfTokens.HEADER, token);
        check("POST token dung (header) di tiep", run(csrf, "POST", "/learn", "/1/position", ses, none, hdr, true, new HashMap<>()).passed);
        check("POST khong co session -> 403", Integer.valueOf(403).equals(run(csrf, "POST", "/login", null, new HashMap<>(), good, none, false, new HashMap<>()).error));
        check("DELETE khong token -> 403", Integer.valueOf(403).equals(run(csrf, "DELETE", "/x", null, ses, none, none, true, new HashMap<>()).error));
        check("token khac session khong dung duoc", !CsrfTokens.matches(session(new HashMap<>()), token));
    }

    static void pure() {
        check("youtube watch", "dQw4w9WgXcQ".equals(VideoEmbed.youtubeId("https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=5")));
        check("youtu.be", "dQw4w9WgXcQ".equals(VideoEmbed.youtubeId("https://youtu.be/dQw4w9WgXcQ")));
        check("embed", "dQw4w9WgXcQ".equals(VideoEmbed.youtubeId("https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ")));
        check("http bi tu choi", VideoEmbed.youtubeId("http://www.youtube.com/watch?v=dQw4w9WgXcQ") == null);
        check("host gia bi tu choi", VideoEmbed.youtubeId("https://youtube.com.evil.com/watch?v=dQw4w9WgXcQ") == null);
        check("host gia 2", VideoEmbed.youtubeId("https://evil.com/youtube.com/watch?v=dQw4w9WgXcQ") == null);
        check("userinfo bi tu choi", VideoEmbed.youtubeId("https://www.youtube.com@evil.com/watch?v=dQw4w9WgXcQ") == null);
        check("javascript bi tu choi", VideoEmbed.youtubeId("javascript:alert(1)") == null);
        check("id sai do dai", VideoEmbed.youtubeId("https://youtu.be/abc") == null);
        check("id co ky tu nguy hiem", VideoEmbed.youtubeId("https://youtu.be/\"><script>x") == null);
        check("mp4 host ngoai danh sach bi tu choi", VideoEmbed.directVideoUrl("https://example.com/a.mp4") == null);
        check("safeHttpUrl javascript", VideoEmbed.safeHttpUrl("javascript:alert(1)") == null);
        check("safeHttpUrl data", VideoEmbed.safeHttpUrl("data:text/html,hi") == null);
        check("safeHttpUrl https", VideoEmbed.safeHttpUrl("https://example.com/f.pdf") != null);
        check("percent 1/3 -> 33", CourseProgress.percent(3, 1) == 33);
        check("percent 2/3 -> 66 (lam tron xuong)", CourseProgress.percent(3, 2) == 66);
        check("percent 6/7 -> 85", CourseProgress.percent(7, 6) == 85);
        check("percent 0/0 -> 0", CourseProgress.percent(0, 0) == 0);
        check("percent 3/3 -> 100", CourseProgress.percent(3, 3) == 100);
        check("status 0 bai -> not_started", CourseProgress.courseStatus(5, 0).equals("not_started"));
        check("status 2/5 -> in_progress", CourseProgress.courseStatus(5, 2).equals("in_progress"));
        check("status 5/5 -> completed", CourseProgress.courseStatus(5, 5).equals("completed"));
        check("khoa 0 bai khong phai completed", !CourseProgress.courseStatus(0, 0).equals("completed"));
        check("lesson pos>0 -> in_progress", CourseProgress.lessonStatus("not_started", 12).equals("in_progress"));

        Course c = new Course();
        c.setStatus("published"); c.setFree(false); c.setUnlockCoins(20); c.setRequiredCourseTitle("N5");
        check("evaluate: thieu xu", RoadmapService.evaluate(c, false, true, 5).getCode().equals(EligibilityResult.NOT_ENOUGH_COINS));
        check("evaluate: du xu", RoadmapService.evaluate(c, false, true, 20).isOk());
        check("evaluate: tien quyet uu tien truoc xu", RoadmapService.evaluate(c, false, false, 0).getCode().equals(EligibilityResult.REQUIRED_COURSE));
        check("evaluate: da dang ky", RoadmapService.evaluate(c, true, true, 99).getCode().equals(EligibilityResult.ALREADY_ENROLLED));
        c.setFree(true);
        check("evaluate: mien phi bo qua unlock_coins", RoadmapService.evaluate(c, false, true, 0).isOk() && c.getCost() == 0);
        c.setStatus("draft");
        check("evaluate: khoa nhap", RoadmapService.evaluate(c, false, true, 99).getCode().equals(EligibilityResult.NOT_AVAILABLE));
    }

    static void db() throws Exception {
        exec("SET FOREIGN_KEY_CHECKS=0", "DELETE FROM coin_transactions", "DELETE FROM study_logs", "DELETE FROM lesson_progress",
                "DELETE FROM enrollments", "DELETE FROM users WHERE id = 3",
                "UPDATE users SET coin_balance = 50 WHERE id = 2",
                "INSERT INTO users (id, role_id, full_name, email, password_hash, coin_balance, status) VALUES (3, 2, 'Nguoi it xu', 'poor@example.com', 'x', 5, 'active')",
                "SET FOREIGN_KEY_CHECKS=1");

        RoadmapService roadmap = new RoadmapService();
        EnrollmentService enroll = new EnrollmentService();
        ProgressService progress = new ProgressService();
        LessonService lessons = new LessonService();
        CourseService courses = new CourseService();

        // F10
        List<LevelBlock> guestMap = roadmap.buildRoadmap(null);
        check("roadmap khach: du 5 cap theo thu tu", guestMap.size() == 5 && "N5".equals(guestMap.get(0).getLevel().getCode()) && "N1".equals(guestMap.get(4).getLevel().getCode()));
        check("roadmap khach: N5 co 2 khoa, khong co eligibility", guestMap.get(0).getCourses().size() == 2 && guestMap.get(0).getCourses().get(0).getEligibility() == null);
        List<LevelBlock> userMap = roadmap.buildRoadmap(2L);
        CourseCard c2 = userMap.get(0).getCourses().get(1);
        check("roadmap: khoa 2 bi khoa vi tien quyet", c2.getEligibility().getCode().equals(EligibilityResult.REQUIRED_COURSE) && c2.getEligibility().getReason().contains("Cần hoàn thành"));
        check("roadmap: khoa 3 du xu de mo", userMap.get(1).getCourses().get(0).getEligibility().isOk());

        // F12 + F13
        check("enroll khoa 2 khi chua xong khoa 1 -> BLOCKED", enroll.enroll(2, 2).getStatus().equals(EnrollResult.BLOCKED));
        check("enroll khoa 1 -> ENROLLED", enroll.enroll(2, 1).getStatus().equals(EnrollResult.ENROLLED));
        check("enroll lai khoa 1 -> ALREADY", enroll.enroll(2, 1).getStatus().equals(EnrollResult.ALREADY_ENROLLED));
        check("chi co 1 ban ghi enrollment", scalar("SELECT COUNT(*) FROM enrollments WHERE user_id=2 AND course_id=1") == 1);
        check("khoa 3 user it xu -> BLOCKED va khong co enrollment", enroll.enroll(3, 3).getStatus().equals(EnrollResult.BLOCKED)
                && scalar("SELECT COUNT(*) FROM enrollments WHERE user_id=3") == 0 && scalar("SELECT coin_balance FROM users WHERE id=3") == 5);
        try { enroll.enroll(2, 999); check("khoa khong ton tai -> 404", false); } catch (AppException e) { check("khoa khong ton tai -> 404", e.getStatus() == 404); }

        // dang ky dong thoi khoa 3 (20 xu): chi 1 lan, tru xu 1 lan
        ExecutorService pool = Executors.newFixedThreadPool(6);
        List<Future<EnrollResult>> fs = new ArrayList<>();
        CountDownLatch go = new CountDownLatch(1);
        for (int i = 0; i < 6; i++) fs.add(pool.submit(() -> { go.await(); return enroll.enroll(2, 3); }));
        go.countDown();
        int enrolledCount = 0, alreadyCount = 0;
        for (Future<EnrollResult> f : fs) {
            String st = f.get().getStatus();
            if (st.equals(EnrollResult.ENROLLED)) enrolledCount++; else if (st.equals(EnrollResult.ALREADY_ENROLLED)) alreadyCount++;
        }
        check("6 request dong thoi: dung 1 ENROLLED, 5 ALREADY", enrolledCount == 1 && alreadyCount == 5);
        check("xu chi bi tru 1 lan (50 -> 30)", scalar("SELECT coin_balance FROM users WHERE id=2") == 30);
        check("1 giao dich course_unlock", scalar("SELECT COUNT(*) FROM coin_transactions WHERE user_id=2 AND transaction_type='course_unlock'") == 1);

        // F14 quyen
        try { lessons.loadLessonPage(2L, false, 3); check("chua dang ky khoa 2 xem bai 3 -> 403", false); }
        catch (ForbiddenException e) { check("chua dang ky khoa 2 xem bai 3 -> 403", true); }
        LessonPage preview = lessons.loadLessonPage(3L, false, 1);
        check("bai xem thu: nguoi chua dang ky van vao duoc", preview.getContext().isPreview() && !preview.isEnrolled());
        check("video youtube duoc nhan dien, URL tho bi go", preview.getVideos().size() == 1 && "dQw4w9WgXcQ".equals(preview.getVideos().get(0).getYoutubeId()) && preview.getVideos().get(0).getMediaUrl() == null);
        try { lessons.loadLessonPage(2L, false, 9999); check("bai khong ton tai -> 404", false); } catch (AppException e) { check("bai khong ton tai -> 404", e.getStatus() == 404); }
        LessonPage p2 = lessons.loadLessonPage(2L, false, 2);
        check("bai 2: co 2 tu vung, 1 ngu phap, 2 kanji, 1 tai lieu", p2.getVocabulary().size() == 2 && p2.getGrammar().size() == 1 && p2.getKanji().size() == 2 && p2.getDocuments().size() == 1);
        check("tai lieu http(s) co safeUrl", p2.getDocuments().get(0).getSafeUrl() != null);
        check("sidebar co 2 bai, prev/next dung", p2.getOutline().size() == 2 && p2.getPrevLessonId() != null && p2.getPrevLessonId() == 1 && p2.getNextLessonId() == null);
        check("study_logs duoc ghi khi mo bai", scalar("SELECT COUNT(*) FROM study_logs WHERE user_id=2 AND lesson_id=2 AND activity_type='lesson_view'") == 1);
        check("last_lesson_id = 2 sau khi mo bai 2", scalar("SELECT last_lesson_id FROM enrollments WHERE user_id=2 AND course_id=1") == 2);

        // F17 truoc khi hoan thanh bai nao
        NextLesson n = lessons.nextLesson(2, 1);
        check("nextLesson: bai gan nhat chua xong -> bai 2", n.getKind().equals(NextLesson.CONTINUE) && n.getLessonId() == 2);
        check("nextLesson: chua dang ky -> NOT_ENROLLED", lessons.nextLesson(3, 1).getKind().equals(NextLesson.NOT_ENROLLED));

        // F16
        check("savePosition kep ve thoi luong (900)", lessons.savePosition(2L, false, 2, 5000) == 900);
        check("savePosition 40s", lessons.savePosition(2L, false, 2, 40) == 40);
        check("lesson_progress in_progress + 40s", scalar("SELECT COUNT(*) FROM lesson_progress WHERE user_id=2 AND lesson_id=2 AND status='in_progress' AND last_position_seconds=40") == 1);
        check("bai gan het: cho xem lai tu dau", lessons.savePosition(2L, false, 2, 898) == 898 && lessons.loadLessonPage(2L, false, 2).getResumeSeconds() == 0);
        try { lessons.savePosition(3L, false, 3, 10); check("savePosition bai cua khoa chua dang ky -> 403", false); } catch (ForbiddenException e) { check("savePosition bai cua khoa chua dang ky -> 403", true); }

        // F15
        try { progress.completeLesson(3, 1); check("hoan thanh bai khi chua dang ky -> 403", false); } catch (ForbiddenException e) { check("hoan thanh bai khi chua dang ky -> 403", true); }
        CompletionResult r1 = progress.completeLesson(2, 1);
        check("hoan thanh bai 1: +10 xu, 50%", !r1.isAlreadyCompleted() && r1.getCoinsAwarded() == 10 && r1.getProgress().getPercent() == 50);
        CompletionResult r1b = progress.completeLesson(2, 1);
        check("hoan thanh lai: khong cong them", r1b.isAlreadyCompleted() && r1b.getCoinsAwarded() == 0);
        check("xu 30 -> 40", scalar("SELECT coin_balance FROM users WHERE id=2") == 40);
        check("saved position khong lam mat trang thai completed", lessons.savePosition(2L, false, 1, 30) == 30 && scalar("SELECT COUNT(*) FROM lesson_progress WHERE user_id=2 AND lesson_id=1 AND status='completed'") == 1);
        check("khoa 1 chua hoan thanh -> chua co completed_at", scalar("SELECT COUNT(*) FROM enrollments WHERE user_id=2 AND course_id=1 AND completed_at IS NULL") == 1);

        // hoan thanh dong thoi bai 2
        pool = Executors.newFixedThreadPool(6);
        List<Future<CompletionResult>> cf = new ArrayList<>();
        CountDownLatch go2 = new CountDownLatch(1);
        for (int i = 0; i < 6; i++) cf.add(pool.submit(() -> { go2.await(); return progress.completeLesson(2, 2); }));
        go2.countDown();
        long awarded = 0; int firsts = 0;
        for (Future<CompletionResult> f : cf) { CompletionResult r = f.get(); awarded += r.getCoinsAwarded(); if (!r.isAlreadyCompleted()) firsts++; }
        pool.shutdown();
        check("6 request hoan thanh cung luc: 1 lan duoc cong, tong 10 xu", firsts == 1 && awarded == 10);
        check("xu 40 -> 50", scalar("SELECT coin_balance FROM users WHERE id=2") == 50);
        check("dung 2 giao dich lesson_complete", scalar("SELECT COUNT(*) FROM coin_transactions WHERE user_id=2 AND transaction_type='lesson_complete'") == 2);
        check("balance_after khop so du", scalar("SELECT balance_after FROM coin_transactions WHERE user_id=2 ORDER BY id DESC LIMIT 1") == 50);
        check("khoa 1 hoan thanh 100%", progress.courseProgress(2, 1).getPercent() == 100 && progress.courseProgress(2, 1).isCompleted());
        check("completed_at duoc ghi", scalar("SELECT COUNT(*) FROM enrollments WHERE user_id=2 AND course_id=1 AND completed_at IS NOT NULL") == 1);

        // F17 sau khi xong het
        NextLesson done = lessons.nextLesson(2, 1);
        check("nextLesson: xong het -> ALL_DONE + bai dau", done.getKind().equals(NextLesson.ALL_DONE) && done.getLessonId() == 1);

        // F13 mo khoa 2 sau khi xong khoa 1
        check("khoa 2 mo duoc sau khi xong khoa 1", enroll.enroll(2, 2).getStatus().equals(EnrollResult.ENROLLED));
        check("khoa 2 dang ky xong, khong ton xu", scalar("SELECT coin_balance FROM users WHERE id=2") == 50);

        // F11
        CourseDetail d = courses.getDetail(2L, false, 1);
        check("chi tiet khoa 1: 2 chuong, 2 bai, 100%, da dang ky", d.getChapters().size() == 2 && d.getLessonCount() == 2 && d.getProgress().getPercent() == 100 && d.isEnrolled());
        check("chi tiet khoa: tai lieu chi co tieu de (khong URL)", !d.getMaterials().isEmpty());
        CourseDetail g = courses.getDetail(null, false, 1);
        check("khach xem duoc chi tiet khoa, khong co dau hoan thanh", !g.isLoggedIn() && g.getProgress().getPercent() == 0 && g.getEligibility() == null);
        exec("UPDATE courses SET status='draft' WHERE id=3");
        try { courses.getDetail(2L, false, 3); check("khoa nhap -> 404 voi nguoi thuong", false); } catch (AppException e) { check("khoa nhap -> 404 voi nguoi thuong", e.getStatus() == 404); }
        check("khoa nhap: admin van xem duoc", courses.getDetail(1L, true, 3).getCourse().getId() == 3);
        check("khoa nhap khong hien tren roadmap", roadmap.buildRoadmap(2L).get(1).getCourses().isEmpty());
        exec("UPDATE courses SET status='published' WHERE id=3");

        // an bai -> % tu dung (F18)
        exec("UPDATE lessons SET status='draft' WHERE id=2");
        check("an 1 bai: % tinh lai tren bai dang mo (1/1)", progress.courseProgress(2, 1).getTotalLessons() == 1 && progress.courseProgress(2, 1).getPercent() == 100);
        exec("UPDATE lessons SET status='published' WHERE id=2");
        check("bai an khong vao duoc", true);
    }
}
