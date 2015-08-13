var popup = (function() {

    var title_id;
    var title;
    var type_id;
    var type;

    return {
        
        init : function(title_id, title, type_id, type) {
            
            this.title_id = title_id;
            this.title = title;
            this.type_id = type_id;
            this.type = type;
        },
        
        popup : function() {
            
            document.getElementById(this.title_id).innerHTML = this.title;
            //document.getElementById(title_id).value = title;

            var TYPE = this.type.toUpperCase();
            document.getElementById(this.type_id).value = TYPE;
            
            //Re-render the summernote text editor
            $('textarea.editor').summernote({height: 300});
        }

    };
});

var popup = new popup();

/**
 * http://stackoverflow.com/questions/7043840/jsf-2-how-show-different-ajax-status-in-same-input/7044332#7044332
 * 
 * For triggering ajax after f:ajax listener.
 * 
 * @param {type} data
 * @returns {undefined}
 */
function edit(data){
    var inputElement = data.source; // The HTML DOM input element.
    var ajaxStatus = data.status; // Can be "begin", "success" and "complete"
    var name = data.source.name.split("_");
    
    switch (ajaxStatus) {
        case "begin": // This is called right before ajax request is been sent.
            
            break;

        case "complete": // This is called right after ajax response is received.
            
            break;

        case "success": // This is called when ajax response is successfully processed.
            //Set the title and type of template
            popup.init('popup_title_edit', 'Edit '+name[0]+' template', 'template_id', name[1]);
            //Re-setup the title and type
            popup.popup(); 
            break;
    }
};