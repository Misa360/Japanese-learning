-- 02_courses.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS levels;

CREATE TABLE levels (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    name VARCHAR(100) NOT NULL,
    sort_order SMALLINT UNSIGNED NOT NULL,
    description TEXT NULL,
    CONSTRAINT uq_levels_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE courses (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    level_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NOT NULL,
    description TEXT NULL,
    thumbnail_path VARCHAR(500) NULL,
    price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    published_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_courses_slug UNIQUE (slug),
    CONSTRAINT fk_courses_level_id FOREIGN KEY (level_id) REFERENCES levels (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_courses_price CHECK (price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_courses_level_status ON courses (level_id, status);
CREATE INDEX idx_courses_status_published ON courses (status, published_at);

CREATE TABLE enrollments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    course_id BIGINT UNSIGNED NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME NULL,
    CONSTRAINT uq_enrollments_user_course UNIQUE (user_id, course_id),
    CONSTRAINT fk_enrollments_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enrollments_course_id FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_enrollments_course_status ON enrollments (course_id, status);
CREATE INDEX idx_enrollments_user_status ON enrollments (user_id, status);

-- Dữ liệu mẫu JLPT
INSERT INTO levels (id, code, name, sort_order, description) VALUES
(1, 'N5', 'JLPT N5 - Sơ cấp 1', 1, 'Trình độ căn bản nhất, bảng chữ cái và giao tiếp cơ bản'),
(2, 'N4', 'JLPT N4 - Sơ cấp 2', 2, 'Trình độ sơ cấp nâng cao'),
(3, 'N3', 'JLPT N3 - Trung cấp', 3, 'Cầu nối giữa sơ cấp và cao cấp'),
(4, 'N2', 'JLPT N2 - Thượng cấp', 4, 'Trình độ sử dụng trong công việc hàng ngày'),
(5, 'N1', 'JLPT N1 - Cao cấp', 5, 'Trình độ cao nhất, giao tiếp phức tạp và chuyên ngành');

SET FOREIGN_KEY_CHECKS = 1;