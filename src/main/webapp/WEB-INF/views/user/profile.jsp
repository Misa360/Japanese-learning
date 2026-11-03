<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Hồ sơ cá nhân" scope="request"/>
<c:set var="nameVal" value="${empty form ? profile.fullName : form.full_name}"/>
<c:set var="levelVal" value="${empty form ? profile.targetLevelId : form.target_level_id}"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container jl-user-layout">
  <aside><jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/></aside>
  <section>
    <h1>Hồ sơ cá nhân</h1>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
    <form class="jl-form" method="post" action="${ctx}/profile" enctype="multipart/form-data">
      <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">

      <c:if test="${not empty profile.avatarPath}">
        <img class="jl-avatar" src="${ctx}/media/avatar/<c:out value='${profile.avatarPath}'/>" alt="Ảnh đại diện" width="96" height="96">
      </c:if>
      <label for="avatar">Ảnh đại diện (JPG/PNG, tối đa 2 MB)</label>
      <input id="avatar" name="avatar" type="file" accept="image/png,image/jpeg">
      <c:if test="${not empty errors.avatar}"><span class="jl-field-error"><c:out value="${errors.avatar}"/></span></c:if>

      <label>Email</label>
      <input type="text" value="<c:out value='${profile.email}'/>" disabled>
      <c:if test="${not empty profile.username}"><label>Tên đăng nhập</label><input type="text" value="<c:out value='${profile.username}'/>" disabled></c:if>

      <label for="full_name">Tên hiển thị</label>
      <input id="full_name" name="full_name" type="text" maxlength="100" required value="<c:out value='${nameVal}'/>">
      <c:if test="${not empty errors.full_name}"><span class="jl-field-error"><c:out value="${errors.full_name}"/></span></c:if>

      <label for="target_level_id">Cấp JLPT mục tiêu</label>
      <select id="target_level_id" name="target_level_id">
        <option value="">— Chưa chọn —</option>
        <c:forEach var="lv" items="${levels}">
          <option value="${lv.id}" ${lv.id == levelVal ? 'selected' : ''}><c:out value="${lv.code}"/> – <c:out value="${lv.name}"/></option>
        </c:forEach>
      </select>
      <c:if test="${not empty errors.target_level_id}"><span class="jl-field-error"><c:out value="${errors.target_level_id}"/></span></c:if>

      <button type="submit" class="jl-btn">Lưu thay đổi</button>
    </form>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
