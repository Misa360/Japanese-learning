<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="d" value="${dashboard}"/>
<c:set var="pageTitle" value="Khóa học của tôi" scope="request"/>
<c:set var="activeNav" value="dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container jl-user-layout">
  <aside>
    <jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/>
  </aside>

  <section>
    <%-- Breadcrumb --%>
    <div class="jl-page-breadcrumb" style="font-size:0.75rem;font-weight:700;color:#94a3b8;margin-bottom:12px;">
      <a href="${ctx}/" style="color:#64748b;">Trang chủ</a> / <span style="color:var(--jl-teal);">Khóa học của tôi</span>
    </div>

    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

    <%-- Hero banner khi chưa có khóa tiếp tục --%>
    <div class="jl-empty-banner">
      <div class="jl-empty-banner-content">
        <h2>
          <c:choose>
            <c:when test="${not empty d.continueCourseTitle and d.inProgressCourses gt 0}">
              Tiếp tục học: <c:out value="${d.continueCourseTitle}"/>
            </c:when>
            <c:when test="${not empty d.continueCourseTitle}">
              Bắt đầu học: <c:out value="${d.continueCourseTitle}"/>
            </c:when>
            <c:when test="${d.enrolledCourses gt 0}">
              Bạn đã hoàn thành các khóa học đã đăng ký
            </c:when>
            <c:otherwise>
              Chọn khóa học đầu tiên của bạn
            </c:otherwise>
          </c:choose>
        </h2>
        <p>
          <c:choose>
            <c:when test="${not empty d.motivation}"><c:out value="${d.motivation}"/></c:when>
            <c:otherwise>Khám phá các khóa học bên dưới và chọn lộ trình phù hợp với bạn nhé.</c:otherwise>
          </c:choose>
        </p>

        <div style="display:flex;flex-wrap:wrap;gap:12px;align-items:center;">
          <c:if test="${not empty d.continueCourseId}">
            <a class="jl-btn" href="${ctx}/courses/${d.continueCourseId}/continue" style="background:#ffffff;color:var(--jl-dark);">
              <c:choose><c:when test="${d.inProgressCourses gt 0}">Tiếp tục học</c:when><c:otherwise>Vào học ngay</c:otherwise></c:choose>
            </a>
          </c:if>
          <a class="jl-btn jl-btn-outline" href="${ctx}/roadmap">
            <c:choose><c:when test="${d.enrolledCourses gt 0}">Xem lộ trình</c:when><c:otherwise>Khám phá khóa học</c:otherwise></c:choose>
          </a>
          <c:if test="${d.checkedInToday}">
            <span class="jl-chip jl-chip-streak" style="background:#ffffff;padding:8px 14px;font-size:0.8125rem;">Đã điểm danh hôm nay</span>
          </c:if>
        </div>
      </div>

      <img class="jl-dashboard-mascot" src="${ctx}/assets/img/icons/nuiphusi.png" alt="Minh họa học tiếng Nhật" loading="lazy">
    </div>

    <%-- Quick Stats Row --%>
    <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(130px, 1fr));gap:12px;margin:20px 0;">
      <div class="jl-card jl-dashboard-stat-coin" style="text-align:center;padding:14px;">
        <span style="font-size:1.5rem;font-weight:900;color:#b45309;"><fmt:formatNumber value="${d.coinBalance}"/> xu</span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Xu hiện có</span>
      </div>
      <div class="jl-card jl-dashboard-stat-streak" style="text-align:center;padding:14px;">
        <span style="font-size:1.5rem;font-weight:900;color:var(--jl-teal);"><c:out value="${d.streakDays}"/> ngày</span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Ngày liên tiếp</span>
      </div>
      <div class="jl-card jl-dashboard-stat-courses" style="text-align:center;padding:14px;">
        <span style="font-size:1.5rem;font-weight:900;color:var(--jl-dark);"><c:out value="${d.inProgressCourses}"/></span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Khóa đang học</span>
      </div>
      <div class="jl-card jl-dashboard-stat-lessons" style="text-align:center;padding:14px;">
        <span style="font-size:1.5rem;font-weight:900;color:#10b981;"><c:out value="${d.completedLessons}"/></span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Bài hoàn thành</span>
      </div>
    </div>

    <h3 class="jl-dashboard-section-title">Khóa học gợi ý</h3>
    <c:choose>
      <c:when test="${empty suggestedCourses}">
        <p class="jl-dashboard-courses-empty">Chưa có khóa học nào.</p>
      </c:when>
      <c:otherwise>
        <div class="jl-my-courses-grid jl-dashboard-suggested-grid">
          <c:forEach var="course" items="${suggestedCourses}" varStatus="loop">
            <article class="jl-my-course-card jl-dashboard-suggested-card">
              <div class="jl-my-course-cover jl-my-course-cover-fallback jl-dashboard-cover-${course.levelCode}" aria-hidden="true">
                <span>KHÓA HỌC</span><strong><c:out value="${course.levelCode}"/></strong>
              </div>
              <div class="jl-my-course-card-content">
                <div class="jl-my-course-card-meta">
                  <span class="jl-chip jl-chip-level"><c:out value="${course.levelCode}"/></span>
                  <span class="jl-my-course-status">Chưa đăng ký</span>
                </div>
                <h2 class="jl-my-course-title"><a href="${ctx}/courses/${course.id}"><c:out value="${course.title}"/></a></h2>
                <c:if test="${not empty course.description}"><p class="jl-dashboard-course-description"><c:out value="${course.description}"/></p></c:if>
                <c:choose>
                  <c:when test="${course.free}"><span class="jl-chip jl-dashboard-course-badge">Miễn phí</span></c:when>
                  <c:otherwise><span class="jl-chip jl-dashboard-course-badge">Mở khóa bằng <fmt:formatNumber value="${course.cost}"/> xu</span></c:otherwise>
                </c:choose>
              </div>
              <a class="jl-btn jl-my-course-action" href="${ctx}/courses/${course.id}">Xem khóa học →</a>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>

