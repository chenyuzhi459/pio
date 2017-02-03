$(function(){
var hostUrl='http://192.168.0.110:8080';

// 项目
$('#allProcessBtn').click(
    function(){
        $.ajax({
            url: hostUrl+"/pio/process",
            type: 'GET',
            dataType:"json",
            contentType: "application/json; charset=utf-8",
            success: function(data){
                $('#allProcess').text(JSON.stringify(data, null, 4));
                $('#processSelect').html("");
                $('#operatorJson').val("");
                $('#operatorJson').hide();

                $('#processSelect').append($('<option value="">所有</option>'));
                $(data).each(function(idx, item){
                    $('#processSelect').append($('<option value="'+item.id+'">'+item.name + "-" + item.status + "-" + item.description + '</option>'));
                });
            }
        });
    }
);

var process;
$('#operatorJson').hide();
$('#processSelect').change(
    function(item){
        var select = item.target;
        var processId = $(this).children('option:selected').val();
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
                $('#allProcess').text(JSON.stringify(data, null, 4));
                $('#operatorSelect').html("");

                $('#operatorSelect').append($('<option value="">所有</option>'));
//                $('#operatorSelect').change();
                $(process.rootOperator.execUnits[0].operators).each(function(idx, item){
                    $('#operatorSelect').append($('<option value="'+item.name+'">'+item.fullName + "-" + item.xPos + "-" + item.yPos + '</option>').attr("operator", $.toJSON(item)));
                });

            }
        });
    }
);


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



}
);