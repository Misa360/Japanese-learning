<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Lịch sử học tập" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>
<main class="jl-container jl-user-layout jl-history-page">
  <section class="jl-history-content" aria-labelledby="history-title">
    <header class="jl-history-heading">
      <span class="jl-history-heading-icon" aria-hidden="true">
        <svg viewBox="0 0 24 24"><path d="M4 5.5h16M4 10h16M4 14.5h10M4 19h10"/><path d="M3 3.5h18v17H3z"/></svg>
      </span>
      <div>
        <h1 id="history-title">Lịch sử học tập</h1>
        <p>Theo dõi các bài học và lượt làm bài của bạn</p>
      </div>
    </header>

    <div class="jl-history-stats" aria-label="Tổng quan lịch sử">
      <article class="jl-history-stat jl-history-stat-study">
        <span class="jl-history-stat-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z"/><path d="M8 7h8M8 11h8"/></svg></span>
        <span class="jl-history-stat-label">Lịch sử học bài</span>
        <strong><fmt:formatNumber value="${history.pager.total}" groupingUsed="true"/> bài</strong>
      </article>
      <article class="jl-history-stat jl-history-stat-exercise">
        <span class="jl-history-stat-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M8 5h12v16H4V5h2M8 3h8v4H8zM8 12h8M8 16h5"/></svg></span>
        <span class="jl-history-stat-label">Bài tập</span>
        <strong>—</strong>
      </article>
      <article class="jl-history-stat jl-history-stat-practice">
        <span class="jl-history-stat-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="m4 19 4.5-1 10-10a2.12 2.12 0 0 0-3-3l-10 10L4 19Z"/><path d="m14 6 4 4M4 22h16"/></svg></span>
        <span class="jl-history-stat-label">Luyện đề</span>
        <strong>—</strong>
      </article>
      <article class="jl-history-stat jl-history-stat-jlpt">
        <span class="jl-history-stat-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M5 3h14v18H5zM8 7h8M8 11h8M8 15h5"/><path d="m15 17 1.5 1.5L20 15"/></svg></span>
        <span class="jl-history-stat-label">Thi thử JLPT</span>
        <strong>—</strong>
      </article>
    </div>

    <section class="jl-history-records" aria-label="Danh sách lịch sử">
      <nav class="jl-history-tabs" aria-label="Loại lịch sử">
        <a href="${ctx}/history?tab=lesson" class="${historyTab == 'lesson' ? 'is-active' : ''}" ${historyTab == 'lesson' ? 'aria-current="page"' : ''}>Lịch sử học bài</a>
        <a href="${ctx}/history?tab=exercise" class="${historyTab == 'exercise' ? 'is-active' : ''}" ${historyTab == 'exercise' ? 'aria-current="page"' : ''}>Bài tập</a>
        <a href="${ctx}/history?tab=practice" class="${historyTab == 'practice' ? 'is-active' : ''}" ${historyTab == 'practice' ? 'aria-current="page"' : ''}>Luyện đề</a>
        <a href="${ctx}/history?tab=jlpt" class="${historyTab == 'jlpt' ? 'is-active' : ''}" ${historyTab == 'jlpt' ? 'aria-current="page"' : ''}>Thi thử JLPT</a>
      </nav>

      <c:if test="${historyTab == 'lesson'}">
        <div class="jl-history-filters">
          <label class="jl-history-search" for="history-search">
            <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="11" cy="11" r="7.5"/><path d="m16.5 16.5 4 4"/></svg>
            <input id="history-search" type="search" placeholder="Tìm theo tên bài học hoặc khóa học" autocomplete="off">
          </label>
          <label class="jl-history-level" for="history-level">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 5h16l-6.5 7.5v5l-3 1v-6L4 5Z"/></svg>
            <select id="history-level">
              <option value="all">Tất cả cấp độ</option>
              <option value="N5">N5</option><option value="N4">N4</option><option value="N3">N3</option><option value="N2">N2</option><option value="N1">N1</option>
            </select>
          </label>
        </div>
      </c:if>

      <div class="jl-history-table-wrap">
        <c:choose>
          <c:when test="${historyTab == 'lesson' and not empty history.items}">
            <div class="jl-scroll">
              <table class="jl-history-table" id="history-table">
                <thead><tr><th scope="col">STT</th><th scope="col">Tên bài học</th><th scope="col">Khóa học</th><th scope="col">Cấp độ</th><th scope="col">Thời lượng</th><th scope="col">Ngày học</th></tr></thead>
                <tbody>
                  <c:forEach var="h" items="${history.items}" varStatus="loop">
                    <tr class="jl-history-row" data-level="<c:out value='${h.levelCode}'/>" data-search="<c:out value='${h.lessonTitle} ${h.courseTitle}'/>">
                      <td><c:out value="${(history.pager.page - 1) * history.pager.size + loop.index + 1}"/></td>
                      <td><a href="${ctx}/learn/${h.lessonId}"><c:out value="${h.lessonTitle}"/></a></td>
                      <td><a href="${ctx}/courses/${h.courseId}"><c:out value="${h.courseTitle}"/></a></td>
                      <td><span class="jl-history-level-tag"><c:out value="${empty h.levelCode ? '—' : h.levelCode}"/></span></td>
                      <td><fmt:formatNumber value="${h.durationSeconds / 60}" maxFractionDigits="0"/> phút</td>
                      <td><fmt:formatDate value="${h.loggedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <div class="jl-history-empty" id="history-filter-empty" hidden>
              <span class="jl-history-empty-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M4 5h16v14H4zM8 9h8M8 13h5"/></svg></span>
              <p>Không tìm thấy bài học phù hợp</p>
            </div>
            <jsp:include page="/WEB-INF/views/partials/pagination.jsp"/>
          </c:when>
          <c:when test="${historyTab == 'lesson'}">
            <div class="jl-history-empty" role="status">
              <span class="jl-history-empty-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M4 5h16v14H4zM8 9h8M8 13h5"/></svg></span>
              <h2>Bạn chưa học bài nào</h2>
              <p>Lịch sử học sẽ xuất hiện ở đây sau khi bạn bắt đầu một bài học.</p>
              <a href="${ctx}/roadmap" class="jl-history-start">Xem lộ trình học</a>
            </div>
          </c:when>
          <c:otherwise>
            <div class="jl-history-empty" role="status">
              <span class="jl-history-empty-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M4 5h16v14H4zM8 9h8M8 13h5"/></svg></span>
              <h2>Chưa có lịch sử ở mục này</h2>
              <p>Khi bạn hoàn thành bài làm, kết quả sẽ được lưu tại đây.</p>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </section>
  </section>
</main>
<script>
(() => {
  const search = document.getElementById('history-search');
  const level = document.getElementById('history-level');
  if (!search || !level) return;
  const rows = [...document.querySelectorAll('.jl-history-row')];
  const empty = document.getElementById('history-filter-empty');
  const filter = () => {
    const query = search.value.trim().toLocaleLowerCase('vi');
    let visible = 0;
    rows.forEach(row => {
      const matches = (!query || row.dataset.search.toLocaleLowerCase('vi').includes(query)) &&
        (level.value === 'all' || row.dataset.level === level.value);
      row.hidden = !matches;
      if (matches) visible++;
    });
    empty.hidden = visible > 0;
  };
  search.addEventListener('input', filter);
  level.addEventListener('change', filter);
})();
</script>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
