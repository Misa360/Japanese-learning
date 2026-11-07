<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Video yêu thích" scope="request"/>
<c:set var="activeNav" value="favorites" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container jl-favorite-videos-page">
  <section class="jl-favorites-hero" aria-labelledby="favoritesTitle">
    <div class="jl-favorites-hero-content">
      <h1 id="favoritesTitle">Video yêu thích</h1>
      <p>Danh sách video được bạn đánh dấu vào yêu thích</p>
      <form class="jl-favorites-search" method="get" action="${ctx}/courses/favorites" role="search">
        <label class="jl-favorites-search-icon" for="favoriteSearch" aria-hidden="true">
          <svg viewBox="0 0 24 24" fill="none"><circle cx="10.8" cy="10.8" r="6.8"/><path d="m16 16 5 5"/></svg>
        </label>
        <input id="favoriteSearch" name="q" type="search" value="<c:out value='${param.q}'/>" placeholder="Tìm kiếm với tên video" aria-label="Tìm kiếm với tên video">
      </form>
    </div>
    <img class="jl-favorites-hero-art" src="${ctx}/assets/img/icons/sakura.png" alt="Hoa anh đào" loading="lazy">
  </section>

  <div class="jl-favorites-list-heading">
    <h2>Video yêu thích của bạn</h2>
    <label class="jl-favorites-filter">
      <svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M4 5h16l-6.2 7.1v5.4l-3.6 1.8v-7.2L4 5Z"/></svg>
      <select aria-label="Lọc video yêu thích">
        <option>Tất cả</option>
      </select>
      <svg class="jl-favorites-chevron" viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6Z"/></svg>
    </label>
  </div>

  <div class="jl-favorites-empty" role="status">
    <svg class="jl-favorites-empty-art" viewBox="0 0 220 190" aria-hidden="true">
      <circle cx="110" cy="91" r="79" fill="#f1f3f7"/>
      <path d="M57 76 76 39h69l19 37v71H57V76Z" fill="#fff"/>
      <path d="m57 76 19-37h69l19 37H57Z" fill="#a9adc3"/>
      <path d="M57 76h50l9 15h29l8-15h14" fill="none" stroke="#fff" stroke-width="4" stroke-dasharray="4 5"/>
      <circle cx="91" cy="112" r="5" fill="#a9adc3"/><circle cx="129" cy="112" r="5" fill="#a9adc3"/>
      <path d="M101 143h18" stroke="#a9adc3" stroke-width="7" stroke-linecap="round"/>
      <circle cx="31" cy="94" r="8" fill="#e4e7ee"/><circle cx="190" cy="79" r="6" fill="#e4e7ee"/>
      <circle cx="168" cy="25" r="6" fill="#c9ccdc"/><circle cx="178" cy="22" r="6" fill="#e0e2eb"/>
    </svg>
    <h3>Hiện tại chưa có dữ liệu nào</h3>
  </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
