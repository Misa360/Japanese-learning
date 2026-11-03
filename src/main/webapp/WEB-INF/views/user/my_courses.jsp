<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Khóa học của tôi" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container jl-user-layout">
  <aside><jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/></aside>
  <section>
    <h1>Khóa học của tôi</h1>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
    <nav class="jl-tabs" aria-label="Lọc theo trạng thái">
      <a class="${filter == 'all' ? 'is-active' : ''}" href="${ctx}/my-courses">Tất cả</a>
      <a class="${filter == 'not_started' ? 'is-active' : ''}" href="${ctx}/my-courses?status=not_started">Chưa học</a>
      <a class="${filter == 'in_progress' ? 'is-active' : ''}" href="${ctx}/my-courses?status=in_progress">Đang học</a>
      <a class="${filter == 'completed' ? 'is-active' : ''}" href="${ctx}/my-courses?status=completed">Hoàn thành</a>
    </nav>
    <c:choose>
      <c:when test="${empty courses}">
        <p class="jl-muted">Không có khóa học nào ở mục này. <a href="${ctx}/roadmap">Xem lộ trình để đăng ký</a>.</p>
      </c:when>
      <c:otherwise>
        <div class="jl-grid">
          <c:forEach var="co" items="${courses}">
            <article class="jl-card">
              <h3><a href="${ctx}/courses/${co.courseId}"><c:out value="${co.title}"/></a></h3>
              <p class="jl-muted"><c:out value="${co.levelCode}"/></p>
              <progress max="100" value="${co.progress.percent}"></progress>
              <p class="jl-muted"><c:out value="${co.progress.percent}"/>% (<c:out value="${co.progress.completedLessons}"/>/<c:out value="${co.progress.totalLessons}"/> bài) ·
                <c:choose>
                  <c:when test="${co.progress.status == 'completed'}">Hoàn thành</c:when>
                  <c:when test="${co.progress.status == 'in_progress'}">Đang học</c:when>
                  <c:otherwise>Chưa học</c:otherwise>
                </c:choose></p>
              <a class="jl-btn" href="${ctx}/courses/${co.courseId}/continue">Tiếp tục học</a>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
