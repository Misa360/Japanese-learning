-- seed_demo_f10_f18.sql  (TUY CHON - chi de thu F10-F18; khong nap len DB that)
-- Sau khi nap 10_seed_data.sql + migrations/002:
--   khoa 1 (N5, mien phi)            : 2 bai, bai 1 la bai xem thu
--   khoa 2 (N5, tien quyet = khoa 1)
--   khoa 3 (N4, mo khoa bang 20 xu)
-- Tai khoan: student@example.com (id 2, 50 xu), mat khau: password
SET NAMES utf8mb4;

UPDATE lessons SET is_preview = 1 WHERE id = 1;
UPDATE materials SET media_url = 'https://www.youtube.com/watch?v=dQw4w9WgXcQ' WHERE id = 1;   -- thay bang video that cua nhom

INSERT INTO materials (id, lesson_id, material_type, title, content_text, media_url, sort_order) VALUES
(3, 1, 'transcript', 'Transcript bài 1', 'あ (a) い (i) う (u) え (e) お (o)', NULL, 3),
(4, 2, 'document', 'Phiếu bài tập bài 2', NULL, 'https://example.com/files/bai2.pdf', 1);

INSERT INTO courses (id, level_id, title, slug, description, price, status, published_at, is_free, required_course_id, unlock_coins) VALUES
(2, 1, 'N5 Nâng cao (cần xong N5 Cấp tốc)', 'n5-nang-cao', 'Củng cố N5 trước khi lên N4.', 0.00, 'published', NOW(), 1, 1, 0),
(3, 2, 'N4 Căn bản (mở khóa bằng xu)', 'n4-can-ban', 'Khóa N4 mở bằng xu.', 0.00, 'published', NOW(), 0, NULL, 20);

INSERT INTO chapters (id, course_id, title, description, sort_order) VALUES
(3, 2, 'Chương 1', NULL, 1),
(4, 3, 'Chương 1', NULL, 1);

INSERT INTO lessons (id, chapter_id, title, slug, lesson_type, duration_seconds, sort_order, status, published_at) VALUES
(3, 3, 'Ôn tập N5 - bài 1', 'on-tap-n5-1', 'mixed', 300, 1, 'published', NOW()),
(4, 4, 'N4 - bài 1', 'n4-bai-1', 'mixed', 300, 1, 'published', NOW());
