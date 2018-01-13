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

var getStart = function() {
    var startDay = new Date(new Date().setDate(new Date().getDate() - DAYS));
    
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

var initDashboard = function() {
    $.when(
        callSignupsWS(),
        callUnsubscribeWS()
    ).done(function(c1,c2){
        /*
        var signupsPlot = function() {
            var plot = [];
            for(var i=0; i < DAYS; i++) {
                var date = new Date(getStart());
                date.setDate(date.getDate() + i);
                var dateString = date.toISOString().slice(0,10);
                var subscriptions = c1[0][dateString];
                var count = (subscriptions) ? subscriptions.length : 0;
                
                plot.push([date,count]);
            }
            return plot;
        };
        
        var signupsPlotRS = function() {
            var plot = [];
            for(var i=0; i < DAYS; i++) {
                var date = new Date(getStart());
                date.setDate(date.getDate() + i);
                var dateString = date.toISOString().slice(0,10);
                var epochSecond = date.getTime() / 1000;
                var subscriptions = c1[0][dateString];
                var count = (subscriptions) ? subscriptions.length : 0;
                
                plot.push({ x: epochSecond , y: count});
            }
            return plot;
        };
        
        var unsubscribePlot = function() {
            var plot = [];
            for(var i=0; i < DAYS; i++) {
                var date = new Date(getStart());
                date.setDate(date.getDate() + i);
                var dateString = date.toISOString().slice(0,10);
                var subscriptions = c2[0][dateString];
                var count = (subscriptions) ? subscriptions.length : 0;
                
                plot.push([date,count]);
            }
            return plot;
        };
        
        var unsubscribePlotRS = function() {
            var plot = [];
            for(var i=0; i < DAYS; i++) {
                var date = new Date(getStart());
                date.setDate(date.getDate() + i);
                var dateString = date.toISOString().slice(0,10);
                var epochSecond = date.getTime() / 1000;
                var subscriptions = c2[0][dateString];
                var count = (subscriptions) ? subscriptions.length : 0;
                
                plot.push({x: epochSecond, y: count});
            }
            return plot;
        };
        /*
        var rlp = new Rickshaw.Graph( {	
            element: document.getElementById("rickshaw"),
            renderer: 'bar',
            tickSize: 1,
            min: -0.2,
            //max: 1.2,
            padding: { top: 0.1 },
            series: [
                {data: signupsPlotRS(), color: '#2f9fe0',name: "New signups"},
                {data: unsubscribePlotRS(), color: '#D9534F',name: "Unsubscribes"}
            ]
        });
        */
        
        //var hover = new Rickshaw.Graph.HoverDetail({ 
        //    graph: rlp,
            /*formatter: function(series, x, y) {
                
		var date = '<span class="date">' + new Date(x * 1000).toISOString().slice(0,10) + '</span>';
		var swatch = '<span class="detail_swatch" style="background-color: ' + series.color + '"></span>';
		var content = swatch + series.name + ": " + parseInt(y) + '<br>' + date;
		return content;
            }*/
        //});
        
        /*
        var legend = new Rickshaw.Graph.Legend({
            graph: rlp, 
            element: document.getElementById('legend')
        });
        
        var xAxis = new Rickshaw.Graph.Axis.Time( {
            graph: rlp
        });
        
        var yAxis = new Rickshaw.Graph.Axis.Y( {
            graph: rlp
        });

        rlp.render();
        
        $.plot(
            $("#chart-flot-line"), 
            [
                {data: unsubscribePlot(), label: "Unsubscribes"}, 
                {data: signupsPlot(), label: "New Signups"},
            ], 
            {
                series: {
                    lines: {show: true}, 
                    points: {show: true}
                },
                //lines: {show: true},
                //points: {show: true},
                grid: {hoverable: true, clickable: true},
                xaxis: {
                    mode : "time",
                    timeformat:"%d-%b",
                    
                },
                yaxis: {
                    tickSize : 1,
                    tickDecimals : 0,
                    min : 0
                }
            }
        );

        $("<div id='tooltip-signup'></div>").css({
                position: "absolute",
                display: "none",
                //border: "1px solid #fdd",
                padding: "2px",
                //"background-color": "#fee",
                opacity: 0.80
        }).appendTo("body");
        
        $("#chart-flot-line").bind("plothover", function (event, pos, item) {
            if (item) {
                    var date = new Date(item.datapoint[0]),
                        count = item.datapoint[1];
                    var dateString = date.toString();
                    var dayOfMonth = date.getDate();
                    var month = date.getShortMonthName();
                    $("#tooltip-signup").html(
                            "<strong>" + count + "</strong>" 
                            + " Signup" + ((count > 1) ? "s" : "")
                            + " on " + dayOfMonth + "-" + month
                    )
                    .css(function(){
                        return {
                            top: item.pageY-25, left: item.pageX+5};
                    }())
                    .fadeIn(200);
            } else {
                    $("#tooltip").hide();
            }
        });
        */
        // ChartJS
        var signupsChartJS = function() {
            var xAxis = [];
            var yAxis = [];
            for(var i=0; i < DAYS; i++) {
                var date = new Date(getStart());
                date.setDate(date.getDate() + i);
                xAxis.push(date);
                
                var dateString = date.toISOString().slice(0,10);
                var subscriptions = c1[0][dateString];
                var count = (subscriptions) ? subscriptions.length : 0;
                
                yAxis.push(count);
            }
            return [xAxis,yAxis];
        }();
        var unsubscribeChartJS = function() {
            var xAxis = [];
            var yAxis = [];
            for(var i=0; i < DAYS; i++) {
                var date = new Date(getStart());
                date.setDate(date.getDate() + i);
                xAxis.push(date);
                
                var dateString = date.toISOString().slice(0,10);
                var subscriptions = c2[0][dateString];
                var count = (subscriptions) ? -subscriptions.length : 0;
                
                yAxis.push(count);
            }
            return [xAxis,yAxis];
        }();
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
                                //beginAtZero: true,
                                stepSize : 1,
                            }
                        }]
                },
            }
        });
    }) 
}

$(document).ready(function(){
    initDashboard();
})