var SUB_LINK = "../";
var SERVER_URL = "userlist-list";
var strReloadPage = "<p style='font-size: 14px;'><span class='fa fa-spin fa-refresh fa-2x'></span> โหลดข้อมูล..</p>";
var PAGE_NO = 1;
var TOTAL_PAGE = 20;
$(function () {
    $(document).ready(function () {
        //_disabledElements();
        $("#content-table-list").html("<tr><td colspan='3'>" + strReloadPage + "</td></tr>");
        _refreshPage();
    });

    $("#content-table-list").on("click", "#btn-manage", function () {
        if (_preparingAllPerm(_checkPermUser_IsCreate(), _checkPermGroup_IsCreate())) {
            $("#lbl-user_code").text($(this).attr("key_id"));
            _findData({key_id: $(this).attr("key_id")});
        }
    });

    $("#btn-manage-cancel").on("click", function () {
        $("#content-manage").hide();
        $("#content-table").show();
        $("#content-search-box").show();
        $("#content-pagination").show();
    });

    $("#content-manage-list").on("click", ".chb_is_read", function () {
        if ($(this).is(":checked")) {
            _updatePermission({key_id: $(this).attr("key_id"), key_code: $("#lbl-user_code").text(), update_action: "is_read", update_status: true});
        } else {
            _updatePermission({key_id: $(this).attr("key_id"), key_code: $("#lbl-user_code").text(), update_action: "is_read", update_status: false});
        }
    });

    $("#content-manage-list").on("click", ".chb_is_create", function () {
        if ($(this).is(":checked")) {
            _updatePermission({key_id: $(this).attr("key_id"), key_code: $("#lbl-user_code").text(), update_action: "is_create", update_status: true});
        } else {
            _updatePermission({key_id: $(this).attr("key_id"), key_code: $("#lbl-user_code").text(), update_action: "is_create", update_status: false});
        }
    });

    $("#content-manage-list").on("click", ".chb_is_update", function () {
        if ($(this).is(":checked")) {
            _updatePermission({key_id: $(this).attr("key_id"), key_code: $("#lbl-user_code").text(), update_action: "is_update", update_status: true});
        } else {
            _updatePermission({key_id: $(this).attr("key_id"), key_code: $("#lbl-user_code").text(), update_action: "is_update", update_status: false});
        }
    });

    $("#content-manage-list").on("click", ".chb_is_delete", function () {
        if ($(this).is(":checked")) {
            _updatePermission({key_id: $(this).attr("key_id"), key_code: $("#lbl-user_code").text(), update_action: "is_delete", update_status: true});
        } else {
            _updatePermission({key_id: $(this).attr("key_id"), key_code: $("#lbl-user_code").text(), update_action: "is_delete", update_status: false});
        }
    });

    $(".pagination").on("click", ".pagination_link", function (e) {
        e.preventDefault();
        if ($(this).attr("page_no") !== undefined) {
            PAGE_NO = $(this).attr("page_no");
            _displayData({});
        }
    });

    $("#sel-table-pagination").on("change", function () {
        TOTAL_PAGE = $(this).val();
        PAGE_NO = 1;

        _displayData({});
    });

});

function _displayData(sendData) {
    sendData['action_name'] = "get_userlist";

    if ($.fn.DataTable.isDataTable('#tableId')) {
        $('#tableId').DataTable().destroy();
    }
    $('#tableId tbody').empty();
    $.ajax({
        url: SUB_LINK + SERVER_URL,
        type: "GET",
        data: sendData,

        success: function (res) {
            if (res['success']) {
                $("#content-table-list").html(res['data']);

            } else {
                swal(res['err_title'], res['err_msg'], "error");
            }
            setTimeout(function () {

                $('#tableId').DataTable({

                    "lengthMenu": [[20, 50], [20, 50, "All"]],
                });
            }, 100);
        }
    });

}

function _findData(sendData) {
    sendData['action_name'] = "find_userlist";
    $.ajax({
        url: SUB_LINK + SERVER_URL,
        type: "GET",
        data: sendData,
        beforeSend: function () {
            $("#content-manage-list").html("<tr><td colspan='6'>" + strReloadPage + "</td></tr>");
        },
        success: function (res) {
            if (res['success']) {
                $("#content-manage-list").html(res['data']);
                $("#content-manage").show();
                $("#content-table").hide();
                $("#content-search-box").hide();
                $("#content-pagination").hide();

//                _preparingICheck();
            } else {
                swal(res['err_title'], res['err_msg'], "error");
            }
        }
    });
}

function _refreshPage() {
    _displayData({});

    setTimeout(function () {
        _refreshPage();
    }, 60000);
    console.log("[user_list.js] -- refreshed");
}

function _updatePermission(sendData) {
    sendData['action_name'] = "update_permission";
    $.ajax({
        url: SUB_LINK + SERVER_URL,
        type: "GET",
        data: sendData,
        success: function (res) {
            if (res['success']) {
                swal("ข้อความระบบ", "บันทึกข้อมูลสำเร็จ", "success");
            } else {
                swal(res['err_title'], res['err_msg'], "error");
                console.log(res['err_sql']);
            }
        }
    });
}

function _disabledElements() {
    $("#txt-find-code").attr('disabled', true);
    $("#btn-show-all").attr('disabled', true);
    $("#btn-search-code").attr('disabled', true);
}