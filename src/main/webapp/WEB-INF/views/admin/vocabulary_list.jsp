<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- TODO F35: Kho từ vựng. Bảng từ vựng: từ, cách đọc, nghĩa, cấp độ, số bài đang dùng; tìm kiếm, lọc cấp độ, phân trang. Mau: dat pageTitle, include partials/header + footer nhu cac view khac, in du lieu bang <c:out>. --%>
<c:set var="pageTitle" value="Kho từ vựng" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <h1>Kho từ vựng</h1>
  <p class="jl-muted">Chưa triển khai.</p>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
