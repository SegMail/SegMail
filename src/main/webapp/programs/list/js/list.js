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
};

