var objBox = null;
var serverURL = "../";
var sql_date = 'YYYY-MM-DD';
var refreshz;
var inp = "";
var doc_no = "";
var ic_code = "";
var qty = "";
var whcode = "";
var shelfcode = "";
var value = "";
var maxW = 336;
var maxH = 280;
var canvas = document.getElementById("canvasx");
var ctx = canvas.getContext("2d");
var cw = canvas.width;
var ch = canvas.height;
$(function () {
    objBox = $('#root').clone();
    //  $('#stage').html('');
    __getItemCode();
    setTimeout(function () {
        //loadList({});
        $('#load').hide();
    }, 200);

    $('#btn_codesearch').on('click', function () {
        var from_item = $('#from_item').val();
        var to_item = $('#to_item').val();
        maxW = $('#to_width').val();
        maxH = $('#to_height').val();
        loadList({from_item: from_item, to_item: to_item});
    });
    $('#btn_allpic').on('click', function () {

        maxW = $('#to_width').val();
        maxH = $('#to_height').val();
        loadList({});
    });

});
function __getItemCode() {
    $.ajax({
        url: serverURL + 'get-item',
        method: 'GET',
        dataType: 'json',
        success: function (response) {
            if (response.success) {
                var data = "<option value='noting'>เลือกรหัสสินค้า</option>";
                $.each(response.data, function (key, obj) {
                    data += "<option value='" + obj['item_code'] + "'>" + obj['item_code'] + ' ~ ' + obj['item_name'] + "</option>";

                });
                $('.item_select').html(data);

                $(".item_select").select2();
            }

        },
        error: function () {

        }

    });
}


function _refreshPAGE(data) {


//   for(var i=0;i<data.length;i++){
//      console.log(data[i])
    /*   $('#root_'+data[i]).hide('fast');*/

//    }
    setTimeout(function () {

        re(data, 0);
    }, 100);


    // setTimeout(_refreshPAGE, 5000);

}
var temp = $('#previewz');
$('#showimg').html('');
function re(data, i) {



    var img = new Image;
    img.onload = function () {
        var iw = img.width;
        var ih = img.height;
        var scale = Math.min((maxW / iw), (maxH / ih));
        var iwScaled = iw * scale;
        var ihScaled = ih * scale;
        canvas.width = iwScaled;
        canvas.height = ihScaled;
        ctx.drawImage(img, 0, 0, iwScaled, ihScaled);

    }
    var loot = temp.clone();
    if (i < data.length) {

        img.src = data[i]['image_file'];
        setTimeout(function () {
            loot.attr('id', data[i]['guid_code']);
            loot.attr('src', canvas.toDataURL("image/jpeg"));
            $('#showimg').html(loot);
            $('#count_num').text('รูปที่:' + (i + 1) + '/' + data.length);
            $('#progresbarz').css('width', (((i + 1) / data.length) * 100) + '%');
            setTimeout(function () {
                var dataz = {};
                dataz['image_id'] = data[i]['image_id'];
                dataz['guid_code'] = data[i]['guid_code'];
                dataz['image_file'] = $('#' + data[i]['guid_code']).attr('src');
                console.log(dataz)

                $.ajax({
                    url: serverURL + 'save-img',
                    method: 'POST',
                    cache: false,
                    data: dataz,
                    dataType: 'JSON',
                    success: function (res) {
                        console.log(res)
                        refreshz = setTimeout(function () {
                            i++;
                            re(data, i);
                        }, 800);
                    },
                    error: function (res) {
                        swal({
                            title: 'เกิดข้อผิดพลาด',
                            text: res.responseText,
                            type: "error",
                            timer: 2000,
                            showConfirmButton: false});
                        refreshz = setTimeout(function () {
                            i++;
                            re(data, i);
                        }, 1000);
                    }
                });
            }, 200);
        }, 500);

        

    } else {

        setTimeout(function () {
            clearTimeout(refreshz);
            img.src = '';
            $('#count_num').text('');
            $('#progresbarz').css('width', '0%');
            swal({
                title: 'ข้อความระบบ!',
                text: 'ปรับรูปภาพสำเร็จ' + data.length + ' รูป',
                type: "success",
                timer: 6000,
                showConfirmButton: true});
        }, 2000);



    }

}

function loadList(data) {

    $('#load').show();
    $('#doc_not_found').hide();
    // console.log(data);

    //$('#resultx').html('');
    $.ajax({
        url: serverURL + 'get-imagex1',
        method: 'GET',
        cache: false,
        data: data,
        success: function (res) {

            console.log(res.data)
            if (res.success) {
                var dataz = "";
                var datax = [];
                $.each(res.data, function (key, obj) {
                    datax.push({image_id: obj.image_id, guid_code: obj.guid_code, image_file: obj.image_file});
                });
                _refreshPAGE(datax)
            }


            // $('#resultx').html(res);

        },
        error: function (res) {
            swal({
                title: 'เกิดข้อผิดพลาด',
                text: res.responseText,
                type: "error",
                timer: 2000,
                showConfirmButton: false});
        },
        complete: function () {
            $('#load').hide();
        }
    });
}



function arrayBufferToBase64(buffer) {
    var binary = '';
    var bytes = new Uint8Array(buffer);
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[ i ]);
    }
    return window.btoa(binary);
}


function credit(obj) {
    var id = obj.id.substring(1);
    console.log(id);
    if ($("#R" + id).html() != "")
    {
        $("#R" + id).empty();
    } else {

        $.ajax({
            url: serverURL + 'arcustomer-detail',
            method: 'POST',
            cache: false,
            data: {cust: id},
            success: function (res) {

                //console.log(res);
                $("#R" + id).html(res);
            },
            error: function () {

            }

        });

    }
}




var id_use = [];
function genid() {
    var $gen_length = 4;
    var $lip_text = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var $lip_length = $lip_text.length;

    var $gen_text = '';
    for (var $i = 0; $i < $gen_length; $i++) {
        $gen_text += $lip_text.charAt(Math.floor(Math.random() * ($lip_length - 0 + 1)) + 0);
    }

    if (!checkTempId($gen_text)) {
        $gen_text = genid();
    }
    id_use.push($gen_text);
    return $gen_text;
}

function checkTempId(id_new) {
    for (var id_temp in id_use) {
        if (id_temp == id_new) {
            return false;
        }
    }
    return true;
}

