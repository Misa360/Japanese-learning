<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="jl-sidebar-wrapper">
  <%-- User Quick Info Card --%>
  <div class="jl-sidebar-usercard">
    <div class="jl-sidebar-username">
      <c:out value="${empty sessionScope.fullName ? sessionScope.username : sessionScope.fullName}"/>
    </div>
    <div class="jl-sidebar-chips">
      <span class="jl-chip jl-chip-coin" title="Số xu tích lũy">Xu <fmt:formatNumber value="${sidebarSummary.coinBalance}" groupingUsed="true"/></span>
      <span class="jl-chip jl-chip-streak" title="Chuỗi ngày học liên tục">Chuỗi <c:out value="${sidebarSummary.streakDays}"/></span>
      <span class="jl-chip jl-chip-level" title="Cấp độ mục tiêu"><c:out value="${sidebarSummary.levelCode}"/></span>
    </div>
  </div>

  <%-- Navigation Menu --%>
  <nav class="jl-usernav" aria-label="Khu vực cá nhân">
    <a class="${activeNav == 'coin-stats' ? 'is-active' : ''}" href="${ctx}/reward/coins">
      Thống kê
    </a>
    <a class="${activeNav == 'history' ? 'is-active' : ''}" href="${ctx}/history">
      Lịch sử học tập
    </a>
    <a class="${activeNav == 'my-courses' ? 'is-active' : ''}" href="${ctx}/my-courses">
      Lớp học của tôi
    </a>

    <div class="jl-usernav-group-title">Góc học tập</div>
    <a class="${activeNav == 'roadmap' || activeNav == 'dashboard' ? 'is-active' : ''}" href="${ctx}/roadmap">
      Khóa học &amp; lộ trình
    </a>
    <a class="${activeNav == 'favorites' ? 'is-active' : ''}" href="${ctx}/courses/favorites">
      Bài học video yêu thích
    </a>
    <a class="${activeNav == 'flashcards' ? 'is-active' : ''}" href="${ctx}/practice/flashcards">
      Flashcard
    </a>
    <div class="jl-usernav-group-title">Thi thử</div>
    <a class="${activeNav == 'jlpt' ? 'is-active' : ''}" href="${ctx}/practice/jlpt">
      Thi thử JLPT
    </a>

  </nav>
</div>

