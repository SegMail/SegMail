var callREST = function() {
    var ajax_url = '/SegMail/rest/segmail.entity.campaign.campaignactivity/0/100'
    $.ajax({
        type: "GET",
        url: ajax_url,
        dataType: 'json',
        success: function (data) {
            console.log(data);
        },
        error: function(error) {
            console.log(error);
        }
    })
}

$(document).ready(function(){
    callREST();
})