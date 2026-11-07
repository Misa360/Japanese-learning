<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isAdmin" value="${fn:toLowerCase(sessionScope.role) == 'admin'}"/>
<c:set var="course" value="${detail.course}"/>
<c:set var="pageTitle" value="${course.title}" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container">
  <p class="jl-muted"><a href="${ctx}/roadmap">← Lộ trình</a><c:if test="${not empty detail.level}"> · <c:out value="${detail.level.code}"/></c:if></p>
  <h1><c:out value="${course.title}"/></h1>
  <c:if test="${course.status != 'published'}"><p class="jl-flash jl-flash-info">Khóa học chưa công khai (chỉ quản trị viên xem được).</p></c:if>
  <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

  <p><c:out value="${course.description}"/></p>
  <p class="jl-badges">
    <c:choose>
      <c:when test="${course.cost > 0}"><span class="jl-badge jl-badge-coin">Mở khóa bằng <c:out value="${course.cost}"/> xu</span></c:when>
      <c:otherwise><span class="jl-badge jl-badge-free">Miễn phí</span></c:otherwise>
    </c:choose>
    <span class="jl-badge"><c:out value="${detail.lessonCount}"/> bài học</span>
    <c:if test="${not empty course.requiredCourseId}">
      <span class="jl-badge">Cần xong: <c:out value="${course.requiredCourseTitle}"/></span>
    </c:if>
  </p>

  <section class="jl-actions">
    <c:choose>
      <c:when test="${detail.enrolled}">
        <progress max="100" value="${detail.progress.percent}"></progress>
        <span class="jl-muted"><c:out value="${detail.progress.percent}"/>% (<c:out value="${detail.progress.completedLessons}"/>/<c:out value="${detail.progress.totalLessons}"/> bài)</span>
        <a class="jl-btn" href="${ctx}/courses/${course.id}/continue">Tiếp tục học</a>
      </c:when>
      <c:when test="${not detail.loggedIn}">
        <a class="jl-btn" href="${ctx}/login">Đăng nhập để đăng ký</a>
      </c:when>
      <c:when test="${detail.eligibility.ok}">
        <form method="post" action="${ctx}/courses/${course.id}/enroll">
          <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
          <button type="submit" class="jl-btn">
            <c:choose>
              <c:when test="${detail.eligibility.cost > 0}">Mở khóa và đăng ký (<c:out value="${detail.eligibility.cost}"/> xu)</c:when>
              <c:otherwise>Đăng ký học</c:otherwise>
            </c:choose>
          </button>
        </form>
      </c:when>
      <c:otherwise>
        <p class="jl-locked"><c:out value="${detail.eligibility.reason}"/></p>
      </c:otherwise>
    </c:choose>
  </section>

  <h2>Nội dung khóa học</h2>
  <c:choose>
    <c:when test="${empty detail.chapters}"><p class="jl-muted">Khóa học chưa có chương nào.</p></c:when>
    <c:otherwise>
      <c:forEach var="ch" items="${detail.chapters}">
        <section class="jl-chapter">
          <h3><c:out value="${ch.title}"/></h3>
          <c:if test="${not empty ch.description}"><p class="jl-muted"><c:out value="${ch.description}"/></p></c:if>
          <ul class="jl-lessons">
            <c:forEach var="ls" items="${ch.lessons}">
              <li class="${ls.status == 'completed' ? 'is-done' : ''}">
                <span class="jl-tick"><c:choose><c:when test="${ls.status == 'completed'}">✓</c:when><c:when test="${ls.status == 'in_progress'}">…</c:when><c:otherwise>○</c:otherwise></c:choose></span>
                <c:choose>
                  <c:when test="${detail.enrolled or ls.preview or isAdmin}">
                    <a href="${ctx}/learn/${ls.id}"><c:out value="${ls.title}"/></a>
                  </c:when>
                  <c:otherwise><c:out value="${ls.title}"/></c:otherwise>
                </c:choose>
                <c:if test="${ls.preview}"><span class="jl-badge">Xem thử</span></c:if>
                <c:if test="${ls.durationSeconds > 0}"><span class="jl-muted">· <fmt:formatNumber value="${ls.durationSeconds / 60}" maxFractionDigits="0"/> phút</span></c:if>
              </li>
            </c:forEach>
          </ul>
        </section>
      </c:forEach>
    </c:otherwise>
  </c:choose>

  <c:if test="${not empty detail.materials}">
    <h2>Tài liệu</h2>
    <p class="jl-muted">Nội dung tài liệu mở trong từng bài học sau khi đăng ký.</p>
    <ul>
      <c:forEach var="mt" items="${detail.materials}">
        <li><c:out value="${mt.title}"/> <span class="jl-muted">(<c:out value="${mt.type}"/> · <c:out value="${mt.lessonTitle}"/>)</span></li>
      </c:forEach>
    </ul>
  </c:if>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
