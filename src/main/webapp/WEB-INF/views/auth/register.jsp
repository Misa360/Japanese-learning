<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đăng ký" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container">
  <section class="jl-auth-card">
    <h1>Tạo tài khoản</h1>
    <form method="post" action="${ctx}/register" novalidate>
      <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">

      <label for="username">Tên đăng nhập</label>
      <input id="username" name="username" type="text" maxlength="30" required autocomplete="username" value="<c:out value='${form.username}'/>">
      <c:if test="${not empty errors.username}"><span class="jl-field-error"><c:out value="${errors.username}"/></span></c:if>

      <label for="email">Email</label>
      <input id="email" name="email" type="email" maxlength="255" required autocomplete="email" value="<c:out value='${form.email}'/>">
      <c:if test="${not empty errors.email}"><span class="jl-field-error"><c:out value="${errors.email}"/></span></c:if>

      <label for="password">Mật khẩu (từ 8 ký tự, có chữ và số)</label>
      <input id="password" name="password" type="password" maxlength="72" required autocomplete="new-password">
      <c:if test="${not empty errors.password}"><span class="jl-field-error"><c:out value="${errors.password}"/></span></c:if>

      <label for="password_confirm">Nhập lại mật khẩu</label>
      <input id="password_confirm" name="password_confirm" type="password" maxlength="72" required autocomplete="new-password">
      <c:if test="${not empty errors.password_confirm}"><span class="jl-field-error"><c:out value="${errors.password_confirm}"/></span></c:if>

      <button type="submit" class="jl-btn">Đăng ký</button>
    </form>
    <p class="jl-muted">Đã có tài khoản? <a href="${ctx}/login">Đăng nhập</a></p>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
