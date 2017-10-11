
var substringMatcher = function(strs) {
  return function findMatches(q, cb) {
    var matches, substringRegex;

    // an array that will be populated with substring matches
    matches = [];

    // regex used to determine if a string contains the substring `q`
    substrRegex = new RegExp(q, 'i');

    // iterate through the pool of strings and for any string that
    // contains the substring `q`, add it to the `matches` array
    $.each(strs, function(i, str) {
      if (substrRegex.test(str)) {
        matches.push(str);
      }
    });

    cb(matches);
  };
};

function refresh(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".jumbotron");
    /*var valid = addressForm.form();
    if(!valid) {
        $addressForm.focusInvalid();
        return false;
    }*/

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            blockRefresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            blockRefresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            break;
    }
};

$(document).ready(function(){
    page_navigation();
})