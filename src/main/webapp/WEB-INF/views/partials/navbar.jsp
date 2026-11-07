<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="loggedIn" value="${not empty sessionScope.userId}"/>
<c:set var="navAdmin" value="${fn:toLowerCase(sessionScope.role) == 'admin'}"/>
<c:set var="navCsrf" value="${empty requestScope.csrfToken ? sessionScope.csrfToken : requestScope.csrfToken}"/>
<header class="jl-navbar">
  <div class="jl-container jl-navbar-inner">
    <%-- Left Logo --%>
    <c:if test="${not loggedIn}">
      <a class="jl-logo-brand jl-navbar-logo" href="${ctx}/">
        <div class="jl-logo-badge">り</div>
        <span class="jl-logo-text">RIKI<span class="riki-brand-sub">日本語</span></span>
      </a>
    </c:if>
    <c:if test="${loggedIn}">
      <a class="jl-logo-brand jl-navbar-mobile-logo" href="${ctx}/dashboard">
        <span class="jl-logo-badge">り</span>
        <span class="jl-logo-text">RIKI<span class="riki-brand-sub">日本語</span></span>
      </a>
    </c:if>

    <%-- Center Announcement Banner --%>
    <div class="jl-nav-announcement">
      <span>Học tiếng Nhật từng bước, vững vàng JLPT</span>
      <a href="${ctx}/roadmap" class="jl-announcement-tag">Xem lộ trình</a>
    </div>

    <%-- Right Nav Navigation --%>
    <nav class="jl-nav" aria-label="Điều hướng chính">
      <a href="${ctx}/roadmap">Khóa học</a>
      <c:if test="${loggedIn}">
        <a href="${ctx}/dashboard">Tổng quan</a>
        <a href="${ctx}/my-courses">Khóa học của tôi</a>
        <c:if test="${navAdmin}"><a href="${ctx}/admin" style="color:#d97706;font-weight:800;">Quản trị</a></c:if>
        
        <%-- User profile pill --%>
        <a class="jl-nav-account" href="${ctx}/profile" aria-label="Tài khoản của <c:out value='${sessionScope.fullName}'/>" title="Tài khoản: <c:out value='${sessionScope.fullName}'/>">
          <c:choose>
            <c:when test="${sessionScope.gender == 'male'}">
              <img src="${ctx}/assets/img/icons/student_nam.png" alt="Tài khoản học viên nam" class="jl-nav-account-avatar">
            </c:when>
            <c:otherwise>
              <img src="${ctx}/assets/img/icons/student_nu.png" alt="Tài khoản học viên nữ" class="jl-nav-account-avatar">
            </c:otherwise>
          </c:choose>
          <span><c:out value="${empty sessionScope.fullName ? sessionScope.username : sessionScope.fullName}"/></span>
        </a>

        <form class="jl-inline" method="post" action="${ctx}/logout">
          <input type="hidden" name="csrf_token" value="<c:out value='${navCsrf}'/>">
          <button type="submit" class="jl-linkbtn" title="Đăng xuất">Đăng xuất</button>
        </form>
      </c:if>
      <c:if test="${not loggedIn}">
        <a href="${ctx}/login">Đăng nhập</a>
        <a class="jl-btn jl-btn-small" href="${ctx}/register">Đăng ký</a>
      </c:if>
    </nav>
  </div>
</header>
<c:if test="${loggedIn}">
  <div class="jl-topbar-context">
    <div class="jl-topbar-context-inner">
      <a href="${ctx}/dashboard">Trang chủ</a>
      <span aria-hidden="true">/</span>
      <span class="jl-topbar-current"><c:out value="${empty pageTitle ? 'Trang chủ' : pageTitle}"/></span>
    </div>
  </div>
</c:if>
<c:if test="${loggedIn}">
  <aside class="jl-global-sidebar" aria-label="Menu tài khoản">
    <div class="jl-sidebar-brand">
      <a class="jl-logo-brand" href="${ctx}/dashboard">
        <span class="jl-logo-badge">り</span>
        <span class="jl-logo-text">RIKI<span class="riki-brand-sub">日本語</span></span>
      </a>
    </div>
    <jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/>
  </aside>
</c:if>

