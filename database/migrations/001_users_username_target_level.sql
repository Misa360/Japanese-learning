-- 001: F01/F02 can username (dang ky + dang nhap bang username); F08 can cap JLPT muc tieu.
-- Chay SAU 01_users_roles.sql va 02_courses.sql (can bang levels). install.sql da goi san.
SET NAMES utf8mb4;
ALTER TABLE users
    ADD COLUMN username VARCHAR(30) COLLATE utf8mb4_unicode_ci NULL AFTER role_id,
    ADD COLUMN target_level_id BIGINT UNSIGNED NULL AFTER avatar_path,
    ADD CONSTRAINT uq_users_username UNIQUE (username),
    ADD CONSTRAINT fk_users_target_level FOREIGN KEY (target_level_id) REFERENCES levels (id) ON DELETE SET NULL ON UPDATE CASCADE;
-- DB cu da co tai khoan mau: gan username (bo qua neu da co)
UPDATE users SET username = 'admin' WHERE id = 1 AND username IS NULL;
UPDATE users SET username = 'student' WHERE id = 2 AND username IS NULL;
