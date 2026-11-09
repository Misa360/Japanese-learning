-- Tao DB tu dau:  cd database && mysql -u root -p jlearn < install.sql   (DB jlearn phai ton tai, utf8mb4)
SOURCE schema/01_users_roles.sql;
SOURCE schema/02_courses.sql;
SOURCE schema/03_lesson_content.sql;
SOURCE schema/04_learning.sql;
SOURCE schema/05_practice.sql;
SOURCE schema/06_flashcard.sql;
SOURCE schema/07_reward.sql;
SOURCE schema/08_motivation.sql;
SOURCE schema/09_admin.sql;
SOURCE migrations/001_users_username_target_level.sql;
SOURCE migrations/002_courses_unlock_enrollment_last_lesson.sql;
SOURCE migrations/bosung.sql;
SOURCE migrations/003_exam_catalog_metadata.sql;
SOURCE schema/10_seed_data.sql;
SOURCE schema/99_indexes_constraints.sql;
-- Du lieu thu cho F10-F18 (tuy chon, KHONG dung tren DB that): SOURCE seed/seed_demo_f10_f18.sql;
