-- 05_practice.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS quiz_attempt_answers;
DROP TABLE IF EXISTS quiz_attempts;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS quizzes;

CREATE TABLE quizzes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT UNSIGNED NULL,
    title VARCHAR(200) NOT NULL,
    quiz_type VARCHAR(30) NOT NULL,
    jlpt_level_id BIGINT UNSIGNED NULL,
    time_limit_seconds INT UNSIGNED NULL,
    passing_score DECIMAL(5,2) NULL,
    max_attempts SMALLINT UNSIGNED NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_quizzes_lesson_id FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_quizzes_level_id FOREIGN KEY (jlpt_level_id) REFERENCES levels (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_quizzes_lesson_status ON quizzes (lesson_id, status);
CREATE INDEX idx_quizzes_type_level ON quizzes (quiz_type, jlpt_level_id, status);

CREATE TABLE questions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT UNSIGNED NOT NULL,
    question_type VARCHAR(30) NOT NULL, -- single_choice, multiple_choice, text_input
    content MEDIUMTEXT NOT NULL,
    explanation MEDIUMTEXT NULL,
    points DECIMAL(7,2) NOT NULL DEFAULT 1.00,
    sort_order INT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_questions_points CHECK (points >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_questions_quiz_order ON questions (quiz_id, sort_order);

CREATE TABLE answers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT UNSIGNED NOT NULL,
    content TEXT NOT NULL,
    is_correct TINYINT(1) NOT NULL DEFAULT 0,
    sort_order SMALLINT UNSIGNED NOT NULL,
    CONSTRAINT fk_answers_question_id FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_answers_question_order ON answers (question_id, sort_order);

CREATE TABLE quiz_attempts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    quiz_id BIGINT UNSIGNED NOT NULL,
    attempt_no INT UNSIGNED NOT NULL,
    score DECIMAL(7,2) NULL,
    total_points DECIMAL(7,2) NOT NULL,
    correct_count INT UNSIGNED NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL, -- in_progress, completed, abandoned
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at DATETIME NULL,
    duration_seconds INT UNSIGNED NULL,
    CONSTRAINT uq_quiz_attempts_user_quiz_no UNIQUE (user_id, quiz_id, attempt_no),
    CONSTRAINT fk_quiz_attempts_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_quiz_attempts_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_quiz_attempts_user_submitted ON quiz_attempts (user_id, submitted_at);
CREATE INDEX idx_quiz_attempts_quiz_status ON quiz_attempts (quiz_id, status);

CREATE TABLE quiz_attempt_answers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT UNSIGNED NOT NULL,
    question_id BIGINT UNSIGNED NOT NULL,
    selected_answer_ids JSON NULL, -- Mảng lưu các ID đáp án chọn [1, 2] cho câu hỏi nhiều đáp án hoặc đơn đáp án
    text_answer TEXT NULL,        -- Cho câu tự luận
    is_correct TINYINT(1) NULL,
    points_awarded DECIMAL(7,2) NOT NULL DEFAULT 0.00,
    answered_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_attempt_answers_attempt_question UNIQUE (attempt_id, question_id),
    CONSTRAINT fk_qaa_attempt FOREIGN KEY (attempt_id) REFERENCES quiz_attempts (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_qaa_question FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_qaa_points CHECK (points_awarded >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;