/* file:           demo.js
 * version:        1.3
 * last changed:   23.03.2014
 * description:    This file can be removed before you use template in production. 
 *                 It contains with elements used only for demo preview, and you doesnt need to include 
 *                 it in your project, couse this features is individual.
 */
//http://www.daterangepicker.com/

$(document).ready(function () {
    gDemos = {
        init: function () {

            if ($(".dtrange").length > 0) {
                $(".dtrange").daterangepicker({
                    timePicker: true,
                    ranges: {
                        'Today': [moment(), moment()],
                        'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
                        'Last 7 Days': [moment().subtract('days', 6), moment()],
                        'Last 30 Days': [moment().subtract('days', 29), moment()],
                        'This Month': [moment().startOf('month'), moment().endOf('month')],
                        'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
                    },
                    opens: 'right',
                    startDate: moment().subtract('days', 29),
                    endDate: moment()
                },
                function (start, end, event) {
                    $('#reportrange2 span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
                    block_refresh($('#all_batch_job_block'));
                    //$.post('/SegMail/program/batch',$('#all_batch_job_controls').serialize());
                    jsf.ajax.request(
                        $("#all_batch_job_controls"),
                        event,
                        {
                            'all_batch_job_controls' : 'all_batch_job_controls',
                            'javax.faces.source' : 'all_batch_job_controls',
                            'javax.faces.partial.event' : 'click',
                            'javax.faces.partial.execute' : 'all_batch_job_controls all_batch_job_controls',
                            'javax.faces.partial.render' : 'all_batch_job_block',
                            'javax.faces.behavior.event' : 'click',
                            'javax.faces.partial.ajax' : 'true'}
                    );
                    
                    //block_refresh($('#all_batch_job_block'));//No need, because already rendered.
                }

                );
                $("#reportrange2 span").html(moment().subtract('days', 29).format('MMMM D, YYYY') + ' - ' + moment().format('MMMM D, YYYY'));

            }
            /* eof daterangepicker */

        }
    }
    gDemos.init();

});



function gDemo() {
    gDemos.init();

}
