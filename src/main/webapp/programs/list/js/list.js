function saveSettings(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");


    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            //$('#contact_details_form').validationEngine('validate');
            break;

        case "success": // This is called when ajax response is successfully processed.

            break;
    }
}
;

$(document).ready(function () {
    $('#uploader').on('change.bs.fileinput', function (event) {
        event.stopPropagation();
        //alert(event.target);
        //console.log(event.target);
        //https://github.com/anpur/client-line-navigator
        var files = document.getElementById('fileUploaded').files;
            /*var reader = new FileReader();
            reader.onload = function (evt) {

                alert(evt.target.result);
                console.log(evt.target.result);
            }
            
            reader.readAsBinaryString(files[0]);*/
        var navigator = new FileNavigator(files[0]);
        navigator.readLines(0,1,function lineReadHandler(err, partIndex, lines, eof){
            if (err) console.log(err);
            
            //Get the first line
            var firstLine = lines[0];
            console.log(firstLine);
            
            var headers = firstLine.split(',');
            
            $.each(headers, function (i, item) {
                $('#field_selector select').append($('<option>', { 
                    value: item,
                    text : item 
                })).select2();
                
                alert(item);
                //$('#field_selector select').select2('data',{id: item, text: item});
            })
        })
        

    });
});

