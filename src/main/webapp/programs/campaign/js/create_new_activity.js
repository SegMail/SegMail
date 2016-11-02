function create_new_activity(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".refresh");

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.

            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            break;
    }
}
;