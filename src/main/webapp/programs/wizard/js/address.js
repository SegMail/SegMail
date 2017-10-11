
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

$(document).ready(function(){
    initTypeaheadAddress();
    
})