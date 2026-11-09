-- 003: metadata cho bo de thi thu va phan thi luyen de.
-- Chay sau migrations/bosung.sql. Khong xoa hoac viet lai du lieu hien co.
-- exam_period luu ngay dau thang (VD 2024-12-01) de loc hien thi theo 12/2024.
ALTER TABLE quizzes
    ADD COLUMN exam_period DATE NULL AFTER jlpt_level_id,
    ADD INDEX idx_quizzes_mock_period (quiz_type, exam_period, jlpt_level_id, status),
    ADD CONSTRAINT chk_quizzes_exam_period CHECK (quiz_type = 'jlpt_mock' OR exam_period IS NULL);

-- Luyen de co the chon rieng Tu vung va Chu Han; bo de thi thu van dung cung bang phan thi.
ALTER TABLE quiz_sections
    DROP CONSTRAINT chk_quiz_sections_type;

ALTER TABLE quiz_sections
    ADD CONSTRAINT chk_quiz_sections_type CHECK (
        section_type IN ('vocabulary', 'kanji', 'vocab_kanji', 'grammar', 'reading', 'listening', 'other')
    );
