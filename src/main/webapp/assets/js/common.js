/* common.js - Riki JLearn interactive helpers */
document.addEventListener('DOMContentLoaded', function() {
  // Auto-dismiss alerts after 5s
  var flashes = document.querySelectorAll('.jl-flash');
  flashes.forEach(function(flash) {
    setTimeout(function() {
      flash.style.opacity = '0';
      flash.style.transition = 'opacity 0.4s ease';
      setTimeout(function() {
        if (flash.parentNode) flash.parentNode.removeChild(flash);
      }, 400);
    }, 5000);
  });
});

