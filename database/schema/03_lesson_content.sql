-- 03_lesson_content.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS lesson_grammar;
DROP TABLE IF EXISTS lesson_kanji;
DROP TABLE IF EXISTS lesson_vocabulary;
DROP TABLE IF EXISTS grammar_points;
DROP TABLE IF EXISTS kanji;
DROP TABLE IF EXISTS vocabulary;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS lessons;
DROP TABLE IF EXISTS chapters;

CREATE TABLE chapters (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    sort_order INT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_chapters_course_order UNIQUE (course_id, sort_order),
    CONSTRAINT fk_chapters_course_id FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lessons (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    chapter_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NOT NULL,
    description TEXT NULL,
    lesson_type VARCHAR(30) NOT NULL,
    duration_seconds INT UNSIGNED NOT NULL DEFAULT 0,
    sort_order INT UNSIGNED NOT NULL,
    is_preview TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    published_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_lessons_chapter_slug UNIQUE (chapter_id, slug),
    CONSTRAINT uq_lessons_chapter_order UNIQUE (chapter_id, sort_order),
    CONSTRAINT fk_lessons_chapter_id FOREIGN KEY (chapter_id) REFERENCES chapters (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE materials (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT UNSIGNED NOT NULL,
    material_type VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content_text MEDIUMTEXT NULL,
    media_url VARCHAR(1000) NULL,
    thumbnail_url VARCHAR(1000) NULL,
    duration_seconds INT UNSIGNED NULL,
    sort_order INT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_materials_lesson_id FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_materials_lesson_order ON materials (lesson_id, sort_order);

-- Kho từ vựng dùng chung
CREATE TABLE vocabulary (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(100) NOT NULL,
    reading VARCHAR(150) NULL,
    meaning_vi VARCHAR(500) NOT NULL,
    example_ja TEXT NULL,
    example_vi TEXT NULL,
    audio_url VARCHAR(1000) NULL,
    jlpt_level_id BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vocabulary_level_id FOREIGN KEY (jlpt_level_id) REFERENCES levels (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_vocabulary_word ON vocabulary (word);
CREATE INDEX idx_vocabulary_level ON vocabulary (jlpt_level_id);

-- Kho Kanji dùng chung
CREATE TABLE kanji (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    character_kanji VARCHAR(10) NOT NULL,
    onyomi VARCHAR(255) NULL,
    kunyomi VARCHAR(255) NULL,
    meaning_vi VARCHAR(500) NOT NULL,
    stroke_count TINYINT UNSIGNED NULL,
    jlpt_level_id BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_kanji_character UNIQUE (character_kanji),
    CONSTRAINT fk_kanji_level_id FOREIGN KEY (jlpt_level_id) REFERENCES levels (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_kanji_level ON kanji (jlpt_level_id);

-- Kho Ngữ pháp dùng chung
CREATE TABLE grammar_points (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    pattern VARCHAR(255) NULL,
    explanation_vi MEDIUMTEXT NOT NULL,
    example_ja TEXT NULL,
    example_vi TEXT NULL,
    jlpt_level_id BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_grammar_points_level_id FOREIGN KEY (jlpt_level_id) REFERENCES levels (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_grammar_points_level ON grammar_points (jlpt_level_id);

-- Các bảng quan hệ nhiều-nhiều (Pivot)
CREATE TABLE lesson_vocabulary (
    lesson_id BIGINT UNSIGNED NOT NULL,
    vocabulary_id BIGINT UNSIGNED NOT NULL,
    sort_order INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (lesson_id, vocabulary_id),
    CONSTRAINT fk_lv_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_lv_vocabulary FOREIGN KEY (vocabulary_id) REFERENCES vocabulary (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lesson_kanji (
    lesson_id BIGINT UNSIGNED NOT NULL,
    kanji_id BIGINT UNSIGNED NOT NULL,
    sort_order INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (lesson_id, kanji_id),
    CONSTRAINT fk_lk_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_lk_kanji FOREIGN KEY (kanji_id) REFERENCES kanji (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lesson_grammar (
    lesson_id BIGINT UNSIGNED NOT NULL,
    grammar_id BIGINT UNSIGNED NOT NULL,
    sort_order INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (lesson_id, grammar_id),
    CONSTRAINT fk_lg_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_lg_grammar FOREIGN KEY (grammar_id) REFERENCES grammar_points (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;