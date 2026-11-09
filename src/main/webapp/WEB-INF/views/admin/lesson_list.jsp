<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- TODO F35: Quản lý bài học. Breadcrumb: Khóa học > [tên khóa] > Bài học. Bài học nhóm theo chương. Mỗi dòng có: Sửa, Nội dung, Bài tập (/admin/quizzes?type=lesson&lesson_id=). Mau: dat pageTitle, include partials/header + footer nhu cac view khac, in du lieu bang <c:out>. --%>
<c:set var="pageTitle" value="Quản lý bài học" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <h1>Quản lý bài học</h1>
  <p class="jl-muted">Chưa triển khai.</p>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
