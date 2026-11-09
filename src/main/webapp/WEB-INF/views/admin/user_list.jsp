<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Người dùng" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout jl-admin-users-page">
  <header class="jl-page-header"><div><h1>Quản lý người dùng</h1><p class="jl-muted">Tìm kiếm, phân quyền và kiểm soát trạng thái tài khoản.</p></div></header>
  <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
  <form method="get" action="${ctx}/admin/users" class="jl-admin-user-filters">
    <label class="jl-admin-user-search"><span>⌕</span><input type="search" name="q" value="<c:out value='${query}'/>" placeholder="Tìm tên, email hoặc tên đăng nhập" aria-label="Tìm người dùng"></label>
    <select name="role" aria-label="Lọc vai trò"><option value="">Tất cả vai trò</option><option value="user" <c:if test="${roleFilter == 'user'}">selected</c:if>>Học viên</option><option value="admin" <c:if test="${roleFilter == 'admin'}">selected</c:if>>Quản trị viên</option></select>
    <select name="status" aria-label="Lọc trạng thái"><option value="">Mọi trạng thái</option><option value="active" <c:if test="${statusFilter == 'active'}">selected</c:if>>Đang hoạt động</option><option value="locked" <c:if test="${statusFilter == 'locked'}">selected</c:if>>Đã khóa</option></select>
    <button class="jl-btn" type="submit">Lọc danh sách</button><a class="jl-btn jl-btn-outline" href="${ctx}/admin/users">Đặt lại</a>
  </form>
  <div class="jl-admin-user-summary"><strong><fmt:formatNumber value="${pager.total}" groupingUsed="true"/></strong> tài khoản</div>
  <div class="jl-table-wrap"><table class="jl-table jl-admin-users-table"><thead><tr><th>Người dùng</th><th>Vai trò</th><th>Trạng thái</th><th>Xu</th><th>Ngày tham gia</th><th>Lần đăng nhập cuối</th><th>Quản lý</th></tr></thead><tbody>
    <c:forEach var="user" items="${users}"><tr>
      <td><strong><c:out value="${user.fullName}"/></strong><small class="jl-admin-user-sub"><c:out value="${user.email}"/><c:if test="${not empty user.username}"> · @<c:out value="${user.username}"/></c:if></small></td>
      <td><span class="jl-admin-user-role ${user.roleName == 'admin' ? 'is-admin' : ''}"><c:choose><c:when test="${user.roleName == 'admin'}">Quản trị viên</c:when><c:otherwise>Học viên</c:otherwise></c:choose></span></td>
      <td><span class="jl-admin-status ${user.status == 'active' ? 'jl-admin-status-published' : 'jl-admin-status-archived'}"><c:choose><c:when test="${user.status == 'active'}">Đang hoạt động</c:when><c:otherwise>Đã khóa</c:otherwise></c:choose></span></td>
      <td><fmt:formatNumber value="${user.coinBalance}" groupingUsed="true"/></td>
      <td><fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy"/></td>
      <td><c:choose><c:when test="${not empty user.lastLoginAt}"><fmt:formatDate value="${user.lastLoginAt}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise><span class="jl-muted">Chưa đăng nhập</span></c:otherwise></c:choose></td>
      <td><div class="jl-admin-user-actions">
        <form method="post" action="${ctx}/admin/users/${user.id}/role" class="jl-admin-inline-form">
          <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="return_q" value="<c:out value='${query}'/>"><input type="hidden" name="return_role" value="<c:out value='${roleFilter}'/>"><input type="hidden" name="return_status" value="<c:out value='${statusFilter}'/>">
          <select name="role" aria-label="Vai trò của ${user.email}" <c:if test="${user.id == currentUserId}">disabled</c:if>><option value="user" <c:if test="${user.roleName == 'user'}">selected</c:if>>Học viên</option><option value="admin" <c:if test="${user.roleName == 'admin'}">selected</c:if>>Quản trị viên</option></select>
          <button type="submit" class="jl-admin-small-action" <c:if test="${user.id == currentUserId}">disabled title="Không thể đổi vai trò tài khoản đang sử dụng"</c:if>>Lưu vai trò</button>
        </form>
        <form method="post" action="${ctx}/admin/users/${user.id}/status" class="jl-admin-inline-form">
          <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>"><input type="hidden" name="return_q" value="<c:out value='${query}'/>"><input type="hidden" name="return_role" value="<c:out value='${roleFilter}'/>"><input type="hidden" name="return_status" value="<c:out value='${statusFilter}'/>">
          <input type="hidden" name="status" value="${user.status == 'active' ? 'locked' : 'active'}">
          <button type="submit" class="jl-admin-small-action ${user.status == 'active' ? 'is-danger' : ''}" <c:if test="${user.id == currentUserId}">disabled title="Không thể đổi trạng thái tài khoản đang sử dụng"</c:if>><c:choose><c:when test="${user.status == 'active'}">Khóa</c:when><c:otherwise>Mở khóa</c:otherwise></c:choose></button>
        </form>
      </div></td>
    </tr></c:forEach>
    <c:if test="${empty users}"><tr><td colspan="7" class="jl-admin-empty">Không tìm thấy tài khoản phù hợp.</td></tr></c:if>
  </tbody></table></div>
  <c:if test="${pager.totalPages > 1}"><nav class="jl-pagination" aria-label="Phân trang người dùng"><ul>
    <c:if test="${pager.hasPrev}"><c:url var="prevUrl" value="/admin/users"><c:param name="page" value="${pager.page - 1}"/><c:if test="${not empty query}"><c:param name="q" value="${query}"/></c:if><c:if test="${not empty roleFilter}"><c:param name="role" value="${roleFilter}"/></c:if><c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}"/></c:if></c:url><li><a href="${prevUrl}">‹ Trước</a></li></c:if>
    <c:forEach var="p" items="${pager.window}"><c:url var="pageUrl" value="/admin/users"><c:param name="page" value="${p}"/><c:if test="${not empty query}"><c:param name="q" value="${query}"/></c:if><c:if test="${not empty roleFilter}"><c:param name="role" value="${roleFilter}"/></c:if><c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}"/></c:if></c:url><li><a href="${pageUrl}" class="${p == pager.page ? 'is-active' : ''}"><c:out value="${p}"/></a></li></c:forEach>
    <c:if test="${pager.hasNext}"><c:url var="nextUrl" value="/admin/users"><c:param name="page" value="${pager.page + 1}"/><c:if test="${not empty query}"><c:param name="q" value="${query}"/></c:if><c:if test="${not empty roleFilter}"><c:param name="role" value="${roleFilter}"/></c:if><c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}"/></c:if></c:url><li><a href="${nextUrl}">Sau ›</a></li></c:if>
  </ul></nav></c:if>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
