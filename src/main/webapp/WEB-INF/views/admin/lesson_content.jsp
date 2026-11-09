<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- TODO F35: Nội dung bài học. Breadcrumb: Khóa học > Bài học > [tên bài] > Nội dung. 4 tab: Từ vựng, Kanji, Ngữ pháp (chọn từ kho rồi gắn vào bài, đổi thứ tự, gỡ ra), Tài liệu (video/tài liệu của bài). Mau: dat pageTitle, include partials/header + footer nhu cac view khac, in du lieu bang <c:out>. --%>
<c:set var="pageTitle" value="Nội dung bài học" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <h1>Nội dung bài học</h1>
  <p class="jl-muted">Chưa triển khai.</p>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
