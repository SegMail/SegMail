var SUMMERNOTE_HEIGHT = 280;


/**
 * To submit a JSF partial request using pure JS, you need to:
 * 1) Attach a f:ajax in the form that you want to submit in the xhtml page.
 * 2) In this JS function, pass in the source, ajax event and list of params. Refer to
 * https://docs.oracle.com/cd/E17802_01/j2ee/javaee/javaserverfaces/2.0/docs/js-api/symbols/jsf.ajax.html#.request
 * for detailed explanation of each param.
 * 
 * @param {type} activityId
 * @param {type} event
 * @returns {undefined}
 */

function load_activity(activityId, event) {
    jsf.ajax.request(
            $('#FormCampaignActivities'),
            event,
            {
            })
}

function refresh_summernote() {
    $('textarea.editor').summernote({
        height: SUMMERNOTE_HEIGHT
    });
}

function preview() {

    $('#content').next().find('.note-editable').each(function () {

        var height = $('#editor-form').height();
        $('#preview').html($(this).html());
        var maxWidth = largestWidth('#preview');
        var scaleX = $('#preview').width() / maxWidth;
        if(scaleX > 1)
            scaleX = 1/scaleX;
        var scaleY = $('#preview').height() / height;
        if(scaleY > 1)
            scaleY = 1/scaleY;
        $('#preview').css({
            transform: 'scale(' + scaleX + ','+scaleY+')',
            'transform-origin': '0 0 0'
        });
    });
    /*var realTextboxParent = document.getElementById('content').nextSibling;
     for (var i=0; i<realTextboxParent.childNodes.length; i++){
     var child = realTextboxParent.childNodes[i];
     if(child.className === 'note-editable'){
     var scale = $('#preview').innerWidth() / child
     $('#preview').html(child.innerHTML).css({
     transform : 'scale('+scale+')',
     'transform-origin' : '0 0 0'
     });
     break;
     }
     }*/

}

function highlightAndCreateLinks() {

    var index = 1;
    var prevPos = 0;
    var positions = [];
    //Clear the preview pane first
    $('#links').empty();
    
    $('#preview').find('a').each(function () {
        //console.log($(this).attr('href'));
        var offset = $(this).offset().top - $('#preview').offset().top;
        //console.log($(this).attr('href')+'(offset:'+offset+')');
        
        positions.push(offset);
        
        var marginTop = Math.max(offset - prevPos - $('#links div').last().height(),0);
        //Add new element to links pane
        $('#links').append(
                "<div style='margin-top: "
                + marginTop
                + "px;'>"
                + "<span class='badge badge-primary'>"
                + (index++)
                + "</span> "
                + $(this).text()
                + "</div>"
                );
        //factor in the height for offset
        var prevHeight = $('#links div').last().height();
        prevPos = offset;// - 
        
    });
    //Reorganize their position, or factor in their height during creation
    
}

/**
 * This algorithm must produce an evenly distributed list of items based on their 
 * position offset in posArray.
 * 
 * @param {type} posArray
 * @returns {undefined}
 */
function rearrangeDivs(posArray) {
    for(var i=0; i<posArray.length; i++) {
        
    }
}

function largestWidth(selector) {
    var maxWidth = 0;
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