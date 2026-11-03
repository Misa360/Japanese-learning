-- 06_flashcard.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS flashcard_reviews;
DROP TABLE IF EXISTS user_flashcard_states;
DROP TABLE IF EXISTS flashcards;
DROP TABLE IF EXISTS flashcard_decks;

CREATE TABLE flashcard_decks (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NULL, -- NULL = Bộ thẻ chuẩn hệ thống
    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    jlpt_level_id BIGINT UNSIGNED NULL,
    visibility VARCHAR(20) NOT NULL DEFAULT 'private',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_flashcard_decks_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_flashcard_decks_level_id FOREIGN KEY (jlpt_level_id) REFERENCES levels (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_decks_user_visibility ON flashcard_decks (user_id, visibility);
CREATE INDEX idx_decks_level_visibility ON flashcard_decks (jlpt_level_id, visibility);

CREATE TABLE flashcards (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    deck_id BIGINT UNSIGNED NOT NULL,
    front_content TEXT NOT NULL,
    back_content TEXT NOT NULL,
    example_text TEXT NULL,
    audio_url VARCHAR(1000) NULL,
    sort_order INT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_flashcards_deck_id FOREIGN KEY (deck_id) REFERENCES flashcard_decks (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_flashcards_deck_order ON flashcards (deck_id, sort_order);

-- Tinh chỉnh: Bảng lưu trạng thái hiện tại (SRS) - Tối ưu truy vấn "Cần học hôm nay"
CREATE TABLE user_flashcard_states (
    user_id BIGINT UNSIGNED NOT NULL,
    flashcard_id BIGINT UNSIGNED NOT NULL,
    repetition_count INT UNSIGNED NOT NULL DEFAULT 0,
    interval_days INT UNSIGNED NOT NULL DEFAULT 0,
    ease_factor DECIMAL(4,2) NOT NULL DEFAULT 2.50,
    next_review_at DATETIME NOT NULL,
    last_reviewed_at DATETIME NULL,
    PRIMARY KEY (user_id, flashcard_id),
    CONSTRAINT fk_ufs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ufs_flashcard FOREIGN KEY (flashcard_id) REFERENCES flashcards (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ufs_user_next_review ON user_flashcard_states (user_id, next_review_at);

-- Lịch sử ôn tập (Review Log)
CREATE TABLE flashcard_reviews (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    flashcard_id BIGINT UNSIGNED NOT NULL,
    rating TINYINT UNSIGNED NOT NULL, -- 1: Again, 2: Hard, 3: Good, 4: Easy
    reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    next_review_at DATETIME NULL,
    interval_days INT UNSIGNED NOT NULL DEFAULT 0,
    ease_factor DECIMAL(4,2) NULL,
    CONSTRAINT fk_freviews_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_freviews_flashcard FOREIGN KEY (flashcard_id) REFERENCES flashcards (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_freviews_user_reviewed ON flashcard_reviews (user_id, flashcard_id, reviewed_at);

SET FOREIGN_KEY_CHECKS = 1;