<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Bài tập" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <div class="jl-admin-detail-top"><a href="${ctx}/admin/lessons/${lesson.lessonId}">← <c:out value="${lesson.title}"/></a></div>
  <h1><c:choose><c:when test="${quiz.id > 0}">Chỉnh sửa bài tập</c:when><c:otherwise>Tạo bài tập</c:otherwise></c:choose></h1>
  <form class="jl-form" method="post" action="${ctx}${formAction}">
    <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="id" value="${quiz.id}"><input type="hidden" name="lesson_id" value="${lesson.lessonId}">
    <label for="title">Tên bài tập</label><input id="title" name="title" maxlength="200" required value="<c:out value='${quiz.title}'/>">
    <label for="status">Trạng thái</label><select id="status" name="status"><option value="draft" <c:if test="${quiz.status == 'draft' || empty quiz.status}">selected</c:if>>Bản nháp</option><option value="published" <c:if test="${quiz.status == 'published'}">selected</c:if>>Đã xuất bản</option><option value="archived" <c:if test="${quiz.status == 'archived'}">selected</c:if>>Lưu trữ</option></select>
    <p><button class="jl-btn" type="submit">Lưu bài tập</button><a class="jl-btn jl-btn-outline" href="${ctx}/admin/lessons/${lesson.lessonId}">Hủy</a></p>
  </form>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
