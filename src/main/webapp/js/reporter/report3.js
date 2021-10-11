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

    $('#date_begin').val(today);
    $('#date_end').val(today);
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

        var search_item = $('#search_item').val();
        var date_begin = $('#date_begin').val();
        var date_end = $('#date_end').val();
        var item_branch = $('#item_branch').select2('val');
        var group_mail = $('#group_mail').select2('val');
        var group_sub = $('#group_sub').select2('val');
        var group_sub2 = $('#group_sub2').select2('val');
        var item_brand = $('#item_brand').select2("val");
        var item_pattern = $('#item_pattern').select2("val");
        var search_category = $('#search_category').select2("val");
        var ar_code = $('#ar_code').select2("val");
        var emp_code = $('#emp_code').select2("val");
        var department_code = $('#department_codex').select2("val");

        var departmentcode = '';
        for (var i = 0; i < department_code.length; i++) {
            if (i == 0) {
                departmentcode += "'" + department_code[i] + "'"
            } else {
                departmentcode += ",'" + department_code[i] + "'"
            }
        }

        var category = '';
        for (var i = 0; i < search_category.length; i++) {
            if (i == 0) {
                category += "'" + search_category[i] + "'"
            } else {
                category += ",'" + search_category[i] + "'"
            }
        }
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
            url: "../getReporter3",
            data: {category: category, department_code: departmentcode, search_item: search_item, date_end: date_end, date_begin: date_begin, empcode: empcode, arcode: arcode, group_mail: group_mail, group_sub: group_sub, group_sub2: group_sub2, branchcode: branchcode, itembrand: itembrand, itempattern: itempattern},
            error: function (responseText) {
                console.log(responseText)
                $('#btn-process').removeAttr('disabled')
                $('#btn-process').text('ประมวลผล');
                $('#modal').modal('hide');
                setTimeout(function () {
                    alert('ดึงข้อมูลล้มเหลว');
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
                                    <th>สาขา</th>
                                    <th>พนักงาน</th>
                                    <th>วันที่</th>
                                    <th>เลขที่</th>
                                    <th>ลูกค้า</th>
                                    <th>สินค้า</th>
                                    <th>คลัง</th>
                                    <th>หน่วย</th>
                                    <th>จำนวน</th>
                                    <th>มูลค่าขาย</th>
                                    <th>ต้นทุน</th>
                                    <th>กำไร</th>
                                    <th>GP</th>
                                </tr>`;
    for (var i = 0; i < data.length; i++) {
        html += `<tr>
                                    <td nowrap class='text-left'>${data[i].branch_code}</td>
                                    <td nowrap class='text-left'>${data[i].sale_name}</td>
                                    <td nowrap  class='text-right'>${data[i].doc_date}</td>
                                    <td nowrap>${data[i].doc_no}</td>
                                    <td nowrap class='text-left'>${data[i].cust_name}</td>
                                    <td nowrap class='text-left'>${data[i].item_name}</td>
                                    <td nowrap class='text-left'>${data[i].wh_shelf}</td>
                                    <td class='text-center'>${data[i].unit_name}</td>
                                    <td class='text-right'>${data[i].qty_t}</td>
                                    <td class='text-right'>${data[i].sum_amount_exclude_vat_t}</td>
                                    <td class='text-right'>${data[i].sum_of_cost_1_t}</td>
                                    <td class='text-right'>${data[i].gp}</td>
                                    <td class='text-right'>${data[i].gpx}</td>
                   
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
    getBranch();
    getCategorylist();
    getPatternList();
    getGroupmail();
    getBrand();
    getDesign();
    getEmp();
    getCust();
    getDepart();
}



function getDepart() {
    $.get("../getDepart", function (responseText) {
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
            $(".selectdepartx").html(dataz);
            $(".selectdepartx").select2({theme: 'bootstrap'});
        }
    });
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
    $('#modal').modal('show');
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
        setTimeout(function () {
            $('#modal').modal('hide');
        }, 3000)

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
