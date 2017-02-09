$(function(){
var hostUrl='http://192.168.0.110:8080';

var isFirst = true;
function getAllProcess(){
//    $("#processTable tr:not(:first)").empty("");
    $("#processTable").find("tbody").empty('');
    $.ajax({
        url: hostUrl+"/pio/process",
        type: 'GET',
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            $(data).each(function(idx, item){
                var row = $("<tr></tr>");
                row.append($('<td></td>').text(item.id));
                row.append($('<td></td>').text(item.name));
                row.append($('<td></td>').text(item.description));
                row.append($('<td></td>').text(item.status));
                $('#processTable').append(row);
                var column = $('<td></td>');
                var view = $('<a href="javascript:void(0);">查看</a>');
                view.attr("processId", item.id);
                view.click(function(obj){
                    var a = $(obj.target);
                    var processId = a.attr("processId");
                    changeProcess(processId);
                });
                column.append(view);
                column.append('&nbsp;');
                row.append(column);
                if(isFirst){
                    isFirst = false;
                    view.click();
                }
                var run = $('<a href="javascript:void(0);">运行</a>');
                run.attr("processId", item.id);
                run.click(function(obj){
                    var a = $(obj.target);
                    var processId = a.attr("processId");
                    runProcess(processId);
                });
                column.append(run);
                column.append('&nbsp;');

                var result = $('<a href="javascript:void(0);">结果</a>');
                result.attr("processId", item.id);
                result.click(function(obj){
                    var a = $(obj.target);
                    var processId = a.attr("processId");
                    getProcessResult(processId);
                });
                column.append(result);
                column.append('&nbsp;');
            });
        }
    });
}
getAllProcess();

function getProcessResult(processId){
    $.ajax({
        url: hostUrl+"/pio/process/result/" + processId,
        type: 'GET',
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            changeProcessShow(data);
        }
    });
}

function changeProcess(processId){
    $.ajax({
        url: hostUrl+"/pio/process/" + processId,
        type: 'GET',
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            changeProcessShow(data);
        }
    });
}

function runProcess(processId){
    $.ajax({
        url: hostUrl+"/pio/process/run/" + processId,
        type: 'GET',
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            changeProcessShow(data);
        }
    });
}

function changeProcessShow(data){
    process = data;
    $('#processId').text(process.id);
    $('#processName').val(process.name);
    $('#processDesc').val(process.description);
    $('#processJson').text(JSON.stringify(data, null, 4));

//    $("#processOperatorTable tr:not(:first)").empty("");
    $("#processOperatorTable").find("tbody").empty('');
    if(process.rootOperator.execUnits.length > 0){
        $(process.rootOperator.execUnits[0].operators).each(function(idx, item){
            var row = $("<tr></tr>");
            row.append($('<td></td>').text(item.name));
            row.append($('<td></td>').text(item.fullName));
            row.append($('<td></td>').text(item.description));
            row.append($('<td></td>').text(item.group));
            row.append($('<td></td>').text(item.status));
            $('#processOperatorTable').append(row);
            var column = $('<td></td>');
            row.append(column);

            var view = $('<a href="javascript:void(0);">查看</a>');
            view.attr("operatorName", item.name);
            view.attr("processId", process.id);
            view.click(function(obj){
                var a = $(obj.target);
                var processId = a.attr("processId");
                var operatorName = a.attr("operatorName");
                changeOperator(processId, operatorName);
            });
            column.append(view);

            var result = $('<a href="javascript:void(0);">结果</a>');
            result.attr("operatorName", item.name);
            result.attr("processId", process.id);
            result.click(function(obj){
                var a = $(obj.target);
                var processId = a.attr("processId");
                var operatorName = a.attr("operatorName");
                getOperatorResult(processId, operatorName);
            });
            column.append(result);

        });
    }

    $("#connectionTable tr:not(:first)").empty("");
    $(process.connections).each(function(idx, item){
        var row = $("<tr></tr>");
        row.append($('<td></td>').text(item.fromOperator));
        row.append($('<td></td>').text(item.fromPort));
        row.append($('<td></td>').text(item.toOperator));
        row.append($('<td></td>').text(item.toPort));
        $('#connectionTable').append(row);
    });
}

function changeOperator(processId, operatorName){
    $.ajax({
        url: hostUrl+"/pio/operator/" + processId + "/" + operatorName,
        type: 'GET',
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            $('#operatorJson').text(JSON.stringify(data, null, 4));
        }
    });
}

function getOperatorResult(processId, operatorName){
    $.ajax({
        url: hostUrl+"/pio/operator/result/" + processId + "/" + operatorName,
        type: 'GET',
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            $('#operatorJson').text(JSON.stringify(data, null, 4));
        }
    });
}


$('#operatorSelect').change(
    function(){
        var operator = $.evalJSON($(this).children('option:selected').attr('operator'));
        var operatorId = operator.name;
        $.ajax({
            url: hostUrl+"/pio/operator/" + process.id + "/" + operatorId,
            type: 'GET',
            dataType:"json",
            contentType: "application/json; charset=utf-8",
            success: function(data){
                $('#operatorJson').val(JSON.stringify(data, null, 4));
                $('#operatorJson').show();
            }
        });

    }
);

$('#processCreateBtn').click(function(){
    var processName = $('#processName').val();
    var processDesc = $('#processDesc').val();
    $.ajax({
        url: hostUrl+"/pio/process",
        type: 'POST',
        data:{
             "name": processName,
             "description": processDesc
        },
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            $('#allOperators').text(JSON.stringify(data, null, 4));
            getAllProcess();
        }
    });
});




// 算子
$('#allOperatorBtn').click(function(){
    $.ajax({
        url: hostUrl+"/pio/operator",
        type: 'GET',
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            $('#allOperators').text(JSON.stringify(data, null, 4));
        },
        error: function(xhr, ajaxOptions, thrownError) {
            console.info("error.");
            if (xhr.status == 200) {
                alert(ajaxOptions);
            }
            else {
                alert(xhr.status);
                alert(thrownError);
            }
        }
    });
});

$.ajax({
    url: hostUrl+"/pio/operator",
    type: 'GET',
    dataType:"json",
    contentType: "application/json; charset=utf-8",
    success: function(data){
//        $('#allOperators').text(JSON.stringify(data, null, 4));
        $(data).each(function(idx, item){
            var row = $("<tr></tr>");
            row.append($('<td></td>').text(item.name));
            row.append($('<td></td>').text(item.fullName));
            row.append($('<td></td>').text(item.description));
            row.append($('<td></td>').text(item.group));
            $('#operatorTable').append(row);
            row.append($('<td></td>').append($('<span></span>').text($.toJSON(item))));
        });
    },
    error: function(xhr, ajaxOptions, thrownError) {
        console.info("error.");
        if (xhr.status == 200) {
            alert(ajaxOptions);
        }
        else {
            alert(xhr.status);
            alert(thrownError);
        }
    }
});

}
);