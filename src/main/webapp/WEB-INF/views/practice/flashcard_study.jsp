<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="${deck.title}" scope="request"/>
<c:set var="activeNav" value="flashcards" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container jl-user-layout jl-flashcard-study-layout">
  <aside><jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/></aside>
  <section class="jl-flashcard-page jl-flashcard-study-surface">
    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
    <nav class="jl-flashcard-breadcrumb" aria-label="Đường dẫn"><a href="${ctx}/dashboard">Trang chủ</a><span>/</span><a href="${ctx}/practice/flashcards">Flashcard</a><span>/</span><strong><c:out value="${deck.title}"/></strong></nav>

    <c:choose>
      <c:when test="${empty deck.cards}">
        <div class="jl-card jl-flashcard-empty"><span class="jl-flashcard-empty-icon" aria-hidden="true">✓</span>
          <h2><c:choose><c:when test="${deck.cardCount == 0}">Bộ thẻ này chưa có thẻ nào</c:when><c:otherwise>Bạn đã ôn hết thẻ đến hạn</c:otherwise></c:choose></h2>
          <p class="jl-muted"><c:choose><c:when test="${deck.cardCount == 0}">Thêm thẻ đầu tiên để bắt đầu học.</c:when><c:otherwise>Các thẻ sẽ xuất hiện lại theo lịch ôn tập.</c:otherwise></c:choose></p>
        </div>
      </c:when>
      <c:otherwise>
        <div id="flashcard-study" class="jl-flashcard-session"
             data-review-url="${ctx}/practice/flashcards/${deck.id}/review"
             data-csrf="<c:out value='${csrfToken}'/>">
          <header class="jl-flashcard-study-hero">
            <div class="jl-flashcard-deck-mark" aria-hidden="true">▣</div>
            <div class="jl-flashcard-study-title"><h1><c:out value="${deck.title}"/></h1>
              <p class="jl-flashcard-deck-kicker">BỘ THẺ FLASHCARD</p>
              <div class="jl-flashcard-meta">
                <span>▤ <strong><c:out value="${deck.cardCount}"/></strong> thuật ngữ</span>
                <c:if test="${not empty deck.levelCode}"><span class="jl-flashcard-level-badge"><c:out value="${deck.levelCode}"/></span></c:if>
                <span>◷ <strong id="remaining-count"><c:out value="${fn:length(deck.cards)}"/></strong> thẻ đến hạn</span>
              </div>
              <c:if test="${not empty deck.description}"><p class="jl-flashcard-deck-description"><c:out value="${deck.description}"/></p></c:if>
            </div>
            <a class="jl-flashcard-back-link" href="${ctx}/practice/flashcards" aria-label="Quay lại thư viện">＋</a>
          </header>

          <section class="jl-flashcard-practice-card" aria-label="Thẻ đang học">
            <div id="flashcard-items">
              <c:forEach var="card" items="${deck.cards}" varStatus="loop">
                <article class="jl-flashcard-item ${loop.index == 0 ? 'is-current' : ''}" data-card-id="${card.id}" data-index="${loop.index}" <c:if test="${loop.index != 0}">hidden</c:if>>
                  <button type="button" class="jl-flashcard-box" data-flip aria-expanded="false" aria-label="Lật thẻ để xem đáp án">
                    <span class="jl-flashcard-face jl-flashcard-front">
                      <span class="jl-flashcard-label">Mặt trước</span><span class="jl-flashcard-content" lang="ja"><c:out value="${card.frontContent}"/></span>
                      <span class="jl-flashcard-flip-hint">⟳ &nbsp; Bấm vào thẻ để lật</span>
                    </span>
                    <span class="jl-flashcard-face jl-flashcard-back" hidden>
                      <span class="jl-flashcard-label">Mặt sau</span><span class="jl-flashcard-content"><c:out value="${card.backContent}"/></span>
                      <c:if test="${not empty card.exampleText}"><span class="jl-flashcard-example" lang="ja"><c:out value="${card.exampleText}"/></span></c:if>
                      <span class="jl-flashcard-flip-hint">⟳ &nbsp; Bấm vào thẻ để xem mặt trước</span>
                    </span>
                  </button>
                  <div class="jl-flashcard-rating" data-rating-controls hidden>
                    <p>Bạn nhớ từ này như thế nào?</p>
                    <button type="button" class="jl-btn jl-rating-again" data-rating="1">Học lại <small>Ôn lại ngay</small></button>
                    <button type="button" class="jl-btn jl-rating-hard" data-rating="2">Khó <small>Khoảng 1 ngày</small></button>
                    <button type="button" class="jl-btn jl-rating-good" data-rating="3">Tốt <small>Theo lịch ôn</small></button>
                    <button type="button" class="jl-btn jl-rating-easy" data-rating="4">Dễ <small>Giãn cách lâu hơn</small></button>
                  </div>
                </article>
              </c:forEach>
            </div>
            <div class="jl-flashcard-card-footer"><span>⟳ &nbsp; Bấm vào thẻ để lật</span><span>Nghe phát âm sẽ sớm có</span></div>
          </section>

          <nav class="jl-flashcard-progress" aria-label="Điều hướng thẻ">
            <button type="button" class="jl-flashcard-step" data-prev aria-label="Thẻ trước">‹</button>
            <div class="jl-flashcard-progress-track"><p><strong id="card-number">1</strong> / <span class="jl-progress-total"><c:out value="${fn:length(deck.cards)}"/></span></p><div class="jl-flashcard-progress-bar"><span id="flashcard-progress-fill"></span></div></div>
            <button type="button" class="jl-flashcard-step is-next" data-next aria-label="Thẻ tiếp theo">›</button>
          </nav>
          <p id="flashcard-feedback" class="jl-flashcard-feedback" role="status" aria-live="polite"></p>
          <a id="flashcard-finished" class="jl-btn" href="${ctx}/practice/flashcards" hidden>Quay lại thư viện</a>
        </div>
      </c:otherwise>
    </c:choose>

    <c:if test="${not empty deck.cards}">
      <section class="jl-flashcard-vocabulary" aria-labelledby="flashcard-vocabulary-title">
        <div class="jl-flashcard-vocabulary-heading"><h2 id="flashcard-vocabulary-title">Từ vựng đến hạn</h2><span><c:out value="${fn:length(deck.cards)}"/> thẻ</span></div>
        <div class="jl-flashcard-vocabulary-grid">
          <c:forEach var="card" items="${deck.cards}" varStatus="loop">
            <article class="jl-flashcard-word ${loop.index == 0 ? 'is-current' : ''}" data-word-index="${loop.index}">
              <span class="jl-flashcard-word-kanji" lang="ja"><c:out value="${card.frontContent}"/></span>
              <span class="jl-flashcard-word-meaning"><c:out value="${card.backContent}"/></span>
            </article>
          </c:forEach>
        </div>
      </section>
    </c:if>

    <c:if test="${deck.userId == sessionScope.userId}">
      <section class="jl-card jl-flashcard-add" aria-labelledby="add-flashcard-title">
        <p class="jl-flashcard-eyebrow">MỞ RỘNG BỘ THẺ</p><h2 id="add-flashcard-title">Thêm flashcard</h2>
        <form method="post" action="${ctx}/practice/flashcards/${deck.id}/cards" class="jl-flashcard-add-form">
          <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
          <label><span>Mặt trước <small>(từ hoặc câu tiếng Nhật)</small></span><textarea name="front_content" maxlength="10000" rows="2" required></textarea></label>
          <label><span>Mặt sau <small>(cách đọc, nghĩa)</small></span><textarea name="back_content" maxlength="10000" rows="2" required></textarea></label>
          <label><span>Ví dụ (không bắt buộc)</span><textarea name="example_text" maxlength="10000" rows="2"></textarea></label>
          <button class="jl-btn" type="submit">＋ Thêm thẻ</button>
        </form>
      </section>
    </c:if>

    <c:if test="${not empty deck.recentReviews}">
      <section class="jl-card jl-flashcard-history"><h2>Ôn gần đây</h2>
        <ul><c:forEach var="review" items="${deck.recentReviews}">
          <li><span lang="ja"><c:out value="${review.frontContent}"/></span><span class="jl-muted"><c:out value="${review.ratingLabel}"/> · ôn lại sau <c:out value="${review.intervalDays}"/> ngày</span></li>
        </c:forEach></ul>
      </section>
    </c:if>
  </section>
</main>
<script src="${ctx}/assets/js/flashcard.js" defer></script>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
