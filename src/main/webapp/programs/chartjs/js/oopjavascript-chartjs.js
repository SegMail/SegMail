
//A factory class to construct the ChartJS line chart
var lineChartPlotter = (function(){
    //var lineChartData;
    //var append;
    
    return {
        //getters
        //getLineChartData : function(){ return this.lineChartData; },
        //getAppend : function(){ return this.append; },
        
        //setters
        //setLineChartData : function(lineChartData){ this.lineChartData = lineChartData; },
        //setAppend : function(append){ this.append = append; },
        
        //produce the chart
        createChart : function(lineChartData,isResponsive){
            var ctx = document.getElementById(lineChartData.getCanvasId()).getContext("2d");
            chart = new Chart(ctx).Line(lineChartData, {
                responsive: isResponsive
            });
            chart.update();
        }
    }
}());

//A factory class to return a representation of a ChartJS data
var lineChartDataFactory = (function(){
    var canvasId;
    var labels = [];
    var datasets; // = [];

    return{
        //getters
        getCanvasId : function(){ return this.canvasId; },
        getLabels : function(){ return this.labels; },
        getDatasets : function(){ return this.datasets; },
        //setters
        setCanvasId : function(canvasId){ this.canvasId = canvasId; },
        setLabels : function(labels){ this.labels = labels; },
        setDatasets : function(datasets){ this.datasets = datasets; },
        //Data manipulation methods
        //Adds a new dataset at the last position
        addDataset : function(dataset){
            this.datasets.push(dataset);
        },
        //Removes the last dataset added
        removeDataset : function(){
            return this.datasets.pop();
        },
        //Removes all dataset
        clearDataset : function(){
        this.datasets.length = 0;
        },
        //Adds a new label
        addLabel : function(label){
            this.label.push(label);
        },
        //Removes the last label added
        removeLabel : function(){
            return this.label.pop();
        },
        //Removes all labels
        clearLabels : function(){
            this.labels.length = 0;
        },
        //Produce a ChartJS line chart data object
        getLineChartData : function(){
            return{
                labels : this.labels,
                datasets : this.datasets,
                canvasId : this.canvasId
            }
        }
    }
}());

//our pseudo class representation of the ChartJS linechart dataset
var dataset = (function(){
    var label;
    var fillColor;
    var strokeColor;
    var pointColor;
    var pointStrokeColor;
    var pointHighlightFill;
    var pointHighlightStroke;
    var data;
    return {
        //getters
        getLabel : function(){return this.label; },
        getFillColor : function(){return this.fillColor; },
        getStrokeColor : function(){return this.strokeColor; },
        getPointColor : function(){return this.pointColor; },
        getPointStrokeColor : function(){return this.pointStrokeColor; },
        getPointHighlightFill : function(){return this.pointHighlightFill; },
        getPointHighlightStroke : function(){return this.pointHighlightStroke; },
        getData : function(){return this.data; },
        //setters
        setLabel : function(label){this.label = label; },
        setFillColor : function(fillColor){this.fillColor = fillColor; },
        setStrokeColor : function(strokeColor){this.strokeColor = strokeColor; },
        setPointColor : function(pointColor){this.pointColor = pointColor; },
        setPointStrokeColor : function(pointStrokeColor){this.pointStrokeColor = pointStrokeColor; },
        setPointHighlightFill : function(pointHighlightFill){this.pointHighlightFill = pointHighlightFill; },
        setPointHighlightStroke : function(pointHighlightStroke){this.pointHighlightStroke = pointHighlightStroke; },
        setData : function(data){this.data = data; },
        
        //import from global settings
        importOptions : function(options){
            if (typeof options.label !== 'undefined') { setLabel(options.label); };
            if (typeof options.fillColor !== 'undefined') { setFillColor(options.fillColor); };
            if (typeof options.strokeColor !== 'undefined') { setStrokeColor(options.strokeColor); };
            if (typeof options.pointColor !== 'undefined') { setPointColor(options.pointColor); };
            if (typeof options.pointStrokeColor !== 'undefined') { setPointStrokeColor(options.pointStrokeColor); };
            if (typeof options.pointHighlightStroke !== 'undefined') { setPointHighlightFill(options.pointHighlightStroke); };
            if (typeof options.pointHighlightStroke !== 'undefined') { setPointHighlightStroke(options.pointHighlightStroke); };
            if (typeof options.data !== 'undefined') { setData(options.data); };
        }
            
    };
}());

//replots the chart with new dataset
//var data : the dataset to be added, should be an array
//var chart : the chart object
//var append : a boolean to determine if the new dataset should replace 
//
function replotChart(newData,append,options,globalSettings,chartId,chartParentId,chartObj){

    //create new dataset
    //if not actual options is passed in, chart will take on default values?
    newDataset = (options.length === 0) ? chart.datasets[0] : options;
    //newDataset.data = newData;

    //determine X-axis
    var xDiv = newData.length; // number of divisions in the X-axis
    var newLabels = [];
    var newNewData = [];
    if(xDiv > globalSettings.maxLabels){
        var start = 0;
        var end = newData.length;
        var div = (end - start) / globalSettings.maxLabels;
        for(var start; start <= end; start += div){
            newLabels.push(Math.ceil(start));
            if(isNaN(newData[Math.ceil(start)]))
                newNewData.push(0);
            else
                newNewData.push(newData[Math.ceil(start)]);
        }
    }
    else{
        newNewData = newData;
        for(i in newData){
            newLabels.push(i);
        }
    }


    var data = {
        labels : newLabels,
        datasets: [
            {
                label: newDataset.label,
                fillColor: newDataset.fillColor,
                strokeColor: newDataset.strokeColor,
                pointColor: newDataset.pointColor,
                pointStrokeColor: newDataset.pointStrokeColor,
                pointHighlightFill: newDataset.pointHighlightFill,
                pointHighlightStroke: newDataset.pointHighlightStroke,
                data: newNewData
            }
        ]
    }
    //As from:
    //http://stackoverflow.com/questions/24815851/how-do-clear-a-chart-from-a-canvas-so-that-hover-events-cannot-be-triggered
    //The chart object cannot be cleared by calling destroy() or clear(), hence we have to remove the canvas
    //
    //var canvas = document.getElementById(chartId);
    var parent = document.getElementById(chartParentId);
    //Fastest way of removing all child nodes
    //http://stackoverflow.com/questions/3955229/remove-all-child-elements-of-a-dom-node-in-javascript
    //
    while(parent.firstChild){
        parent.removeChild(parent.firstChild);
    }
    //Add a brand new child node back
    
    parent.innerHTML = '<canvas id="canvas" height="180" width="400"></canvas>';
    
    var ctx = document.getElementById(chartId).getContext("2d");
    
    if(typeof chartObj !== "undefined") chartObj.destroy();
    chartObj = new Chart(ctx).Line(data, {
        responsive: true
    });
    chartObj.update();
}

