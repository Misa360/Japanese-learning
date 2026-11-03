<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="loggedIn" value="${not empty sessionScope.userId}"/>
<c:set var="pageTitle" value="Lộ trình N5 → N1" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container">
  <h1>Lộ trình học tiếng Nhật N5 → N1</h1>
  <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

  <c:forEach var="block" items="${levels}">
    <section class="jl-level">
      <h2><c:out value="${block.level.code}"/> <small><c:out value="${block.level.name}"/></small></h2>
      <c:if test="${not empty block.level.description}"><p class="jl-muted"><c:out value="${block.level.description}"/></p></c:if>

      <c:choose>
        <c:when test="${empty block.courses}"><p class="jl-muted">Chưa có khóa học ở cấp này.</p></c:when>
        <c:otherwise>
          <div class="jl-grid">
            <c:forEach var="card" items="${block.courses}">
              <article class="jl-card">
                <h3><a href="${ctx}/courses/${card.course.id}"><c:out value="${card.course.title}"/></a></h3>

                <p class="jl-badges">
                  <c:choose>
                    <c:when test="${card.course.cost > 0}"><span class="jl-badge jl-badge-coin">Mở khóa bằng <c:out value="${card.course.cost}"/> xu</span></c:when>
                    <c:otherwise><span class="jl-badge jl-badge-free">Miễn phí</span></c:otherwise>
                  </c:choose>
                  <c:if test="${not empty card.course.requiredCourseId}">
                    <span class="jl-badge">Cần xong: <c:out value="${card.course.requiredCourseTitle}"/></span>
                  </c:if>
                </p>

                <c:choose>
                  <c:when test="${card.enrolled}">
                    <progress max="100" value="${card.progress.percent}"></progress>
                    <p class="jl-muted">
                      <c:out value="${card.progress.percent}"/>% ·
                      <c:choose>
                        <c:when test="${card.progress.status == 'completed'}">Hoàn thành</c:when>
                        <c:when test="${card.progress.status == 'in_progress'}">Đang học</c:when>
                        <c:otherwise>Chưa học</c:otherwise>
                      </c:choose>
                    </p>
                    <a class="jl-btn" href="${ctx}/courses/${card.course.id}/continue">Tiếp tục học</a>
                  </c:when>
                  <c:when test="${not loggedIn}">
                    <a class="jl-btn jl-btn-secondary" href="${ctx}/login">Đăng nhập để đăng ký</a>
                  </c:when>
                  <c:when test="${card.eligibility.ok}">
                    <form method="post" action="${ctx}/courses/${card.course.id}/enroll">
                      <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
                      <button type="submit" class="jl-btn">
                        <c:choose>
                          <c:when test="${card.eligibility.cost > 0}">Mở khóa (<c:out value="${card.eligibility.cost}"/> xu)</c:when>
                          <c:otherwise>Đăng ký học</c:otherwise>
                        </c:choose>
                      </button>
                    </form>
                  </c:when>
                  <c:otherwise>
                    <p class="jl-locked">🔒 <c:out value="${card.eligibility.reason}"/></p>
                  </c:otherwise>
                </c:choose>
              </article>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
  </c:forEach>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
