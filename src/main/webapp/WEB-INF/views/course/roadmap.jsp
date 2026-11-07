<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="loggedIn" value="${not empty sessionScope.userId}"/>
<c:set var="pageTitle" value="Lộ trình học" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container jl-roadmap-page">
  <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

  <section class="jl-roadmap-hero" aria-labelledby="roadmap-title">
    <div class="jl-roadmap-hero-copy">
      <span class="jl-roadmap-eyebrow">RIKI NIHONGO · LỘ TRÌNH HỌC</span>
      <h1 id="roadmap-title">Học tiếng Nhật theo lộ trình N5 đến N1</h1>
      <p>Chọn cấp độ phù hợp, học lần lượt từng khóa và theo dõi tiến độ của bạn ngay trên trang này.</p>
      <a class="jl-roadmap-hero-link" href="#jl-roadmap-levels">Khám phá các cấp độ <span aria-hidden="true">↓</span></a>
    </div>
    <div class="jl-roadmap-hero-art" aria-hidden="true">
      <span class="jl-roadmap-orbit jl-roadmap-orbit-one"></span>
      <span class="jl-roadmap-orbit jl-roadmap-orbit-two"></span>
      <span class="jl-roadmap-japanese">日本語</span>
      <span class="jl-roadmap-book"><span>あ</span><small>HỌC TỪNG BƯỚC</small></span>
    </div>
  </section>

  <nav class="jl-roadmap-level-nav" id="jl-roadmap-levels" aria-label="Chọn cấp độ">
    <c:forEach var="block" items="${levels}">
      <a href="#jl-level-${block.level.code}" class="jl-roadmap-level-link">
        <span class="jl-roadmap-level-code"><c:out value="${block.level.code}"/></span>
        <span class="jl-roadmap-level-name"><c:out value="${block.level.name}"/></span>
        <span class="jl-roadmap-level-count"><c:out value="${fn:length(block.courses)}"/> khóa</span>
      </a>
    </c:forEach>
  </nav>

  <div class="jl-roadmap-levels">
    <c:forEach var="block" items="${levels}">
      <section class="jl-roadmap-level" id="jl-level-${block.level.code}" aria-labelledby="jl-heading-${block.level.code}">
        <header class="jl-roadmap-level-heading">
          <div class="jl-roadmap-level-title">
            <span class="jl-roadmap-heading-code"><c:out value="${block.level.code}"/></span>
            <div>
              <h2 id="jl-heading-${block.level.code}"><c:out value="${block.level.name}"/></h2>
              <c:if test="${not empty block.level.description}"><p><c:out value="${block.level.description}"/></p></c:if>
            </div>
          </div>
          <span class="jl-roadmap-course-count"><c:out value="${fn:length(block.courses)}"/> khóa học</span>
        </header>

        <c:choose>
          <c:when test="${empty block.courses}">
            <div class="jl-roadmap-no-courses">Chưa có khóa học ở cấp độ này.</div>
          </c:when>
          <c:otherwise>
            <div class="jl-roadmap-course-grid">
              <c:forEach var="card" items="${block.courses}">
                <article class="jl-roadmap-course-card">
                  <a class="jl-roadmap-course-cover jl-roadmap-course-cover-${block.level.code}" href="${ctx}/courses/${card.course.id}" aria-label="Xem chi tiết khóa học">
                    <c:choose>
                      <c:when test="${not empty card.course.thumbnailPath}">
                        <img src="<c:out value='${card.course.thumbnailPath}'/>" alt="Ảnh bìa khóa <c:out value='${card.course.title}'/>" loading="lazy">
                      </c:when>
                      <c:otherwise>
                        <span class="jl-roadmap-cover-kicker">KHÓA HỌC</span>
                        <strong><c:out value="${block.level.code}"/></strong>
                        <span class="jl-roadmap-cover-caption">TIẾNG NHẬT</span>
                      </c:otherwise>
                    </c:choose>
                    <span class="jl-roadmap-cover-mark" aria-hidden="true"><c:out value="${block.level.code}"/></span>
                  </a>

                  <div class="jl-roadmap-course-body">
                    <div class="jl-roadmap-course-badges">
                      <span class="jl-roadmap-course-level"><c:out value="${block.level.code}"/></span>
                      <c:choose>
                        <c:when test="${card.course.cost > 0}"><span class="jl-roadmap-course-cost"><fmt:formatNumber value="${card.course.cost}" groupingUsed="true"/> xu</span></c:when>
                        <c:otherwise><span class="jl-roadmap-course-free">Miễn phí</span></c:otherwise>
                      </c:choose>
                    </div>
                    <h3><a href="${ctx}/courses/${card.course.id}"><c:out value="${card.course.title}"/></a></h3>
                    <c:if test="${not empty card.course.description}"><p class="jl-roadmap-course-description"><c:out value="${card.course.description}"/></p></c:if>

                    <c:if test="${not empty card.course.requiredCourseId}">
                      <p class="jl-roadmap-prerequisite">Yêu cầu hoàn thành: <c:out value="${card.course.requiredCourseTitle}"/></p>
                    </c:if>

                    <c:if test="${card.enrolled}">
                      <div class="jl-roadmap-progress">
                        <div class="jl-roadmap-progress-label">
                          <span>
                            <c:choose>
                              <c:when test="${card.progress.status == 'completed'}">Đã hoàn thành</c:when>
                              <c:when test="${card.progress.status == 'in_progress'}">Đang học</c:when>
                              <c:otherwise>Chưa bắt đầu</c:otherwise>
                            </c:choose>
                          </span>
                          <strong><c:out value="${card.progress.percent}"/>%</strong>
                        </div>
                        <progress max="100" value="${card.progress.percent}" aria-label="Tiến độ khóa học"></progress>
                      </div>
                    </c:if>

                    <div class="jl-roadmap-course-action">
                      <c:choose>
                        <c:when test="${card.enrolled}">
                          <a class="jl-roadmap-action-primary" href="${ctx}/courses/${card.course.id}/continue">Tiếp tục học <span aria-hidden="true">→</span></a>
                        </c:when>
                        <c:when test="${not loggedIn}">
                          <a class="jl-roadmap-action-primary" href="${ctx}/login">Đăng nhập để đăng ký <span aria-hidden="true">→</span></a>
                        </c:when>
                        <c:when test="${card.eligibility.ok}">
                          <form method="post" action="${ctx}/courses/${card.course.id}/enroll">
                            <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
                            <button type="submit" class="jl-roadmap-action-primary">
                              <c:choose>
                                <c:when test="${card.eligibility.cost > 0}">Mở khóa khóa học</c:when>
                                <c:otherwise>Đăng ký học</c:otherwise>
                              </c:choose>
                              <span aria-hidden="true">→</span>
                            </button>
                          </form>
                        </c:when>
                        <c:otherwise>
                          <span class="jl-roadmap-action-locked"><c:out value="${card.eligibility.reason}"/></span>
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </div>
                </article>
              </c:forEach>
            </div>
          </c:otherwise>
        </c:choose>
      </section>
    </c:forEach>
  </div>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
