$(function(){
var hostUrl='http://192.168.0.110:8080';

var isFirst = true;
function getAllProcess(){
    $("#processTable tr:not(:first)").empty("");
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
                var view = $('<a href="javascript:void(0);">查看</a>');
                view.attr("processId", item.id);
                view.click(function(obj){
                    var a = $(obj.target);
                    var processId = a.attr("processId");
                    changeProcess(processId);
                });
                row.append($('<td></td>').append(view));
                if(isFirst){
                    isFirst = false;
                    view.click();
                }
            });
        }
    });
}
getAllProcess();

function changeProcess(processId){
    $.ajax({
        url: hostUrl+"/pio/process/" + processId,
        type: 'GET',
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        success: function(data){
            process = data;
            $('#processId').text(process.id);
            $('#processName').val(process.name);
            $('#processDesc').val(process.description);
            $('#processJson').text(JSON.stringify(data, null, 4));

            $("#processOperatorTable tr:not(:first)").empty("");
            $(process.rootOperator.execUnits[0].operators).each(function(idx, item){
                var row = $("<tr></tr>");
                row.append($('<td></td>').text(item.name));
                row.append($('<td></td>').text(item.fullName));
                row.append($('<td></td>').text(item.description));
                row.append($('<td></td>').text(item.group));
                $('#processOperatorTable').append(row);
            });

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