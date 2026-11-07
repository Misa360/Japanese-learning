<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Không tìm thấy trang" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container jl-error">
  <h1>404 – Không tìm thấy trang</h1>
  <c:if test="${not empty requestScope['javax.servlet.error.message']}"><p><c:out value="${requestScope['javax.servlet.error.message']}"/></p></c:if>
  <p><a class="jl-btn" href="${ctx}/">Về trang chủ</a></p>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
