$(document).ready(function () {

    getFilter()
    var now = new Date();
    var month = (now.getMonth() + 1);
    var day = now.getDate();
    if (month < 10)
        month = "0" + month;
    if (day < 10)
        day = "0" + day;
    var today = now.getFullYear() + '-' + month + '-' + day;
    $('#balance_date').val(today);
    $('#sale_date_begin').val(today);
    $('#sale_date_end').val(today);
    $('#group_mail').on('change', function () {

        getGroupsub();
    });

    $('#group_sub').on('change', function () {

        getGroupsub2();
    });

    $('#wh_code').on('change', function () {

        getShelfCode();
    });

    $('#btn-copy').on('click', function () {

        let el = document.getElementById("tableId");
        let body = document.body;
        let range;
        let sel;
        if (document.createRange && window.getSelection) {
            range = document.createRange();
            sel = window.getSelection();
            sel.removeAllRanges();
            try {
                range.selectNodeContents(el);
                sel.addRange(range);
            } catch (e) {
                range.selectNode(el);
                sel.addRange(range);
            }
        }
        document.execCommand("Copy");
        alert("Copy Table to Clipboard");

    });


    $('#btn-process').on('click', function () {
        console.log('112348')
        var search_item = $('#search_item').val();
        var balance_date = $('#balance_date').val();
        var sale_date_begin = $('#sale_date_begin').val();
        var sale_date_end = $('#sale_date_end').val();
        var doc_po = $('#doc_po').val();
        var item_branch = $('#item_branch').select2('val');
        var wh_code = $('#wh_code').select2('val');
        var group_mail = $('#group_mail').select2('val');
        var group_sub = $('#group_sub').select2('val');
        var group_sub2 = $('#group_sub2').select2('val');
        var item_brand = $('#item_brand').select2("val");
        var item_pattern = $('#item_pattern').select2("val");
        var ar_code = $('#ar_code').select2("val");
        var emp_code = $('#emp_code').select2("val");
        var send_time = $('#send_time').val();
        var percent = $('#percent').val();
        var stock_age = $('#stock_age').val();



        var arcode = '';
        for (var i = 0; i < ar_code.length; i++) {
            if (i == 0) {
                arcode += "'" + ar_code[i] + "'"
            } else {
                arcode += ",'" + ar_code[i] + "'"
            }
        }
        var empcode = '';
        for (var i = 0; i < emp_code.length; i++) {
            if (i == 0) {
                empcode += "'" + emp_code[i] + "'"
            } else {
                empcode += ",'" + emp_code[i] + "'"
            }
        }
        var branchcode = '';
        for (var i = 0; i < item_branch.length; i++) {
            if (i == 0) {
                branchcode += "'" + item_branch[i] + "'"
            } else {
                branchcode += ",'" + item_branch[i] + "'"
            }
        }
        var whcode = '';
        for (var i = 0; i < wh_code.length; i++) {
            if (i == 0) {
                whcode += "'" + wh_code[i] + "'"
            } else {
                whcode += ",'" + wh_code[i] + "'"
            }
        }
        var itembrand = '';
        for (var i = 0; i < item_brand.length; i++) {
            if (i == 0) {
                itembrand += "'" + item_brand[i] + "'"
            } else {
                itembrand += ",'" + item_brand[i] + "'"
            }
        }
        var itempattern = '';
        for (var i = 0; i < item_pattern.length; i++) {
            if (i == 0) {
                itempattern += "'" + item_pattern[i] + "'"
            } else {
                itempattern += ",'" + item_pattern[i] + "'"
            }
        }



        $('#btn-process').text('กำลังประมวลผล...');
        $('#btn-process').attr('disabled', 'true');
        $('#modal').modal('show');
        $.ajax({
            method: 'GET',
            url: "../getReporter2",
            data: {search_item: search_item, send_time: send_time, percent: percent, stock_age: stock_age, sale_date_end: sale_date_end, sale_date_begin: sale_date_begin, empcode: empcode, arcode: arcode, balance_date: balance_date, doc_po: doc_po, group_mail: group_mail, group_sub: group_sub, group_sub2: group_sub2, branchcode: branchcode, whcode: whcode, itembrand: itembrand, itempattern: itempattern},
            error: function (responseText) {
                console.log(responseText)
                $('#btn-process').removeAttr('disabled')
                $('#btn-process').text('ประมวลผล');


                setTimeout(function () {
                    alert('ดึงข้อมูลล้มเหลว');
                    $('#modal').modal('hide');
                }, 2000);
            },
            success: function (responseText) {
                $('#btn-process').text('ประมวลผล');
                $('#btn-process').removeAttr('disabled')
                console.log(responseText)
                setTimeout(function () {
                    $('#modal').modal('hide');
                }, 2000)
                displayTable(responseText);

            }
        });
    });

});

function displayTable(data) {
    var html = "";
    if ($.fn.DataTable.isDataTable('#tableId')) {
        $('#tableId').DataTable().destroy();
    }
    $('#tableId tbody').empty();

    var header = `<tr style='background-color:#68FFF4'>
                                    <th>BarCode</th>
                                    <th>Description</th>
                                    <th>อายุสต๊อก</th>
                                    <th>ขนส่ง(วัน)</th>
                                    <th>SalesQTY</th>
                                    <th>Balance</th>
                                    <th>Unit</th>
                                    <th>SellingDay</th>
                                    <th>SD</th>
                                    <th>StockAging</th>
                                    <th>LeadTime</th>
                                    <th>Growth (%)</th>
                                    <th>RequireQTY</th>
                                    <th>WaitReceive</th>
                                    <th>EstOrder</th>
                                    <th>OrderQty</th>
                                    <th>Min</th>
                                    <th>Max</th>
                                    <th>RemarkPurchase</th>
                                    <th>PR</th>
                                    <th>SalesPrice</th>
                                </tr>`;
    for (var i = 0; i < data.length; i++) {
        html += `<tr>
                                    <td  class='text-left'>${data[i].barcode}</td>
                                    <td nowrap class='text-left'>${data[i].item_name}</td>
                                    <td class='text-right'>${data[i].age_stock}</td>
                                    <td>${data[i].time_stock}</td>
                                    <td class='text-right'>${data[i].qty}</td>
                                    <td class='text-right'>${data[i].balance_qty}</td>
                                    <td>${data[i].unit_code}</td>
                                    <td class='text-right'>${data[i].sellingday}</td>
                                    <td class='text-right'>${data[i].sd}</td>
                                    <td class='text-right'>${data[i].stockaging}</td>
                                    <td class='text-right'>${data[i].leadtime}</td>
                                    <td class='text-right'>${data[i].growth}</td>
                                    <td class='text-right'>${data[i].requireqty}</td>
                                    <td class='text-right'>${data[i].waitreceive}</td>
                                    <td class='text-right'>${data[i].estorder}</td>
                                    <td class='text-right'>${data[i].orderqty}</td>
                                    <td class='text-right'>${data[i].min}</td>
                                    <td class='text-right'>${data[i].max}</td>
                                    <td class='text-right'>${data[i].remarkpurchase}</td>
                                    <td class='text-right'>${data[i].pr}</td>
                                    <td class='text-right'>${data[i].salesprice}</td>
                                </tr>`
    }
    $('#th_header').html(header);
    $('#tbody_detail').html(html);

    setTimeout(function () {

        $('#tableId').DataTable({
            dom: 'Bfrtip',
            buttons: [
                'copy', 'excel'
            ],
            "lengthMenu": [[20, 100 - 1], [50, 100, "All"]],
        });
    }, 100);


}

function getFilter() {
    $('#modal').modal('show');
    getBranch();
    getWarehouse();
    getCategorylist();
    getPatternList();
    getGroupmail();
    getBrand();
    getDesign();
    getEmp();
    getCust();
    setTimeout(function () {
        $('#modal').modal('hide');
        $('#modal').modal('hide');
        $('#modal').modal('hide');
    }, 7000)
}



function getGroupsub2() {
    var code = $('#group_sub').select2('val');
    console.log(code)
    $.get("../getGroupsub2", {code: code}, function (responseText) {
        //console.log(responseText);
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            data += '<option value="">ไม่เลือก</option>';
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }

            $(".selectgroupsub2").html(data);
            $(".selectgroupsub2").select2({theme: 'bootstrap'});
            if (responseText.length > 0) {
                $('.selectgroupsub2').attr("disabled", false);
            } else {
                $('.selectgroupsub2').attr("disabled", true);
            }

        }
    });
}
function getWarehouse() {
    $.get("../getWarehouse", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            //console.log(responseText);
            var dataz = "";
            dataz += '<option value="">ไม่เลือก</option>';
            for (var i = 0; i < responseText.length; i++) {
                dataz += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectwhcode").html(dataz);
            $(".selectwhcode").select2({theme: 'bootstrap'});
            setTimeout(function () {
                getShelfCode()
            }, 1000);
        }
    });
}

function getShelfCode() {
    var code = $('#wh_code').select2('val');
    console.log("code " + code)
    $.get("../getShelf", {code: code}, function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            data += '<option value="">ไม่เลือก</option>';
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectshelfcode").html(data);
            $(".selectshelfcode").select2({theme: 'bootstrap'});
            if (responseText.length > 0) {
                $('.selectshelfcode').attr("disabled", false);
            } else {
                $('.selectshelfcode').attr("disabled", true);
            }
        }
    });
}

function getEmp() {
    $.get("../getEmp", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            //console.log(responseText);
            var dataz = "";
            for (var i = 0; i < responseText.length; i++) {
                dataz += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectemp").html(dataz);
            $(".selectemp").select2({theme: 'bootstrap'});
        }
    });
}
function getCust() {

    $.get("../getCust", function (responseText) {

        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            //console.log(responseText);
            var dataz = "";
            for (var i = 0; i < responseText.length; i++) {
                dataz += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectcust").html(dataz);
            $(".selectcust").select2({theme: 'bootstrap'});
        }


    });

}
function getBrand() {
    $.get("../getBrand", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            //console.log(responseText);
            var dataz = "";
            for (var i = 0; i < responseText.length; i++) {
                dataz += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectitembrand").html(dataz);
            $(".selectitembrand").select2({theme: 'bootstrap'});
        }
    });
}

function getGroupsub() {
    var code = $('#group_mail').select2('val');
    console.log("code " + code)
    $.get("../getGroupsub", {code: code}, function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            data += '<option value="">ไม่เลือก</option>';
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectgroupsub").html(data);
            $(".selectgroupsub").select2({theme: 'bootstrap'});
            $(".selectgroupsub2").select2({theme: 'bootstrap'});
            if (responseText.length > 0) {
                $('.selectgroupsub').attr("disabled", false);
            } else {
                $('.selectgroupsub').attr("disabled", true);
            }


        }
    });
}

function getGroupmail() {
    $.get("../getGroupmain", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            console.log(responseText);
            var main = "";
            main += '<option value="">ไม่เลือก</option>';
            for (var i = 0; i < responseText.length; i++) {
                main += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectgroupmail").html(main);
            $(".selectgroupmail").select2({theme: 'bootstrap'});


            setTimeout(function () {
                getGroupsub()
            }, 1000);

        }
    });
}

function getPatternList() {
    $.get("../getPatternList", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectitempattern").html(data);
            $(".selectitempattern").select2({theme: 'bootstrap'});
        }
    });
}

function getBranch() {
    console.log('getbranch')
    $.get("../getbranchlist", function (responseText) {
        console.log(responseText)
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + '~' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectbranch").html(data);
            $(".selectbranch").select2({theme: 'bootstrap'});
        }
    });
}

function getCategorylist() {
    $.get("../getCategorylist", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectcategory").html(data);
            $(".selectcategory").select2({theme: 'bootstrap'});
        }
    });
}

function getClass() {
    $.get("../getClass", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectclass").html(data);
            $(".selectclass").select2({theme: 'bootstrap'});
        }
    });
}

function getGrade() {
    $.get("../getGrade", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectgrade").html(data);
            $(".selectgrade").select2({theme: 'bootstrap'});
        }
    });
}

function getDesign() {
    $.get("../getDesign", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectdesign").html(data);
            $(".selectdesign").select2({theme: 'bootstrap'});
        }
    });
}

function getModel() {
    $.get("../getModel", function (responseText) {
        if (responseText === "fail")
        {
            console.log('ล้มเหลว');
        } else if (responseText === "noitem") {
            alert('ไม่พบข้อมูล');
        } else {
            // console.log(responseText);
            var data = "";
            for (var i = 0; i < responseText.length; i++) {
                data += '<option value="' + responseText[i].code + '">' + responseText[i].code + ':' + responseText[i].name_1 + '</option>';
            }
            //console.log(data);
            $(".selectmodel").html(data);
            $(".selectmodel").select2({theme: 'bootstrap'});
        }
    });
}
