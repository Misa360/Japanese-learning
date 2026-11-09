<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Câu hỏi Luyện đề" scope="request"/><c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout"><c:choose><c:when test="${param.type == 'practice'}">
  <div class="jl-admin-detail-top"><a href="${ctx}/admin/quizzes/${quiz.id}?type=practice">← <c:out value="${quiz.title}"/></a><a class="jl-btn" href="${ctx}/admin/questions/new?type=practice&amp;quiz_id=${quiz.id}">＋ Thêm câu hỏi</a></div>
  <h1>Ngân hàng câu hỏi</h1><p class="jl-muted">Câu hỏi của bộ luyện đề <strong><c:out value="${quiz.title}"/></strong>. Câu hỏi đã có lượt làm sẽ không cho sửa để bảo toàn lịch sử.</p>
  <div class="jl-table-wrap"><table class="jl-table"><thead><tr><th>#</th><th>Nội dung</th><th>Phần thi</th><th>Dạng</th><th>Điểm</th><th>Thao tác</th></tr></thead><tbody><c:forEach var="question" items="${questions}"><tr><td><c:out value="${question.sortOrder}"/></td><td><c:out value="${question.content}"/></td><td><c:out value="${question.sectionTitle}"/></td><td><c:out value="${question.questionType}"/></td><td><c:out value="${question.points}"/></td><td><a href="${ctx}/admin/questions/${question.id}?type=practice&amp;quiz_id=${quiz.id}">Sửa</a></td></tr></c:forEach><c:if test="${empty questions}"><tr><td colspan="6" class="jl-admin-empty">Chưa có câu hỏi.</td></tr></c:if></tbody></table></div>
</c:when><c:otherwise><h1>Danh sách câu hỏi</h1><p class="jl-muted">Quản lý câu hỏi cho đề thi thử ở màn riêng.</p></c:otherwise></c:choose></main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
