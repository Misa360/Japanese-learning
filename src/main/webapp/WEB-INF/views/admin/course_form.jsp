<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Biểu mẫu khóa học" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <h1><c:choose><c:when test="${course.id > 0}">Sửa khóa học</c:when><c:otherwise>Tạo khóa học</c:otherwise></c:choose></h1>
  <c:if test="${not empty formError}"><p role="alert"><c:out value="${formError}"/></p></c:if>
  <form method="post" action="${ctx}${formAction}" class="jl-form">
    <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
    <label for="title">Tên khóa học</label><input id="title" name="title" maxlength="200" required value="<c:out value='${course.title}'/>">
    <label for="slug">Slug</label><input id="slug" name="slug" maxlength="220" pattern="[a-z0-9]+(-[a-z0-9]+)*" required value="<c:out value='${course.slug}'/>"><small>Chữ thường, số và dấu gạch ngang.</small>
    <label for="description">Mô tả</label><textarea id="description" name="description" rows="5"><c:out value="${course.description}"/></textarea>
    <label for="thumbnail_path">Đường dẫn ảnh bìa</label><input id="thumbnail_path" name="thumbnail_path" maxlength="500" value="<c:out value='${course.thumbnailPath}'/>">
    <label for="level_id">Cấp độ</label><select id="level_id" name="level_id" required><c:forEach var="level" items="${levels}"><option value="${level.id}" <c:if test="${course.levelId == level.id}">selected</c:if>><c:out value="${level.code}"/> — <c:out value="${level.name}"/></option></c:forEach></select>
    <label for="status">Trạng thái</label><select id="status" name="status"><option value="draft" <c:if test="${course.status == 'draft' || empty course.status}">selected</c:if>>Bản nháp</option><option value="published" <c:if test="${course.status == 'published'}">selected</c:if>>Đã xuất bản</option><option value="archived" <c:if test="${course.status == 'archived'}">selected</c:if>>Lưu trữ</option></select>
    <label for="is_free">Hình thức</label><select id="is_free" name="is_free"><option value="true" <c:if test="${course.free}">selected</c:if>>Miễn phí</option><option value="false" <c:if test="${not course.free}">selected</c:if>>Trả phí</option></select>
    <label for="unlock_coins">Giá mở khóa (xu)</label><input id="unlock_coins" type="number" name="unlock_coins" min="0" value="${course.unlockCoins}">
    <label for="required_course_id">Khóa học tiên quyết (không bắt buộc)</label><select id="required_course_id" name="required_course_id"><option value="">Không có</option><c:forEach var="other" items="${allCourses}"><c:if test="${other.id != course.id}"><option value="${other.id}" <c:if test="${course.requiredCourseId == other.id}">selected</c:if>><c:out value="${other.title}"/></option></c:if></c:forEach></select>
    <p><button class="jl-btn jl-btn-primary" type="submit">Lưu khóa học</button> <a class="jl-btn" href="${ctx}/admin/courses">Hủy</a></p>
  </form>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
