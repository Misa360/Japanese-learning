<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đổi mật khẩu" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container jl-user-layout">
  <aside><jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/></aside>
  <section>
    <h1>Đổi mật khẩu</h1>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
    <c:if test="${not empty error}"><div class="jl-flash jl-flash-error" role="alert"><c:out value="${error}"/></div></c:if>
    <form class="jl-form" method="post" action="${ctx}/account/password">
      <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
      <label for="old_password">Mật khẩu hiện tại</label>
      <input id="old_password" name="old_password" type="password" maxlength="200" required autocomplete="current-password">
      <c:if test="${not empty errors.old_password}"><span class="jl-field-error"><c:out value="${errors.old_password}"/></span></c:if>

      <label for="new_password">Mật khẩu mới (từ 8 ký tự, có chữ và số)</label>
      <input id="new_password" name="new_password" type="password" maxlength="72" required autocomplete="new-password">
      <c:if test="${not empty errors.new_password}"><span class="jl-field-error"><c:out value="${errors.new_password}"/></span></c:if>

      <label for="new_password_confirm">Nhập lại mật khẩu mới</label>
      <input id="new_password_confirm" name="new_password_confirm" type="password" maxlength="72" required autocomplete="new-password">
      <c:if test="${not empty errors.new_password_confirm}"><span class="jl-field-error"><c:out value="${errors.new_password_confirm}"/></span></c:if>

      <button type="submit" class="jl-btn">Đổi mật khẩu</button>
    </form>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
