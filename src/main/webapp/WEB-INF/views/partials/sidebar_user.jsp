<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- Menu khu vuc nguoi hoc. Servlet dat request attribute "activeNav" de to dam muc hien tai. --%>
<nav class="jl-usernav" aria-label="Khu vực cá nhân">
  <a class="${activeNav == 'dashboard' ? 'is-active' : ''}" href="${ctx}/dashboard">Tổng quan</a>
  <a class="${activeNav == 'my-courses' ? 'is-active' : ''}" href="${ctx}/my-courses">Khóa học của tôi</a>
  <a class="${activeNav == 'history' ? 'is-active' : ''}" href="${ctx}/history">Lịch sử học tập</a>
  <a class="${activeNav == 'profile' ? 'is-active' : ''}" href="${ctx}/profile">Hồ sơ cá nhân</a>
  <a class="${activeNav == 'password' ? 'is-active' : ''}" href="${ctx}/account/password">Đổi mật khẩu</a>
</nav>
