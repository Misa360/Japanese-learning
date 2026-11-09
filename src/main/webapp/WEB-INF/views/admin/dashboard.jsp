<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- F33: Tong quan quan tri. Bo cuc va class CSS lay theo user/dashboard.jsp (banner + the so lieu jl-card + muc "Loi tat"). Du lieu: request attribute "stats" do AdminDashboardServlet dat. --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Tổng quan quản trị" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>

<main class="jl-container jl-user-layout">
  <section>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

    <div class="jl-empty-banner">
      <div class="jl-empty-banner-content">
        <h2>Xin chào, <c:out value="${empty sessionScope.fullName ? sessionScope.username : sessionScope.fullName}"/></h2>
        <p>Quản lý khóa học, bài học, đề thi và flashcard của hệ thống tại đây.</p>
        <div style="display:flex;flex-wrap:wrap;gap:12px;align-items:center;">
          <a class="jl-btn" href="${ctx}/admin/courses" style="background:#ffffff;color:var(--jl-dark);">Quản lý khóa học</a>
          <a class="jl-btn jl-btn-outline" href="${ctx}/admin/quizzes?type=jlpt_mock">Thi thử JLPT</a>
        </div>
      </div>
      <img class="jl-dashboard-mascot" src="${ctx}/assets/img/icons/nuiphusi.png" alt="Minh họa học tiếng Nhật" loading="lazy">
    </div>

    <%-- Hang the so lieu: cung kieu "Quick Stats Row" cua user dashboard --%>
    <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(150px, 1fr));gap:12px;margin:20px 0;">
      <a class="jl-card jl-dashboard-stat-courses jl-admin-statlink" href="${ctx}/admin/users">
        <span class="jl-admin-stat-value"><fmt:formatNumber value="${stats.students}" groupingUsed="true"/></span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Học viên</span>
      </a>
      <a class="jl-card jl-dashboard-stat-streak jl-admin-statlink" href="${ctx}/admin/courses">
        <span class="jl-admin-stat-value"><fmt:formatNumber value="${stats.courses}" groupingUsed="true"/></span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Khóa học (<c:out value="${stats.publishedCourses}"/> xuất bản)</span>
      </a>
      <a class="jl-card jl-dashboard-stat-lessons jl-admin-statlink" href="${ctx}/admin/courses">
        <span class="jl-admin-stat-value"><fmt:formatNumber value="${stats.lessons}" groupingUsed="true"/></span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Bài học</span>
      </a>
      <a class="jl-card jl-dashboard-stat-coin jl-admin-statlink" href="${ctx}/admin/quizzes?type=practice">
        <span class="jl-admin-stat-value"><fmt:formatNumber value="${stats.quizzes}" groupingUsed="true"/></span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Đề / bài tập</span>
      </a>
      <div class="jl-card jl-admin-statlink">
        <span class="jl-admin-stat-value"><fmt:formatNumber value="${stats.attempts7d}" groupingUsed="true"/></span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Lượt làm bài 7 ngày</span>
      </div>
      <a class="jl-card jl-admin-statlink ${stats.pendingRedemptions > 0 ? 'jl-admin-stat-alert' : ''}" href="${ctx}/admin/rewards">
        <span class="jl-admin-stat-value"><fmt:formatNumber value="${stats.pendingRedemptions}" groupingUsed="true"/></span>
        <span class="jl-muted" style="display:block;margin-top:2px;">Đổi thưởng chờ xử lý</span>
      </a>
    </div>

    <h3 class="jl-dashboard-section-title">Lối tắt</h3>
    <div class="jl-admin-shortcuts">
      <a class="jl-card jl-admin-shortcut" href="${ctx}/admin/courses"><strong>Khóa học</strong><span>Khóa học, chương, bài học và nội dung.</span></a>
      <a class="jl-card jl-admin-shortcut" href="${ctx}/admin/quizzes?type=practice"><strong>Luyện đề</strong><span>Bài tập, đề luyện và câu hỏi.</span></a>
      <a class="jl-card jl-admin-shortcut" href="${ctx}/admin/flashcard-decks"><strong>Flashcard hệ thống</strong><span>Bộ thẻ, thẻ và nhập hàng loạt.</span></a>
      <a class="jl-card jl-admin-shortcut" href="${ctx}/admin/users"><strong>Người dùng</strong><span>Danh sách tài khoản học viên.</span></a>
      <a class="jl-card jl-admin-shortcut" href="${ctx}/admin/audit"><strong>Nhật ký hệ thống</strong><span>Lịch sử thao tác của quản trị viên.</span></a>
    </div>
  </section>
</main>

<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
