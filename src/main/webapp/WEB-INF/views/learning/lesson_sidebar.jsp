<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isAdmin" value="${fn:toLowerCase(sessionScope.role) == 'admin'}"/>
<%-- Thanh ben: danh sach bai cua khoa kem dau hoan thanh. Dung request attribute "lessonPage" (LessonPage). --%>
<nav class="jl-sidebar" aria-label="Danh sách bài học">
  <h2><a href="${ctx}/courses/${lessonPage.context.courseId}"><c:out value="${lessonPage.context.courseTitle}"/></a></h2>
  <progress id="course-progress" max="100" value="${lessonPage.courseProgress.percent}"></progress>
  <p class="jl-muted"><span id="course-progress-text"><c:out value="${lessonPage.courseProgress.percent}"/>% (<c:out value="${lessonPage.courseProgress.completedLessons}"/>/<c:out value="${lessonPage.courseProgress.totalLessons}"/> bài)</span></p>

  <c:forEach var="item" items="${lessonPage.outline}">
    <c:if test="${item.chapterId != prevChapter}"><h3><c:out value="${item.chapterTitle}"/></h3></c:if>
    <c:set var="prevChapter" value="${item.chapterId}"/>
    <div class="jl-side-item ${item.completed ? 'is-done' : ''} ${item.id == lessonPage.context.lessonId ? 'is-current' : ''}" data-lesson-id="${item.id}">
      <span class="jl-tick"><c:choose><c:when test="${item.completed}">✓</c:when><c:when test="${item.status == 'in_progress'}">…</c:when><c:otherwise>○</c:otherwise></c:choose></span>
      <c:choose>
        <c:when test="${lessonPage.enrolled or item.preview or isAdmin}"><a href="${ctx}/learn/${item.id}"><c:out value="${item.title}"/></a></c:when>
        <c:otherwise>🔒 <c:out value="${item.title}"/></c:otherwise>
      </c:choose>
    </div>
  </c:forEach>
</nav>
