<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Lịch sử học tập" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container jl-user-layout">
  <aside><jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/></aside>
  <section>
    <h1>Lịch sử học tập</h1>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
    <c:choose>
      <c:when test="${empty history.items}"><p class="jl-muted">Bạn chưa học bài nào. <a href="${ctx}/roadmap">Bắt đầu từ lộ trình</a>.</p></c:when>
      <c:otherwise>
        <div class="jl-scroll"><table class="jl-table">
          <thead><tr><th>Bài học</th><th>Khóa học</th><th>Thời điểm</th><th>Thời lượng</th></tr></thead>
          <tbody>
            <c:forEach var="h" items="${history.items}">
              <tr>
                <td><a href="${ctx}/learn/${h.lessonId}"><c:out value="${h.lessonTitle}"/></a></td>
                <td><a href="${ctx}/courses/${h.courseId}"><c:out value="${h.courseTitle}"/></a></td>
                <td><fmt:formatDate value="${h.loggedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                <td><fmt:formatNumber value="${h.durationSeconds / 60}" maxFractionDigits="0"/> phút</td>
              </tr>
            </c:forEach>
          </tbody>
        </table></div>
        <jsp:include page="/WEB-INF/views/partials/pagination.jsp"/>
      </c:otherwise>
    </c:choose>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
