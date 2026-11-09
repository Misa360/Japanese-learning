<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- TODO F37: Biểu mẫu phần thưởng. Trường: title, description, coin_cost, reward_type, stock_quantity (trống = vô hạn), starts_at, ends_at, status. Mau: dat pageTitle, include partials/header + footer nhu cac view khac, in du lieu bang <c:out>. --%>
<c:set var="pageTitle" value="Biểu mẫu phần thưởng" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <h1>Biểu mẫu phần thưởng</h1>
  <p class="jl-muted">Chưa triển khai.</p>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
