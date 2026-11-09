<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="adminCsrf" value="${empty requestScope.csrfToken ? sessionScope.csrfToken : requestScope.csrfToken}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <title><c:out value="${empty pageTitle ? 'Quản trị' : pageTitle}"/> | Quản trị Riki Nihongo</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/assets/css/base.css?v=teal-palette-20261107">
  <link rel="stylesheet" href="${ctx}/assets/css/layout.css?v=card-shell-20261107-v7">
  <link rel="stylesheet" href="${ctx}/assets/css/components.css?v=flashcard-theme-20261107-v3">
  <link rel="stylesheet" href="${ctx}/assets/css/learning.css?v=roadmap-refresh-20261107-v3">
  <link rel="stylesheet" href="${ctx}/assets/css/practice.css?v=flashcard-study-20261107-v4">
  <link rel="stylesheet" href="${ctx}/assets/css/admin.css?v=admin-userstyle-4">
</head>
<%-- Khung admin DUNG CHUNG kieu dang voi khu hoc vien (cung CSS, cung shell: sidebar trai + navbar + breadcrumb), chi doi menu thanh menu quan tri. --%>
<body class="jl-authenticated-shell jl-admin">
<header class="jl-navbar">
  <div class="jl-container jl-navbar-inner">
    <a class="jl-logo-brand jl-navbar-mobile-logo" href="${ctx}/admin">
      <span class="jl-logo-badge">り</span>
      <span class="jl-logo-text">RIKI<span class="riki-brand-sub">日本語</span></span>
    </a>

    <div class="jl-nav-announcement">
      <span>Khu vực quản trị · quản lý khóa học, đề thi, flashcard</span>
      <a href="${ctx}/admin" class="jl-announcement-tag">Tổng quan</a>
    </div>

    <nav class="jl-nav" aria-label="Điều hướng quản trị">
      <a href="${ctx}/admin/courses">Khóa học</a>
      <a href="${ctx}/admin/users">Người dùng</a>
      <a class="jl-nav-account" href="${ctx}/account/password" title="Đổi mật khẩu">
        <c:choose>
          <c:when test="${sessionScope.gender == 'male'}">
            <img src="${ctx}/assets/img/icons/student_nam.png" alt="" class="jl-nav-account-avatar">
          </c:when>
          <c:otherwise>
            <img src="${ctx}/assets/img/icons/student_nu.png" alt="" class="jl-nav-account-avatar">
          </c:otherwise>
        </c:choose>
        <span><c:out value="${empty sessionScope.fullName ? sessionScope.username : sessionScope.fullName}"/></span>
      </a>
      <form class="jl-inline" method="post" action="${ctx}/logout">
        <input type="hidden" name="csrf_token" value="<c:out value='${adminCsrf}'/>">
        <button type="submit" class="jl-linkbtn" title="Đăng xuất">Đăng xuất</button>
      </form>
    </nav>
  </div>
</header>
<div class="jl-topbar-context">
  <div class="jl-topbar-context-inner">
    <a href="${ctx}/admin">Quản trị</a>
    <span aria-hidden="true">/</span>
    <span class="jl-topbar-current"><c:out value="${empty pageTitle ? 'Tổng quan' : pageTitle}"/></span>
  </div>
</div>
<aside class="jl-global-sidebar" aria-label="Menu quản trị">
  <div class="jl-sidebar-brand">
    <a class="jl-logo-brand" href="${ctx}/admin">
      <span class="jl-logo-badge">り</span>
      <span class="jl-logo-text">RIKI<span class="riki-brand-sub">日本語</span></span>
    </a>
  </div>
  <jsp:include page="/WEB-INF/views/partials/sidebar_admin.jsp"/>
</aside>
