
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
            dispatchSuccessEvt();
            break;
    }
};

var dispatchSuccessEvt = function() {
    var page = $('#page').val();
    switch(page) {
        case "address"  :
            initTypeaheadAddress();
            break;
        case "list"  :
            initTypeaheadListname();
            break;
        case "auto"     :
            refresh_summernote('textarea.editor');
            break;
        case "collect"     :
            loadSignupCodes();
            break;
            
    }
}

/** 
 * welcome page
 * 
 */

/**
 * verify address page
 * 
 */

var initTypeaheadAddress = function() {
    $('#email').typeahead({
        source : substringMatcher(existing_addresses)
    });
}

var addressForm = function() {
    return $("#ProgramSetupWizard").validate({
        rules: {
            'ProgramSetupWizard:email': {
                required: true,
                email: true
            }
        }
    });
};

/**
 * 
 * create list page
 */
var listForm = function() {
    return $("#ProgramSetupWizard").validate({
        rules: {
            'ProgramSetupWizard:listname': {
                required: true
            }
        }
    });
};

var initTypeaheadListname = function() {
    $('#listname').typeahead({
        source : substringMatcher(existing_lists)
    });
}

/**
 * 
 * setup autoresponder page
 */
var refresh_summernote = function (selector) {
    $(selector).summernote({
        height: 260, //try the CSS flex approach next time
        toolbar: [
             ["font", ["bold", "italic", "underline","style"]],
             ["test", ["picture", "link"]],
             ["para", ["ol", "ul", "paragraph", "height"]],
             ["misc", ["codeview", "help", "MailMerge"]]
        ],
        MailMerge: {
            tags: function() {
                var allTagsAndLinks = [];
                for(var key in mailmergeTagsSubscriber) {
                    if (mailmergeTagsSubscriber.hasOwnProperty(key)) {
                        allTagsAndLinks.push(key);
                    }
                }
                for(var key in listTags) {
                    if (listTags.hasOwnProperty(key)) {
                        allTagsAndLinks.push(listTags[key]);
                    }
                }
                for(var key in mailmergeLinks) {
                    if (mailmergeLinks.hasOwnProperty(key)) {
                        allTagsAndLinks.push(key);
                    }
                }
                return allTagsAndLinks;
            }()
        }
    });
}

var autoForm = function() {
    return $("#ProgramSetupWizard").validate({
        rules: {
            'ProgramSetupWizard:confirm_subject': {
                required: true
            },
            'ProgramSetupWizard:welcome_subject': {
                required: true
            }
        }
    });
};

/**
 * 
 * collect signup page
 */
var loadSignupCodes = function() {
    $.getScript(CONTEXT_PATH+'/programs/signupcode/js/signupcode.js', function(data, textStatus, jqxhr) {
        buildSignupCodeOrDie();
    });
    $.getScript(CONTEXT_PATH+'/programs/subscribers/js/add_datasource_popup.js', function() {
    });
    $.getScript(CONTEXT_PATH+'/programs/subscribers/js/import_subscriber_popup.js', function() {
    });
}

$(document).ready(function(){
    page_navigation();
    dispatchSuccessEvt();
})