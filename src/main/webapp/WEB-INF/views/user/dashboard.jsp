<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="d" value="${dashboard}"/>
<c:set var="pageTitle" value="Tổng quan" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container jl-user-layout">
  <aside><jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/></aside>
  <section>
    <h1>Xin chào, <c:out value="${d.user.fullName}"/></h1>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
    <p class="jl-motivation"><c:out value="${d.motivation}"/></p>

    <div class="jl-stats">
      <div class="jl-stat"><span class="jl-stat-num"><fmt:formatNumber value="${d.coinBalance}"/></span><span class="jl-muted">Xu hiện có</span></div>
      <div class="jl-stat"><span class="jl-stat-num"><c:out value="${d.streakDays}"/></span><span class="jl-muted">Ngày liên tiếp</span></div>
      <div class="jl-stat"><span class="jl-stat-num"><c:out value="${d.inProgressCourses}"/></span><span class="jl-muted">Khóa đang học</span></div>
      <div class="jl-stat"><span class="jl-stat-num"><c:out value="${d.completedLessons}"/></span><span class="jl-muted">Bài đã hoàn thành</span></div>
    </div>

    <div class="jl-actions">
      <c:if test="${not empty d.continueCourseId}">
        <a class="jl-btn" href="${ctx}/courses/${d.continueCourseId}/continue">Tiếp tục học: <c:out value="${d.continueCourseTitle}"/></a>
      </c:if>
      <c:if test="${empty d.continueCourseId}"><a class="jl-btn" href="${ctx}/roadmap">Chọn khóa học đầu tiên</a></c:if>
      <c:choose>
        <c:when test="${d.checkedInToday}"><span class="jl-badge jl-badge-free">Đã điểm danh hôm nay ✓</span></c:when>
        <c:otherwise>
          <%-- TODO F28: thay bang form POST toi route diem danh (kem csrf_token) khi CheckinServlet xong. --%>
          <button type="button" class="jl-btn jl-btn-secondary" disabled>Điểm danh (sắp có)</button>
        </c:otherwise>
      </c:choose>
    </div>

    <h2>Huy hiệu mới nhất</h2>
    <c:choose>
      <c:when test="${not empty d.latestBadge}">
        <div class="jl-card"><strong><c:out value="${d.latestBadge.name}"/></strong>
          <p class="jl-muted"><c:out value="${d.latestBadge.description}"/></p>
          <p class="jl-muted">Nhận ngày <fmt:formatDate value="${d.latestBadge.earnedAt}" pattern="dd/MM/yyyy"/></p></div>
      </c:when>
      <c:otherwise><p class="jl-muted">Chưa có huy hiệu nào. Hoàn thành bài học để nhận huy hiệu đầu tiên!</p></c:otherwise>
    </c:choose>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
