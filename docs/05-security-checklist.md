# Checklist bảo mật (đã kiểm chứng bằng src/test/e2e/e2e.sh trừ mục ghi chú)

- [x] Mật khẩu BCrypt (cost 12 mặc định), không lưu rõ; hash PHP `$2y$` vẫn đăng nhập được.
- [x] Khóa tạm 5 lần sai / 15 phút (theo tài khoản và theo IP), thông báo lỗi chung, không lộ sai email hay sai mật khẩu.
- [x] Chống session fixation: `changeSessionId()` sau đăng nhập và đổi mật khẩu; đăng xuất hủy session.
- [x] Chống open redirect (`SafeRedirect`): chỉ đường dẫn nội bộ.
- [x] CSRF cho mọi POST/PUT/DELETE/PATCH (field hoặc header), so sánh thời gian không đổi.
- [x] Phân quyền mặc định từ chối; `/admin/*` 403 với người không phải admin; quyền sở hữu dữ liệu kiểm ở Service (chống IDOR).
- [x] Upload ảnh: kiểm tra nội dung thật, ghi lại ảnh, tên ngẫu nhiên, lưu ngoài webapp, phục vụ qua AvatarServlet với tên cố định mẫu.
- [x] Giới hạn tần suất đăng ký/đăng nhập theo IP (bộ nhớ từng node).
- [x] Cookie HttpOnly; SameSite=Lax qua context.xml (Tomcat 9).
- [ ] Bật `<secure>true</secure>` trong web.xml và HTTPS khi triển khai thật.
- [ ] CSP trong SecurityHeadersFilter chưa thử trên trình duyệt thật với YouTube API.
- [ ] Đổi mật khẩu không hủy các phiên khác của cùng người dùng (cần danh sách phiên – chưa làm).
