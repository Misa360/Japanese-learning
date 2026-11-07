/* F16 + F15 (phan giao dien): luu vi tri video dinh ky va bam "Hoan thanh bai" khong tai lai trang.
 * Can: #lesson-player[data-position-url, data-csrf, data-start, data-duration, data-video-id]
 * Server chi in ra video da duoc VideoEmbed cho phep; o day khong tu dung URL tu du lieu nguoi dung. */
(function () {
  'use strict';

  var SAVE_EVERY_MS = 20000;   // 15-30 giay theo dac ta
  var MIN_DELTA = 5;           // khong gui neu vi tri doi < 5 giay

  function postForm(url, data, csrf) {
    var body = new URLSearchParams(data).toString();
    return fetch(url, {
      method: 'POST',
      credentials: 'same-origin',
      keepalive: true,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'X-CSRF-Token': csrf,
        'X-Requested-With': 'XMLHttpRequest',
        'Accept': 'application/json'
      },
      body: body
    });
  }

  /* ---------------- F16: luu vi tri ---------------- */
  function initPlayer() {
    var root = document.getElementById('lesson-player');
    if (!root) return;
    var url = root.getAttribute('data-position-url');
    var csrf = root.getAttribute('data-csrf') || '';
    var start = parseInt(root.getAttribute('data-start') || '0', 10) || 0;
    var duration = parseInt(root.getAttribute('data-duration') || '0', 10) || 0;
    var videoId = root.getAttribute('data-video-id') || '';
    var getTime = null;
    var timer = null;
    var lastSent = -1;

    function clamp(s) {
      s = Math.max(0, Math.floor(s || 0));
      return duration > 0 ? Math.min(s, duration) : s;
    }

    function save(force) {
      if (!getTime || !url) return;
      var sec = clamp(getTime());
      if (!force && lastSent >= 0 && Math.abs(sec - lastSent) < MIN_DELTA) return;
      lastSent = sec;
      postForm(url, { position_seconds: String(sec) }, csrf).catch(function () { /* mang chap chon: bo qua, lan sau gui lai */ });
    }

    function startTimer() { if (!timer) timer = setInterval(function () { save(false); }, SAVE_EVERY_MS); }
    function stopTimer() { if (timer) { clearInterval(timer); timer = null; } }

    document.addEventListener('visibilitychange', function () { if (document.visibilityState === 'hidden') save(true); });
    window.addEventListener('pagehide', function () { save(true); });

    var html5 = document.getElementById('html5-player');
    if (html5) {
      getTime = function () { return html5.currentTime; };
      html5.addEventListener('loadedmetadata', function () {
        if (start > 0 && (!html5.duration || start < html5.duration - 5)) html5.currentTime = start;
      });
      html5.addEventListener('play', startTimer);
      html5.addEventListener('pause', function () { stopTimer(); save(true); });
      html5.addEventListener('ended', function () { stopTimer(); save(true); });
      return;
    }

    if (!videoId || !document.getElementById('yt-player')) return;
    function createYT() {
      var player = new YT.Player('yt-player', {
        videoId: videoId,
        host: 'https://www.youtube-nocookie.com',
        playerVars: { rel: 0, modestbranding: 1, playsinline: 1, start: start, origin: window.location.origin },
        events: {
          onReady: function () { getTime = function () { return player.getCurrentTime(); }; },
          onStateChange: function (e) {
            if (e.data === 1) startTimer();                         // dang phat
            else if (e.data === 2 || e.data === 0) { stopTimer(); save(true); }   // tam dung / het
          }
        }
      });
    }
    if (window.YT && window.YT.Player) { createYT(); }
    else {
      var prev = window.onYouTubeIframeAPIReady;
      window.onYouTubeIframeAPIReady = function () { if (typeof prev === 'function') prev(); createYT(); };
      var s = document.createElement('script');
      s.src = 'https://www.youtube.com/iframe_api';
      document.head.appendChild(s);
    }
  }

  /* ---------------- F15: nut hoan thanh ---------------- */
  function initComplete() {
    var form = document.getElementById('complete-form');
    if (!form) return;
    var btn = document.getElementById('complete-btn');
    var toast = document.getElementById('lesson-toast');
    form.addEventListener('submit', function (ev) {
      if (!window.fetch || !window.URLSearchParams) return;   // trinh duyet cu: gui form binh thuong
      ev.preventDefault();
      if (btn.disabled) return;
      btn.disabled = true;                                     // chan bam hai lan
      var csrf = form.querySelector('input[name="csrf_token"]').value;
      postForm(form.getAttribute('action'), { csrf_token: csrf }, csrf)
        .then(function (r) { return r.json().then(function (j) { return { ok: r.ok, body: j }; }); })
        .then(function (res) {
          if (!res.ok || !res.body.ok) throw new Error((res.body && res.body.error) || 'Lỗi');
          var b = res.body;
          btn.textContent = 'Đã hoàn thành ✓';
          var bar = document.getElementById('course-progress');
          var txt = document.getElementById('course-progress-text');
          if (bar) bar.value = b.percent;
          if (txt) txt.textContent = b.percent + '% (' + b.completedLessons + '/' + b.totalLessons + ' bài)';
          var item = document.querySelector('.jl-side-item.is-current');
          if (item) { item.classList.add('is-done'); var t = item.querySelector('.jl-tick'); if (t) t.textContent = '✓'; }
          if (toast) {
            toast.textContent = b.alreadyCompleted ? 'Bạn đã hoàn thành bài này rồi'
              : 'Hoàn thành bài học!' + (b.coinsAwarded > 0 ? ' +' + b.coinsAwarded + ' xu' : '') + (b.courseCompleted ? ' Bạn đã hoàn thành khóa học.' : '');
            toast.hidden = false;
          }
        })
        .catch(function () { btn.disabled = false; form.submit(); });   // loi: thu lai bang gui form thuong
    });
  }

  initPlayer();
  initComplete();
})();
