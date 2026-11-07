<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Điểm danh & Day Streak" scope="request"/>
<c:set var="activeNav" value="history" scope="request"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp"/>

<main class="jl-container jl-user-layout">
  <aside>
    <jsp:include page="/WEB-INF/views/partials/sidebar_user.jsp"/>
  </aside>

  <section>
    <%-- Breadcrumb --%>
    <div class="jl-page-breadcrumb" style="font-size:0.75rem;font-weight:700;color:#94a3b8;margin-bottom:12px;">
      <a href="${ctx}/" style="color:#64748b;">Trang chủ</a> / <span style="color:var(--jl-teal);">Điểm danh &amp; Streak</span>
    </div>

    <jsp:include page="/WEB-INF/views/partials/flash.jsp"/>

    <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(320px, 1fr));gap:24px;align-items:start;">
      <%-- Card 1: Day Streak Popup Replica matching image_20f4955f.png --%>
      <div class="cartoon-card" style="padding:28px;text-align:center;border-color:var(--jl-teal);">
        <div style="display:flex;align-items:center;justify-content:center;gap:12px;margin-bottom:20px;">
          <div style="text-align:left;">
            <div style="font-size:2.2rem;font-weight:900;color:var(--jl-teal);line-height:1;">1 ngày</div>
            <div style="font-size:1.6rem;font-weight:900;color:var(--jl-teal);letter-spacing:0.04em;">streak !</div>
          </div>
        </div>

        <div style="border:2px solid #99f6e4;border-radius:18px;padding:16px;background:#fff;margin-bottom:20px;">
          <div style="display:grid;grid-template-columns:repeat(7, 1fr);gap:6px;margin-bottom:12px;">
            <div><span style="font-size:11px;font-weight:700;color:#94a3b8;">T4</span><div class="jl-streak-petal" style="background:#f0fdfa;"></div></div>
            <div><span style="font-size:11px;font-weight:700;color:#94a3b8;">T5</span><div class="jl-streak-petal" style="background:#f0fdfa;"></div></div>
            <div><span style="font-size:11px;font-weight:700;color:#94a3b8;">T6</span><div class="jl-streak-petal" style="background:#f0fdfa;"></div></div>
            <div><span style="font-size:11px;font-weight:700;color:#94a3b8;">T7</span><div class="jl-streak-petal" style="background:#f0fdfa;"></div></div>
            <div><span style="font-size:11px;font-weight:700;color:#94a3b8;">CN</span><div class="jl-streak-petal" style="background:#f0fdfa;"></div></div>
            <div><span style="font-size:11px;font-weight:700;color:#94a3b8;">T2</span><div class="jl-streak-petal" style="background:#f0fdfa;"></div></div>
            <div><span style="font-size:11px;font-weight:700;color:#94a3b8;">T3</span><div class="jl-streak-petal" style="background:var(--jl-teal);color:#fff;font-weight:900;">✓</div></div>
          </div>
          <p style="font-size:0.875rem;font-weight:800;color:var(--jl-teal-dark);">
            Chúc mừng! Bạn đã duy trì Day Streak thành công!
          </p>
        </div>

        <div style="display:flex;align-items:center;justify-content:space-between;padding:0 8px;">
          <div style="display:flex;align-items:center;gap:8px;">
            <span style="font-size:1.25rem;font-weight:900;color:#f59e0b;">x1 lần</span>
          </div>

          <form method="post" action="${ctx}/reward/checkin">
            <input type="hidden" name="csrf_token" value="<c:out value='${csrfToken}'/>">
            <button type="submit" class="jl-btn" style="padding:8px 20px;">Điểm danh ngay</button>
          </form>
        </div>
      </div>

      <%-- Card 2: Lịch sử học (Study Calendar) matching image_75d1624c.png --%>
      <div class="cartoon-card" style="padding:24px;">
        <h3 style="font-size:1.1rem;font-weight:900;margin-bottom:14px;color:var(--jl-dark);">Lịch sử học</h3>

        <div style="border:2px solid var(--jl-teal);border-radius:20px;padding:16px;background:#fff;margin-bottom:16px;">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:14px;font-weight:800;">
            <button type="button" class="cartoon-btn-yellow" style="padding:2px 8px;font-size:11px;">&lt;</button>
            <span style="font-size:0.95rem;color:var(--jl-dark);">Tháng 11/2026</span>
            <button type="button" class="cartoon-btn-yellow" style="padding:2px 8px;font-size:11px;">&gt;</button>
          </div>

          <div style="display:grid;grid-template-columns:repeat(7, 1fr);gap:4px;text-align:center;font-size:11px;font-weight:700;color:#94a3b8;margin-bottom:8px;">
            <span>SUN</span><span>MON</span><span>TUE</span><span>WED</span><span>THU</span><span>FRI</span><span>SAT</span>
          </div>

          <div style="display:grid;grid-template-columns:repeat(7, 1fr);gap:6px;text-align:center;font-size:12px;font-weight:800;">
            <%-- Day 1 & 2: Chưa học (pink) --%>
            <div style="padding:8px 0;background:#fee2e2;color:#ef4444;border-radius:8px;">1</div>
            <div style="padding:8px 0;background:#fee2e2;color:#ef4444;border-radius:8px;">2</div>
            <%-- Day 3: Đã học (teal) --%>
            <div style="padding:8px 0;background:var(--jl-teal);color:#fff;border-radius:8px;">3</div>
            <%-- Days 4..30 --%>
            <c:forEach var="dNum" begin="4" end="30">
              <div style="padding:8px 0;color:#334155;border-radius:8px;">${dNum}</div>
            </c:forEach>
          </div>
        </div>

        <%-- Legend matching image_75d1624c.png --%>
        <div style="display:flex;gap:18px;font-size:0.75rem;font-weight:700;color:#334155;padding:0 4px;">
          <div style="display:flex;align-items:center;gap:6px;">
            <span style="width:14px;height:14px;background:#99f6e4;border-radius:4px;display:inline-block;"></span>
            <span>Đã học</span>
          </div>
          <div style="display:flex;align-items:center;gap:6px;">
            <span style="width:14px;height:14px;background:#fee2e2;border-radius:4px;display:inline-block;"></span>
            <span>Chưa học</span>
          </div>
          <div style="display:flex;align-items:center;gap:6px;">
            <span style="width:14px;height:14px;background:#2563eb;border-radius:4px;display:inline-block;"></span>
            <span>Đóng băng</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>

