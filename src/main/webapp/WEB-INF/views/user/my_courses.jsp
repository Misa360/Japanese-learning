<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Lớp học của tôi" scope="request"/>
<c:set var="activeNav" value="my-courses" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container jl-user-layout jl-my-courses-layout">
  <aside>
    <jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/>
  </aside>

  <section class="jl-my-courses-page">
    <nav class="jl-my-courses-breadcrumb" aria-label="Đường dẫn">
      <a href="${ctx}/">Trang chủ</a><span>/</span><strong>Lớp học của tôi</strong>
    </nav>

    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

    <section class="jl-my-courses-hero">
      <div class="jl-my-courses-hero-copy">
        <p class="jl-my-courses-hero-eyebrow">MỖI NGÀY MỘT CHÚT, TIẾN BỘ THẬT NHIỀU</p>
        <c:choose>
          <c:when test="${empty courses}">
            <h2>Chưa có khóa học nào</h2>
            <p>Chọn khóa học phù hợp để bắt đầu hành trình tiếng Nhật của bạn.</p>
          </c:when>
          <c:otherwise>
            <h2>Tiếp tục hành trình học tiếng Nhật</h2>
            <p>Theo dõi các khóa học và tiến độ học tập của bạn tại đây.</p>
          </c:otherwise>
        </c:choose>
        <a class="jl-my-courses-store-link" href="${ctx}/roadmap">Khám phá lộ trình học <span aria-hidden="true">→</span></a>
      </div>
      <img class="jl-my-courses-hero-art" src="${ctx}/assets/img/icons/sakura2.png" alt="Hoa anh đào" loading="lazy">
    </section>

    <div class="jl-my-courses-panel">
      <div class="jl-my-courses-heading">
        <span class="jl-my-courses-heading-icon" aria-hidden="true">▤</span>
        <h1>Lớp học của tôi</h1>
      </div>

    <%-- Filter Tabs --%>
    <nav class="jl-tabs" aria-label="Lọc theo trạng thái">
      <a class="${filter == 'all' || empty filter ? 'is-active' : ''}" href="${ctx}/my-courses">Tất cả</a>
      <a class="${filter == 'not_started' ? 'is-active' : ''}" href="${ctx}/my-courses?status=not_started">Chưa học</a>
      <a class="${filter == 'in_progress' ? 'is-active' : ''}" href="${ctx}/my-courses?status=in_progress">Đang học</a>
      <a class="${filter == 'completed' ? 'is-active' : ''}" href="${ctx}/my-courses?status=completed">Hoàn thành</a>
    </nav>

    <c:choose>
      <c:when test="${empty courses}">
        <div class="jl-my-courses-empty-message">Danh mục này hiện chưa có khóa học.</div>
      </c:when>
      <c:otherwise>
        <div class="jl-my-courses-grid">
          <c:forEach var="co" items="${courses}">
            <article class="jl-my-course-card">
              <c:choose>
                <c:when test="${not empty co.thumbnailPath}"><img class="jl-my-course-cover" src="<c:out value='${co.thumbnailPath}'/>" alt="Ảnh khóa học <c:out value='${co.title}'/>" loading="lazy"></c:when>
                <c:otherwise><div class="jl-my-course-cover jl-my-course-cover-fallback" aria-hidden="true"><span>KHÓA HỌC</span><strong><c:out value="${co.levelCode}"/></strong></div></c:otherwise>
              </c:choose>
              <div class="jl-my-course-card-content">
                <div class="jl-my-course-card-meta">
                  <span class="jl-chip jl-chip-level"><c:out value="${co.levelCode}"/></span>
                  <span class="jl-my-course-status">
                    <c:choose>
                      <c:when test="${co.progress.status == 'completed'}"><span class="is-completed">✓ Hoàn thành</span></c:when>
                      <c:when test="${co.progress.status == 'in_progress'}"><span class="is-in-progress">▶ Đang học</span></c:when>
                      <c:otherwise>Chưa học</c:otherwise>
                    </c:choose>
                  </span>
                </div>

                <h2 class="jl-my-course-title">
                  <a href="${ctx}/courses/${co.courseId}"><c:out value="${co.title}"/></a>
                </h2>

                <progress max="100" value="${co.progress.percent}" aria-label="Tiến độ khóa học"></progress>
                
                <div class="jl-my-course-progress-labels">
                  <span>Tiến độ: <c:out value="${co.progress.percent}"/>%</span>
                  <span><c:out value="${co.progress.completedLessons}"/>/<c:out value="${co.progress.totalLessons}"/> bài</span>
                </div>
              </div>

              <a class="jl-btn jl-my-course-action" href="${ctx}/courses/${co.courseId}/continue">
                Vào học ngay →
              </a>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
    </div>
  </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>

