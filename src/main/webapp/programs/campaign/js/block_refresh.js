function refresh_list(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".refresh");
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

