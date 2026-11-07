# Cấu trúc thư mục & chỗ sửa (bản JSP/Servlet)

```
jlearn-web/
├─ pom.xml                         (thay composer.json)  WAR, Java 11, Tomcat 9 (javax.servlet)
├─ database/
│  ├─ install.sql                  tạo DB từ đầu (chạy 01..09, migrations, 10, 99)
│  ├─ schema/01..09, 10_seed, 99   schema gốc của nhóm
│  ├─ migrations/001, 002          cột bổ sung (username, target_level_id, is_free, required_course_id, unlock_coins, last_lesson_id)
│  └─ seed/seed_demo_f10_f18.sql   dữ liệu thử (tùy chọn)
├─ src/main/java/com/jlearn/
│  ├─ config/AppConfig             đọc app.properties + biến môi trường (thay config/*.php)
│  ├─ filter/                      Middleware → Filter (thứ tự trong web.xml)
│  ├─ controller/, controller/admin/   Controller → Servlet
│  ├─ service/                     nghiệp vụ, transaction
│  ├─ dao/                         truy vấn SQL (PreparedStatement) – thay phần truy vấn của Models/
│  ├─ model/                       POJO + view-model
│  ├─ util/                        DbUtil, Validator, CsrfTokens, Flash, WebUtil, PasswordHasher, ...
│  ├─ exception/                   AppException & con
│  └─ listener/AppContextListener  múi giờ, thư mục upload, đóng pool
├─ src/main/resources/app.properties
├─ src/main/webapp/
│  ├─ WEB-INF/web.xml, views/…     JSP (layouts/partials/…); META-INF/context.xml (SameSite)
│  └─ assets/{css,js,img}          thay public/assets
├─ src/test/java/…, src/test/e2e/  SmokeCheck, PureCheck, e2e.sh
├─ storage/{uploads,logs,cache}    ảnh upload nằm NGOÀI webapp (đường dẫn: upload.dir)
└─ deploy/nginx.conf
```

## PHP → Java

| PHP | Java |
|---|---|
| app/Controllers/AuthController | LoginServlet, RegisterServlet, LogoutServlet, ChangePasswordServlet |
| UserController | DashboardServlet, MyCoursesServlet, ProfileServlet, HistoryServlet |
| Core/Router, App, Request, Response, View, Session | container Servlet + @WebServlet + WebUtil |
| Core/Csrf, Middleware/CsrfMiddleware | util/CsrfTokens + filter/CsrfFilter |
| Core/Auth + Auth/Guest/AdminMiddleware | filter/AuthenticationFilter, GuestFilter, AdminFilter |
| Middleware/RateLimit, SecurityHeaders | filter/RateLimitFilter, SecurityHeadersFilter |
| Core/Database, Model | util/DbUtil (Hikari, tx) + dao/* |
| Core/Validator | util/Validator |
| Helpers/escape.php | `<c:out>` (JSTL) |
| Exceptions/* | exception/* |
| Models/* | model/* (POJO) + dao/*Dao. Không tạo class riêng cho Enrollment, LessonProgress, StudyLog, Lesson, Role, LoginAttempt (DAO trả view-model) |
| Views/layouts/main, auth | partials/header.jsp + footer.jsp (layouts/admin.jsp để dành cho khu admin) |
| routes/*.php | @WebServlet + docs/03-routes.md |
| config/*.php | src/main/resources/app.properties |

## Chức năng → file

Đã làm: F01–F04 (Auth*), F05 (filter/*), F06–F09 (User*, StudyStats*, AvatarService, views user/ + partials/pagination),
F10–F18 (Roadmap/Course/Enrollment/Lesson/Progress Service, views course/ learning/, assets/js/lesson-player.js).
F22 (flashcard) dùng `FlashcardServlet`, `FlashcardService`, ba DAO/model flashcard, hai JSP practice và `assets/js/flashcard.js`.
Bản **tối thiểu** để chạy được các phần sau (người phụ trách mở rộng): `CoinService` (F24), `CheckinService` (F28, chỉ đọc chuỗi ngày),
`BadgeService` (F30, chỉ đọc huy hiệu mới nhất), `MotivationService` (F32). Các hạng mục chưa triển khai khác vẫn giữ
khung/controller/service/DAO/model/view và chú thích TODO tại đúng vị trí để người phụ trách mở rộng.

## Quy tắc khi thêm chức năng
- Route mới công khai: thêm vào `PUBLIC_*` của AuthenticationFilter. Form POST: `<input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">`.
- Transaction đụng xu/enrollment/tiến độ: `UserDao.lockForUpdate` đầu tiên (tránh deadlock), rồi `CoinService.award/spend`.
- Dữ liệu in ra view luôn qua `<c:out>`; SQL chỉ PreparedStatement; user_id lấy từ session (`requireUserId`).
