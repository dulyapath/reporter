var log = console.log.bind();
var isErr = false;

var jxLoad = null;
$(document).ready(function () {
    var database = $("#db_code").val();
    $.ajax({
        url: 'verify-db-process',
        method: 'POST',
        success: function (res) {

            if (res.success) {
                jxLoad = setTimeout(function () {
                    var stop = $('#log').data('stop');
                    if (stop !== 1 && !isErr) {
                        location.href = "index.jsp?database=" + database;
                    }
                }, 500);
            } else {

            }
        }
    });

    loadLog();
});

var countError = 0;

function loadLog() {
    $('#log').text('');
    $.ajax({
        url: 'verify-log',
        method: 'GET',
        success: function (res) {
            var logs = res.split('\n');
            var isComplete = false;
            for (var i = 0; i < logs.length; i++) {
                (function (i) {
                    if (logs[i] != '') {
                        var strLog = logs[i].split(',');

                        if (strLog[0] != 'complete') {
                            isErr = strLog[2] == 0;
                            $('#log').append(strFull(strLog[0]) + ' : ' + strLog[1] + ' <span style="color: ' + ((strLog[2] == 1 ? 'green' : 'red')) + ';"> ' + (strLog[2] == 1 ? 'done' : 'fail') + '</span>\n');
                            if (jxLoad != null && isErr) {
                                clearTimeout(jxLoad);
                                log('cancel');
                            }
                        } else if (strLog[0] == 'wait') {
//                            countError++;
//                            if(countError > 10){
//                                isComplete = true;
//                                $('#log').append(strFull(strLog[0]) + ' : ' + strLog[1] + ' <span style="color: red;"> ' + (strLog[2] == 1 ? 'done' : 'fail') + '</span>\n');
//                            }
                        } else {
                            isComplete = true;
                            $('#log').append('<span style="color: green;">Complete</span>\n\n');
                        }
                    }
                })(i);
            }
            if (!isComplete) {
                setTimeout(loadLog, 500);
            }

        }
    });
}

function strFull(str) {
    var arr = [
        {key: 't', name: 'table'},
        {key: 'c', name: 'column'}
    ];
    for (var i = 0; i < arr.length; i++) {
        var tmp = arr[i];
        if (str == tmp.key) {
            return tmp.name;
        }
    }
    return str;
}