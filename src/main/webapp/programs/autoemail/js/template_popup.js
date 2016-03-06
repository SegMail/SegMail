var SUMMERNOTE_HEIGHT = 300;

var CONFIRMATION_PLACEHOLDER = '[confirm]';

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
            //In addition, add our custom mail-merge buttons
            $('textarea.editor').summernote({
                /*height: SUMMERNOTE_HEIGHT,
                oninit: function() {
                    var confirmButton 
                            = '<button id="insertConfirmButton" type="button" class="btn btn-default btn-sm btn-small" title="Insert confirmation link" data-event="something" tabindex="-1" onclick="popup.test()">[confirm]</button>';
                    var insertGroup 
                            = '<div class="note-file btn-group">' + confirmButton + '</div>';
                    $(insertGroup).appendTo($('.note-toolbar'));
                    $('#insertConfirmButton').tooltip({placement: 'bottom'});
                    $('#insertConfirmButton').click(function(event) {
                        //var text = $('.note-editable').children('p').text();
                        alert("text");
                    });
                }*/
                height: SUMMERNOTE_HEIGHT,
    focus: false,
    toolbar: [
            ['style', ['bold', 'italic', 'underline', 'clear']],
            ['font', ['strikethrough']],
            ['fontsize', ['fontsize']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['height', ['height']],
            ['view', ['fullscreen', 'codeview']],
        ],
    oninit: function() {
        // Add "open" - "save" buttons
        var noteBtn = '<button id="makeSnote" type="button" class="btn btn-default btn-sm btn-small" title="Identify a music note" data-event="something" tabindex="-1"><i class="fa fa-music"></i></button>';            
        var fileGroup = '<div class="note-file btn-group">' + noteBtn + '</div>';
        $(fileGroup).appendTo($('.note-toolbar'));
        // Button tooltips
        $('#makeSnote').tooltip({container: 'body', placement: 'bottom'});
        // Button events
        $('#makeSnote').click(function(event) {
            var highlight = window.getSelection(),  
            spn = '<span class="snote" style="color:blue;">' + highlight + '</span>',
            text = $('.note-editable').children('p').text(),
            range = highlight.getRangeAt(0),
            startText = text.substring(0, range.startOffset), 
            endText = text.substring(range.endOffset, text.length);

            $('.note-editable').html(startText + spn + endText);
        });
     }
            });
        },
        
        test : function() {
            alert("test");
        },
        
        clear : function() {
            document.getElementById(this.title_id).innerHTML = "";
            document.getElementById(this.type_id).value = "";
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