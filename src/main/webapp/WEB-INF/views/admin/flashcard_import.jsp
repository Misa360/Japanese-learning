<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- TODO F39: Nhập thẻ hàng loạt. Ô dán văn bản hoặc chọn file CSV: mỗi dòng 'mặt trước ; mặt sau ; ví dụ'. Hiện kết quả: tổng dòng, thành công, lỗi (từ flashcard_import_batches). Mau: dat pageTitle, include partials/header + footer nhu cac view khac, in du lieu bang <c:out>. --%>
<c:set var="pageTitle" value="Nhập thẻ hàng loạt" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <h1>Nhập thẻ hàng loạt</h1>
  <p class="jl-muted">Chưa triển khai.</p>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
