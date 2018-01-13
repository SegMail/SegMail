function refreshDatasource(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $("#FormAddDatasource .modal-dialog");
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            initAddSubsc();
            refresh_select2();
            break;
    }
};