-- 002: F13 (khoa mien phi + dieu kien mo khoa) va F16/F17 (bai hoc gan nhat). Chay SAU 03/04 (can bang lessons).
--   is_free = 1 -> dang ky khong ton xu; is_free = 0 -> ton unlock_coins xu (tru qua CoinService, cung transaction voi enrollment)
--   required_course_id -> phai hoan thanh khoa nay truoc (NULL = khong dieu kien)
-- Cot price (DECIMAL) co san KHONG duoc dung cho logic xu.
SET NAMES utf8mb4;
ALTER TABLE courses
    ADD COLUMN is_free TINYINT(1) NOT NULL DEFAULT 1 AFTER price,
    ADD COLUMN required_course_id BIGINT UNSIGNED NULL AFTER is_free,
    ADD COLUMN unlock_coins BIGINT UNSIGNED NOT NULL DEFAULT 0 AFTER required_course_id,
    ADD CONSTRAINT fk_courses_required_course FOREIGN KEY (required_course_id)
        REFERENCES courses (id) ON DELETE SET NULL ON UPDATE CASCADE;
CREATE INDEX idx_courses_required ON courses (required_course_id);

ALTER TABLE enrollments
    ADD COLUMN last_lesson_id BIGINT UNSIGNED NULL AFTER completed_at,
    ADD CONSTRAINT fk_enrollments_last_lesson FOREIGN KEY (last_lesson_id)
        REFERENCES lessons (id) ON DELETE SET NULL ON UPDATE CASCADE;
