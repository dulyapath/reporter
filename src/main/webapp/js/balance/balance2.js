var log = console.log.bind();
var subLink = null;
var restURI = 'balanceList';
var objMainRow = null;
var objSubRow = null;
var objSubTable = null;

var objSubRowData1 = null, objSubRowData2 = null, objSubRowData3 = null, objSubRowData4 = null, objSubRowData5 = null;
var objSubTableData1 = null, objSubTableData2 = null, objSubTableData3 = null, objSubTableData4 = null, objSubTableData5 = null;
var searchItem = false;
var searchBarCode = false;

var onProcess = false;

var sortIc = 'down';
var sortCol = 'ic_code';


$(function () {
    initObj();



    $("#contentSearchBox").on("keyup", "#txtSearchItem", function () {
        searchItem = true;
        searchBarCode = false;
        $("#txtSearchBarcode").val('');
        loadList();
    });

    $("#contentSearchBox").on("click", "#btnSearchItem", function () {
        searchItem = true;
        searchBarCode = false;
        $("#txtSearchBarcode").val('');
        loadList();
    });

    $("#contentSearchBox").on("click", "#btnSearchBarcode", function () {
        searchBarCode = true;
        searchItem = false;
        $("#txtSearchItem").val('');
        loadList();
    });

    $("#contentTableBox").on("click", "#mainList #btnExpand", function () {
        var gid = $(this).closest('tr').attr('id');
        if ($('#' + gid + '_EXPAND').length === 0) {
            loadSubList(gid);
            $("#" + gid).data('toggle', true);
//            $('#' + gid).find('td:nth-child(4) h5 b').css('color', 'red').text('ซ่อน');
        } else {
            var toggle = $('#' + gid).data('toggle');
            if (toggle) {
                $("#" + gid).data('toggle', false);
//                $('#' + gid).find('td:nth-child(4) h5 b').css('color', '#00ca6d').text('แสดง');
            } else {
                $("#" + gid).data('toggle', true);
//                $('#' + gid).find('td:nth-child(4) h5 b').css('color', 'red').text('ซ่อน');
            }
            $('#' + gid + '_EXPAND').toggle();
        }
    });

    $("#contentTableBox").on("click", "#mainList #btnChart", function () {
        var itemCode = $(this).closest('tr').data('ic_code');
        window.location.href = "../sale-summary/list.jsp?item_code=" + itemCode;
    });
});



function sortICode() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortIc').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'ic_code';
    loadList();
}

function sortIcName() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortIcName').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'ic_name';
    loadList();
}

function sortBalancePO() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortBalancePO').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'balance_po';
    loadList();
}

function sortOrder() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortOrder').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'balance_order';
    loadList();
}

function sortSale() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortSale').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'balance_sale';
    loadList();
}

function sortWH() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortWH').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'warehouse';
    loadList();
}

function sortShelf() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortShelf').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'location';
    loadList();
}

function sortQty() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortQty').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'balance_qty';
    loadList();
}

function sortLastIn() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortLastIn').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'last_in';
    loadList();
}

function sortLastSale() {
    sortIc = sortIc === 'down' ? 'up' : 'down';
    $('#sortLastSale').attr("class", "fa fa-sort-" + sortIc);
    sortCol = 'last_sale';
    loadList();
}


function initObj() {
    subLink = $("#hSubLink").val();
    objMainRow = $("#contentTableBox").find("#mainRow").clone();
    objMainRow.show();
    objMainRow.removeAttr('id');

    objSubRow = $("#contentTableBox").find("#subRow").find("table tbody > tr:nth-child(1)").clone();
    objSubTable = $("#contentTableBox").find("#subRow").clone();
    objSubTable.show();
    objSubTable.removeAttr('id');
    objSubTable.find('table tbody > tr:nth-child(1)').remove();

    objSubRowData1 = $("#contentTableBox").find("#subRowData1").find("table tbody > tr:nth-child(1)").clone();
    objSubTableData1 = $("#contentTableBox").find("#subRowData1").clone();
    objSubTableData1.show();
    objSubTableData1.removeAttr('id');

    objSubRowData2 = $("#contentTableBox").find("#subRowData2").find("table tbody > tr:nth-child(1)").clone();
    objSubTableData2 = $("#contentTableBox").find("#subRowData2").clone();
    objSubTableData2.show();
    objSubTableData2.removeAttr('id');

    objSubRowData3 = $("#contentTableBox").find("#subRowData3").find("table tbody > tr:nth-child(1)").clone();
    objSubTableData3 = $("#contentTableBox").find("#subRowData3").clone();
    objSubTableData3.show();
    objSubTableData3.removeAttr('id');

    objSubRowData4 = $("#contentTableBox").find("#subRowData4").find("table tbody > tr:nth-child(1)").clone();
    objSubTableData4 = $("#contentTableBox").find("#subRowData4").clone();
    objSubTableData4.show();
    objSubTableData4.removeAttr('id');

    objSubRowData5 = $("#contentTableBox").find("#subRowData5").find("table tbody > tr:nth-child(1)").clone();
    objSubTableData5 = $("#contentTableBox").find("#subRowData5").clone();
    objSubTableData5.show();
    objSubTableData5.removeAttr('id');

    $("#contentTableBox").find("#mainList").html('');


}

function loadList() {
    onProcess = true;
    var list = $("#contentTableBox").find("#mainList"), load = $("#contentTableBox").find("#load");
    var sendData = {};
    if (searchItem) {
        sendData.search_item = $("#txtSearchItem").val();
    }
    if (searchBarCode) {
        sendData.search_barcode = $("#txtSearchBarcode").val();
    }

    sendData.sort = sortIc === 'down' ? 'asc' : 'desc';
    sendData.sort_col = sortCol;

    $.ajax(subLink + restURI + '?action_name=loadList', {
        method: 'GET',
        dataType: 'json',
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            load.show();
        },
        success: function (res) {
            console.log(res)
            list.html('');
            if (res.success) {

                if (res.data.length > 0) {

                    $.each(res.data, function (key, obj) {
                        var objResult = objMainRow.clone();
                        var gid = genid();
                        objResult.attr('id', gid);
                        objResult.data('ic_code', obj.ic_code);
                        objResult.data('toggle', false);

                        var tdIdx = 2;
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.ic_code);
                        //objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.ic_name + " (" + obj.barcode + ")");
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.ic_name);
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.ic_unit_name + " (" + obj.ic_unit_code + ")");
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.balance_po, 0));
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.balance_order, 0));
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.balance_sale, 0));
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.warehouse);
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.location);
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.balance_qty, 0));
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(dateThai(obj.last_in));
                        objResult.find('td:nth-child(' + (++tdIdx) + ') h5').text(dateThai(obj.last_sale));

                        list.append(objResult);
                    });
                }
            }
        }, complete: function () {
            load.hide();
            onProcess = false;
        }
    });
}

function loadSubList(gid) {
    var subTable = objSubTable.clone();
    var itemCode = $('#' + gid).data('ic_code');
    var subGid = gid + '_EXPAND';

    subTable.attr('id', subGid);
    $('#' + gid).after(subTable);

    var $endPoint = $('#' + subGid);
    $.ajax(subLink + restURI + '?action_name=loadSub', {
        method: "GET",
        data: {data: JSON.stringify({ic_code: itemCode})},
        success: function (res) {
            if (res.success) {
                if (res.data1.length > 0) {
                    loadSubData(1, $endPoint.find('#subRowData1').find('#subList'), res.data1, objSubRowData1.clone());
                    $endPoint.find('#subRowData1').show();
                }

                if (res.data2.length > 0) {
                    loadSubData(2, $endPoint.find('#subRowData2').find('#subList'), res.data2, objSubRowData2.clone());
                    $endPoint.find('#subRowData2').show();
                }

                if (res.data3.length > 0) {
                    loadSubData(2, $endPoint.find('#subRowData3').find('#subList'), res.data3, objSubRowData3.clone());
                    $endPoint.find('#subRowData3').show();
                }

                if (res.data4.length > 0) {
                    loadSubData(3, $endPoint.find('#subRowData4').find('#subList'), res.data4, objSubRowData4.clone());
                    $endPoint.find('#subRowData4').show();
                }

                if (res.data5.length > 0) {
                    loadSubData(3, $endPoint.find('#subRowData5').find('#subList'), res.data5, objSubRowData5.clone());
                    $endPoint.find('#subRowData5').show();
                }
            }
        }
    });
}

function loadSubData(type, content, res, objSubRow) {
    $.each(res, function (key, obj) {
        var subRow = objSubRow;
        var tdIdx = 0;
        subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(dateThai(obj.doc_date));
        subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.doc_time);
        switch (type) {
            case 1:
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.cust_code + ' (' + obj.cust_name + ')');
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.branch_code + ' (' + obj.branch_name + ')');
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.doc_no);
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.po_qty, 0));
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.in_qty, 0));
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.cancel_qty, 0));
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.balance_qty, 0));
                break;
            case 2:
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.cust_code + ' (' + obj.cust_name + ')');
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.branch_code + ' (' + obj.branch_name + ')');
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.doc_no);
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.po_qty, 0));
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.in_qty, 0));
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.balance_qty, 0));
                break;
            case 3:
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.doc_no);
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.branch_code + ' (' + obj.branch_name + ')');
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.cust_code + ' (' + obj.cust_name + ')');
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.wh_code);
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.shelf_code);
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.qty, 0));
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.unit_name + ' (' + obj.unit_code + ')');
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.price, 0));
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(obj.discount);
                subRow.find('td:nth-child(' + (++tdIdx) + ') h5').text(numberFormat2(obj.sum_amount, 0));
                break;
        }

        content.append(subRow);
    });
}

function numberFormat2(number, fix) {
    number = parseFloat(number);
    number = isNaN(number) ? 0.00 : number;
    var tmp = 0.00;
    if (fix > 0) {
        tmp = number.toFixed(fix).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
    } else {
        number = Math.round(number);
        tmp = number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
    return tmp;
}

function dateThai(strDate) {
    var Months = ["ม.ค", "ก.พ", "มี.ค", "เม.ย", "พ.ค", "มิ.ย", "ก.ค", "ส.ค", "ก.ย", "ต.ค", "พ.ย", "ธ.ค"];
    if (strDate !== undefined) {
        var sptDate = strDate.split('-');
        var day = sptDate[2];
        var month = sptDate[1];
        var year = sptDate[0];

        return day + ' ' + Months[parseInt(month) - 1] + ' ' + year;
    } else {
        return "-";
    }
}

var id_used = [];
function genid(len, renew) {
    var $reNew = renew || false;
    var $gen_length = len || 4;
    var $lip_text = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var $lip_length = $lip_text.length;

    var $gen_text = '';
    for (var $i = 0; $i < $gen_length; $i++) {
        $gen_text += $lip_text.charAt(Math.floor(Math.random() * ($lip_length - 0 + 1)) + 0);
    }

//    log("Max generate :: "+(Math.pow($lip_length, $gen_length)));

    if (!checkTempId($gen_text) || $gen_text.length !== $gen_length) {
        $gen_text = genid($gen_length, true);
    }
    if (!$reNew) {
        id_used.push($gen_text);
    }
    return $gen_text;
}

function checkTempId(id_new) {
    return id_used.indexOf(id_new) === -1 ? true : false;
}