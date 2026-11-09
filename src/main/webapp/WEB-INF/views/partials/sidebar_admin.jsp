<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- F33: menu quan tri, kieu dang giong sidebar_user.jsp. Moi muc so sanh voi activeNav do AdminBaseServlet.render dat. Duoc nap MOT LAN boi partials/admin_header.jsp (khong include lai trong tung trang). --%>
<div class="jl-sidebar-wrapper">
  <div class="jl-sidebar-usercard">
    <div class="jl-sidebar-username">
      <c:out value="${empty sessionScope.fullName ? sessionScope.username : sessionScope.fullName}"/>
    </div>
    <div class="jl-sidebar-chips jl-sidebar-chips-admin">
      <span class="jl-chip jl-chip-level" title="Vai trò">Quản trị viên</span>
    </div>
  </div>

  <nav class="jl-usernav" aria-label="Menu quản trị">
    <a class="${activeNav == 'admin-dashboard' ? 'is-active' : ''}" href="${ctx}/admin">Tổng quan</a>
    <a class="${activeNav == 'admin-users' ? 'is-active' : ''}" href="${ctx}/admin/users">Người dùng</a>

    <div class="jl-usernav-group-title">Nội dung học</div>
    <a class="${activeNav == 'admin-courses' ? 'is-active' : ''}" href="${ctx}/admin/courses">Khóa học</a>

    <div class="jl-usernav-group-title">Luyện tập</div>
    <a class="${activeNav == 'admin-practice' ? 'is-active' : ''}" href="${ctx}/admin/quizzes?type=practice">Luyện đề</a>
    <a class="${activeNav == 'admin-jlpt' ? 'is-active' : ''}" href="${ctx}/admin/quizzes?type=jlpt_mock">Thi thử JLPT</a>
    <a class="${activeNav == 'admin-flashcards' ? 'is-active' : ''}" href="${ctx}/admin/flashcard-decks">Flashcard</a>

    <div class="jl-usernav-group-title">Khác</div>
    <a class="${activeNav == 'admin-rewards' ? 'is-active' : ''}" href="${ctx}/admin/rewards">Phần thưởng</a>
    <a class="${activeNav == 'admin-audit' ? 'is-active' : ''}" href="${ctx}/admin/audit">Nhật ký</a>
  </nav>
</div>
