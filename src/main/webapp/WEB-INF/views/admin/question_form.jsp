<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Câu hỏi Luyện đề" scope="request"/><c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout"><c:choose><c:when test="${param.type == 'practice'}">
  <div class="jl-admin-detail-top"><a href="${ctx}/admin/quizzes/${quiz.id}?type=practice">← <c:out value="${quiz.title}"/></a></div>
  <h1><c:choose><c:when test="${question.id > 0}">Chỉnh sửa câu hỏi</c:when><c:otherwise>Thêm câu hỏi</c:otherwise></c:choose></h1>
  <form class="jl-form jl-admin-question-form" method="post" action="${ctx}${formAction}"><input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="type" value="practice"><input type="hidden" name="quiz_id" value="${quiz.id}"><input type="hidden" name="id" value="${question.id}">
    <label for="section_id">Phần thi</label><select id="section_id" name="section_id" required><option value="">Chọn phần thi</option><c:forEach var="section" items="${sections}"><option value="${section.id}" <c:if test="${question.sectionId == section.id}">selected</c:if>><c:out value="${section.title}"/></option></c:forEach></select>
    <label for="question_type">Dạng câu hỏi</label><select id="question_type" name="question_type"><option value="single_choice" <c:if test="${question.questionType == 'single_choice'}">selected</c:if>>Một đáp án đúng</option><option value="multiple_choice" <c:if test="${question.questionType == 'multiple_choice'}">selected</c:if>>Nhiều đáp án đúng</option><option value="text_input" <c:if test="${question.questionType == 'text_input'}">selected</c:if>>Điền đáp án</option></select>
    <label for="content">Nội dung câu hỏi</label><textarea id="content" name="content" rows="5" required><c:out value="${question.content}"/></textarea>
    <label for="explanation">Giải thích đáp án</label><textarea id="explanation" name="explanation" rows="3"><c:out value="${question.explanation}"/></textarea>
    <label for="audio_url">Đường dẫn âm thanh (không bắt buộc)</label><input id="audio_url" type="url" name="audio_url" value="<c:out value='${question.audioUrl}'/>">
    <label for="image_url">Đường dẫn hình ảnh (không bắt buộc)</label><input id="image_url" type="url" name="image_url" value="<c:out value='${question.imageUrl}'/>">
    <label for="points">Điểm</label><input id="points" type="number" name="points" min="0.01" step="0.01" required value="${question.points}">
    <label for="sort_order">Thứ tự câu hỏi</label><input id="sort_order" type="number" name="sort_order" min="0" value="${question.sortOrder}">
    <fieldset class="jl-admin-answer-set"><legend>Đáp án <small>(chọn đáp án đúng; dạng nhiều đáp án có thể chọn nhiều)</small></legend><c:forEach begin="0" end="3" var="i"><div class="jl-admin-answer-row"><input type="text" name="answer_text" maxlength="500" aria-label="Nội dung đáp án ${i + 1}" value="<c:out value='${question.answers[i].content}'/>"><label><input type="checkbox" name="correct_indices" value="${i}" <c:if test="${question.answers[i].correct || (question.id == 0 and question.questionType == 'single_choice' and i == 0)}">checked</c:if>> Đúng</label></div></c:forEach></fieldset>
    <p><button class="jl-btn" type="submit">Lưu câu hỏi</button><a class="jl-btn jl-btn-outline" href="${ctx}/admin/quizzes/${quiz.id}?type=practice">Hủy</a></p>
  </form>
</c:when><c:otherwise><h1>Biểu mẫu câu hỏi</h1><p class="jl-muted">Phần câu hỏi của Thi thử JLPT được quản lý riêng.</p></c:otherwise></c:choose></main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
