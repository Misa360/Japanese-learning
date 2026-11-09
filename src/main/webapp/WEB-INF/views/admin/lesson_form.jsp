<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- TODO F35: Biểu mẫu bài học. Trường: chapter_id, title, slug, mô tả, lesson_type, duration_seconds, sort_order, is_preview, status. Mau: dat pageTitle, include partials/header + footer nhu cac view khac, in du lieu bang <c:out>. --%>
<c:set var="pageTitle" value="Biểu mẫu bài học" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <h1>Biểu mẫu bài học</h1>
  <p class="jl-muted">Chưa triển khai.</p>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
