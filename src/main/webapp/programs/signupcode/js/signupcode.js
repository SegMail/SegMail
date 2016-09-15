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

var refresh_select2 = function() {
    //$('select.form-select').select2();
    var select = $('body').find("select.select2");
    select.select2("destroy").select2();
};

var buildSignupCode = function() {
    
    var code = '<form action="'+serverUrl+'" method="post" enctype="application/x-www-form-urlencoded" >';
    code += '\n<input name="client" type="hidden" value="'+clientId+'" />';
    code += '\n<input name="list" type="hidden" value="'+listId+'" />';
    for(var i = 0; i < fieldList.length; i++) {
        var fieldName = fieldList[i]['name'];
        var fieldKey = fieldList[i]['key'];
        var fieldDesc = fieldList[i]['description'];
        var fieldMandatory = fieldList[i]['mandatory'];
        //console.log('field name: '+fieldName+', fieldKey: '+fieldKey+', fieldDesc: '+fieldDesc+', fieldMandatory: '+fieldMandatory);
        
        var fieldCode = '<label>'+fieldName+': </label>'
                        + '<input '+(fieldMandatory ? 'required ':'')+'name="'+fieldKey+'" />';
        code += '\n'+fieldCode + '<br />';
                        
    }
    code += '<button >Subscribe</button>';
    code += '\n</form>';
    $('#codeEditor').text(code);
}

var refreshEverything = function() {
    block_refresh($('.block'));
    refresh_select2();
    buildSignupCode();
    setupSignupCodePanel();
    block_refresh($('.block'));
    
};

$(document).ready(function(){
    refreshEverything();
});