$(document).ready(function(){
    $('#FormJobStatus input.statusCheckbox').each(function(index,item){
        if($(item).attr('checked'))
            $(item).parent('label').addClass('active');
        else
            $(item).parent('label').removeClass('active');
    })
})

function refresh_status(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    //var ajaxloader = document.getElementById("ajaxloader");

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            //ajaxloader.style.display = 'block';
            block_refresh(block);
            block_refresh($('#all_batch_job_block'));
            break;

        case "complete": // This is called right after ajax response is received.
            //ajaxloader.style.display = 'none';
            block_refresh(block);
            //block_refresh($('#all_batch_job_block'));
            break;

        case "success": // This is called when ajax response is successfully processed.
            //block_refresh(block);
            //filterByStatus();
            break;
    }
}
;
