/* global moment */

var log = console.log.bind();
var subLink = null;
var page_code = null;
var user_code = null;

var user_perm_is_read = null;
var user_perm_is_create = null;
var user_perm_is_update = null;
var user_perm_is_delete = null;

var group_perm_is_read = [];
var group_perm_is_create = [];
var group_perm_is_update = [];
var group_perm_is_delete = [];

const now = moment().toDate();
const dToDate = convertDate(now);
const dFromDate = convertDate(moment(now).add(-7, 'day'));

$(function () {
    $(document).ready(function () {
        page_code = $("#page_code").val();
        user_code = $("#user_code").val();
        subLink = $("#hSubLink").val();
        log("--> PAGE_CODE: " + page_code);
        log("--> USER_CODE: " + user_code);

        // ###################################################
        setTimeout(function () {
            _updatePerm();
            _refreshGlobal();
        }, 500);
    });
});

function _updatePerm() {
    if (user_code === "SUPERADMIN" || user_code === "SMLADMIN") {
        user_perm_is_read = true;
        user_perm_is_create = true;
        user_perm_is_update = true;
        user_perm_is_delete = true;
        $(".overlay").hide();
    } else {
        _getPermUser({'key_id': page_code});
    }
    setTimeout(function () {
        _updatePerm();
    }, 15000);
}

function _refreshGlobal() {
    setTimeout(function () {
        _refreshGlobal();
    }, 45000);

    log("[_globals.js] -- refreshed");
}

function _getPermUser(sendData) {
    sendData['action_name'] = "find_permission_user";
    $.ajax({
        url: subLink + "userlist-list",
        type: "GET",
        data: sendData,
        success: function (res) {
            if (res['success']) {
                if (res['data'].length > 0) {
                    user_perm_is_read = res['data'][0]['is_read'];
                    user_perm_is_create = res['data'][0]['is_create'];
                    user_perm_is_update = res['data'][0]['is_update'];
                    user_perm_is_delete = res['data'][0]['is_delete'];
                } else {
                    user_perm_is_read = false;
                    user_perm_is_create = false;
                    user_perm_is_update = false;
                    user_perm_is_delete = false;
                }
                _getPermGroup({key_id: page_code});
            } else {
                swal(res['err_title'], res['err_msg'], "error");
            }
        }
    });
}

function _getPermGroup(sendData) {
    sendData['action_name'] = "find_permission_group";
    $.ajax({
        url: subLink + "usergroup-list",
        type: "GET",
        data: sendData,
        success: function (res) {
            if (res['success']) {
                group_perm_is_read = [];
                group_perm_is_create = [];
                group_perm_is_update = [];
                group_perm_is_delete = [];
                if (res['data'].length > 0) {
                    $.each(res['data'], function (key, obj) {
                        group_perm_is_read.push(obj['is_read']);
                        group_perm_is_create.push(obj['is_create']);
                        group_perm_is_update.push(obj['is_update']);
                        group_perm_is_delete.push(obj['is_delete']);
                    });
                } else {
                    group_perm_is_read.push(false);
                    group_perm_is_create.push(false);
                    group_perm_is_update.push(false);
                    group_perm_is_delete.push(false);
                }
                _checkPermUser_IsRead();
            } else {
                swal(res['err_title'], res['err_msg'], "error");
            }
        }
    });

}

// ##########################  CHECK PERMISSION ###########################

function _checkPermUser_IsRead() {
    if (user_code !== "SUPERADMIN") {
        console.log(user_perm_is_read)
        if (!user_perm_is_read) {
            _checkPermGroup_IsRead();
        } else {
            $(".overlay").hide();
        }
    } else {
        $(".overlay").hide();
    }
}

function _checkPermUser_IsCreate() {
    var status = false;
    if (user_code !== "SUPERADMIN") {
        if (user_perm_is_create) {
            status = true;
        }
    } else {
        status = true;
    }
    return status;
}

function _checkPermUser_IsUpdate() {
    var status = false;
    if (user_code !== "SUPERADMIN") {
        if (user_perm_is_update) {
            status = true;
        }
    } else {
        status = true;
    }
    return status;
}

function _checkPermUser_IsDelete() {
    var status = false;
    if (user_code !== "SUPERADMIN") {
        if (user_perm_is_delete) {
            status = true;
        }
    } else {
        status = true;
    }
    return status;
}

function _checkPermGroup_IsRead() {
    console.log(group_perm_is_read)
    if (!_preparingPermGroup(group_perm_is_read)) {
        window.location = '../index.jsp';
    } else {
        $(".overlay").hide();
    }
}

function _checkPermGroup_IsCreate() {
    var status = false;
    if (user_code !== "SUPERADMIN") {
        if (!_preparingPermGroup(group_perm_is_create)) {

        } else {
            status = true;
        }
    } else {
        status = true;
    }
    return status;
}

function _checkPermGroup_IsUpdate() {
    var status = false;
    if (user_code !== "SUPERADMIN") {
        if (!_preparingPermGroup(group_perm_is_update)) {
        } else {
            status = true;
        }
    } else {
        status = true;
    }
    return status;
}

function _checkPermGroup_IsDelete() {
    var status = false;
    if (user_code !== "SUPERADMIN") {
        if (!_preparingPermGroup(group_perm_is_delete)) {

        } else {
            status = true;
        }
    } else {
        status = true;
    }
    return status;
}

// ##########################  CUSTOM FUNCTION ###########################

function _preparingPermGroup(group_perm) {
    var status = false;
    for (i = 0; i < group_perm.length; i++) {
        if (group_perm[i]) {
            status = true;
        }
    }
    return status;
}

function _preparingAllPerm(user_perm, group_perm) {
    console.log('user_perm',user_perm)
    console.log('group_perm',group_perm)
    var status = false;
    if (user_code !== "SUPERADMIN") {
        if (!user_perm) {
            if (group_perm) {
                status = true;
            } else {
                swal("ข้อความระบบ", "Permission Denined.", "error");
            }
        } else {
            status = true;
        }
    } else {
        status = true;
    }
    return status;
}

function _preparingICheck() {
    $('input').iCheck({
        checkboxClass: 'icheckbox_flat-green'
    });
}

function optDatePicker() {
    return {
        language: 'th',
        thaiyear: true,
        format: 'dd/mm/yyyy',
        autoclose: true
    };
}

function convertDate(date, date_format) {
    return moment(date).add(date_format === 'th' ? 543 : 0, 'year').format('DD/MM/YYYY');
}