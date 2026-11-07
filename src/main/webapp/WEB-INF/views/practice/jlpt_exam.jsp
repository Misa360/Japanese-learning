<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Thi thử JLPT" scope="request"/>
<c:set var="activeNav" value="jlpt" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container">
  <h1>Thi thử JLPT</h1>
  <section class="jl-card" style="padding:24px;">
    <h2>Chức năng đang được hoàn thiện</h2>
    <p class="jl-muted">Đường dẫn thi thử đã hoạt động, nhưng hệ thống hiện chưa có đề thi JLPT được triển khai.</p>
    <a class="jl-btn" href="${pageContext.request.contextPath}/roadmap">Xem lộ trình học</a>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
