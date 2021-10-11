<%@page import="org.json.JSONArray"%>
<%@include file="../globalsub.jsp"  %>
<%@page import="Model.Permission"%>
<%@page import="utils.PermissionUtil"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%    String pageName = "รายงานสินค้าคงเหลือ เรียงตามคลัง";
    String pageCode = "R00000001";

    String xProviderCode24 = request.getSession().getAttribute("provider").toString();
    String xUser24 = request.getSession().getAttribute("user").toString();
    PermissionUtil pmu = new PermissionUtil(xProviderCode24);
    JSONArray pmList = pmu.getPermissUser(xUser24);
    if (!pmu.getKey(pmList, "R00000001").getBoolean("is_read")) {
        String site = new String("../index.jsp");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.sendRedirect(site);
        return;
    }
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
//    request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/reporter/report1.js", "../js/_globals.js"));

    HttpSession _sess = request.getSession();
%>
<jsp:include  page="../theme/header.jsp" flush="true" />
<style>
    body {
        color: black;
    }
    .buttons-excel{
        margin-left: 5px !important;
    }
</style>
<input type="hidden" value="" id="r_status">
<input type="hidden" value="<%=_sess.getAttribute("user")%>" id="userlogin">
<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=pageCode%>" id="page_code">
<input type="hidden" id="hSubLink" value="${sublink}">
<div class="modal fade" id="modal" tabindex="-1" role="dialog" data-keyboard="false" data-backdrop="static">
    <div class="modal-dialog" role="document">

        <img src="../images/load.gif">

    </div>
</div>
<div class="content-wrapper" style="background-color: #fff">

    <div class="content-header">
        <div class="container-fluid">
            <div class="clearfix"></div>
            <!--Content Search Box-->
            <div class="row" id="contentSearchBox">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="x_panel">
                        <div class="x_content">
                            <div class="row">
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="date_begin" id="txt-search_item">ค้นหาชื่อ-รหัส</label>
                                        <input class="form-control " id="search_item" type="text" style="height:34px">
                                    </div>
                                </div>
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="date_begin " class="txt-date_begin">คงเหลือ ณ วันที่</label>
                                        <input class="form-control " id="date_begin" type="date" style="height:34px">
                                    </div>
                                </div>
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="group_mail" id="txt-group_mail">คลังสินค้า</label>
                                        <select class="form-control selectwhcode" id="wh_code" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>
                                <div class="col-sm-3 col-md-3" style="display:none">
                                    <div class="form-group">
                                        <label for="shelf_code" id="txt-shelf_code">ที่เก็บสินค้า</label>
                                        <select class="form-control selectshelfcode" id="shelf_code"  disabled="disabled"   style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>


                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="group_mail" id="txt-group_mail">กลุ่มสินค้าหลัก</label>
                                        <select class="form-control selectgroupmail" id="group_mail" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="group_sub" id="txt-group_sub">กลุ่มสินค้าย่อย</label>
                                        <select class="form-control selectgroupsub" id="group_sub"  disabled="disabled"  style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>

                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="group_sub" id="txt-group_sub2">กลุ่มสินค้าย่อย2</label>
                                        <select class="form-control selectgroupsub2" id="group_sub2"  disabled="disabled"  style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="item_class" id="txt-item_class">ระดับสินค้า</label>
                                        <select class="form-control selectclass" id="item_class" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>

                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="item_brand" id="txt-item_brand">ยี่ห้อสินค้า</label>
                                        <select class="form-control selectitembrand" id="item_brand" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="item_grade" id="txt-item_grade">เกรดสินค้า</label>
                                        <select class="form-control selectgrade" id="item_grade" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>

                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="item_pattern" id="txt-item_pattern">รูปแบบสินค้า</label>
                                        <select class="form-control selectitempattern" id="item_pattern" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="item_design" id="txt-item_design">รูปทรงสินค้า</label>
                                        <select class="form-control selectdesign" id="item_design" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group row">
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="search_category" id="txt-search_category">หมวดสินค้า</label>
                                        <select class="form-control selectcategory" id="search_category" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="item_model" id="txt-item_model">รุ่นสินค้า</label>
                                        <select class="form-control selectmodel" id="item_model" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>

                                <div class="col-sm-3 col-md-3" style="display:none">
                                    <div class="form-group">
                                        <label for="item_pattern" id="txt-item_branch">สาขา</label>
                                        <select class="form-control selectbranch" id="item_branch" multiple="multiple" style='width: 100%;height: 100%'></select>
                                    </div>
                                </div>   
                                <div class="col-sm-3 col-md-3">
                                    <div class="form-group">
                                        <label for="item_pattern" id="txt-balancetype">จำนวนคงเหลือ</label>
                                        <select class="form-control selectbalancetype" id="balance_type" style="height:34px">
                                            <option value="0">แสดงทั้งหมด</option>
                                            <option value="1">แสดงคงเหลือมากกว่าศูนย์</option>
                                            <option value="3">แสดงคงเหลือน้อยกว่าศูนย์</option>
                                            <option value="2">แสดงคงเหลือที่เป็นศูนย์</option>
                                            <option value="4">แสดงคงเหลือที่ไม่เท่ากับศูนย์</option>
                                        </select>
                                    </div>
                                </div>   
                            </div>
                            <div class="row">
                                <div class="col-sm-6 col-md-6">
                                    <button class="btn btn-primary" id="btn-process">ประมวลผล</button>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--Content Table Box-->
            <div class="row" id="contentTableBox" style="margin-top: 5px;">
                <div class="col-md-12 col-sm-12 col-xs-12">

                    <div class="table-responsive">
                        <table id="tableId" class="table table-striped table-bordered text-center">
                            <thead id="th_header">

                            </thead>
                            <tbody id="tbody_detail"></tbody>
                        </table>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include  page="../theme/footer.jsp" flush="true" />
<script>
    var a = new Date();
    var secrchByItem = false;
    var secrchByBarcode = false;
    var ses = new webkitSpeechRecognition();
    ses.continuous = true;
    ses.lang = 'TH';
    ses.onresult = function (e) {
        if (event.results.length > 0) {
            sonuc = event.results[event.results.length - 1];
            if (sonuc.isFinal) {
                var oldValue = $('#item').val();
                var newValue = sonuc[0].transcript;
                if (oldValue.length > 0)
                {
                    oldValue = oldValue + ' ';
                }
                if (newValue.indexOf('เริ่มใหม่') != -1) {
                    clearValue();
                    $("#item").text('เริ่มใหม่');
                } else {
                    $('#item').val(oldValue + sonuc[0].transcript);
                    secrchByItem = true;
                }
            }
        }
    };

    var $speechworking = false;



    function clearValue() {
        $('#item').val('');
    }

    function eylem() {
        if ($speechworking == false)
        {
            $speechworking = true;
            $('#speech').text('หยุดค้นหาด้วยเสียง');
            ses.start();
        } else {
            $('#speech').text('ค้นหาด้วยเสียง');
            $speechworking = false;
            ses.stop();
        }
    }
</script>