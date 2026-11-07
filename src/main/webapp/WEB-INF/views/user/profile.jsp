<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông tin cá nhân" scope="request"/>
<c:set var="activeNav" value="profile" scope="request"/>
<c:set var="nameVal" value="${empty form ? profile.fullName : form.full_name}"/>
<c:set var="levelVal" value="${empty form ? profile.targetLevelId : form.target_level_id}"/>
<c:set var="genderVal" value="${empty form ? profile.gender : form.gender}"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container jl-user-layout">
  <aside>
    <jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/>
  </aside>

  <section>
    <%-- Breadcrumb --%>
    <div class="jl-page-breadcrumb" style="font-size:0.75rem;font-weight:700;color:#94a3b8;margin-bottom:12px;">
      <a href="${ctx}/" style="color:#64748b;">Trang chủ</a> / <span style="color:var(--jl-teal);">Thông tin cá nhân</span>
    </div>

    <%-- Hero Account Banner matching image_87e53804.png --%>
    <div style="background:#20bdb2;border:3px solid #103a3f;border-radius:28px;padding:28px;color:#103a3f;position:relative;overflow:visible;margin-bottom:36px;box-shadow:6px 8px 0 #103a3f;">
      <h2 style="font-size:1.6rem;font-weight:900;color:#103a3f;margin-bottom:4px;">Thông tin tài khoản</h2>
      <p style="font-size:0.875rem;color:#28575a;">Bạn có thể chỉnh sửa thông tin cá nhân tại đây</p>

      <%-- Mascot Mechanic with Wrench --%>
      <div style="position:absolute;right:24px;bottom:0;top:0;display:flex;align-items:center;">
        <span style="font-size:4rem;" aria-hidden="true"></span>
      </div>

      <%-- Overlapping Avatar circle with camera icon --%>
      <div style="position:absolute;bottom:-26px;left:28px;">
        <div style="position:relative;display:inline-block;">
          <c:choose>
            <c:when test="${not empty profile.avatarPath}">
              <img class="jl-avatar" src="${ctx}/media/avatar/<c:out value='${profile.avatarPath}'/>" alt="Avatar" style="width:72px;height:72px;border:4px solid #fff;background:#fff;">
            </c:when>
            <c:otherwise>
              <c:choose>
                <c:when test="${profile.gender == 'male'}"><img class="jl-avatar" src="${ctx}/assets/img/icons/student_nam.png" alt="Avatar học viên nam" style="width:72px;height:72px;border:4px solid #fff;background:#fff;"></c:when>
                <c:otherwise><img class="jl-avatar" src="${ctx}/assets/img/icons/student_nu.png" alt="Avatar học viên nữ" style="width:72px;height:72px;border:4px solid #fff;background:#fff;"></c:otherwise>
              </c:choose>
            </c:otherwise>
          </c:choose>
          <label for="avatar" style="position:absolute;bottom:0;right:0;width:24px;height:24px;background:#0f172a;color:#fff;border-radius:50%;display:flex;align-items:center;justify-content:center;cursor:pointer;font-size:11px;border:2px solid #fff;" title="Đổi ảnh đại diện">
            Đổi
          </label>
        </div>
      </div>
    </div>

    <%-- Tabs matching image_87e53804.png --%>
    <div class="jl-tabs" style="border-bottom:2px solid #b8d8d5;padding-bottom:12px;margin-bottom:24px;gap:12px;">
      <a class="is-active" href="${ctx}/profile">Thông tin cá nhân</a>
      <a href="${ctx}/account/password">Thay đổi mật khẩu</a>
      <a href="${ctx}/profile">Liên kết tài khoản</a>
    </div>

    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

    <%-- Form matching 2-column layout in image_87e53804.png --%>
    <form class="jl-form" method="post" action="${ctx}/profile" enctype="multipart/form-data" style="background:#fff;border:3px solid #103a3f;border-radius:22px;padding:24px;box-shadow:4px 5px 0 #103a3f;">
      <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
      <input id="avatar" name="avatar" type="file" accept="image/png,image/jpeg" style="display:none;">

      <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;">
        <%-- Full Name --%>
        <div>
          <label for="full_name">Tên học viên <span style="color:#ef4444;">*</span></label>
          <input id="full_name" name="full_name" type="text" maxlength="100" required value="<c:out value='${nameVal}'/>" placeholder="Họ và tên">
          <c:if test="${not empty errors.full_name}"><span class="jl-field-error"><c:out value="${errors.full_name}"/></span></c:if>
        </div>

        <%-- Email --%>
        <div>
          <label>Email <span style="color:#ef4444;">*</span></label>
          <input type="text" value="<c:out value='${profile.email}'/>" disabled style="background:#f1f5f9;cursor:not-allowed;">
        </div>

        <%-- Username --%>
        <div>
          <label>Tên đăng nhập</label>
          <input type="text" value="<c:out value='${empty profile.username ? sessionScope.username : profile.username}'/>" disabled style="background:#f1f5f9;cursor:not-allowed;">
        </div>

        <%-- Target Level --%>
        <div>
          <label for="target_level_id">Cấp JLPT mục tiêu <span style="color:#ef4444;">*</span></label>
          <select id="target_level_id" name="target_level_id">
            <option value="">— Chưa chọn —</option>
            <c:forEach var="lv" items="${levels}">
              <option value="${lv.id}" ${lv.id == levelVal ? 'selected' : ''}><c:out value="${lv.code}"/> – <c:out value="${lv.name}"/></option>
            </c:forEach>
          </select>
          <c:if test="${not empty errors.target_level_id}"><span class="jl-field-error"><c:out value="${errors.target_level_id}"/></span></c:if>
        </div>
        <div>
          <label for="gender">Giới tính</label>
          <select id="gender" name="gender" required>
            <option value="">Chọn giới tính</option>
            <option value="male" ${genderVal == 'male' ? 'selected' : ''}>Nam</option>
            <option value="female" ${genderVal == 'female' ? 'selected' : ''}>Nữ</option>
          </select>
          <c:if test="${not empty errors.gender}"><span class="jl-field-error"><c:out value="${errors.gender}"/></span></c:if>
        </div>
      </div>

      <div style="margin-top:16px;">
        <button type="submit" class="jl-btn" style="padding:10px 24px;">Lưu thông tin</button>
      </div>
    </form>
  </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>

