<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đăng nhập" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="riki-login-page">
  <div class="riki-login-shell">
    <section class="riki-login-intro" aria-label="Chào mừng đến với Riki Nihongo">
      <a class="riki-login-brand" href="${ctx}/">
        <span class="riki-login-brand-mark">り</span>
        <span>RIKI<small>日本語</small></span>
      </a>
      <div class="riki-login-copy">
        <span class="riki-login-kicker">HỌC TIẾNG NHẬT CÙNG RIKI</span>
        <h1>Mỗi ngày một chút,<br><em>tiến bộ thật nhiều.</em></h1>
        <p>Đăng nhập để tiếp tục lộ trình học, ôn tập từ vựng và theo dõi mục tiêu JLPT của bạn.</p>
      </div>
      <div class="riki-login-visual" aria-hidden="true">
        <div class="riki-login-sun"></div>
        <div class="riki-login-card-note"><small>TỪ VỰNG HÔM NAY</small><strong>頑張る</strong><span>がんばる · cố gắng</span></div>
        <div class="riki-login-book riki-login-book-a">あ</div>
        <div class="riki-login-book riki-login-book-b">日</div>
        <div class="riki-login-flower" aria-hidden="true"></div>
      </div>
      <div class="riki-login-footnote">Học vững nền tảng, tự tin chinh phục JLPT</div>
    </section>

    <section class="riki-login-form-panel">
      <div class="riki-login-form-heading">
        <span class="riki-mobile-mark">り</span>
        <span class="riki-login-kicker">CHÀO MỪNG BẠN QUAY LẠI</span>
        <h2>Đăng nhập</h2>
        <p>Nhập thông tin tài khoản để tiếp tục học.</p>
      </div>

      <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
      <c:if test="${not empty error}">
        <div class="jl-flash jl-flash-error" role="alert"><c:out value="${error}"/></div>
      </c:if>

      <form method="post" action="${ctx}/login" autocomplete="on" class="riki-login-form">
        <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>"/>
        <input type="hidden" name="next" value="<c:out value='${next}'/>"/>

        <div class="riki-field">
          <label for="identifier">Email hoặc tên đăng nhập</label>
          <input id="identifier" name="identifier" type="text" maxlength="255" required autofocus autocomplete="username" placeholder="Nhập email hoặc tên đăng nhập" value="<c:out value='${identifier}'/>"/>
        </div>

        <div class="riki-field">
          <label for="password">Mật khẩu</label>
          <input id="password" name="password" type="password" maxlength="200" required autocomplete="current-password" placeholder="Nhập mật khẩu"/>
        </div>

        <div class="riki-login-options">
          <label class="riki-remember"><input type="checkbox" checked/> <span>Ghi nhớ đăng nhập</span></label>
          <a href="${ctx}/account/password">Quên mật khẩu?</a>
        </div>

        <button type="submit" class="riki-login-submit">Đăng nhập <span aria-hidden="true">→</span></button>
      </form>

      <p class="riki-register-prompt">Chưa có tài khoản? <a href="${ctx}/register">Đăng ký học miễn phí</a></p>
      <a class="riki-back-home" href="${ctx}/">← Quay lại trang chủ</a>
    </section>
  </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
