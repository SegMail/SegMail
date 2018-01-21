var toggleMenu = function () {
    if ($(document).has('#no_subscribers').length) {
        page_navigation();
    }
};

$(document).ready(function(){
    toggleMenu();
})