
var toggleMenu = function () {
    if ($(document).has('#FormSubscriberView').length) {
        page_navigation();
    }
}

$(document).ready(function(){
    toggleMenu();
})