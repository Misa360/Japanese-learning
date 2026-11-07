<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đăng ký" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="riki-login-page">
  <div class="riki-login-shell">
    <section class="riki-login-intro" aria-label="Giới thiệu đăng ký học tiếng Nhật">
      <a class="riki-login-brand" href="${ctx}/">
        <span class="riki-login-brand-mark">り</span>
        <span>RIKI<small>日本語</small></span>
      </a>
      <div class="riki-login-copy">
        <span class="riki-login-kicker">HỌC TIẾNG NHẬT CÙNG RIKI</span>
        <h1>Bắt đầu từ hôm nay,<br><em>tiến bộ mỗi ngày.</em></h1>
        <p>Tạo tài khoản miễn phí để theo dõi lộ trình học, ôn tập từ vựng và chinh phục mục tiêu JLPT của bạn.</p>
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
        <span class="riki-login-kicker">TẠO TÀI KHOẢN MIỄN PHÍ</span>
        <h2>Đăng ký</h2>
        <p>Điền thông tin để bắt đầu hành trình học tiếng Nhật.</p>
      </div>

      <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

      <form method="post" action="${ctx}/register" autocomplete="on" novalidate class="riki-login-form">
        <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>"/>

        <div class="riki-field">
          <label for="full_name">Họ và tên</label>
          <input id="full_name" name="full_name" type="text" maxlength="100" required autocomplete="name" placeholder="Nhập họ và tên" value="<c:out value='${form.full_name}'/>"/>
          <c:if test="${not empty errors.full_name}"><span class="jl-field-error"><c:out value="${errors.full_name}"/></span></c:if>
        </div>

        <div class="riki-field">
          <label for="username">Tên đăng nhập</label>
          <input id="username" name="username" type="text" maxlength="30" required autocomplete="username" placeholder="Nhập tên đăng nhập" value="<c:out value='${form.username}'/>"/>
          <c:if test="${not empty errors.username}"><span class="jl-field-error"><c:out value="${errors.username}"/></span></c:if>
        </div>

        <div class="riki-field">
          <label for="gender">Giới tính</label>
          <select id="gender" name="gender" required>
            <option value="">Chọn giới tính</option>
            <option value="male" ${form.gender == 'male' ? 'selected' : ''}>Nam</option>
            <option value="female" ${form.gender == 'female' ? 'selected' : ''}>Nữ</option>
          </select>
          <c:if test="${not empty errors.gender}"><span class="jl-field-error"><c:out value="${errors.gender}"/></span></c:if>
        </div>

        <div class="riki-field">
          <label for="email">Email</label>
          <input id="email" name="email" type="email" maxlength="255" required autocomplete="email" placeholder="Nhập email" value="<c:out value='${form.email}'/>"/>
          <c:if test="${not empty errors.email}"><span class="jl-field-error"><c:out value="${errors.email}"/></span></c:if>
        </div>

        <div class="riki-field">
          <label for="password">Mật khẩu (ít nhất 8 ký tự, có chữ và số)</label>
          <input id="password" name="password" type="password" maxlength="72" required autocomplete="new-password" placeholder="Tạo mật khẩu"/>
          <c:if test="${not empty errors.password}"><span class="jl-field-error"><c:out value="${errors.password}"/></span></c:if>
        </div>

        <div class="riki-field">
          <label for="password_confirm">Nhập lại mật khẩu</label>
          <input id="password_confirm" name="password_confirm" type="password" maxlength="72" required autocomplete="new-password" placeholder="Nhập lại mật khẩu"/>
          <c:if test="${not empty errors.password_confirm}"><span class="jl-field-error"><c:out value="${errors.password_confirm}"/></span></c:if>
        </div>

        <button type="submit" class="riki-login-submit">Tạo tài khoản <span aria-hidden="true">→</span></button>
      </form>

      <p class="riki-register-prompt">Đã có tài khoản? <a href="${ctx}/login">Đăng nhập</a></p>
      <a class="riki-back-home" href="${ctx}/">← Quay lại trang chủ</a>
    </section>
  </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
