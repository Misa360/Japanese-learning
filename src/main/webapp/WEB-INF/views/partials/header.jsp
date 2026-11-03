<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${empty pageTitle ? 'Trang chủ' : pageTitle}"/> – JLearn</title>
  <link rel="stylesheet" href="${ctx}/assets/css/base.css">
  <link rel="stylesheet" href="${ctx}/assets/css/layout.css">
  <link rel="stylesheet" href="${ctx}/assets/css/components.css">
  <link rel="stylesheet" href="${ctx}/assets/css/learning.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/partials/navbar.jsp"/>
