function track_activity(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".block");
    var trackingBlock = $('#FormTrackEmailActivity .block');

    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(trackingBlock);
            break;

        case "success": // This is called when ajax response is successfully processed.
            block_refresh(block);
            block_refresh(trackingBlock);
            break;
    }
}
;
var renderLinksAndRefreshStats = function (timeout) {
    setTimeout(function () {
        var position = 0;
        var wsCounts = $('#html-content').find('a').size();
        
        $('#links-start').empty();
        $('#stats-table').find('tbody').empty();
        $('#stats-table').find('tfoot').empty();
        $('#html-content').find('a').each(function (index) {
            //console.log(index);
            var redirectLink = $(this).attr('href');
            var linkText = $(this).text();
            var offset = $(this).offset().top - $('#html-content').offset().top;
            var marginTop1 = offset;

            if (index > 0) {
                var lastOffset = position;
                marginTop1 = marginTop1 - lastOffset;
            }
            //minus another half a line cause you want the link-button to centralize
            var marginTop = Math.max(marginTop1, 0);
            if(marginTop > 0) //It is not "sticking" to the previous button
                marginTop -= 0.5*12; //12px is the pre-defined size of the div
            else
                marginTop -= 0.25*$('#links-start div').last().height();
            var link = 
                    '<div style="margin-top: '+marginTop+'px">'
                    + "<span class='link-button'>"
                    + (index + 1) //key 1
                    + "</span> "
                    + linkText //key 2
                    + "</div>";
            
            $('#links-start').append(link);
            $('#links-start div').addClass('css-bounce');
            position = position + marginTop + $('#links-start div').last().height();
            //console.log(index+' done');

            //Call webservice to get counts and update 
            callWS(web_service_endpoint, 
                'getClickCountForActivity', 
                'http://webservice.campaign.program.segmail/',{
                'redirectLink': redirectLink
            }, function (result) {
                //Add result to table
                var totalClicks = Number($('#sent').html());
                var clickPercent = (Number(result) / totalSent) * 100.0;
                var row = '<tr>' +
                        '<td>' + (index + 1) + '</td>' +
                        '<td>' + linkText + '</td>' +
                        '<td>' + result + '</td>' +
                        '<td>' + clickPercent + '%' + '</td>' +
                        '</tr>';
                $('#stats-table').find('tbody').append(row);
                
                //Once all calls are done
                if (!--wsCounts) {
                    
                    var totalClickthrough = (totalSent === 0) ? 0 : (Number(totalClicks) / totalSent) * 100.0;
                    var summaryRow = '<tr>' +
                                     '<td colspan="2" style="text-align: right;"><strong>Total</strong></td>' +
                                     '<td>' + totalClicks + '</td>' +
                                     '<td>' + totalClickthrough + '%</td>' +
                                     '</tr>';
                    $('#stats-table').find('tfoot').append(summaryRow);
                                
                    if(!$.fn.dataTable.isDataTable('#stats-table')) {
                        $('#stats-table').dataTable({
                            //'destroy': false,
                            //'filter': false,
                            'paging': false,
                            'searching': false,
                            'info': false
                        });
                    }
                    
                    $('#clicked').html(totalClicks);
                }
            }, function () {
                
            });
        });
    }, timeout);
};

var getSentEmails = function() {
    callWS(web_service_endpoint, 
        'getExecutedForCampaignActivity', 
        'http://webservice.campaign.program.segmail/', {
        campaignActivityId : $('#activity-id').val()
    }, function(result){
        $('#sent').html(result);
    }, function(){
        $('#sent').html(0);
    });
};

var getTotalTargeted = function() {
    callWS(web_service_endpoint, 
        'getTotalTargetedForCampaignActivity', 
        'http://webservice.campaign.program.segmail/', {
        campaignActivityId : $('#activity-id').val()
    }, function(result){
        $('#targeted').html(result);
    }, function(){
        $('#targeted').html(0);
    });
}

var getTopNumbers = function(timeout) {
    setTimeout(function(){
        getTotalTargeted();
        getSentEmails();
    },timeout);
};

var refresh = function() {
    block_refresh($('#FormTrackEmailActivity'));
    getTopNumbers(0);
    renderLinksAndRefreshStats(100);
    block_refresh($('#FormTrackEmailActivity'));
}

$(document).ready(function () {
    
});