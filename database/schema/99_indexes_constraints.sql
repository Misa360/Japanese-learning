-- 99_indexes_constraints.sql
-- File này dùng để rà soát hoặc bổ sung ràng buộc toàn cục sau khi nạp xong dữ liệu

-- Đảm bảo điểm kết thúc khóa học/quiz phải lớn hơn hoặc bằng thời gian bắt đầu (nếu có)
ALTER TABLE enrollments 
    ADD CONSTRAINT chk_enrollments_dates CHECK (completed_at IS NULL OR completed_at >= enrolled_at);

ALTER TABLE quiz_attempts 
    ADD CONSTRAINT chk_quiz_attempts_dates CHECK (submitted_at IS NULL OR submitted_at >= started_at);

ALTER TABLE rewards
    ADD CONSTRAINT chk_rewards_active_dates CHECK (ends_at IS NULL OR starts_at IS NULL OR ends_at >= starts_at);