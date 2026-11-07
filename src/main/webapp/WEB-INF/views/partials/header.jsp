<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <title><c:out value="${empty pageTitle ? 'Trang chủ' : pageTitle}"/> | Riki Nihongo</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/assets/css/base.css?v=teal-palette-20261107">
  <link rel="stylesheet" href="${ctx}/assets/css/layout.css?v=card-shell-20261107-v7">
  <link rel="stylesheet" href="${ctx}/assets/css/components.css?v=flashcard-theme-20261107-v3">
  <link rel="stylesheet" href="${ctx}/assets/css/learning.css?v=roadmap-refresh-20261107-v3">
  <link rel="stylesheet" href="${ctx}/assets/css/practice.css?v=flashcard-study-20261107-v4">
</head>
<body class="${not empty sessionScope.userId ? 'jl-authenticated-shell' : ''}">
<jsp:include page="/WEB-INF/views/partials/navbar.jsp"/>

