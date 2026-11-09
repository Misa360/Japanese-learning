-- Bổ sung giới tính để chọn đúng ảnh biểu tượng tài khoản student_nam/student_nu.
-- Chạy một lần trên database hiện có; người dùng cũ cập nhật giới tính trong Hồ sơ.
ALTER TABLE users
    ADD COLUMN gender ENUM('male', 'female') NULL AFTER full_name;

-- =====================================================================================================
-- BO SUNG ADMIN (F34-F36, F39): quan ly khoa hoc > bai hoc > bai tap, luyen de, thi thu JLPT, flashcard he thong
-- Chay mot lan tren database hien co (install.sql da SOURCE file nay). Cac bang moi duoc DROP truoc khi tao lai.
-- Quy uoc quizzes.quiz_type:  'lesson'    = bai tap cua mot bai hoc (quizzes.lesson_id BAT BUOC co, kiem o Service)
--                             'practice'  = luyen de (lesson_id NULL)
--                             'jlpt_mock' = thi thu JLPT (lesson_id NULL, co the chia phan qua quiz_sections)
-- Khong CHECK lesson_id trong DB vi MySQL cam CHECK tren cot co FK ON DELETE SET NULL (loi 3823).
-- =====================================================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS quiz_attempt_sections;
DROP TABLE IF EXISTS quiz_sections;
DROP TABLE IF EXISTS flashcard_import_batches;

-- 1) De / bai tap: them mo ta, thu tu trong bai hoc, cach hien dap an, nguoi tao; rang buoc gia tri.
ALTER TABLE quizzes
    ADD COLUMN description TEXT NULL AFTER title,
    ADD COLUMN sort_order INT UNSIGNED NOT NULL DEFAULT 0 AFTER max_attempts,
    ADD COLUMN show_answer_mode VARCHAR(20) NOT NULL DEFAULT 'after_submit' AFTER sort_order, -- immediate (luyen de) | after_submit (thi thu)
    ADD COLUMN shuffle_questions TINYINT(1) NOT NULL DEFAULT 0 AFTER show_answer_mode,
    ADD COLUMN created_by BIGINT UNSIGNED NULL AFTER status,
    ADD CONSTRAINT fk_quizzes_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT chk_quizzes_type CHECK (quiz_type IN ('lesson', 'practice', 'jlpt_mock')),
    ADD CONSTRAINT chk_quizzes_status CHECK (status IN ('draft', 'published', 'archived')),
    ADD CONSTRAINT chk_quizzes_show_answer CHECK (show_answer_mode IN ('immediate', 'after_submit'));

CREATE INDEX idx_quizzes_lesson_order ON quizzes (lesson_id, sort_order);

-- 2) Phan thi cua luyen de / de thi thu JLPT (Tu vung, Chu Han, Ngu phap, Doc hieu, Nghe hieu...).
-- De khong can chia phan thi thi khong can ban ghi trong bang nay.
CREATE TABLE quiz_sections (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    section_type VARCHAR(30) NOT NULL DEFAULT 'other',
    time_limit_seconds INT UNSIGNED NULL,
    max_score DECIMAL(7,2) NULL,
    passing_score DECIMAL(7,2) NULL,
    sort_order INT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_sections_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_quiz_sections_type CHECK (section_type IN ('vocab_kanji', 'grammar', 'reading', 'listening', 'other'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_quiz_sections_quiz_order ON quiz_sections (quiz_id, sort_order);

-- 3) Cau hoi: gan vao phan thi (tuy chon), them file nghe / hinh anh, rang buoc loai cau hoi.
ALTER TABLE questions
    ADD COLUMN section_id BIGINT UNSIGNED NULL AFTER quiz_id,
    ADD COLUMN audio_url VARCHAR(1000) NULL AFTER content,
    ADD COLUMN image_url VARCHAR(1000) NULL AFTER audio_url,
    ADD CONSTRAINT fk_questions_section_id FOREIGN KEY (section_id) REFERENCES quiz_sections (id) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT chk_questions_type CHECK (question_type IN ('single_choice', 'multiple_choice', 'text_input'));

CREATE INDEX idx_questions_section ON questions (section_id);

-- 4) Diem theo tung phan thi cua mot luot lam bai (trang ket qua thi thu va lich su lam bai).
CREATE TABLE quiz_attempt_sections (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT UNSIGNED NOT NULL,
    section_id BIGINT UNSIGNED NOT NULL,
    score DECIMAL(7,2) NOT NULL DEFAULT 0.00,
    total_points DECIMAL(7,2) NOT NULL DEFAULT 0.00,
    correct_count INT UNSIGNED NOT NULL DEFAULT 0,
    passed TINYINT(1) NULL,
    CONSTRAINT uq_qas_attempt_section UNIQUE (attempt_id, section_id),
    CONSTRAINT fk_qas_attempt FOREIGN KEY (attempt_id) REFERENCES quiz_attempts (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_qas_section FOREIGN KEY (section_id) REFERENCES quiz_sections (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_qas_score CHECK (score >= 0 AND total_points >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5) Chuong: co an thay vi xoa cung (xoa chuong se keo theo xoa bai hoc).
ALTER TABLE chapters
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'active' AFTER sort_order,
    ADD CONSTRAINT chk_chapters_status CHECK (status IN ('active', 'hidden'));

-- 6) Bo flashcard he thong do admin quan ly: user_id NULL + visibility 'public' la bo chuan (da co san tu truoc).
ALTER TABLE flashcard_decks
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'active' AFTER visibility,
    ADD COLUMN created_by BIGINT UNSIGNED NULL AFTER status,
    ADD CONSTRAINT fk_flashcard_decks_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT chk_flashcard_decks_visibility CHECK (visibility IN ('private', 'public')),
    ADD CONSTRAINT chk_flashcard_decks_status CHECK (status IN ('active', 'archived'));

-- 7) Nhat ky nhap hang loat the flashcard (dan van ban / CSV): bao nhieu dong, bao nhieu loi.
CREATE TABLE flashcard_import_batches (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    deck_id BIGINT UNSIGNED NOT NULL,
    imported_by BIGINT UNSIGNED NULL,
    source_name VARCHAR(255) NULL,
    total_rows INT UNSIGNED NOT NULL DEFAULT 0,
    success_rows INT UNSIGNED NOT NULL DEFAULT 0,
    failed_rows INT UNSIGNED NOT NULL DEFAULT 0,
    error_details JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fimport_deck FOREIGN KEY (deck_id) REFERENCES flashcard_decks (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_fimport_user FOREIGN KEY (imported_by) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_fimport_deck_created ON flashcard_import_batches (deck_id, created_at);

SET FOREIGN_KEY_CHECKS = 1;
