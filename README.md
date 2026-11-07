# JLearn – web học tiếng Nhật (JSP/Servlet, MVC)

Java 11, Servlet 4.0 / JSP + JSTL 1.2 (Tomcat 9), HikariCP, MySQL 8, jBCrypt.

## Chạy
1. Tạo DB: `CREATE DATABASE jlearn CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;` rồi `cd database && mysql -u root -p jlearn < install.sql`
   (thêm `seed/seed_demo_f10_f18.sql` để có dữ liệu thử). Tài khoản mẫu: `admin` / `student`, mật khẩu `password`.
2. Cấu hình `src/main/resources/app.properties` hoặc biến môi trường `JLEARN_*` (xem `.env.example`).
3. `mvn package` → chép `target/jlearn.war` vào Tomcat 9 → http://localhost:8080/jlearn/
   (Tomcat 10+: đổi `javax.*` → `jakarta.*`.)

Cấu trúc và chỗ sửa: `docs/07-where-to-edit.md`. Routes: `docs/03-routes.md`. Bảo mật: `docs/05-security-checklist.md`.
