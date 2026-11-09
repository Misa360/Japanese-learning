<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý khóa học" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/admin_header.jsp"/>
<main class="jl-container jl-user-layout">
  <header class="jl-page-header"><div><h1>Quản lý khóa học</h1><p class="jl-muted">Tạo và cập nhật khóa học, sau đó quản lý chương và bài học.</p></div><a class="jl-btn jl-btn-primary" href="${pageContext.request.contextPath}/admin/courses/new">Thêm khóa học</a></header>
  <div class="jl-table-wrap"><table class="jl-table"><thead><tr><th>Khóa học</th><th>Cấp độ</th><th>Giá</th><th>Trạng thái</th><th>Quản lý</th></tr></thead><tbody>
  <c:forEach var="item" items="${courses}"><tr>
    <td><strong><a href="${pageContext.request.contextPath}/admin/courses/${item.id}"><c:out value="${item.title}"/></a></strong><br><small class="jl-muted"><c:out value="${item.slug}"/></small></td>
    <td><c:out value="${item.levelCode}"/></td><td><c:choose><c:when test="${item.free}">Miễn phí</c:when><c:otherwise><c:out value="${item.unlockCoins}"/> xu</c:otherwise></c:choose></td>
    <td><c:choose><c:when test="${item.status == 'published'}"><span class="jl-admin-status jl-admin-status-published">Đã xuất bản</span></c:when><c:when test="${item.status == 'archived'}"><span class="jl-admin-status jl-admin-status-archived">Đã lưu trữ</span></c:when><c:otherwise><span class="jl-admin-status">Bản nháp</span></c:otherwise></c:choose></td>
    <td><a href="${pageContext.request.contextPath}/admin/courses/${item.id}">Mở khóa học →</a></td>
  </tr></c:forEach><c:if test="${empty courses}"><tr><td colspan="5" class="jl-admin-empty">Chưa có khóa học. Hãy tạo khóa học đầu tiên.</td></tr></c:if>
  </tbody></table></div>
</main>
<jsp:include page="/WEB-INF/views/partials/admin_footer.jsp"/>
