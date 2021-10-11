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
        var date_begin = $('#date_begin').val();
        var wh_code = $('#wh_code').select2('val');

        var group_mail = $('#group_mail').select2('val');
        var group_sub = $('#group_sub').select2('val');
        var group_sub2 = $('#group_sub2').select2('val');
        var search_category = $('#search_category').select2("val");
        var item_class = $('#item_class').select2("val");
        var item_brand = $('#item_brand').select2("val");
        var item_grade = $('#item_grade').select2("val");
        var item_pattern = $('#item_pattern').select2("val");
        var item_branch = $('#item_branch').select2("val");
        var item_design = $('#item_design').select2("val");
        var item_model = $('#item_model').select2("val");

        var balance_type = $('#balance_type').val();

        var whcode = '';
        for (var i = 0; i < wh_code.length; i++) {
            if (i == 0) {
                whcode += "'" + wh_code[i] + "'"
            } else {
                whcode += ",'" + wh_code[i] + "'"
            }
        }

        var itembranch = '';
        for (var i = 0; i < item_branch.length; i++) {
            if (i == 0) {
                itembranch += "'" + item_branch[i] + "'"
            } else {
                itembranch += ",'" + item_branch[i] + "'"
            }
        }

        var itemmodel = '';
        for (var i = 0; i < item_model.length; i++) {
            if (i == 0) {
                itemmodel += "'" + item_model[i] + "'"
            } else {
                itemmodel += ",'" + item_model[i] + "'"
            }
        }

        var itemdesign = '';
        for (var i = 0; i < item_design.length; i++) {
            if (i == 0) {
                itemdesign += "'" + item_design[i] + "'"
            } else {
                itemdesign += ",'" + item_design[i] + "'"
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
        var itembrand = '';

        for (var i = 0; i < item_brand.length; i++) {
            if (i == 0) {
                itembrand += "'" + item_brand[i] + "'"
            } else {
                itembrand += ",'" + item_brand[i] + "'"
            }
        }

        var itemgrade = '';
        for (var i = 0; i < item_grade.length; i++) {
            if (i == 0) {
                itemgrade += "'" + item_grade[i] + "'"
            } else {
                itemgrade += ",'" + item_grade[i] + "'"
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

        var itemclass = '';
        for (var i = 0; i < item_class.length; i++) {
            if (i == 0) {
                itemclass += "'" + item_class[i] + "'"
            } else {
                itemclass += ",'" + item_class[i] + "'"
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



        $('#btn-process').text('กำลังประมวลผล...');
        $('#btn-process').attr('disabled', 'true');
        $('#modal').modal('show');
        $.ajax({
            method: 'GET',
            url: "../getReporter1",
            data: {group_sub2: group_sub2, balance_type: balance_type, search: search_item, date_begin: date_begin, wh_code: whcode, shelf_code: '', group_mail: group_mail, group_sub: group_sub, itembrand: itembrand, itempattern: itempattern, itembranch: itembranch, itemmodel: itemmodel, itemdesign: itemdesign, itemgrade: itemgrade, itemclass: itemclass, category: category},
            error: function (responseText) {
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

                displayTable(responseText);
                $('#modal').modal('hide');
            }
        });
    });

});

function displayTable(data) {
    var code = $('#wh_code').select2('val');

    if ($.fn.DataTable.isDataTable('#tableId')) {
        $('#tableId').DataTable().destroy();
    }
    $('#tableId tbody').empty();
    console.log(code)
    var table_head = ``;
    var table_body = ``

    for (var i = 0; i < data.length; i++) {
        table_body += `<tr>`;
        table_body += `<td class="text-left" nowrap>${data[i].item_code}</td>`;
        table_body += `<td class="text-left" nowrap>${data[i].item_name}</td>`;
        table_body += `<td>${data[i].unit_code}</td>`;
        if (code.length > 0) {
            for (var x = 0; x < code.length; x++) {
                if (code[x] == 'RCPS10') {
                    table_head += ` <th>ตู้หยอดเหรียญ</th>`;
                    table_body += `<td class='text-right'>${data[i].rcps10}</td>`;
                }
                if (code[x] == 'RCPS2') {
                    table_head += ` <th>Shop S2</th>`;

                    table_body += `<td class='text-right'>${data[i].rcps2}</td>`;
                }
                if (code[x] == 'RCPS3') {
                    table_head += ` <th>ทุกอย่าง20</th>`;

                    table_body += `<td class='text-right'>${data[i].rcps3}</td>`;
                }
                if (code[x] == 'RCPS4') {
                    table_head += ` <th>Shop S4</th>`;

                    table_body += `<td class='text-right'>${data[i].rcps4}</td>`;
                }
                if (code[x] == 'RCPS5') {

                    table_head += ` <th>Shop S5</th>`;

                    table_body += `<td class='text-right'>${data[i].rcps5}</td>`;
                }
                if (code[x] == 'RCPS8') {

                    table_head += ` <th>Shop S8</th>`;

                    table_body += `<td class='text-right'>${data[i].rcps8}</td>`;
                }
                if (code[x] == 'RCPS9') {

                    table_head += ` <th>Shop S9</th>`;

                    table_body += `<td class='text-right'>${data[i].rcps9}</td>`;
                }
                if (code[x] == 'RCPS91') {

                    table_head += ` <th>Shop S10</th>`;

                    table_body += `<td class='text-right'>${data[i].rcps91}</td>`;
                }
                if (code[x] == 'TEMP') {

                    table_head += ` <th>พัก ราชา GR</th>`;

                    table_body += `<td class='text-right'>${data[i].temp}</td>`;
                }
                if (code[x] == 'WANG') {

                    table_head += ` <th>อำนาจเจริญ</th>`;

                    table_body += `<td class='text-right'>${data[i].wang}</td>`;
                }
                if (code[x] == 'WDKT') {

                    table_head += ` <th>Clearance</th>`;

                    table_body += `<td class='text-right'>${data[i].wdkt}</td>`;
                }
                if (code[x] == 'WDUD') {

                    table_head += ` <th>เดชอุดม</th>`;

                    table_body += `<td class='text-right'>${data[i].wdud}</td>`;
                }
                if (code[x] == 'WH01') {

                    table_head += ` <th>สำนักงานใหญ่PPS</th>`;

                    table_body += `<td class='text-right'>${data[i].wh01}</td>`;
                }
                if (code[x] == 'WH11') {

                    table_head += ` <th>ผลิตPPS</th>`;

                    table_body += `<td class='text-right'>${data[i].wh11}</td>`;
                }
                if (code[x] == 'WH12') {

                    table_head += ` <th>รอซ่อมPPS</th>`;

                    table_body += `<td class='text-right'>${data[i].wh12}</td>`;
                }
                if (code[x] == 'WH14') {

                    table_head += ` <th>สินค้าชำรุดPPS</th>`;

                    table_body += `<td class='text-right'>${data[i].wh14}</td>`;
                }
                if (code[x] == 'WH15') {

                    table_head += ` <th>เคลมสินค้าPPS</th>`;

                    table_body += `<td class='text-right'>${data[i].wh15}</td>`;
                }
                if (code[x] == 'WHLS') {

                    table_head += ` <th>หลังสวน</th>`;

                    table_body += `<td class='text-right'>${data[i].whls}</td>`;
                }
                if (code[x] == 'WHNP') {

                    table_head += ` <th>หนองพอก</th>`;

                    table_body += `<td class='text-right'>${data[i].whnp}</td>`;
                }
                if (code[x] == 'WJON') {

                    table_head += ` <th>แม่ขะจาน</th>`;

                    table_body += `<td class='text-right'>${data[i].wjon}</td>`;
                }
                if (code[x] == 'WKLS') {

                    table_head += ` <th>เอ็มจี กู๊ด</th>`;

                    table_body += `<td class='text-right'>${data[i].wkls}</td>`;
                }
                if (code[x] == 'WKNR') {

                    table_head += ` <th>กุฉินารายณ์</th>`;

                    table_body += `<td class='text-right'>${data[i].wknr}</td>`;
                }
                if (code[x] == 'WKSM') {

                    table_head += ` <th>เขาสมิง</th>`;

                    table_body += `<td class='text-right'>${data[i].wksm}</td>`;
                }
                if (code[x] == 'WKTL') {

                    table_head += ` <th>กันทรลักษ์</th>`;

                    table_body += `<td class='text-right'>${data[i].wktl}</td>`;
                }
                if (code[x] == 'WNKP') {

                    table_head += ` <th>นครพนม</th>`;

                    table_body += `<td class='text-right'>${data[i].wnkp}</td>`;
                }
                if (code[x] == 'WPHK') {

                    table_head += ` <th>Shop S6</th>`;

                    table_body += `<td class='text-right'>${data[i].wphk}</td>`;
                }
                if (code[x] == 'WPMH') {

                    table_head += ` <th>พิบูลมังสาหาร</th>`;

                    table_body += `<td class='text-right'>${data[i].wpmh}</td>`;
                }
                if (code[x] == 'WPPLK') {

                    table_head += ` <th>พิษณุโลก</th>`;

                    table_body += `<td class='text-right'>${data[i].wpplk}</td>`;
                }
                if (code[x] == 'WPSL') {

                    table_head += ` <th>นครราชสีมา</th>`;

                    table_body += `<td class='text-right'>${data[i].wpsl}</td>`;
                }
                if (code[x] == 'WRCP1') {

                    table_head += ` <th>Shop S1</th>`;

                    table_body += `<td class='text-right'>${data[i].wrcp1}</td>`;
                }
                if (code[x] == 'WRCP2') {

                    table_head += ` <th>ออนไลน์</th>`;

                    table_body += `<td class='text-right'>${data[i].wrcp2}</td>`;
                }
                if (code[x] == 'WRSS') {
                    table_head += ` <th>ราษีไศล</th>`;

                    table_body += `<td class='text-right'>${data[i].wrss}</td>`;
                }
                if (code[x] == 'WSAE') {

                    table_head += ` <th>ท่าแซะ</th>`;

                    table_body += `<td class='text-right'>${data[i].wsae}</td>`;
                }
                if (code[x] == 'WSBK') {

                    table_head += ` <th>งานเกษตร</th>`;

                    table_body += `<td class='text-right'>${data[i].wsbk}</td>`;
                }
                if (code[x] == 'WSBP1') {

                    table_head += ` <th>ศูนย์ซ่อมShop</th>`;

                    table_body += `<td class='text-right'>${data[i].wsbp1}</td>`;
                }
                if (code[x] == 'WSCP1') {

                    table_head += ` <th>พยัคฆภูมิพิสัย</th>`;

                    table_body += `<td class='text-right'>${data[i].wscp1}</td>`;
                }
                if (code[x] == 'WSDD') {

                    table_head += ` <th>สว่างแดนดิน</th>`;

                    table_body += `<td class='text-right'>${data[i].wsdd}</td>`;
                }
                if (code[x] == 'WSHK') {

                    table_head += ` <th>เซกา</th>`;

                    table_body += `<td class='text-right'>${data[i].wshk}</td>`;
                }
                if (code[x] == 'WSKL1') {

                    table_head += ` <th>เลย</th>`;

                    table_body += `<td class='text-right'>${data[i].wskl1}</td>`;
                }
                if (code[x] == 'WSKN') {

                    table_head += ` <th>กระนวน</th>`;

                    table_body += `<td class='text-right'>${data[i].wskn}</td>`;
                }
                if (code[x] == 'WSKS') {

                    table_head += ` <th>ศรีสะเกษ</th>`;

                    table_body += `<td class='text-right'>${data[i].wsks}</td>`;
                }
                if (code[x] == 'WSLP') {

                    table_head += ` <th>GRราชา</th>`;

                    table_body += `<td class='text-right'>${data[i].wslp}</td>`;
                }
                if (code[x] == 'WSNS1') {
                    table_head += ` <th>ขายน้ำโสม</th>`;

                    table_body += `<td class='text-right'>${data[i].wsns1}</td>`;
                }
                if (code[x] == 'WSNY') {
                    table_head += ` <th>น้ำยืน</th>`;

                    table_body += `<td class='text-right'>${data[i].wsny}</td>`;
                }
                if (code[x] == 'WSPK') {
                    table_head += ` <th>หนองบัวระเหว</th>`;

                    table_body += `<td class='text-right'>${data[i].wspk}</td>`;
                }
                if (code[x] == 'WSPP1') {
                    table_head += ` <th>ชุมแพ</th>`;

                    table_body += `<td class='text-right'>${data[i].wspp1}</td>`;
                }
                if (code[x] == 'WSPT') {
                    table_head += ` <th>ประทาย</th>`;

                    table_body += `<td class='text-right'>${data[i].wspt}</td>`;
                }
                if (code[x] == 'WSRI') {
                    table_head += ` <th>สุรินทร์</th>`;

                    table_body += `<td class='text-right'>${data[i].wsri}</td>`;
                }
                if (code[x] == 'WSRN') {
                    table_head += ` <th>วารินชำราบ</th>`;

                    table_body += `<td class='text-right'>${data[i].wsrn}</td>`;
                }
                if (code[x] == 'WSRS') {
                    table_head += ` <th>หล่มสัก</th>`;

                    table_body += `<td class='text-right'>${data[i].wsrs}</td>`;
                }
                if (code[x] == 'WSSK') {
                    table_head += ` <th>ศรีสงคราม</th>`;

                    table_body += `<td class='text-right'>${data[i].wssk}</td>`;
                }
                if (code[x] == 'WSSN') {
                    table_head += ` <th>สกลนคร</th>`;

                    table_body += `<td class='text-right'>${data[i].wssn}</td>`;
                }
                if (code[x] == 'WSTP') {
                    table_head += ` <th>ตระการพืชผล</th>`;

                    table_body += `<td class='text-right'>${data[i].wstp}</td>`;
                }
                if (code[x] == 'WSWC') {
                    table_head += ` <th>เลิงนกทา</th>`;

                    table_body += `<td class='text-right'>${data[i].wswc}</td>`;
                }
                if (code[x] == 'WTAT') {
                    table_head += ` <th>ตราด</th>`;

                    table_body += `<td class='text-right'>${data[i].wtat}</td>`;
                }
                if (code[x] == 'WTPN') {
                    table_head += ` <th>ธาตุพนม</th>`;

                    table_body += `<td class='text-right'>${data[i].wtpn}</td>`;
                }
                if (code[x] == 'WVSP') {
                    table_head += ` <th>Shop S10</th>`;

                    table_body += `<td class='text-right'>${data[i].wvsp}</td>`;
                }
                if (code[x] == 'WWNY') {
                    table_head += ` <th>สุวรรณภูมิ</th>`;

                    table_body += `<td class='text-right'>${data[i].wwny}</td>`;
                }
                if (code[x] == 'WWSM') {
                    table_head += ` <th>วังสามหมอ</th>`;

                    table_body += `<td class='text-right'>${data[i].wwsm}</td>`;
                }
                if (code[x] == 'WYST') {
                    table_head += ` <th>ยโสธร</th>`;

                    table_body += `<td class='text-right'>${data[i].wyst}</td>`;
                }
            }

        } else {


            table_body += `<td class='text-right'>${data[i].rcps10}</td>`;
            table_body += `<td class='text-right'>${data[i].rcps2}</td>`;
            table_body += `<td class='text-right'>${data[i].rcps3}</td>`;
            table_body += `<td class='text-right'>${data[i].rcps4}</td>`;
            table_body += `<td class='text-right'>${data[i].rcps5}</td>`;
            table_body += `<td class='text-right'>${data[i].rcps8}</td>`;
            table_body += `<td class='text-right'>${data[i].rcps9}</td>`;
            table_body += `<td class='text-right'>${data[i].rcps91}</td>`;
            table_body += `<td class='text-right'>${data[i].temp}</td>`;
            table_body += `<td class='text-right'>${data[i].wang}</td>`;
            table_body += `<td class='text-right'>${data[i].wdkt}</td>`;
            table_body += `<td class='text-right'>${data[i].wdud}</td>`;
            table_body += `<td class='text-right'>${data[i].wh01}</td>`;
            table_body += `<td class='text-right'>${data[i].wh11}</td>`;
            table_body += `<td class='text-right'>${data[i].wh12}</td>`;
            table_body += `<td class='text-right'>${data[i].wh14}</td>`;
            table_body += `<td class='text-right'>${data[i].wh15}</td>`;
            table_body += `<td class='text-right'>${data[i].whls}</td>`;
            table_body += `<td class='text-right'>${data[i].whnp}</td>`;
            table_body += `<td class='text-right'>${data[i].wjon}</td>`;
            table_body += `<td class='text-right'>${data[i].wkls}</td>`;
            table_body += `<td class='text-right'>${data[i].wknr}</td>`;
            table_body += `<td class='text-right'>${data[i].wksm}</td>`;
            table_body += `<td class='text-right'>${data[i].wktl}</td>`;
            table_body += `<td class='text-right'>${data[i].wnkp}</td>`;
            table_body += `<td class='text-right'>${data[i].wphk}</td>`;
            table_body += `<td class='text-right'>${data[i].wpmh}</td>`;
            table_body += `<td class='text-right'>${data[i].wpplk}</td>`;
            table_body += `<td class='text-right'>${data[i].wpsl}</td>`;
            table_body += `<td class='text-right'>${data[i].wrcp1}</td>`;
            table_body += `<td class='text-right'>${data[i].wrcp2}</td>`;
            table_body += `<td class='text-right'>${data[i].wrss}</td>`;
            table_body += `<td class='text-right'>${data[i].wsae}</td>`;
            table_body += `<td class='text-right'>${data[i].wsbk}</td>`;
            table_body += `<td class='text-right'>${data[i].wsbp1}</td>`;
            table_body += `<td class='text-right'>${data[i].wscp1}</td>`;
            table_body += `<td class='text-right'>${data[i].wsdd}</td>`;
            table_body += `<td class='text-right'>${data[i].wshk}</td>`;
            table_body += `<td class='text-right'>${data[i].wskl1}</td>`;
            table_body += `<td class='text-right'>${data[i].wskn}</td>`;
            table_body += `<td class='text-right'>${data[i].wsks}</td>`;
            table_body += `<td class='text-right'>${data[i].wslp}</td>`;
            table_body += `<td class='text-right'>${data[i].wsns1}</td>`;
            table_body += `<td class='text-right'>${data[i].wsny}</td>`;
            table_body += `<td class='text-right'>${data[i].wspk}</td>`;
            table_body += `<td class='text-right'>${data[i].wspp1}</td>`;
            table_body += `<td class='text-right'>${data[i].wspt}</td>`;
            table_body += `<td class='text-right'>${data[i].wsri}</td>`;
            table_body += `<td class='text-right'>${data[i].wsrn}</td>`;
            table_body += `<td class='text-right'>${data[i].wsrs}</td>`;
            table_body += `<td class='text-right'>${data[i].wssk}</td>`;
            table_body += `<td class='text-right'>${data[i].wssn}</td>`;
            table_body += `<td class='text-right'>${data[i].wstp}</td>`;
            table_body += `<td class='text-right'>${data[i].wswc}</td>`;
            table_body += `<td class='text-right'>${data[i].wtat}</td>`;
            table_body += `<td class='text-right'>${data[i].wtpn}</td>`;
            table_body += `<td class='text-right'>${data[i].wvsp}</td>`;
            table_body += `<td class='text-right'>${data[i].wwny}</td>`;
            table_body += `<td class='text-right'>${data[i].wwsm}</td>`;
            table_body += `<td class='text-right'>${data[i].wyst}</td>`;
        }
        table_body += `<td class='text-right'>${data[i].sum_qty}</td>`;
        table_body += `<td class='text-right'>${data[i].price}</td>`;
        table_body += `<td class='text-right'>${data[i].sum_amount_price}</td>`;
        table_body += `</tr>`
    }

    table_head = '<>';
    table_head = `<tr style='background-color:#68FFF4'><th>รหัสสินค้า</th>
                            <th>ชื่อสินค้า</th>
                            <th>หน่วยนับ</th> `
    if (code.length > 0) {
        for (var i = 0; i < code.length; i++) {
            if (code[i] == 'RCPS10') {
                table_head += ` <th>ตู้หยอดเหรียญ</th>`;

            }
            if (code[i] == 'RCPS2') {
                table_head += ` <th>Shop S2</th>`;


            }
            if (code[i] == 'RCPS3') {
                table_head += ` <th>ทุกอย่าง20</th>`;


            }
            if (code[i] == 'RCPS4') {
                table_head += ` <th>Shop S4</th>`;

            }
            if (code[i] == 'RCPS5') {

                table_head += ` <th>Shop S5</th>`;

            }
            if (code[i] == 'RCPS8') {

                table_head += ` <th>Shop S8</th>`;


            }
            if (code[i] == 'RCPS9') {

                table_head += ` <th>Shop S9</th>`;


            }
            if (code[i] == 'RCPS91') {

                table_head += ` <th>Shop S10</th>`;


            }
            if (code[i] == 'TEMP') {

                table_head += ` <th>พัก ราชา GR</th>`;


            }
            if (code[i] == 'WANG') {

                table_head += ` <th>อำนาจเจริญ</th>`;


            }
            if (code[i] == 'WDKT') {

                table_head += ` <th>Clearance</th>`;


            }
            if (code[i] == 'WDUD') {

                table_head += ` <th>เดชอุดม</th>`;

            }
            if (code[i] == 'WH01') {

                table_head += ` <th>สำนักงานใหญ่PPS</th>`;


            }
            if (code[i] == 'WH11') {

                table_head += ` <th>ผลิตPPS</th>`;


            }
            if (code[i] == 'WH12') {

                table_head += ` <th>รอซ่อมPPS</th>`;


            }
            if (code[i] == 'WH14') {

                table_head += ` <th>สินค้าชำรุดPPS</th>`;


            }
            if (code[i] == 'WH15') {

                table_head += ` <th>เคลมสินค้าPPS</th>`;

            }
            if (code[i] == 'WHLS') {

                table_head += ` <th>หลังสวน</th>`;

            }
            if (code[i] == 'WHNP') {

                table_head += ` <th>หนองพอก</th>`;

            }
            if (code[i] == 'WJON') {

                table_head += ` <th>แม่ขะจาน</th>`;

            }
            if (code[i] == 'WKLS') {

                table_head += ` <th>เอ็มจี กู๊ด</th>`;


            }
            if (code[i] == 'WKNR') {

                table_head += ` <th>กุฉินารายณ์</th>`;


            }
            if (code[i] == 'WKSM') {

                table_head += ` <th>เขาสมิง</th>`;

            }
            if (code[i] == 'WKTL') {

                table_head += ` <th>กันทรลักษ์</th>`;

            }
            if (code[i] == 'WNKP') {

                table_head += ` <th>นครพนม</th>`;

            }
            if (code[i] == 'WPHK') {

                table_head += ` <th>Shop S6</th>`;

            }
            if (code[i] == 'WPMH') {

                table_head += ` <th>พิบูลมังสาหาร</th>`;

            }
            if (code[i] == 'WPPLK') {

                table_head += ` <th>พิษณุโลก</th>`;

            }
            if (code[i] == 'WPSL') {

                table_head += ` <th>นครราชสีมา</th>`;


            }
            if (code[i] == 'WRCP1') {

                table_head += ` <th>Shop S1</th>`;


            }
            if (code[i] == 'WRCP2') {

                table_head += ` <th>ออนไลน์</th>`;

            }
            if (code[i] == 'WRSS') {
                table_head += ` <th>ราษีไศล</th>`;


            }
            if (code[i] == 'WSAE') {

                table_head += ` <th>ท่าแซะ</th>`;

            }
            if (code[i] == 'WSBK') {

                table_head += ` <th>งานเกษตร</th>`;

            }
            if (code[i] == 'WSBP1') {

                table_head += ` <th>ศูนย์ซ่อมShop</th>`;


            }
            if (code[i] == 'WSCP1') {

                table_head += ` <th>พยัคฆภูมิพิสัย</th>`;

            }
            if (code[i] == 'WSDD') {

                table_head += ` <th>สว่างแดนดิน</th>`;

            }
            if (code[i] == 'WSHK') {

                table_head += ` <th>เซกา</th>`;

            }
            if (code[i] == 'WSKL1') {

                table_head += ` <th>เลย</th>`;

            }
            if (code[i] == 'WSKN') {

                table_head += ` <th>กระนวน</th>`;


            }
            if (code[i] == 'WSKS') {

                table_head += ` <th>ศรีสะเกษ</th>`;


            }
            if (code[i] == 'WSLP') {

                table_head += ` <th>GRราชา</th>`;


            }
            if (code[i] == 'WSNS1') {
                table_head += ` <th>ขายน้ำโสม</th>`;


            }
            if (code[i] == 'WSNY') {
                table_head += ` <th>น้ำยืน</th>`;

            }
            if (code[i] == 'WSPK') {
                table_head += ` <th>หนองบัวระเหว</th>`;


            }
            if (code[i] == 'WSPP1') {
                table_head += ` <th>ชุมแพ</th>`;


            }
            if (code[i] == 'WSPT') {
                table_head += ` <th>ประทาย</th>`;


            }
            if (code[i] == 'WSRI') {
                table_head += ` <th>สุรินทร์</th>`;

            }
            if (code[i] == 'WSRN') {
                table_head += ` <th>วารินชำราบ</th>`;


            }
            if (code[i] == 'WSRS') {
                table_head += ` <th>หล่มสัก</th>`;


            }
            if (code[i] == 'WSSK') {
                table_head += ` <th>ศรีสงคราม</th>`;


            }
            if (code[i] == 'WSSN') {
                table_head += ` <th>สกลนคร</th>`;

            }
            if (code[i] == 'WSTP') {
                table_head += ` <th>ตระการพืชผล</th>`;

            }
            if (code[i] == 'WSWC') {
                table_head += ` <th>เลิงนกทา</th>`;
            }
            if (code[i] == 'WTAT') {
                table_head += ` <th>ตราด</th>`;
            }
            if (code[i] == 'WTPN') {
                table_head += ` <th>ธาตุพนม</th>`;
            }
            if (code[i] == 'WVSP') {
                table_head += ` <th>Shop S10</th>`;
            }
            if (code[i] == 'WWNY') {
                table_head += ` <th>สุวรรณภูมิ</th>`;
            }
            if (code[i] == 'WWSM') {
                table_head += ` <th>วังสามหมอ</th>`;
            }
            if (code[i] == 'WYST') {
                table_head += ` <th>ยโสธร</th>`;
            }
        }

    } else {
        table_head += ` <th>ตู้หยอดเหรียญ</th>`;
        table_head += ` <th>Shop S2</th>`;
        table_head += ` <th>ทุกอย่าง20</th>`;
        table_head += ` <th>Shop S4</th>`;
        table_head += ` <th>Shop S5</th>`;
        table_head += ` <th>Shop S8</th>`;
        table_head += ` <th>Shop S9</th>`;
        table_head += ` <th>Shop S10</th>`;
        table_head += ` <th>พัก ราชา GR</th>`;
        table_head += ` <th>อำนาจเจริญ</th>`;
        table_head += ` <th>Clearance</th>`;
        table_head += ` <th>เดชอุดม</th>`;
        table_head += ` <th>สำนักงานใหญ่PPS</th>`;
        table_head += ` <th>ผลิตPPS</th>`;
        table_head += ` <th>รอซ่อมPPS</th>`;
        table_head += ` <th>สินค้าชำรุดPPS</th>`;
        table_head += ` <th>เคลมสินค้าPPS</th>`;
        table_head += ` <th>หลังสวน</th>`;
        table_head += ` <th>หนองพอก</th>`;
        table_head += ` <th>แม่ขะจาน</th>`;
        table_head += ` <th>เอ็มจี กู๊ด</th>`;
        table_head += ` <th>กุฉินารายณ์</th>`;
        table_head += ` <th>เขาสมิง</th>`;
        table_head += ` <th>กันทรลักษ์</th>`;
        table_head += ` <th>นครพนม</th>`;
        table_head += ` <th>Shop S6</th>`;
        table_head += ` <th>พิบูลมังสาหาร</th>`;
        table_head += ` <th>พิษณุโลก</th>`;
        table_head += ` <th>นครราชสีมา</th>`;
        table_head += ` <th>Shop S1</th>`;
        table_head += ` <th>ออนไลน์</th>`;
        table_head += ` <th>ราษีไศล</th>`;
        table_head += ` <th>ท่าแซะ</th>`;
        table_head += ` <th>งานเกษตร</th>`;
        table_head += ` <th>ศูนย์ซ่อมShop</th>`;
        table_head += ` <th>พยัคฆภูมิพิสัย</th>`;
        table_head += ` <th>สว่างแดนดิน</th>`;
        table_head += ` <th>เซกา</th>`;
        table_head += ` <th>เลย</th>`;
        table_head += ` <th>กระนวน</th>`;
        table_head += ` <th>ศรีสะเกษ</th>`;
        table_head += ` <th>GRราชา</th>`;
        table_head += ` <th>ขายน้ำโสม</th>`;
        table_head += ` <th>น้ำยืน</th>`;
        table_head += ` <th>หนองบัวระเหว</th>`;
        table_head += ` <th>ชุมแพ</th>`;
        table_head += ` <th>ประทาย</th>`;
        table_head += ` <th>สุรินทร์</th>`;
        table_head += ` <th>วารินชำราบ</th>`;
        table_head += ` <th>หล่มสัก</th>`;
        table_head += ` <th>ศรีสงคราม</th>`;
        table_head += ` <th>สกลนคร</th>`;
        table_head += ` <th>ตระการพืชผล</th>`;
        table_head += ` <th>เลิงนกทา</th>`;
        table_head += ` <th>ตราด</th>`;
        table_head += ` <th>ธาตุพนม</th>`;
        table_head += ` <th>Shop S10</th>`;
        table_head += ` <th>สุวรรณภูมิ</th>`;
        table_head += ` <th>วังสามหมอ</th>`;
        table_head += ` <th>ยโสธร</th>`;
    }
    table_head += ` <th>จำนวนรวม</th> 
                            <th>ราคาขายปลีก</th> 
                            <th>ราคารวม</th> </tr>`;

    $('#th_header').html(table_head);
    $('#tbody_detail').html(table_body);

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
    getWarehouse();
    getCategorylist();
    getPatternList();
    getGroupmail();
    getBrand();
    getClass();
    getGrade();
    getDesign();
    getModel();
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
    $('#modal').modal('show');
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
    setTimeout(function () {
        $('#modal').modal('hide');
    }, 4000)
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
