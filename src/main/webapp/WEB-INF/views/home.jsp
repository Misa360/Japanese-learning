<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Học tiếng Nhật cùng Riki" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="riki-home">
  <section class="riki-hero">
    <div class="riki-hero-copy">
      <span class="riki-eyebrow"><span class="riki-live-dot"></span> HỌC TIẾNG NHẬT CÙNG RIKI</span>
      <h1>Chinh phục tiếng Nhật<br><em>theo cách của bạn</em></h1>
      <p>Lộ trình từ N5 đến N1, bài học dễ hiểu và luyện tập mỗi ngày. Bắt đầu miễn phí và tiến bộ từng bước.</p>
      <div class="riki-hero-actions">
        <a class="riki-primary-btn" href="${ctx}/register">Bắt đầu học miễn phí <span aria-hidden="true">→</span></a>
        <a class="riki-text-link" href="${ctx}/roadmap">Khám phá lộ trình <span aria-hidden="true">↗</span></a>
      </div>
      <div class="riki-trust-line"><span class="riki-avatar-stack"><b>あ</b><b>日</b><b>本</b></span><span>Học từ căn bản đến JLPT N1</span></div>
    </div>
    <div class="riki-hero-art" aria-label="Minh họa học tiếng Nhật">
      <div class="riki-sun"></div>
      <div class="riki-kanji-card"><small>HÔM NAY HỌC</small><strong>日本語</strong><span>にほんご · tiếng Nhật</span></div>
      <div class="riki-book book-one">あ</div><div class="riki-book book-two">ア</div>
      <div class="riki-mascot" aria-hidden="true"><i></i></div>
      <div class="riki-speech">がんばろう！</div>
      <div class="riki-art-caption">Mỗi ngày một chút,<br>tiến bộ thật nhiều</div>
    </div>
  </section>

  <section class="riki-level-strip" aria-label="Các cấp độ JLPT">
    <div><span class="riki-strip-label">LỘ TRÌNH HỌC TẬP</span><strong>Chọn cấp độ phù hợp với bạn</strong></div>
    <div class="riki-levels"><a href="${ctx}/roadmap">N5</a><a href="${ctx}/roadmap">N4</a><a href="${ctx}/roadmap">N3</a><a href="${ctx}/roadmap">N2</a><a href="${ctx}/roadmap">N1</a></div>
  </section>

  <section class="riki-features">
    <div class="riki-section-heading"><div><span class="riki-strip-label">HỌC VUI, NHỚ LÂU</span><h2>Cùng bạn đi đến mục tiêu</h2></div><a href="${ctx}/roadmap">Xem lộ trình <span aria-hidden="true">→</span></a></div>
    <div class="riki-feature-grid">
      <article class="riki-feature-card feature-courses"><div class="riki-feature-icon">本</div><span class="riki-feature-kicker">BÀI HỌC THEO LỘ TRÌNH</span><h3>Học có định hướng</h3><p>Từng bài học được sắp xếp theo cấp độ, giúp bạn xây nền tảng vững chắc.</p><a href="${ctx}/roadmap" aria-label="Xem lộ trình học">Khám phá <span aria-hidden="true">→</span></a></article>
      <article class="riki-feature-card feature-practice"><div class="riki-feature-icon">あ</div><span class="riki-feature-kicker">LUYỆN TẬP MỖI NGÀY</span><h3>Ghi nhớ tự nhiên</h3><p>Ôn từ vựng, Kanji và ngữ pháp với flashcard cùng các bài quiz.</p><a href="${ctx}/register" aria-label="Bắt đầu luyện tập">Bắt đầu luyện <span aria-hidden="true">→</span></a></article>
      <article class="riki-feature-card feature-jlpt"><div class="riki-feature-icon">合</div><span class="riki-feature-kicker">SẴN SÀNG CHO JLPT</span><h3>Tự tin trước kỳ thi</h3><p>Luyện đề theo cấp độ và theo dõi tiến độ học tập của bạn.</p><a href="${ctx}/roadmap" aria-label="Xem luyện thi JLPT">Tìm hiểu thêm <span aria-hidden="true">→</span></a></article>
    </div>
  </section>

  <section class="riki-bottom-cta"><div><span class="riki-strip-label">BẮT ĐẦU NGAY HÔM NAY</span><h2>Hành trình tiếng Nhật của bạn bắt đầu từ đây.</h2></div><a class="riki-primary-btn" href="${ctx}/register">Đăng ký miễn phí <span aria-hidden="true">→</span></a></section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
