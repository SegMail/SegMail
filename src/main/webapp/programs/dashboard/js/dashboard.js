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

var getDays = function () {
    var days = $('select#days').val();
    return days;
}

var getStart = function() {
    var startDay = new Date(new Date().setDate(new Date().getDate() - getDays() + 1));
    
    return startDay.toISOString().slice(0,10);
}

var getEnd = function() {
    var date = new Date();
    
    return date.toISOString().slice(0,10);
}


var callSignupsWS = function() {
    return $.ajax({
        type: 'GET',
        url : CONTEXT_PATH + '/rest/dashboard/signups', // /dashboard/signups/117?start=1234567&end=7654321
        data : {
            start : getStart(),
            end   : getEnd()
        }
    })
}

var callUnsubscribeWS = function() {
    return $.ajax({
        type: 'GET',
        url : CONTEXT_PATH + '/rest/dashboard/unsubscribes',
        data : {
            start : getStart(),
            end   : getEnd()
        }
    })
}

var callTotalSubscribersWS = function() {
    return $.ajax({
        type: 'GET',
        url : CONTEXT_PATH + '/rest/dashboard/totalsubcribers',
        data : {
            start : getStart(),
            end   : getEnd()
        }
    })
}

var callLatestSubscribersWS = function() {
    return $.ajax({
        type: 'GET',
        url : CONTEXT_PATH + '/rest/dashboard/latestsubcribers',
        data : {
            start : getStart(),
            end   : getEnd(),
            n : $('#latest_subscribers tbody tr').size()
        }
    })
}

var drawTopChart = function(signups,unsubscribes) {
    // ChartJS
    var signupsChartJS = function() {
        var xAxis = [];
        var yAxis = [];
        for(var i=0; i < getDays(); i++) {
            var date = new Date(getStart());
            date.setDate(date.getDate() + i);
            xAxis.push(date);

            var dateString = date.toISOString().slice(0,10);
            var count = signups[dateString];
            //var count = (subscriptions) ? subscriptions.length : 0;

            yAxis.push(count);
        }
        return [xAxis,yAxis];
    }();
    var unsubscribeChartJS = function() {
        var xAxis = [];
        var yAxis = [];
        for(var i=0; i < getDays(); i++) {
            var date = new Date(getStart());
            date.setDate(date.getDate() + i);
            xAxis.push(date);

            var dateString = date.toISOString().slice(0,10);
            var subscriptions = unsubscribes[dateString];
            var count = (subscriptions > 0) ? -subscriptions : 0;

            yAxis.push(count);
        }
        return [xAxis,yAxis];
    }();
    $('#chartjs').remove();
    $('div#signups').append('<canvas id="chartjs" style="height: 250px; width: 100%; float: left;"></canvas>');
    var ctx = document.getElementById('chartjs').getContext('2d');
    
    var chart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: signupsChartJS[0],
            datasets: [
                {
                    label: 'New signups',
                    data : signupsChartJS[1],
                    backgroundColor: '#2f9fe0',
                    borderColor: '#2f9fe0'
                },
                {
                    label: 'Unsubscribes',
                    data: unsubscribeChartJS[1],
                    backgroundColor: '#D9534F',
                    borderColor: '#D9534F'
                }
            ]
        },
        options: {
            scales: {
                xAxes: [{
                    type: "time",
                    time: {
                        unit: 'day',
                        round: 'day',
                        displayFormats: {
                          day: 'MMM D'
                        }
                    }
                }],
                yAxes:[{
                        ticks:{
                            //stepSize : 1,
                        }
                    }]
            },
        }
    });
}

var drawTotalSubscriptions = function(subscriptions) {
    var totalActive = subscriptions['totalActive'];
    var totalSignups = subscriptions['totalSignups'];
    var totalUnsubscribes = subscriptions['totalUnsubscribes'];
    var totalBounces = subscriptions['totalBounces'];
    
    var sparklineData = [];
    for(var i = 0; i < totalActive.length; i++) {
        var dateCount = totalActive[i];
        sparklineData.push(dateCount[1]);
    }
    $('#active_subscriptions').html((sparklineData !== null) ? sparklineData.join(',') : '');
    $('#totalSignups').html((totalSignups !== null) ? totalSignups : 'No data');
    $('#totalUnsubscribes').html((totalUnsubscribes !== null) ? totalUnsubscribes : 'No data');
    $('#totalBounces').html((totalBounces !== null) ? totalBounces : 'No data');
    
    gSparkline.init("body");
}

var drawLatestSubscribersTable = function(container) {
    var columns = container['columns'];
    var subscribers = container['subscribers'];
    
    var $table = $('table#latest_subscribers');
    $table.empty();
    $table.append('<thead><tr></tr></thead>');
    var $header = $table.find('thead tr');
    for(var i=0; i < columns.length; i++) {
        $header.append('<th>' + columns[i] + '</th>');   
    }
    
    $table.append('<tbody></tbody>');
    var $body = $table.find('tbody')
    var i;
    for(i = 0; i < subscribers.length && i < MAX_LATEST_SUBSCRIBERS; i++) {
        var subscriber = subscribers[i];
        $body.append('<tr data-row='+i+'></tr>');
        var $row = $body.find('tr[data-row=' + i +']');
        for(var j=0; j < columns.length; j++) {
            var fieldVal = subscriber[columns[j]];
            fieldVal = (fieldVal) ? fieldVal : '';
            $row.append('<td>' + fieldVal + '</td>');
        }
    }
    while(++i < MAX_LATEST_SUBSCRIBERS) {
        $body.append('<tr data-row='+i+'></tr>');
        var $row = $body.find('tr[data-row=' + i +']');
        for(var j=0; j < columns.length; j++) {
            $row.append('<td>-</td>');
        }
    }
}

var refreshSignups = function() {
    if($('#signups').size() > 0) {
        $.when(
            callSignupsWS(),
            callUnsubscribeWS()
        ).done(function(c1,c2){
            if(c1 && c2)
                drawTopChart(c1[0],c2[0]);
        });
    }
}

var refreshTotalSubscribers = function() {
    if($('#subscriptions').size() > 0) {
        $.when(
            callTotalSubscribersWS()
        ).done(function(c){
            if(c)
                drawTotalSubscriptions(c);
        });
    }
}

var refreshLatestSubscribers = function() {
    if($('#subscribers').size() > 0) {
        $.when(
            callLatestSubscribersWS()
        ).done(function(c){
            if(c)
                drawLatestSubscribersTable(c);
        });
    }
}

var refreshDashboard = function() {
    refreshSignups();
    refreshTotalSubscribers();
    refreshLatestSubscribers();
}

var initPeriodSelection = function() {
    $('select#days').val(DEFAULT_DAYS);
    $('select#days').change(function(){
        refreshDashboard();
    });
}

var initWelcome = function() {
    if(SHOW_WELCOME) {
        $('#welcome').modal('show');
    }
}

var refresh = function(data) {
    var ajaxstatus = data.status; // Can be "begin", "complete" and "success"
    var block = $(data.source).parents(".modal-dialog");
    
    switch (ajaxstatus) {
        case "begin": // This is called right before ajax request is been sent.
            block_refresh(block);
            break;

        case "complete": // This is called right after ajax response is received.
            block_refresh(block);
            break;

        case "success": // This is called when ajax response is successfully processed.
            initICheckMand();
            break;
    }
};

var initICheckMand = function() {
    gICheck.init('body');
    $('input.dontshow').on('ifChanged',function(event){
        //doing this here because iCheck cannot auto-trigger our input.checkbox's onchange method
        mojarra.ab(this, //this here should be the original checkbox, not the icheck checkbox
            event,'valueChange','@form','@form',{'onevent':refresh,'delay':'0'}); 
    })
}

$(document).ready(function(){
    initPeriodSelection();
    refreshDashboard();
    initWelcome();
    initICheckMand();
})