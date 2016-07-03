function saveSettings(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");


    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            //data.source.onBegin();
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            //data.source.onComplete();
            break;

        case "success": // This is called when ajax response is successfully processed.
            //$(data.source).trigger('onSuccess');
            bindFileInput();
            //data.source.onSuccess();
            break;
    }
}
;

function bindFileInput() {
    $('#uploader').on('change.bs.fileinput', function (event) {
        //event.stopPropagation();
        //https://github.com/anpur/client-line-navigator
        var files = document.getElementById('fileUploaded').files;
        var navigator = new FileNavigator(files[0]);
        navigator.readLines(0,1,function lineReadHandler(err, partIndex, lines, eof){
            if (err) console.log(err);
            
            //Get the first line
            var firstLine = lines[0];
            console.log(firstLine);
            
            var headers = firstLine.split(',');
            $('#field_selector select').empty().append($('<option>', { 
                value: '',
                text : '--No field selected--' 
            }));
            $.each(headers, function (i, item) {
                $('#field_selector select').append($('<option>', { 
                    value: item,
                    text : item 
                })).select2(); 
            });
            $('#field_selector').show();
            $('#importButton').show();
        });
    });
}

/*var onSuccess = function(e){
    bindFileInput();
};


$('#importButton').bind('onSuccess',function(e){
    bindFileInput();
});
*/
$(document).ready(function () {
    //$('#importButton').trigger('onSuccess');
    bindFileInput();
});


var uploader = document.getElementById('uploader');
var importButton = document.getElementById('importButton');

importButton.onSuccess = function(){
    bindFileInput();
}.bind(importButton);