<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="lc" value="${lessonPage.context}"/>
<c:set var="pageTitle" value="${lc.title}" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container jl-lesson-layout">
  <article class="jl-lesson-main">
    <p class="jl-muted"><a href="${ctx}/courses/${lc.courseId}"><c:out value="${lc.courseTitle}"/></a> · <c:out value="${lc.chapterTitle}"/></p>
    <h1><c:out value="${lc.title}"/></h1>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
    <div id="lesson-toast" class="jl-flash jl-flash-success" role="status" hidden></div>

    <%-- Video: chi phat khi server xac nhan URL thuoc nguon cho phep (VideoEmbed). Khong bao gio in URL tho cua admin. --%>
    <c:set var="shown" value="${false}"/>
    <c:forEach var="v" items="${lessonPage.videos}">
      <c:if test="${not shown and (not empty v.youtubeId or not empty v.directVideoUrl)}">
        <c:set var="shown" value="${true}"/>
        <div id="lesson-player" class="jl-player"
             data-position-url="${ctx}/learn/${lc.lessonId}/position"
             data-csrf="<c:out value='${csrfToken}'/>"
             data-start="${lessonPage.resumeSeconds}"
             data-duration="${lc.durationSeconds}"
             data-video-id="<c:out value='${v.youtubeId}'/>">
          <c:choose>
            <c:when test="${not empty v.youtubeId}">
              <div id="yt-player"></div>
              <noscript>
                <iframe src="https://www.youtube-nocookie.com/embed/<c:out value='${v.youtubeId}'/>" title="<c:out value='${v.title}'/>"
                        sandbox="allow-scripts allow-same-origin allow-presentation" allow="fullscreen"
                        referrerpolicy="strict-origin-when-cross-origin" loading="lazy" allowfullscreen></iframe>
              </noscript>
            </c:when>
            <c:otherwise>
              <video id="html5-player" controls preload="metadata" playsinline src="<c:out value='${v.directVideoUrl}'/>"></video>
            </c:otherwise>
          </c:choose>
        </div>
        <p class="jl-muted"><c:out value="${v.title}"/></p>
      </c:if>
    </c:forEach>
    <c:if test="${not shown and not empty lessonPage.videos}">
      <p class="jl-flash jl-flash-info">Video của bài này hiện không khả dụng.</p>
    </c:if>

    <c:if test="${lessonPage.enrolled}">
      <form id="complete-form" class="jl-complete" method="post" action="${ctx}/learn/${lc.lessonId}/complete">
        <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
        <button id="complete-btn" type="submit" class="jl-btn" ${lessonPage.completed ? 'disabled' : ''}>
          <c:choose><c:when test="${lessonPage.completed}">Đã hoàn thành ✓</c:when><c:otherwise>Hoàn thành bài</c:otherwise></c:choose>
        </button>
      </form>
    </c:if>
    <c:if test="${not lessonPage.enrolled}">
      <p class="jl-flash jl-flash-info">Đây là bài xem thử. <a href="${ctx}/courses/${lc.courseId}">Đăng ký khóa học</a> để lưu tiến độ và nhận xu.</p>
    </c:if>
    <%-- TODO F19: nut "Lam quiz" cua bai nay (them khi route quiz cua nhom da chot). --%>

    <c:forEach var="t" items="${lessonPage.texts}">
      <section class="jl-section"><h2><c:out value="${t.title}"/></h2><div class="jl-text"><c:out value="${t.contentText}"/></div></section>
    </c:forEach>

    <c:if test="${not empty lessonPage.transcripts}">
      <section class="jl-section"><h2>Transcript</h2>
        <c:forEach var="t" items="${lessonPage.transcripts}"><div class="jl-text"><c:out value="${t.contentText}"/></div></c:forEach>
      </section>
    </c:if>

    <c:if test="${not empty lessonPage.vocabulary}">
      <section class="jl-section"><h2>Từ vựng</h2>
        <div class="jl-scroll"><table class="jl-table">
          <thead><tr><th>Từ</th><th>Cách đọc</th><th>Nghĩa</th><th>Ví dụ</th></tr></thead>
          <tbody>
          <c:forEach var="w" items="${lessonPage.vocabulary}">
            <tr><td lang="ja"><c:out value="${w.word}"/></td><td><c:out value="${w.reading}"/></td><td><c:out value="${w.meaningVi}"/></td>
                <td><span lang="ja"><c:out value="${w.exampleJa}"/></span><br><span class="jl-muted"><c:out value="${w.exampleVi}"/></span></td></tr>
          </c:forEach>
          </tbody>
        </table></div>
      </section>
    </c:if>

    <c:if test="${not empty lessonPage.grammar}">
      <section class="jl-section"><h2>Ngữ pháp</h2>
        <c:forEach var="g" items="${lessonPage.grammar}">
          <div class="jl-card">
            <h3><c:out value="${g.title}"/></h3>
            <c:if test="${not empty g.pattern}"><p lang="ja"><strong><c:out value="${g.pattern}"/></strong></p></c:if>
            <div class="jl-text"><c:out value="${g.explanationVi}"/></div>
            <c:if test="${not empty g.exampleJa}"><p><span lang="ja"><c:out value="${g.exampleJa}"/></span><br><span class="jl-muted"><c:out value="${g.exampleVi}"/></span></p></c:if>
          </div>
        </c:forEach>
      </section>
    </c:if>

    <c:if test="${not empty lessonPage.kanji}">
      <section class="jl-section"><h2>Kanji</h2>
        <div class="jl-grid">
          <c:forEach var="k" items="${lessonPage.kanji}">
            <div class="jl-card jl-kanji">
              <div class="jl-kanji-char" lang="ja"><c:out value="${k.character}"/></div>
              <p>Onyomi: <c:out value="${k.onyomi}"/><br>Kunyomi: <c:out value="${k.kunyomi}"/></p>
              <p><c:out value="${k.meaningVi}"/><c:if test="${not empty k.strokeCount}"> · <c:out value="${k.strokeCount}"/> nét</c:if></p>
            </div>
          </c:forEach>
        </div>
      </section>
    </c:if>

    <c:if test="${not empty lessonPage.documents}">
      <section class="jl-section"><h2>Tài liệu</h2>
        <ul>
          <c:forEach var="d" items="${lessonPage.documents}">
            <li>
              <c:choose>
                <c:when test="${not empty d.safeUrl}"><a href="<c:out value='${d.safeUrl}'/>" target="_blank" rel="noopener noreferrer nofollow"><c:out value="${d.title}"/></a></c:when>
                <c:otherwise><c:out value="${d.title}"/></c:otherwise>
              </c:choose>
            </li>
          </c:forEach>
        </ul>
      </section>
    </c:if>

    <nav class="jl-lesson-nav">
      <c:if test="${not empty lessonPage.prevLessonId}"><a class="jl-btn jl-btn-secondary" href="${ctx}/learn/${lessonPage.prevLessonId}">← Bài trước</a></c:if>
      <c:if test="${not empty lessonPage.nextLessonId}"><a class="jl-btn jl-btn-secondary" href="${ctx}/learn/${lessonPage.nextLessonId}">Bài tiếp →</a></c:if>
    </nav>
  </article>

  <aside><jsp:include page="/WEB-INF/views/learning/lesson_sidebar.jsp"/></aside>
</main>

<script src="${ctx}/assets/js/lesson-player.js" defer></script>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
