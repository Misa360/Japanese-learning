/* F22: lat mat flashcard va gui ket qua on tap (SRS) co CSRF. */
(function () {
  'use strict';

  var session = document.getElementById('flashcard-study');
  if (!session) return;

  var cards = Array.prototype.slice.call(session.querySelectorAll('.jl-flashcard-item'));
  var cardNumber = document.getElementById('card-number');
  var remaining = document.getElementById('remaining-count');
  var feedback = document.getElementById('flashcard-feedback');
  var finished = document.getElementById('flashcard-finished');
  var progressFill = document.getElementById('flashcard-progress-fill');
  var totalLabel = session.querySelector('.jl-progress-total');
  var vocabularyItems = Array.prototype.slice.call(document.querySelectorAll('[data-word-index]'));
  var current = 0;
  var locked = false;

  cards.forEach(function (card, index) { card.hidden = index !== 0; });

  function updateProgress(index) {
    if (cardNumber) cardNumber.textContent = String(index + 1);
    if (progressFill) progressFill.style.width = ((index + 1) / Math.max(cards.length, 1) * 100) + '%';
    if (totalLabel) totalLabel.textContent = String(cards.length);
    vocabularyItems.forEach(function (item) {
      item.classList.toggle('is-current', Number(item.getAttribute('data-word-index')) === index);
    });
    var previous = session.querySelector('[data-prev]');
    if (previous) previous.disabled = index <= 0;
  }

  function showCard(index) {
    if (index < 0 || index >= cards.length || cards[index].dataset.reviewed === 'true') return;
    cards.forEach(function (card, cardIndex) {
      if (cardIndex !== index) card.classList.remove('is-current');
      card.hidden = cardIndex !== index || card.dataset.reviewed === 'true';
    });
    current = index;
    updateProgress(index);
    showFeedback('');
  }

  var previousButton = session.querySelector('[data-prev]');
  var nextButton = session.querySelector('[data-next]');
  if (previousButton) previousButton.addEventListener('click', function () {
    for (var i = current - 1; i >= 0; i--) if (cards[i].dataset.reviewed !== 'true') return showCard(i);
  });
  if (nextButton) nextButton.addEventListener('click', function () {
    for (var i = current + 1; i < cards.length; i++) if (cards[i].dataset.reviewed !== 'true') return showCard(i);
  });
  updateProgress(0);

  function showFeedback(message, isError) {
    if (!feedback) return;
    feedback.textContent = message || '';
    feedback.classList.toggle('is-error', Boolean(isError));
  }

  function setFace(card, showBack) {
    var front = card.querySelector('.jl-flashcard-front');
    var back = card.querySelector('.jl-flashcard-back');
    var flip = card.querySelector('[data-flip]');
    front.hidden = showBack;
    back.hidden = !showBack;
    flip.setAttribute('aria-expanded', showBack ? 'true' : 'false');
    card.querySelector('[data-rating-controls]').hidden = !showBack;
  }

  function finish() {
    var items = document.getElementById('flashcard-items');
    if (items) items.hidden = true;
    var counter = session.querySelector('.jl-flashcard-counter');
    if (counter) counter.hidden = true;
    if (finished) finished.hidden = false;
    showFeedback('Bạn đã ôn xong các thẻ đang đến hạn.');
  }

  cards.forEach(function (card, index) {
    var flip = card.querySelector('[data-flip]');
    flip.addEventListener('click', function () {
      var showBack = flip.getAttribute('aria-expanded') !== 'true';
      setFace(card, showBack);
      if (showBack) card.querySelector('[data-rating="1"]').focus();
    });

    card.querySelectorAll('[data-rating]').forEach(function (button) {
      button.addEventListener('click', function () {
        if (locked) return;
        locked = true;
        showFeedback('Đang lưu kết quả…');
        var form = new URLSearchParams();
        form.set('card_id', card.getAttribute('data-card-id'));
        form.set('rating', button.getAttribute('data-rating'));

        fetch(session.getAttribute('data-review-url'), {
          method: 'POST',
          credentials: 'same-origin',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            'X-CSRF-Token': session.getAttribute('data-csrf') || '',
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json'
          },
          body: form.toString()
        }).then(function (response) {
          return response.json().then(function (body) {
            if (!response.ok || !body.ok) throw new Error(body.error || 'Không thể lưu kết quả ôn tập.');
            return body;
          });
        }).then(function (body) {
          card.hidden = true;
          card.dataset.reviewed = 'true';
          card.classList.remove('is-current');
          var left = cards.filter(function (item) { return item.dataset.reviewed !== 'true'; }).length;
          if (remaining) remaining.textContent = String(left);
          var nextIndex = -1;
          for (var offset = 1; offset <= cards.length; offset++) {
            var candidate = (index + offset) % cards.length;
            if (cards[candidate].dataset.reviewed !== 'true') { nextIndex = candidate; break; }
          }
          if (nextIndex < 0) {
            finish();
          } else {
            showCard(nextIndex);
            showFeedback(body.message || 'Đã lưu lịch ôn tập.');
            cards[nextIndex].querySelector('[data-flip]').focus();
          }
          locked = false;
        }).catch(function (error) {
          showFeedback(error.message || 'Không thể kết nối. Hãy thử lại.', true);
          locked = false;
        });
      });
    });
  });
})();
