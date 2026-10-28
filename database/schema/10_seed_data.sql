-- 10_seed_data.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. TẠO TÀI KHOẢN MẪU (Mật khẩu mặc định là: password123)
-- Hash bên dưới được tạo bằng bcrypt ($2y$10$...) chuẩn cho PHP/Laravel/NodeJS
INSERT INTO users (id, role_id, full_name, email, password_hash, coin_balance, status, email_verified_at) VALUES 
(1, 1, 'Quản Trị Viên', 'admin@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1000, 'active', NOW()),
(2, 2, 'Học Viên Demo', 'student@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 50, 'active', NOW());

-- 2. TẠO KHÓA HỌC MẪU (N5 Căn Bản)
INSERT INTO courses (id, level_id, title, slug, description, price, status, published_at) VALUES 
(1, 1, 'Tiếng Nhật N5 Cấp Tốc Dành Cho Người Mới Bắt Đầu', 'tieng-nhat-n5-cap-toc', 'Khóa học cung cấp đầy đủ bảng chữ cái, ngữ pháp và từ vựng sơ cấp.', 0.00, 'published', NOW());

-- 3. TẠO CHƯƠNG HỌC
INSERT INTO chapters (id, course_id, title, description, sort_order) VALUES 
(1, 1, 'Chương 1: Bảng chữ cái & Chào hỏi cơ bản', 'Làm quen với Hiragana, Katakana và phát âm chuẩn', 1),
(2, 1, 'Chương 2: Ngữ pháp bài 1 Minna no Nihongo', 'Đại từ nhân xưng và câu khẳng định/phủ định', 2);

-- 4. TẠO BÀI HỌC
INSERT INTO lessons (id, chapter_id, title, slug, lesson_type, duration_seconds, sort_order, status, published_at) VALUES 
(1, 1, 'Bài 1: Làm quen với Hiragana (A, I, U, E, O)', 'bai-1-hiragana-hang-a', 'mixed', 600, 1, 'published', NOW()),
(2, 2, 'Bài 2: Mẫu câu ~ wa ~ desu', 'bai-2-mau-cau-khang-dinh', 'mixed', 900, 1, 'published', NOW());

-- 5. TÀI LIỆU TRONG BÀI (Video / Bài đọc)
INSERT INTO materials (id, lesson_id, material_type, title, content_text, media_url, sort_order) VALUES 
(1, 1, 'video', 'Video hướng dẫn phát âm chuẩn hàng A', NULL, 'https://example.com/videos/hiragana-a.mp4', 1),
(2, 1, 'text', 'Bảng quy tắc viết nét chuẩn', '<p>Chú ý nét bút từ trái qua phải, từ trên xuống dưới...</p>', NULL, 2);

-- 6. TỪ VỰNG DÙNG CHUNG & LIÊN KẾT BÀI HỌC
INSERT INTO vocabulary (id, word, reading, meaning_vi, example_ja, example_vi, jlpt_level_id) VALUES 
(1, 'わたし', 'watashi', 'Tôi', 'わたしは田中です。', 'Tôi là Tanaka.', 1),
(2, 'せんせい', 'sensei', 'Thầy/Cô giáo', 'あのかたは先生です。', 'Vị kia là giáo viên.', 1);

INSERT INTO lesson_vocabulary (lesson_id, vocabulary_id, sort_order) VALUES 
(2, 1, 1),
(2, 2, 2);

-- 7. KHO KANJI & LIÊN KẾT BÀI HỌC
INSERT INTO kanji (id, character_kanji, onyomi, kunyomi, meaning_vi, stroke_count, jlpt_level_id) VALUES 
(1, '日', 'NICHI, JITSU', 'hi, -bi, -ka', 'Ngày, mặt trời, Nhật Bản', 4, 1),
(2, '本', 'HON', 'moto', 'Sách, nguồn gốc', 5, 1);

INSERT INTO lesson_kanji (lesson_id, kanji_id, sort_order) VALUES 
(2, 1, 1),
(2, 2, 2);

-- 8. KHO NGỮ PHÁP
INSERT INTO grammar_points (id, title, pattern, explanation_vi, example_ja, example_vi, jlpt_level_id) VALUES 
(1, 'Câu khẳng định', 'N1 は N2 です', 'Dùng để biểu thị N1 là N2. は đọc là wa.', 'わたしはがくせいです。', 'Tôi là học sinh.', 1);

INSERT INTO lesson_grammar (lesson_id, grammar_id, sort_order) VALUES 
(2, 1, 1);

-- 9. QUIZ TRẮC NGHIỆM MẪU
INSERT INTO quizzes (id, lesson_id, title, quiz_type, jlpt_level_id, time_limit_seconds, passing_score, status) VALUES 
(1, 2, 'Kiểm tra nhanh bài 2', 'lesson', 1, 300, 80.00, 'published');

INSERT INTO questions (id, quiz_id, question_type, content, points, sort_order) VALUES 
(1, 1, 'single_choice', 'Điền trợ từ thích hợp: わたし (___) 田中 です。', 5.00, 1),
(2, 1, 'single_choice', 'Từ "せんせい" có nghĩa là gì?', 5.00, 2);

INSERT INTO answers (id, question_id, content, is_correct, sort_order) VALUES 
-- Câu 1
(1, 1, 'は (wa)', 1, 1),
(2, 1, 'が (ga)', 0, 2),
(3, 1, 'を (o)', 0, 3),
-- Câu 2
(4, 2, 'Học sinh', 0, 1),
(5, 2, 'Giáo viên', 1, 2),
(6, 2, 'Bác sĩ', 0, 3);

-- 10. BỘ THẺ FLASHCARD MẪU
INSERT INTO flashcard_decks (id, user_id, title, description, jlpt_level_id, visibility) VALUES 
(1, NULL, '50 Từ vựng N5 cơ bản nhất', 'Bộ thẻ từ vựng chuẩn do hệ thống phát hành', 1, 'public');

INSERT INTO flashcards (id, deck_id, front_content, back_content, example_text, sort_order) VALUES 
(1, 1, 'わたし', 'Tôi (đại từ nhân xưng ngôi thứ nhất)', 'わたしはベトナム人です。', 1),
(2, 1, 'あなた', 'Bạn, anh/chị (ngôi thứ hai)', 'あなたはがくせいですか。', 2);

-- 11. HUY HIỆU (BADGES)
INSERT INTO badges (id, code, name, description, criteria_type, criteria_value, status) VALUES 
(1, 'first_lesson', 'Bước Chân Đầu Tiên', 'Hoàn thành bài học đầu tiên', 'lesson_complete', 1, 'active'),
(2, 'streak_7', 'Chăm Chỉ 7 Ngày', 'Điểm danh liên tiếp trong 7 ngày', 'checkin_streak', 7, 'active'),
(3, 'quiz_master', 'Chiến Binh Trắc Nghiệm', 'Đạt điểm tuyệt đối trong 5 bài quiz', 'perfect_quiz', 5, 'active');

-- 12. PHẦN THƯỞNG ĐỔI COIN (REWARDS)
INSERT INTO rewards (id, title, description, coin_cost, reward_type, stock_quantity, status) VALUES 
(1, 'Voucher giảm 50.000đ khóa N4', 'Áp dụng cho mọi học viên mua khóa học tiếp theo', 200, 'coupon', 100, 'active'),
(2, 'Avatar Khung Vàng Giới Hạn', 'Trang trí hồ sơ cá nhân đặc biệt', 500, 'avatar_frame', NULL, 'active');

SET FOREIGN_KEY_CHECKS = 1;