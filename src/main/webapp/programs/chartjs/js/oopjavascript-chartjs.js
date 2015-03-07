
//A factory class to construct the ChartJS line chart


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
                datasets : this.datasets
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
        getFillColor : functon(){return this.fillColor; },
        getStrokeColor : function(){return this.strokeColor; },
        getPointColor : function(){return this.pointColor; },
        getPointStrokeColor : function(){return this.pointStrokeColor; },
        getPointHighlightFill : function(){return this.pointHighlightFill; },
        getPointHighlightStroke : function(){return this.pointHighlightStroke; },
        getData : function(){return this.data; },
        //setters
        setLabel : function(label){this.label = label; },
        setFillColor : functon(fillColor){this.fillColor = fillColor; },
        setStrokeColor : function(strokeColor){this.strokeColor = strokeColor; },
        setPointColor : function(pointColor){this.pointColor = pointColor; },
        setPointStrokeColor : function(pointStrokeColor){this.pointStrokeColor = pointStrokeColor; },
        setPointHighlightFill : function(pointHighlightFill){this.pointHighlightFill = pointHighlightFill; },
        setPointHighlightStroke : function(pointHighlightStroke){this.pointHighlightStroke = pointHighlightStroke; },
        setData : function(data){this.data = data; }
    };
}());

//our pseudo class representation of the ChartJS linechart option




