<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thống kê" scope="request"/>
<c:set var="activeNav" value="coin-stats" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<style>
  .coin-page{width:100%;max-width:none;margin:0 auto;padding:24px 0 52px;color:#334155}
  .coin-crumb{font-size:15px;font-weight:800;color:#426267;margin-bottom:22px}
  .coin-crumb a{color:#0f8f89;text-decoration:none}
  .coin-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));align-items:start;gap:clamp(18px,2vw,42px)}
  .coin-card{position:relative;border:0;border-radius:20px;padding:20px 24px;box-shadow:none}
  .coin-streak{background:#3dc0c2;color:#fff}
  .coin-balance{background:#ffb600;color:#fff}
  .coin-card-head{display:flex;align-items:flex-start;justify-content:space-between;gap:18px}
  .coin-number{font-size:40px;line-height:1;font-weight:900;letter-spacing:-1px}
  .coin-label{font-size:20px;font-weight:900;margin-top:4px}
  .coin-card-icon{width:92px;height:92px;flex:0 0 92px;margin:4px 2px 0 0}
  .coin-week{display:grid;grid-template-columns:repeat(7,minmax(0,1fr));gap:7px;min-height:170px;align-items:center;background:#eef8fa;border:0;border-radius:18px;padding:12px 8px;margin-top:16px;color:#334155}
  .coin-day{text-align:center;min-width:0}
  .coin-day-name{font-size:clamp(14px,1.2vw,20px);font-weight:900;color:#9da7bd;margin-bottom:7px}
  .coin-flower{width:58px;height:58px;margin:auto;display:grid;place-items:center;background:transparent;color:#cfeceb}
  .coin-flower.checked{color:#2fb4aa}
  .coin-flower.today{outline:0}
  .coin-flower svg{width:100%;height:100%}
  .coin-note{min-height:170px;display:flex;align-items:center;background:#fff1f1;border:0;border-radius:18px;padding:18px;margin-top:16px;color:#292b35;font-size:14px;line-height:1.7;font-weight:600}
  .coin-note ul{margin:0;padding-left:1.2em}
  .coin-note li+li{margin-top:5px}
  .coin-history{background:transparent;border:0;border-radius:0;padding:0;margin-top:24px;box-shadow:none}
  .coin-history-head{display:flex;justify-content:space-between;align-items:center;gap:12px;margin-bottom:16px}
  .coin-history h2{margin:0;font-size:22px;color:#1e293b}
  .coin-history-count{background:#f1f5f9;border-radius:999px;padding:7px 12px;font-size:13px;font-weight:800;color:#64748b}
  .coin-transaction{display:flex;justify-content:space-between;align-items:center;gap:16px;padding:15px 0;border-top:1px solid #eef2f5}
  .coin-transaction-main{display:flex;align-items:center;gap:13px;min-width:0}
  .coin-sign{width:42px;height:42px;flex:0 0 42px;display:grid;place-items:center;border-radius:13px;background:#e1f7f3;color:#13867e;font-size:24px;font-weight:800}
  .coin-sign.spend{background:#fff0e9;color:#bd6037}
  .coin-description{font-weight:800;color:#253f44;overflow-wrap:anywhere}
  .coin-date{font-size:13px;color:#879499;margin-top:3px}
  .coin-amount{white-space:nowrap;font-size:17px;font-weight:900;color:#14877e}
  .coin-amount.spend{color:#bd6037}
  .coin-empty{padding:28px 14px;text-align:center;background:#f7faf9;border-radius:16px;color:#708087}
  @media(max-width:700px){.coin-page{padding:20px 0 36px}.coin-grid{grid-template-columns:1fr}.coin-card{padding:20px 18px}.coin-card-icon{width:66px;height:66px;flex-basis:66px}.coin-week{min-height:130px;padding:12px 6px}.coin-flower{width:46px;height:46px}.coin-history{padding:0}.coin-transaction{align-items:flex-start}.coin-amount{font-size:15px}}
</style>

<main class="coin-page">
  <div class="coin-crumb"><a href="${ctx}/dashboard">Trang chủ</a> / <span>Thống kê</span></div>

  <section class="coin-grid" aria-label="Tổng quan điểm danh và coin">
    <div class="coin-card coin-streak">
      <div class="coin-card-head">
        <div><div class="coin-number"><c:out value="${dashboard.streakDays}"/></div><div class="coin-label">ngày streak</div></div>
        <svg class="coin-card-icon" viewBox="0 0 120 120" aria-hidden="true">
          <g fill="none" stroke="#d7ffff" stroke-width="4">
            <circle cx="60" cy="32" r="23"/><circle cx="86" cy="50" r="23"/><circle cx="76" cy="80" r="23"/><circle cx="44" cy="80" r="23"/><circle cx="34" cy="50" r="23"/>
          </g>
          <g fill="#d7ffff" fill-opacity=".38"><circle cx="60" cy="45" r="11"/><circle cx="48" cy="58" r="11"/><circle cx="72" cy="58" r="11"/><circle cx="60" cy="71" r="11"/></g>
        </svg>
      </div>
      <div class="coin-week" aria-label="Điểm danh trong 7 ngày gần nhất">
        <c:forEach var="day" items="${weekDays}">
          <div class="coin-day">
            <div class="coin-day-name"><c:out value="${day.label}"/></div>
            <div class="coin-flower ${day.checked ? 'checked' : ''} ${day.today ? 'today' : ''}" title="${day.dateLabel}">
              <svg viewBox="0 0 24 24" aria-hidden="true"><g fill="currentColor"><circle cx="12" cy="5.5" r="4.1"/><circle cx="18.2" cy="10" r="4.1"/><circle cx="15.8" cy="17.2" r="4.1"/><circle cx="8.2" cy="17.2" r="4.1"/><circle cx="5.8" cy="10" r="4.1"/></g></svg>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>

    <div class="coin-card coin-balance">
      <div class="coin-card-head">
        <div><div class="coin-number"><fmt:formatNumber value="${dashboard.coinBalance}" groupingUsed="true"/></div><div class="coin-label">coin</div></div>
        <svg class="coin-card-icon" viewBox="0 0 100 100" aria-hidden="true">
          <circle cx="50" cy="50" r="45" fill="#fff5d5"/>
          <circle cx="50" cy="50" r="37" fill="#fff9e8"/>
          <path d="m50 20 9 18 20 3-14.5 14 3.5 20L50 65l-18 10 3.5-20L21 41l20-3Z" fill="#ffc62f" stroke="#d78d00" stroke-width="3" stroke-linejoin="round"/>
          <path d="m50 31 6 12 13 2-9.5 9 2.3 13L50 61l-12 6 2.3-13-9.5-9 13-2Z" fill="#ffdf63"/>
        </svg>
      </div>
      <div class="coin-note">
        <ul>
          <li>Giữ chuỗi streak để được cộng nhiều coin hơn.</li>
          <li>Hãy tiếp tục học để nhận thêm coin cho bản thân nhé!</li>
        </ul>
      </div>
    </div>
  </section>

  <section class="coin-history" aria-labelledby="coin-history-title">
    <div class="coin-history-head">
      <h2 id="coin-history-title">Lịch sử Coin</h2>
      <span class="coin-history-count"><c:out value="${fn:length(transactions)}"/> giao dịch gần nhất</span>
    </div>
    <c:choose>
      <c:when test="${not empty transactions}">
        <c:forEach var="transaction" items="${transactions}">
          <div class="coin-transaction">
            <div class="coin-transaction-main">
              <span class="coin-sign ${transaction.amount lt 0 ? 'spend' : ''}" aria-hidden="true"><c:out value="${transaction.amount lt 0 ? '−' : '+'}"/></span>
              <div>
                <div class="coin-description"><c:choose><c:when test="${not empty transaction.description}"><c:out value="${transaction.description}"/></c:when><c:otherwise><c:out value="${transaction.type}"/></c:otherwise></c:choose></div>
                <div class="coin-date"><fmt:formatDate value="${transaction.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
              </div>
            </div>
            <div class="coin-amount ${transaction.amount lt 0 ? 'spend' : ''}"><fmt:formatNumber value="${transaction.amount}" groupingUsed="true"/> coin</div>
          </div>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <div class="coin-empty">Bạn chưa có giao dịch coin nào.</div>
      </c:otherwise>
    </c:choose>
  </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
