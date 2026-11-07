<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Component phan trang dung chung. Servlet dat 2 request attribute:
    pager         : com.jlearn.model.Pager
    pagerBaseUrl  : duong dan goc, vd "/jlearn/history" (khong kem ?page=)
  Dung: <jsp:include page="/WEB-INF/views/partials/pagination.jsp"/>
--%>
<c:if test="${pager.totalPages > 1}">
  <nav class="jl-pagination" aria-label="Phân trang">
    <ul>
      <c:if test="${pager.hasPrev}"><li><a href="<c:out value='${pagerBaseUrl}'/>?page=${pager.page - 1}" rel="prev">‹ Trước</a></li></c:if>
      <c:if test="${pager.window[0] > 1}">
        <li><a href="<c:out value='${pagerBaseUrl}'/>?page=1">1</a></li>
        <c:if test="${pager.window[0] > 2}"><li class="jl-ellipsis">…</li></c:if>
      </c:if>
      <c:forEach var="p" items="${pager.window}">
        <li><a href="<c:out value='${pagerBaseUrl}'/>?page=${p}" class="${p == pager.page ? 'is-active' : ''}" <c:if test="${p == pager.page}">aria-current="page"</c:if>>${p}</a></li>
      </c:forEach>
      <c:set var="lastShown" value="${pager.window[pager.window.size() - 1]}"/>
      <c:if test="${lastShown < pager.totalPages}">
        <c:if test="${lastShown < pager.totalPages - 1}"><li class="jl-ellipsis">…</li></c:if>
        <li><a href="<c:out value='${pagerBaseUrl}'/>?page=${pager.totalPages}">${pager.totalPages}</a></li>
      </c:if>
      <c:if test="${pager.hasNext}"><li><a href="<c:out value='${pagerBaseUrl}'/>?page=${pager.page + 1}" rel="next">Sau ›</a></li></c:if>
    </ul>
  </nav>
</c:if>
