<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="loggedIn" value="${not empty sessionScope.userId}"/>
<c:set var="navAdmin" value="${fn:toLowerCase(sessionScope.role) == 'admin'}"/>
<%-- Trang loi (dispatch ERROR) khong qua CsrfFilter nen lay token tu session de nut dang xuat van dung duoc --%>
<c:set var="navCsrf" value="${empty requestScope.csrfToken ? sessionScope.csrfToken : requestScope.csrfToken}"/>
<header class="jl-navbar">
  <div class="jl-container jl-navbar-inner">
    <a class="jl-logo" href="${ctx}/">JLearn</a>
    <nav class="jl-nav" aria-label="Điều hướng chính">
      <a href="${ctx}/roadmap">Lộ trình</a>
      <c:if test="${loggedIn}">
        <a href="${ctx}/dashboard">Tổng quan</a>
        <a href="${ctx}/my-courses">Khóa học của tôi</a>
        <a href="${ctx}/profile">Hồ sơ</a>
        <c:if test="${navAdmin}"><a href="${ctx}/admin">Quản trị</a></c:if>
        <form class="jl-inline" method="post" action="${ctx}/logout">
          <input type="hidden" name="csrf_token" value="<c:out value='${navCsrf}'/>">
          <button type="submit" class="jl-linkbtn">Đăng xuất</button>
        </form>
      </c:if>
      <c:if test="${not loggedIn}">
        <a href="${ctx}/login">Đăng nhập</a>
        <a class="jl-btn jl-btn-small" href="${ctx}/register">Đăng ký</a>
      </c:if>
    </nav>
  </div>
</header>
