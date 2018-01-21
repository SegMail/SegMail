Date.prototype.monthNames = [
    "January", "February", "March",
    "April", "May", "June",
    "July", "August", "September",
    "October", "November", "December"
];

Date.prototype.getMonthName = function() {
    return this.monthNames[this.getMonth()];
};
Date.prototype.getShortMonthName = function() {
    return this.getMonthName().substr(0, 3);
};

var callTimelineService = function(id,successCB,errorCB) {
    $.ajax({
        type: 'GET',
        url: CONTEXT_PATH + '/rest/subscriber/timeline/build/' + id,
        success: function (data) {
            successCB(data);
        },
        error: function(error) {
            errorCB(data);
        }
    })
};

var buildEvent = function(event){
        //var event = data.events[i];
        var $timelineEvent = $('<div class="timeline-event">');
        $timelineEvent.append('<div class="timeline-event-icon">'+event.icon+'</div>');
        $timelineEvent.append('<div class="timeline-event-content">');
        $timelineEvent.find('.timeline-event-content').append('<div class="event-title">'+event.title+'</div>');
        $timelineEvent.find('.timeline-event-content').append('<p>'+event.body+'</p>');
        $timelineEvent.find('.timeline-event-content').append('<div class="event-date"></div>');
        if(event.action) {
            for(var j=0; j < event.action.length; j++) {
                var actionObj = event.action[j];
                var $action = $('<a href="#'+actionObj.href+'">');
                $action.html(actionObj.text);
                $action.addClass(actionObj.class);
                if(actionObj.datamap) {
                    for(var k in actionObj.datamap) {
                        if(actionObj.datamap.hasOwnProperty(k)) {
                            var v = actionObj.datamap[k];
                            $action.attr("data-"+k,v);
                        }
                    }
                }
                if(j >= 1) {
                    $timelineEvent.find('.event-date').append(' | ');
                }
                $timelineEvent.find('.event-date').append($action);
            }
        }
        if(!$timelineEvent.find('.event-date').is(':empty'))
            $timelineEvent.find('.event-date').append(' | ');
        $timelineEvent.find('.event-date').append('<i class="fa fa-clock-o"></i> '+event.datetime);
        $('#subscriber-timeline').append($timelineEvent);
}

var buildTLDate = function(topEvent,bottomEvent) {
    // Case if it is the first item at the top - topEvent is null
    if(!topEvent && bottomEvent){
        var dt = new Date(bottomEvent.isoDatetime);
        
        var $timelineDate = $('<div class="timeline-event">');
        $timelineDate.append('<div class="timeline-date"><div>'
            +'<span>'
                +dt.getFullYear().toString().substr(-2)
            +'</span>'
                +dt.getShortMonthName()
            +'</div></div>');
        $('#subscriber-timeline').append($timelineDate);
        
        return;
    }
    // Case if it is between 2 items
    if(topEvent && bottomEvent) {
        var tdt = new Date(topEvent.isoDatetime);
        var bdt = new Date(bottomEvent.isoDatetime);
        
        var tMonth = tdt.getShortMonthName();
        var tYear = tdt.getFullYear();
        var bMonth = bdt.getShortMonthName();
        var bYear = bdt.getFullYear();
        
        // If the top and bottom month and year differs, insert a 
        // timeline date for the top
        if(tMonth !== bMonth || tYear !== bYear) {
            var $timelineDate = $('<div class="timeline-event">');
            $timelineDate.append('<div class="timeline-date"><div>'
                +'<span>'
                    +tYear.toString().substr(-2)
                +'</span>'
                    +tMonth
                +'</div></div>');
            $('#subscriber-timeline').append($timelineDate);
        }
        return;
    }
    // Case if it is the last item at the bottom
    if(topEvent && !bottomEvent) {
        var dt = new Date(topEvent.isoDatetime);
        var $timelineDate = $('<div class="timeline-event">');
        $timelineDate.append('<div class="timeline-date"><div>'
            +'<span>'
                +dt.getFullYear().toString().substr(-2)
            +'</span>'
                +dt.getShortMonthName()
            +'</div></div>');
        $('#subscriber-timeline').append($timelineDate);
        
        return;
    }
}

var buildTimeline = function(id) {
    block_refresh($('.subscriber-timeline'));
    
    callTimelineService(id,
        function(data){ // success cb
            for(var i=0; i < data.events.length; i++) { // looping through events
                buildTLDate(data.events[i-1],data.events[i]); // to check if we need to insert a date icon
                buildEvent(data.events[i]);
            }
            buildTLDate(null,data.events[data.events.length-1]); // insert the last date
            initEventActions(); // for initializing actions, a little inefficient because it's called twice
        },
        function(data){ // error cb
            
        }
    )
}

var initEventActions = function() {
    initSubscriptionActions();
    initPreviewEmailActions();
}

var initPreviewEmailActions = function() {
    $('.preview-email').click(function(){
        var button = this;
        $.ajax({
            type: 'GET',
            url: CONTEXT_PATH + '/rest/subscriber/preview' + function() {
                if($(button).data('campaign-act-id')) {
                    return '/campaign/' + $(button).data('campaign-act-id');
                }
                if($(button).data('email-key')) {
                    return '/email/' + $(button).data('email-key');
                }
                return '';
            }(),
            success: function (data) {
                console.log(data);
                $('#email-subject').html(data.subject);
                $('#email-body').html(data.body);
            },
            error: function(data) {
                console.log(data);
                noty({
                    text: 'Error: ' + data.result,
                    layout : 'topCenter',
                    type : 'error',
                    timeout : false
                });
            }
        })
    })
    
}

$(document).ready(function(){
    buildTimeline($('#subscriberId').val());
})