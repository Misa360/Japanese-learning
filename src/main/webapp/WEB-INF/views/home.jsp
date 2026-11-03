<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Học tiếng Nhật N5 → N1" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container jl-hero">
  <h1>Học tiếng Nhật từ N5 đến N1</h1>
  <p>Lộ trình rõ ràng, bài học video, từ vựng – ngữ pháp – kanji, theo dõi tiến độ mỗi ngày.</p>
  <p><a class="jl-btn" href="${ctx}/register">Bắt đầu miễn phí</a> <a class="jl-btn jl-btn-secondary" href="${ctx}/roadmap">Xem lộ trình</a></p>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
