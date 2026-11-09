<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Chi tiết khóa học" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout jl-admin-course-detail">
  <div class="jl-admin-detail-top"><a href="${ctx}/admin/courses">← Tất cả khóa học</a><a class="jl-btn jl-btn-outline" href="${ctx}/admin/courses/${course.id}/edit">Chỉnh sửa thông tin</a></div>
  <section class="jl-empty-banner jl-admin-course-hero"><div class="jl-empty-banner-content">
    <span class="jl-chip jl-chip-level"><c:out value="${course.levelCode}"/></span>
    <h1><c:out value="${course.title}"/></h1>
    <p><c:out value="${course.description}"/></p>
    <div class="jl-admin-course-meta">
    <span class="jl-admin-status ${course.status == 'published' ? 'jl-admin-status-published' : ''} ${course.status == 'archived' ? 'jl-admin-status-archived' : ''}"><c:out value="${course.status}"/></span>
      <span><c:choose><c:when test="${course.free}">Miễn phí</c:when><c:otherwise><c:out value="${course.unlockCoins}"/> xu</c:otherwise></c:choose></span>
      <span>Slug: <c:out value="${course.slug}"/></span>
      <c:if test="${not empty course.thumbnailPath}"><span>Ảnh bìa: <c:out value="${course.thumbnailPath}"/></span></c:if>
      <c:if test="${not empty course.requiredCourseTitle}"><span>Tiên quyết: <c:out value="${course.requiredCourseTitle}"/></span></c:if>
    </div>
  </div></section>
  <div class="jl-admin-section-heading"><div><h2>Bài học</h2><p class="jl-muted">Danh sách bài học trong khóa này, không cần quản lý qua chương.</p></div><a class="jl-btn" href="${ctx}/admin/lessons/new?course_id=${course.id}">＋ Thêm bài học</a></div>
  <div class="jl-admin-lesson-list">
    <c:forEach var="lesson" items="${courseLessons}" varStatus="loop">
      <a class="jl-card jl-admin-lesson-row" href="${ctx}/admin/lessons/${lesson.id}">
        <span class="jl-admin-lesson-number"><c:out value="${loop.count}"/></span>
        <span class="jl-admin-lesson-info"><strong><c:out value="${lesson.title}"/></strong><small><c:out value="${lesson.durationSeconds}"/> giây <c:if test="${lesson.preview}">· Bài xem trước</c:if></small></span>
        <span class="jl-admin-status <c:if test='${lesson.status == "published"}'>jl-admin-status-published</c:if>"><c:out value="${lesson.status}"/></span>
        <span class="jl-admin-lesson-arrow">›</span>
      </a>
    </c:forEach>
    <c:if test="${empty courseLessons}"><div class="jl-card jl-admin-course-empty"><strong>Khóa học chưa có bài học</strong><span>Thêm bài học đầu tiên để bắt đầu xây dựng nội dung khóa học.</span><a class="jl-btn" href="${ctx}/admin/lessons/new?course_id=${course.id}">Thêm bài học</a></div></c:if>
  </div>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
