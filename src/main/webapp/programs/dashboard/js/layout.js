var initDemoChart = function () {
    if ($("#chart-flot-line").length > 0) {
        //jFlot

        function labelFormatter(label, series) {
            return "<div style='text-shadow: 1px 2px 1px rgba(0,0,0,0.2); font-size: 11px; text-align:center; padding:2px; color: #FFF; line-height: 13px;'>" + label + "<br/>" + Math.round(series.percent) + "%</div>";
        }

        var sin = [], cos = [], sin2 = [];

        for (var i = 0; i < 10; i += 0.3) {
            sin.push([i, Math.sin(i)]);
            sin2.push([i, Math.sin(i - 1.57)]);
            cos.push([i, Math.cos(i)]);
        }
        $.plot($("#chart-flot-line"), [{data: sin, label: "sin(x)"}, {data: cos, label: "cos(x)"}, {data: sin2, label: "sin(y)"}], {
            series: {lines: {show: true}, points: {show: true}},
            grid: {hoverable: true, clickable: true},
            yaxis: {min: -1.1, max: 1.1}});

    }
}

$(document).ready(function(){
    initDemoChart();
})