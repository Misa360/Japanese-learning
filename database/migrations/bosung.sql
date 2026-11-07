-- Bổ sung giới tính để chọn đúng ảnh biểu tượng tài khoản student_nam/student_nu.
-- Chạy một lần trên database hiện có; người dùng cũ cập nhật giới tính trong Hồ sơ.
ALTER TABLE users
    ADD COLUMN gender ENUM('male', 'female') NULL AFTER full_name;
