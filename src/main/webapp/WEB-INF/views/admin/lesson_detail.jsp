<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Nội dung bài học" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout jl-admin-lesson-detail">
  <div class="jl-admin-detail-top"><a href="${ctx}/admin/courses/${lesson.courseId}">← <c:out value="${lesson.courseTitle}"/></a><span class="jl-admin-status ${lesson.lessonStatus == 'published' ? 'jl-admin-status-published' : ''}"><c:out value="${lesson.lessonStatus}"/></span></div>
  <header class="jl-history-heading"><span class="jl-history-heading-icon" aria-hidden="true">▶</span><div><h1><c:out value="${lesson.title}"/></h1><p><c:out value="${lesson.durationSeconds}"/> giây<c:if test="${lesson.preview}"> · Bài xem trước</c:if></p></div></header>
  <section class="jl-admin-content-section">
    <div class="jl-admin-section-heading"><div><h2>Video và tài liệu</h2><p class="jl-muted">Nội dung học được gắn vào bài học này.</p></div><a class="jl-btn" href="${ctx}/admin/lesson-content/materials/new?lesson_id=${lesson.lessonId}">＋ Thêm video / tài liệu</a></div>
    <div class="jl-admin-content-list">
      <c:forEach var="material" items="${materials}"><article class="jl-card jl-admin-content-row">
        <span class="jl-admin-content-icon">▶</span><div class="jl-admin-content-info"><strong><c:out value="${material.title}"/></strong><small><c:out value="${material.type}"/><c:if test="${not empty material.mediaUrl}"> · <c:out value="${material.mediaUrl}"/></c:if></small></div>
        <a href="${ctx}/admin/lesson-content/materials/${material.id}?lesson_id=${lesson.lessonId}">Chỉnh sửa</a>
      </article></c:forEach>
      <c:if test="${empty materials}"><div class="jl-card jl-admin-inline-empty">Chưa có video hoặc tài liệu cho bài học này.</div></c:if>
    </div>
  </section>
  <section class="jl-admin-content-section">
    <div class="jl-admin-section-heading"><div><h2>Bài tập</h2><p class="jl-muted">Quản lý đề bài, câu hỏi và đáp án.</p></div><a class="jl-btn" href="${ctx}/admin/quizzes/new?type=lesson&amp;lesson_id=${lesson.lessonId}">＋ Tạo bài tập</a></div>
    <div class="jl-admin-content-list">
      <c:forEach var="quiz" items="${quizzes}"><article class="jl-card jl-admin-content-row">
        <span class="jl-admin-content-icon jl-admin-quiz-icon">✓</span><div class="jl-admin-content-info"><strong><c:out value="${quiz.title}"/></strong><small><c:out value="${quiz.questionCount}"/> câu hỏi · <c:out value="${quiz.status}"/></small></div>
        <a href="${ctx}/admin/quizzes/${quiz.id}?type=lesson&amp;lesson_id=${lesson.lessonId}">Quản lý bài tập →</a>
      </article></c:forEach>
      <c:if test="${empty quizzes}"><div class="jl-card jl-admin-inline-empty">Bài học chưa có bài tập.</div></c:if>
    </div>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
