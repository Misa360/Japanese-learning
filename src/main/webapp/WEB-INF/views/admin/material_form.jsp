<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý video và tài liệu" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <div class="jl-admin-detail-top"><a href="${ctx}/admin/lessons/${lesson.lessonId}">← <c:out value="${lesson.title}"/></a></div>
  <h1><c:choose><c:when test="${material.id > 0}">Chỉnh sửa nội dung</c:when><c:otherwise>Thêm video hoặc tài liệu</c:otherwise></c:choose></h1>
  <form class="jl-form" method="post" action="${ctx}/admin/lesson-content/materials">
    <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
    <input type="hidden" name="id" value="${material.id}"><input type="hidden" name="lesson_id" value="${lesson.lessonId}">
    <label for="title">Tiêu đề</label><input id="title" name="title" maxlength="200" required value="<c:out value='${material.title}'/>">
    <label for="material_type">Loại nội dung</label><select id="material_type" name="material_type"><option value="video" <c:if test="${material.type == 'video' || empty material.type}">selected</c:if>>Video</option><option value="document" <c:if test="${material.type == 'document'}">selected</c:if>>Tài liệu</option><option value="text" <c:if test="${material.type == 'text'}">selected</c:if>>Văn bản</option></select>
    <label for="media_url">Đường dẫn video hoặc tài liệu</label><input id="media_url" type="url" name="media_url" maxlength="1000" value="<c:out value='${material.mediaUrl}'/>">
    <label for="content_text">Nội dung / ghi chú</label><textarea id="content_text" name="content_text" rows="5"><c:out value="${material.contentText}"/></textarea>
    <label for="sort_order">Thứ tự hiển thị</label><input id="sort_order" type="number" min="0" name="sort_order" value="${material.sortOrder}">
    <p><button class="jl-btn" type="submit">Lưu nội dung</button><a class="jl-btn jl-btn-outline" href="${ctx}/admin/lessons/${lesson.lessonId}">Hủy</a></p>
  </form>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
