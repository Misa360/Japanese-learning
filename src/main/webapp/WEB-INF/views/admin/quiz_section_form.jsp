<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Phần luyện đề" scope="request"/><c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <c:choose><c:when test="${param.type == 'practice'}">
    <div class="jl-admin-detail-top"><a href="${ctx}/admin/quizzes/${quiz.id}?type=practice">← <c:out value="${quiz.title}"/></a></div>
    <h1><c:choose><c:when test="${section.id > 0}">Chỉnh sửa phần luyện</c:when><c:otherwise>Thêm phần luyện</c:otherwise></c:choose></h1>
    <form class="jl-form" method="post" action="${ctx}${formAction}"><input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="type" value="practice"><input type="hidden" name="quiz_id" value="${quiz.id}"><input type="hidden" name="id" value="${section.id}">
      <label for="title">Tên phần thi</label><input id="title" name="title" maxlength="200" required value="<c:out value='${section.title}'/>">
      <label for="section_type">Nhóm nội dung</label><select id="section_type" name="section_type"><option value="vocabulary" <c:if test="${section.title == 'Từ vựng'}">selected</c:if>>Từ vựng</option><option value="kanji" <c:if test="${section.title == 'Chữ Hán'}">selected</c:if>>Chữ Hán</option><option value="grammar" <c:if test="${section.sectionType == 'grammar'}">selected</c:if>>Ngữ pháp</option><option value="reading" <c:if test="${section.sectionType == 'reading'}">selected</c:if>>Đọc hiểu</option><option value="listening" <c:if test="${section.sectionType == 'listening'}">selected</c:if>>Nghe hiểu</option><option value="other" <c:if test="${section.sectionType == 'other'}">selected</c:if>>Khác</option></select>
      <label for="time_limit_seconds">Thời gian (giây, không bắt buộc)</label><input id="time_limit_seconds" name="time_limit_seconds" type="number" min="1" value="${section.timeLimitSeconds}">
      <label for="sort_order">Thứ tự</label><input id="sort_order" name="sort_order" type="number" min="0" value="${section.sortOrder}">
      <p><button class="jl-btn" type="submit">Lưu phần thi</button><a class="jl-btn jl-btn-outline" href="${ctx}/admin/quizzes/${quiz.id}?type=practice">Hủy</a></p>
    </form>
  </c:when><c:otherwise><h1>Quản lý phần đề thi thử</h1><p class="jl-muted">Mục Thi thử JLPT có luồng quản lý riêng.</p></c:otherwise></c:choose>
</main><jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
