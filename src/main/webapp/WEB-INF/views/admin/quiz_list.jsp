<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:choose><c:when test="${param.type == 'practice'}"><c:set var="pageTitle" value="Luyện đề" scope="request"/></c:when><c:when test="${param.type == 'jlpt_mock'}"><c:set var="pageTitle" value="Thi thử JLPT" scope="request"/></c:when><c:otherwise><c:set var="pageTitle" value="Bài tập của bài học" scope="request"/></c:otherwise></c:choose>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
<c:choose>
  <c:when test="${param.type == 'practice'}">
    <header class="jl-page-header"><div><h1>Quản lý Luyện đề</h1><p class="jl-muted">Tạo ngân hàng đề theo cấp độ, chia phần và quản lý câu hỏi luyện tập.</p></div><a class="jl-btn" href="${ctx}/admin/quizzes/new?type=practice">＋ Tạo bộ luyện đề</a></header>
    <div class="jl-table-wrap"><table class="jl-table"><thead><tr><th>Bộ luyện đề</th><th>Cấp độ</th><th>Phần</th><th>Câu hỏi</th><th>Lượt làm</th><th>Trạng thái</th><th>Quản lý</th></tr></thead><tbody>
      <c:forEach var="quiz" items="${quizzes}"><tr><td><strong><c:out value="${quiz.title}"/></strong><c:if test="${not empty quiz.description}"><small class="jl-admin-user-sub"><c:out value="${quiz.description}"/></small></c:if></td><td><span class="jl-chip jl-chip-level"><c:out value="${quiz.levelCode}"/></span></td><td><c:out value="${quiz.sectionCount}"/></td><td><c:out value="${quiz.questionCount}"/></td><td><fmt:formatNumber value="${quiz.attemptCount}" groupingUsed="true"/></td><td><span class="jl-admin-status ${quiz.status == 'published' ? 'jl-admin-status-published' : ''}"><c:out value="${quiz.status}"/></span></td><td><a href="${ctx}/admin/quizzes/${quiz.id}?type=practice">Mở bộ đề →</a></td></tr></c:forEach>
      <c:if test="${empty quizzes}"><tr><td colspan="7" class="jl-admin-empty">Chưa có bộ luyện đề. Hãy tạo bộ đầu tiên để thêm các phần và câu hỏi.</td></tr></c:if>
    </tbody></table></div>
  </c:when>
  <c:when test="${param.type == 'jlpt_mock'}">
    <h1>Thi thử JLPT</h1><p class="jl-muted">Các bộ đề thi thử được quản lý riêng theo kỳ thi và cấp độ.</p><div class="jl-card jl-admin-course-empty"><strong>Quản lý Thi thử chưa được triển khai.</strong><span>Phần này tách biệt với Luyện đề và bài tập trong bài học.</span></div>
  </c:when>
  <c:otherwise>
    <div class="jl-admin-detail-top"><a href="${ctx}/admin/lessons/${lesson.lessonId}">← <c:out value="${lesson.title}"/></a><a class="jl-btn" href="${ctx}/admin/quizzes/new?type=lesson&amp;lesson_id=${lesson.lessonId}">＋ Tạo bài tập</a></div>
    <h1>Bài tập của bài học</h1><p class="jl-muted">Quản lý bài tập gắn với <strong><c:out value="${lesson.title}"/></strong>.</p>
    <div class="jl-table-wrap"><table class="jl-table"><thead><tr><th>Bài tập</th><th>Số câu hỏi</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
      <c:forEach var="quiz" items="${quizzes}"><tr><td><strong><c:out value="${quiz.title}"/></strong></td><td><c:out value="${quiz.questionCount}"/></td><td><span class="jl-admin-status ${quiz.status == 'published' ? 'jl-admin-status-published' : ''}"><c:out value="${quiz.status}"/></span></td><td><a href="${ctx}/admin/quizzes/${quiz.id}?type=lesson&amp;lesson_id=${lesson.lessonId}">Chỉnh sửa</a></td></tr></c:forEach>
      <c:if test="${empty quizzes}"><tr><td class="jl-admin-empty" colspan="4">Bài học này chưa có bài tập.</td></tr></c:if>
    </tbody></table></div>
  </c:otherwise>
</c:choose>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
