var refresh_summernote = function () {
    $('textarea.editor').summernote({
        height: 280, //try the CSS flex approach next time
        
    });
    observeDOM(document.getElementsByClassName('note-editable')[0], function () {
        onEditorChange();
    });
}

var onEditorChange = function() {
    //render preview
    renderPreview();
    //render mailmerge tags with sameple links
}

var onSave = function() {
    //Block button
    $('#saveButton').prop('disabled',true);
    
    //Copy summernote content back to textarea
    reapply_textarea('editor');
    
    //Copy preview content into a hidden input field for JSF to process
    renderPreview();
    
    //Call webservice
    $.soap({
        url : web_service_endpoint,
        method : 'saveAutoemail',
        appendMethodToURL: 0,
        namespaceQualifier: 'ns',
        namespaceURL: 'http://webservice.autoresponder.program.segmail/',
        noPrefix: 0,
        HTTPHeaders: {
        },
    /*$.ajax({
        type : "POST",
        contentType : "application/json",
        url: "/autoresponder/save",*/
        data : {
            'body' : document.getElementById('editor').innerHTML,//$('#editor').html().text(),
            'bodyProcessed' : $('#processedContent').val()
        },
        success : function(SOAPResponse) {
            var xmlResults = SOAPResponse.toJSON();
            var result = xmlResults['#document']['S:Envelope']['S:Body']['ns2:saveAutoemailResponse']["return"];
            $('#saveResults').html(result); //Don't know how it will look like yet
            $('#saveButton').prop('disabled',false);
        },
        error : function(SOAPResponse) {
            var response = SOAPResponse.toJSON();
            $('#saveResults').html(response); //Don't know how it will look like yet
            $('#saveButton').prop('disabled',false);
        }
    })
};

var toggleMenu = function () {
    if ($(document).has('#FormEditExistingTemplate').length) {
        page_navigation();
    }
};

var renderPreview = function() {
    //Copy the html over from 
    //setTimeout(function(){
        $('#preview').html($('.note-editable').html());
        $('#processedContent').val($('#preview').html());
        
        //Get ratios
        var scaleY = $('#preview-panel').height() / $('#preview').height();
        var scaleX = $('#preview-panel').width() / largestWidth('#preview');//$('#preview').width();
        //Transform
        var scaleYTransform = Math.min(1,scaleY);
        var scaleXTransform = Math.min(1,scaleX);
        $('#preview').css({
            transform: 'scale(' + scaleXTransform + ',' + scaleYTransform + ')',
            'transform-origin': '0 0 0'
        });
        
        
    //},timeout);
};

function largestWidth(selector) {
    var maxWidth = 1;
    var widestSpan = null;
    var $element;
    $(selector).find('*').each(function () {
        $element = $(this);
        if ($element.width() > maxWidth) {
            maxWidth = $element.width();
            widestSpan = $element;
        }
    });
    return maxWidth;
}

var adjustPreviewPanelHeight = function() {
    //Listen for resize on the #editor-panel
    //$('.note-editable').resize(function(){ //seems like only for window
        //Adjust heights
        //Get actual heights and widths
        var editorBottom = $('#editor-panel').offset().top 
                + $('#editor-panel').height() ;
                + parseInt($('#editor-panel').css('margin-bottom').replace("px", ""))
                + parseInt($('#editor-panel').css('border-bottom-width').replace("px", ""));
        var previewHeight = editorBottom - $('#preview-panel').offset().top;
        $('#preview-panel').height(previewHeight);
    //});
    
};

var renderMailmergeTags = function() {
    
}



$(document).ready(function () {
    toggleMenu();
    refresh_summernote();
    adjustPreviewPanelHeight();
});