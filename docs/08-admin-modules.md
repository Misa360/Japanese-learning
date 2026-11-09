# Khu quản trị: cây chức năng, route, file và bảng dữ liệu (F33–F39)

Tất cả servlet admin đã có `@WebServlet` và `doGet` hiển thị trang "Chưa triển khai" (khung). `AdminFilter` chặn mọi `/admin/*` với người không phải admin.
Mọi thao tác ghi phải ghi `audit_logs` (F38) và dùng cờ `status`, không xóa cứng khóa học / chương / bài học / người dùng / bộ thẻ.

## Cây điều hướng

```
Khóa học (F34)  /admin/courses
 ├─ Chương      /admin/chapters?course_id=
 └─ Bài học     /admin/lessons?course_id=                       (F35)
     ├─ Nội dung /admin/lesson-content?lesson_id=&tab=vocabulary|kanji|grammar|materials
     └─ Bài tập  /admin/quizzes?type=lesson&lesson_id=          (F36)
         └─ Câu hỏi /admin/questions?quiz_id=

Kho dùng chung (F35): /admin/vocabulary, /admin/kanji, /admin/grammar   (gắn vào bài qua lesson-content)

Luyện tập (F36, độc lập với bài tập trong bài học)
 ├─ Luyện đề     /admin/quizzes?type=practice      → Phần thi /admin/quiz-sections?quiz_id= → Câu hỏi /admin/questions?quiz_id=
 └─ Thi thử JLPT /admin/quizzes?type=jlpt_mock     → Bộ đề theo kỳ thi/cấp độ → Phần thi → Câu hỏi

Flashcard hệ thống (F39)
 └─ /admin/flashcard-decks → Thẻ /admin/flashcards?deck_id= → Nhập hàng loạt /admin/flashcards/import?deck_id=
```

## Servlet, service, DAO, view

| Mã | Chức năng | Servlet (controller/admin) | Service | DAO | View (views/admin) |
|---|---|---|---|---|---|
| F33 | Dashboard, người dùng | AdminDashboardServlet, AdminUserServlet | AdminService | UserDao | dashboard, user_list |
| F34 | Khóa học, chương | AdminCourseServlet, AdminChapterServlet | AdminService | CourseDao, ChapterDao | course_list/form, chapter_list/form |
| F35 | Bài học | AdminLessonServlet | AdminContentService | LessonDao | lesson_list/form |
| F35 | Nội dung bài học, tài liệu | AdminLessonContentServlet | AdminContentService | MaterialDao, ContentDao | lesson_content, material_form |
| F35 | Kho từ vựng / Kanji / ngữ pháp | AdminVocabularyServlet, AdminKanjiServlet, AdminGrammarServlet | AdminContentService | VocabularyDao, KanjiDao, GrammarDao | vocabulary_*, kanji_*, grammar_* |
| F36 | Đề (bài tập / luyện đề / thi thử) | AdminQuizServlet | AdminQuizService | QuizDao | quiz_list, quiz_form |
| F36 | Phần thi (thi thử) | AdminQuizSectionServlet | AdminQuizService | QuizSectionDao | quiz_section_list/form |
| F36 | Câu hỏi và đáp án | AdminQuestionServlet | AdminQuizService | QuestionDao, AnswerDao | question_list/form |
| F37 | Phần thưởng | AdminRewardServlet | AdminService | RewardDao | reward_list/form |
| F38 | Nhật ký hệ thống | AdminAuditServlet | AdminService | AuditLogDao | audit_log |
| F39 | Bộ flashcard hệ thống, thẻ, nhập hàng loạt | AdminFlashcardDeckServlet, AdminFlashcardServlet | AdminFlashcardService | FlashcardDeckDao, FlashcardDao, FlashcardImportDao | flashcard_deck_*, flashcard_list/form/import |

`AdminBaseServlet` cung cấp `render(...)` (đặt `pageTitle`, `activeNav`, forward) và `isForm(req)`. Menu quản trị: `partials/sidebar_admin.jsp`.

## Bảng dữ liệu bổ sung (`database/migrations/bosung.sql`)

| Thay đổi | Mục đích |
|---|---|
| `quizzes`: thêm `description`, `sort_order`, `show_answer_mode`, `shuffle_questions`, `created_by`; migration 003 thêm `exam_period`; CHECK `quiz_type` ∈ lesson / practice / jlpt_mock, `status` ∈ draft / published / archived | Dùng chung bảng nhưng tách rõ bài tập bài học, luyện đề và từng bộ thi thử; bộ thi thử có kỳ thi riêng |
| Bảng mới `quiz_sections` | Các phần của đề luyện và đề thi thử JLPT |
| `questions`: thêm `section_id`, `audio_url`, `image_url`; CHECK `question_type` | Câu hỏi thuộc phần thi, có nghe / hình |
| Bảng mới `quiz_attempt_sections` | Điểm từng phần của một lượt làm bài |
| `chapters`: thêm `status` (active / hidden) | Ẩn chương thay vì xóa |
| `flashcard_decks`: thêm `status`, `created_by`; CHECK `visibility` | Bộ thẻ hệ thống do admin quản lý, lưu trữ thay vì xóa |
| Bảng mới `flashcard_import_batches` | Nhật ký nhập thẻ hàng loạt |

Quy ước `quizzes.quiz_type`: `lesson` (bắt buộc có `lesson_id`, kiểm ở Service), `practice` và `jlpt_mock` (không gắn bài học).
Không CHECK `lesson_id` trong DB vì MySQL không cho CHECK trên cột có FK `ON DELETE SET NULL`.

`practice` và `jlpt_mock` là hai loại đề riêng trong `quizzes`; bài tập của bài học là `lesson`. `quiz_sections` chia phần cho đề luyện/thi thử, `questions.section_id` gắn câu hỏi vào phần. Kiểu phần gồm `vocabulary`, `kanji`, `grammar`, `reading`, `listening` và `other`. Migration `003_exam_catalog_metadata.sql` thêm `quizzes.exam_period` (lưu ngày đầu tháng, ví dụ `2024-12-01` để lọc kỳ `12/2024`) cho bộ đề thi thử. Thống kê số câu/lượt thi/điểm hiển thị trên thẻ được tính từ `questions` và `quiz_attempts`, không lưu lặp.

## Việc còn lại sau khi có khung

- `FlashcardDeckDao.findAvailable` (phía học viên) cần thêm điều kiện `d.status = 'active'`.
- Phía học viên: trang lịch sử làm bài đọc `quiz_attempts` theo `quiz_type`; thi thử ghi điểm từng phần vào `quiz_attempt_sections`.
