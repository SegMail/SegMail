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

$(document).ready(function(){
    initTypeaheadListname();
    
})