<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Flash message (quy tac chung so 8). Doc tu session["flash"] roi xoa. Neu Nguoi A da co partial nay thi dung ban cua ho
     (chi can doc cung khoa session "flash" = Map<loai, noi dung> do util Flash ghi). --%>
<c:if test="${not empty sessionScope.flash}">
  <c:forEach var="f" items="${sessionScope.flash}">
    <div class="jl-flash jl-flash-<c:out value='${f.key}'/>" role="alert"><c:out value="${f.value}"/></div>
  </c:forEach>
  <c:remove var="flash" scope="session"/>
</c:if>
