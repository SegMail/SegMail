$(window).load(function() {
    var timeout = $('#preloader').data('timeout');
    $('#preloader').delay(timeout).fadeOut('slow');
});