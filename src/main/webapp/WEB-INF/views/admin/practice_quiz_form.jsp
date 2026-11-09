<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đề luyện" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <div class="jl-admin-detail-top"><a href="${ctx}/admin/quizzes?type=practice">← Danh sách Luyện đề</a></div>
  <h1><c:choose><c:when test="${quiz.id > 0}">Chỉnh sửa bộ luyện đề</c:when><c:otherwise>Tạo bộ luyện đề</c:otherwise></c:choose></h1>
  <p class="jl-muted">Bộ luyện đề chứa ngân hàng câu hỏi theo cấp độ và các phần thi có thể chọn khi bắt đầu làm bài.</p>
  <form class="jl-form" method="post" action="${ctx}${formAction}">
    <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="type" value="practice"><input type="hidden" name="id" value="${quiz.id}">
    <label for="title">Tên bộ luyện đề</label><input id="title" name="title" maxlength="200" required value="<c:out value='${quiz.title}'/>">
    <label for="description">Mô tả</label><textarea id="description" name="description" rows="4"><c:out value="${quiz.description}"/></textarea>
    <label for="jlpt_level_id">Cấp độ JLPT</label><select id="jlpt_level_id" name="jlpt_level_id" required><option value="">Chọn cấp độ</option><c:forEach var="level" items="${levels}"><option value="${level.id}" <c:if test="${quiz.jlptLevelId == level.id}">selected</c:if>><c:out value="${level.code}"/> — <c:out value="${level.name}"/></option></c:forEach></select>
    <label for="time_limit_seconds">Thời gian làm bài (giây, để trống nếu không giới hạn)</label><input id="time_limit_seconds" type="number" min="1" name="time_limit_seconds" value="${quiz.timeLimitSeconds}">
    <label for="passing_score">Điểm đạt (%)</label><input id="passing_score" type="number" min="0" max="100" step="0.01" name="passing_score" value="${quiz.passingScore}">
    <label for="max_attempts">Số lượt làm tối đa (để trống nếu không giới hạn)</label><input id="max_attempts" type="number" min="1" name="max_attempts" value="${quiz.maxAttempts}">
    <label for="sort_order">Thứ tự ưu tiên</label><input id="sort_order" type="number" min="0" name="sort_order" value="${quiz.sortOrder}">
    <label for="status">Trạng thái</label><select id="status" name="status"><option value="draft" <c:if test="${quiz.status == 'draft' || empty quiz.status}">selected</c:if>>Bản nháp</option><option value="published" <c:if test="${quiz.status == 'published'}">selected</c:if>>Đã xuất bản</option><option value="archived" <c:if test="${quiz.status == 'archived'}">selected</c:if>>Lưu trữ</option></select>
    <label class="jl-admin-checkbox"><input type="checkbox" name="shuffle_questions" value="true" <c:if test="${quiz.shuffleQuestions}">checked</c:if>> Trộn thứ tự câu hỏi</label>
    <p><button class="jl-btn" type="submit">Lưu bộ luyện đề</button><a class="jl-btn jl-btn-outline" href="${ctx}/admin/quizzes?type=practice">Hủy</a></p>
  </form>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
