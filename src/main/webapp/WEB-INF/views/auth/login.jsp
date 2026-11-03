<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đăng nhập" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container">
  <section class="jl-auth-card">
    <h1>Đăng nhập</h1>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
    <c:if test="${not empty error}"><div class="jl-flash jl-flash-error" role="alert"><c:out value="${error}"/></div></c:if>
    <form method="post" action="${ctx}/login" autocomplete="on">
      <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
      <input type="hidden" name="next" value="<c:out value='${next}'/>">
      <label for="identifier">Email hoặc tên đăng nhập</label>
      <input id="identifier" name="identifier" type="text" maxlength="255" required autofocus autocomplete="username" value="<c:out value='${identifier}'/>">
      <label for="password">Mật khẩu</label>
      <input id="password" name="password" type="password" maxlength="200" required autocomplete="current-password">
      <button type="submit" class="jl-btn">Đăng nhập</button>
    </form>
    <p class="jl-muted">Chưa có tài khoản? <a href="${ctx}/register">Đăng ký</a></p>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
