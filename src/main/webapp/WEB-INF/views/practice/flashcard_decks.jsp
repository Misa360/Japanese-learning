<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Flashcard" scope="request"/>
<c:set var="activeNav" value="flashcards" scope="request"/>
<c:set var="myDeckCount" value="0"/><c:set var="myCardCount" value="0"/>
<c:set var="sharedDeckCount" value="0"/><c:set var="sharedCardCount" value="0"/>
<c:set var="dueCardCount" value="0"/>
<c:forEach var="item" items="${decks}">
  <c:set var="dueCardCount" value="${dueCardCount + item.dueCount}"/>
  <c:choose>
    <c:when test="${item.userId == sessionScope.userId}">
      <c:set var="myDeckCount" value="${myDeckCount + 1}"/>
      <c:set var="myCardCount" value="${myCardCount + item.cardCount}"/>
    </c:when>
    <c:otherwise>
      <c:set var="sharedDeckCount" value="${sharedDeckCount + 1}"/>
      <c:set var="sharedCardCount" value="${sharedCardCount + item.cardCount}"/>
    </c:otherwise>
  </c:choose>
</c:forEach>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<div class="jl-flashcard-surface">
  <main class="jl-flashcard-home">
    <p class="jl-flashcard-breadcrumb"><a href="${ctx}/dashboard">Trang chủ</a><span>/</span><strong>Flashcard</strong></p>
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

    <section class="jl-flashcard-hero">
      <div class="jl-flashcard-mascot" aria-hidden="true"><img src="${ctx}/assets/img/icons/sakura2.png" alt=""></div>
      <div class="jl-flashcard-hero-content">
        <p class="jl-flashcard-eyebrow">MỖI NGÀY MỘT CHÚT, TIẾN BỘ THẬT NHIỀU</p>
        <h1>Xin chào, <c:choose><c:when test="${not empty sessionScope.fullName}"><c:out value="${sessionScope.fullName}"/></c:when><c:when test="${not empty sessionScope.username}"><c:out value="${sessionScope.username}"/></c:when><c:otherwise>bạn</c:otherwise></c:choose>!</h1>
        <p class="jl-flashcard-level-line">Cùng ôn từ vựng tiếng Nhật theo lộ trình JLPT N5–N1 nhé.</p>
        <form class="jl-flashcard-search" method="get" action="${ctx}/practice/flashcards">
          <label class="jl-sr-only" for="deck-search">Tìm kiếm bài học, thư mục</label>
          <span aria-hidden="true">⌕</span>
          <input id="deck-search" type="search" name="q" value="<c:out value='${search}'/>" placeholder="Tìm kiếm bài học, thư mục...">
          <button class="cartoon-btn-yellow" type="submit">Tìm</button>
        </form>
      </div>
    </section>

    <section class="jl-flashcard-room" aria-label="Luyện tập flashcard">
      <div class="jl-flashcard-card-fan" aria-hidden="true"><span>い</span><b>お</b><span>あ</span></div>
      <div class="jl-flashcard-room-copy"><h2>Phòng chơi Flashcard</h2><p>Luyện tập flashcard mỗi ngày để nhớ từ lâu hơn.</p></div>
      <a class="cartoon-btn-yellow" href="#deck-library"><span aria-hidden="true">▦</span> Chọn bộ thẻ</a>
    </section>

    <section class="jl-flashcard-category-grid" aria-label="Danh mục flashcard">
      <a class="jl-flashcard-category" href="#deck-library">
        <span class="jl-flashcard-category-icon is-coral" aria-hidden="true">▤</span><h2>Flashcard của tôi</h2>
        <p>▪ <c:out value="${myDeckCount}"/> bộ thẻ</p><p>▪ <c:out value="${myCardCount}"/> thuật ngữ</p>
      </a>
      <div class="jl-flashcard-category is-disabled" aria-label="Flashcard ưa thích, chưa hỗ trợ">
        <span class="jl-flashcard-category-icon is-yellow" aria-hidden="true"></span><h2>Flashcard ưa thích</h2>
        <p>Đánh dấu yêu thích sẽ có sau</p>
      </div>
      <div class="jl-flashcard-category is-disabled" aria-label="Các lỗi sai cũ, chưa hỗ trợ">
        <span class="jl-flashcard-category-icon is-mint" aria-hidden="true"></span><h2>Các lỗi sai cũ</h2>
        <p><c:out value="${dueCardCount}"/> thẻ đang cần ôn</p>
      </div>
      <a class="jl-flashcard-category" href="#deck-library">
        <span class="jl-flashcard-category-icon is-blue" aria-hidden="true"></span><h2>Flashcard được chia sẻ</h2>
        <p>▪ <c:out value="${sharedDeckCount}"/> bộ thẻ</p><p>▪ <c:out value="${sharedCardCount}"/> thuật ngữ</p>
      </a>
      <details class="jl-flashcard-category jl-flashcard-add-category">
        <summary><span class="jl-flashcard-add-icon" aria-hidden="true">＋</span><strong>Thêm mới</strong></summary>
        <form method="post" action="${ctx}/practice/flashcards" class="jl-flashcard-create-form">
          <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
          <label><span>Tên bộ thẻ</span><input name="title" maxlength="200" required placeholder="Ví dụ: Từ vựng N5 bài 1"></label>
          <label><span>Mô tả</span><textarea name="description" maxlength="4000" rows="2" placeholder="Mô tả ngắn (không bắt buộc)"></textarea></label>
          <button class="cartoon-btn-yellow" type="submit">Tạo bộ thẻ</button>
        </form>
      </details>
    </section>

    <section id="deck-library" class="jl-flashcard-recent">
      <header class="jl-flashcard-recent-heading"><span>Thư viện bộ thẻ</span>
        <strong class="jl-flashcard-due-summary"><c:out value="${dueCardCount}"/> thẻ cần ôn hôm nay</strong>
      </header>
      <c:choose>
        <c:when test="${empty decks}"><div class="jl-flashcard-no-decks"><h2>Chưa có bộ thẻ phù hợp</h2><p>Hãy tạo bộ thẻ mới hoặc thử từ khóa tìm kiếm khác.</p></div></c:when>
        <c:otherwise><div class="jl-flashcard-deck-grid">
          <c:forEach var="deck" items="${decks}">
            <article class="jl-flashcard-deck-card">
              <div class="jl-flashcard-deck-card-heading"><h2><c:out value="${deck.title}"/></h2><span aria-hidden="true">•••</span></div>
              <p class="jl-flashcard-description"><c:out value="${empty deck.description ? 'Bộ thẻ luyện tập tiếng Nhật.' : deck.description}"/></p>
              <div class="jl-flashcard-deck-meta"><span>◉ <c:out value="${deck.cardCount}"/> thuật ngữ</span>
                <c:if test="${not empty deck.levelCode}"><span><c:out value="${deck.levelCode}"/></span></c:if></div>
              <p class="jl-flashcard-deck-owner"><c:choose><c:when test="${deck.userId == sessionScope.userId}">Bộ thẻ của bạn</c:when><c:otherwise>Thư viện JLearn</c:otherwise></c:choose></p>
              <a href="${ctx}/practice/flashcards/${deck.id}" class="cartoon-btn-yellow">
                <c:choose><c:when test="${deck.dueCount > 0}">Học ngay · <c:out value="${deck.dueCount}"/> thẻ đến hạn</c:when><c:otherwise>Xem bộ thẻ</c:otherwise></c:choose>
              </a>
            </article>
          </c:forEach>
        </div></c:otherwise>
      </c:choose>
    </section>
  </main>
</div>
<script src="${ctx}/assets/js/flashcard.js" defer></script>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
