var setupSignupCodePanel = function () {
    
    var editor = CodeMirror.fromTextArea(document.getElementById("codeEditor"), {
        lineNumbers: true,
        matchBrackets: true,
        mode: "htmlmixed",
        indentUnit: 4,
        indentWithTabs: true,
        enterMode: "keep",
        tabMode: "shift"
        
    });
    editor.setSize('100%', '280px');
    
};

var setupDefaultValues = function() {
    if(!$('#subscribeButtonLabel').val())
        $('#subscribeButtonLabel').val('Subscribe');
    
    $('#bootstrapStyles').attr('checked','checked');
    
    
}

var refresh_select2 = function() {
    //$('select.form-select').select2();
    var select = $('body').find("select.select2");
    select.select2("destroy").select2();
};

var buildSignupCodeOrDie = function() {
    var errors = [];
    if(!sendAs || sendAs === '') {
        errors.push('Send As is not setup for this list yet.');
    }
    
    if(!serverUrl || serverUrl === '') {
        errors.push('Web servers are not set up by admin yet, please contact your admin.');
    }
    
    if(!confirmEmailCount || confirmEmailCount <= 0) {
        errors.push('Please create and assign a confirmation email first in "Autoresponders".');
    }
    
    if(!fieldList || fieldList.length <= 0) {
        errors.push('There are no fields for this list. You can re-create them in "Subscription Lists"');
    }
    
    if(errors.length > 0){
        die(errors);
        return;
    }
    
    buildSignupCode();  
}

var die = function(errors) {
    var error = '\n\n\n\n' 
        + '\n========================================================================='
        + '\n==================================ERROR=================================='
        + '\n========================================================================='
        + '\nThe following erros are preventing the signup code from getting generated:';

    for(var i=0; i<errors.length; i++){
        error += '\n'+(i+1)+') '+errors[i];
    }
    $('#codeEditor').text(error);
}

var buildSignupCode = function() {
    
    var code = '<form action="'+serverUrl+'" method="post" enctype="application/x-www-form-urlencoded" >';
    code += '\n<input name="client" type="hidden" value="'+clientId+'" ></input>';
    code += '\n<input name="list" type="hidden" value="'+listId+'" ></input>';
    for(var i = 0; i < fieldList.length; i++) {
        var fieldName = fieldList[i]['name'];
        var fieldKey = fieldList[i]['key'];
        var fieldDesc = fieldList[i]['description'];
        var fieldMandatory = fieldList[i]['mandatory'];
        //console.log('field name: '+fieldName+', fieldKey: '+fieldKey+', fieldDesc: '+fieldDesc+', fieldMandatory: '+fieldMandatory);
        
        var fieldCode = //'<label>'+fieldName+': </label>'
                        '<div class="enclosing-div">'
                        + '<input '
                        +(fieldMandatory === 'true' ? 'required="true" ':'')
                        +'name="'+fieldKey+'" '
                        +'placeholder="' +fieldName+ '" '
                        +' ></input>'
                        +'</div>';
        code += '\n'+fieldCode ;//+ '<br />';
                        
    }
    code += '\n<button >' + $('#subscribeButtonLabel').val() + '</button>';
    code += '\n</form>';
    var codified = $('<div>'+code+'</div>');
    
    if($('#bootstrapStyles').attr('checked')) {
        codified.find('input').addClass('form-control');//Add form-control class to all inputs
        codified.find('div.enclosing-div').addClass('form-group');//Add form-control class to all inputs
        codified.find('button').addClass('btn btn-primary');//Add button class
    } else {
        codified.find('div.enclosing-div').after('<br />');
    }
    
    $('#codeEditor').text(codified.html());
}

var refreshEverything = function() {
    
    refresh_select2();
    buildSignupCodeOrDie();
    setupSignupCodePanel();
    
};

$(document).ready(function(){
    setupDefaultValues();
    refreshEverything();
});

var update = function(data){
    var ajaxStatus = data.status; 
    
    switch (ajaxStatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh($('#FormSignupCode .block'));
            break;

        case "complete": // This is called right after ajax response is received.
            
            break;

        case "success": // This is called when ajax response is successfully processed.
            //block_refresh($('#FormSignupCode .block'));
            refreshEverything();
            break;
    }
};