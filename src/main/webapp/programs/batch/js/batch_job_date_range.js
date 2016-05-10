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
                        'Today': [moment().hours(0).minutes(0).seconds(0), moment().hours(23).minutes(59).seconds(59)],
                        'Yesterday': [moment().subtract('days', 1).hours(0).minutes(0).seconds(0), moment().subtract('days', 1).hours(23).minutes(59).seconds(59)],
                        'Last 7 Days': [moment().subtract('days', 6).hours(0).minutes(0).seconds(0), moment().hours(23).minutes(59).seconds(59)],
                        'Last 30 Days': [moment().subtract('days', 29).hours(0).minutes(0).seconds(0), moment().hours(23).minutes(59).seconds(59)],
                        'This Month': [moment().startOf('month').hours(0).minutes(0).seconds(0), moment().endOf('month').hours(23).minutes(59).seconds(59)],
                        'Last Month': [moment().subtract('month', 1).startOf('month').hours(0).minutes(0).seconds(0), moment().subtract('month', 1).endOf('month').hours(23).minutes(59).seconds(59)]
                    },
                    opens: 'right',
                    startDate: moment().subtract('days', 29),
                    endDate: moment()
                },
                function (start, end, event) {
                    $('#reportrange2 span').html(start.format('D MMMM YYYY, HH:mm:ss') + ' - ' + end.format('D MMMM YYYY, HH:mm:ss'));
                    block_refresh($('#all_batch_job_block'));
                    //$('#range').val(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
                    //$.post('/SegMail/program/batch',$('#all_batch_job_controls').serialize());
                    jsf.ajax.request(
                        $("#all_batch_job_controls"),
                        event,
                        {
                            
                            'all_batch_job_controls' : 'all_batch_job_controls',
                            'javax.faces.source' : 'all_batch_job_controls',
                            'javax.faces.partial.event' : 'click',
                            'javax.faces.partial.execute' : 'batch_job_date_range_panel all_batch_job_controls',
                            'javax.faces.partial.render' : 'all_batch_job_block',
                            'javax.faces.behavior.event' : 'click',
                            'javax.faces.partial.ajax' : 'true',
                            'all_batch_job_controls:start' : start.format('D MMMM YYYY, HH:mm:ss'),
                            'all_batch_job_controls:end' : end.format('D MMMM YYYY, HH:mm:ss')
                        }
                    );
                    
                    //block_refresh($('#all_batch_job_block'));//No need, because already rendered.
                }

                );
                /*$("#reportrange2 span").html(
                        moment().subtract('days', 29).hours(0).minutes(0).seconds(0)
                            .format('D MMMM YYYY, HH:mm:ss') 
                        + ' - ' + 
                        moment().hours(23).minutes(59).seconds(59)
                            .format('D MMMM YYYY, HH:mm:ss'));*/
                $("#reportrange2 span").html('Please select a date range');

            }
            /* eof daterangepicker */

        }
    }
    gDemos.init();

});



function gDemo() {
    gDemos.init();

}
