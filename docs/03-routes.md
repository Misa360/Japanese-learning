# Routes (thay cho routes/web.php và routes/admin.php)

Route khai báo bằng `@WebServlet` trên từng servlet. Mặc định MỌI đường dẫn cần đăng nhập; trang công khai khai báo ở
`filter/AuthenticationFilter` (`PUBLIC_EXACT`, `PUBLIC_PREFIX`, `PUBLIC_GET_REGEX`). `/admin/*` chỉ admin (AdminFilter).
Mọi POST phải có `csrf_token` (field) hoặc header `X-CSRF-Token`.

| Route | Servlet | F | Truy cập |
|---|---|---|---|
| GET / | HomeServlet | – | công khai |
| GET/POST /register | RegisterServlet | F01 | khách (GuestFilter) |
| GET/POST /login | LoginServlet | F02 | khách |
| POST /logout | LogoutServlet | F03 | đăng nhập |
| GET/POST /account/password | ChangePasswordServlet | F04 | đăng nhập |
| GET /dashboard | DashboardServlet | F06 | đăng nhập |
| GET /my-courses?status= | MyCoursesServlet | F07 | đăng nhập |
| GET/POST /profile | ProfileServlet | F08 | đăng nhập |
| GET /history?page= | HistoryServlet | F09 | đăng nhập |
| GET /media/avatar/{32hex}.png\|jpg | AvatarServlet | F08 | công khai |
| GET /roadmap | RoadmapServlet | F10 | công khai |
| GET /courses/{id} | CourseServlet | F11 | công khai |
| POST /courses/{id}/enroll | CourseServlet | F12 | đăng nhập |
| GET /courses/{id}/continue | CourseServlet | F17 | đăng nhập |
| GET /learn/{lessonId} | LearnServlet | F14 | đăng nhập + đã đăng ký hoặc bài xem thử |
| POST /learn/{lessonId}/complete | LearnServlet | F15 | đăng nhập + đã đăng ký |
| POST /learn/{lessonId}/position | LearnServlet | F16 | như F14 |
| GET /practice/flashcards?q= | FlashcardServlet | F22 | đăng nhập |
| POST /practice/flashcards | FlashcardServlet | F22 | đăng nhập + CSRF (tạo bộ riêng) |
| GET /practice/flashcards/{deckId} | FlashcardServlet | F22 | đăng nhập + bộ công khai hoặc của mình |
| POST /practice/flashcards/{deckId}/cards | FlashcardServlet | F22 | chủ bộ thẻ + CSRF |
| POST /practice/flashcards/{deckId}/review | FlashcardServlet | F22 | đăng nhập + bộ công khai hoặc của mình + CSRF |
| /checkin | CheckinServlet (khung) | F28 | |
| GET /practice/jlpt | JlptExamServlet | F23 | đăng nhập; trang hiện báo chưa có đề thi |
| /quiz | QuizServlet (khung) | F19–F21 | |
| /rewards, /coins | RewardServlet (khung) | F25–F27 | |
| /badges, /goals | BadgeServlet, GoalServlet (khung) | F30–F31 | |
| /admin/... | controller/admin/Admin*Servlet (khung) | F33–F39 | admin (AdminFilter) — chi tiết ở docs/08-admin-modules.md |
