
function refresh_list(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            //ajaxloader.style.display = 'none';
            block_refresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            //block_refresh(block);
            break;
    }
}
;

function refresh_edit_box(data) {
            var inputElement = data.source; // The HTML DOM input element.
            var ajaxStatus = data.status; // Can be "begin", "success" and "complete"
            var name = data.source.name.split("_");

            switch (ajaxStatus) {
                case "begin": // This is called right before ajax request is been sent.
                    //Put in the block here and we'll never need primefaces again!
                    break;

                case "complete": // This is called right after ajax response is received.
                    initDateTimePicker('.datetimepicker');
                    //gDateTimepicker.init('body');
                    break;

                case "success": // This is called when ajax response is successfully processed.
                    
                    break;
            }
        }
        ;
        
function initDateTimePicker(elem){
            $(elem).datetimepicker({
                showButtonPanel:false
                //dateFormat: '#{FormJobList.SCHEDULE_JS_DATE_STRING_FORMAT}',
                //timeFormat: '#{FormJobList.SCHEDULE_JS_TIME_STRING_FORMAT}'
            })
        }