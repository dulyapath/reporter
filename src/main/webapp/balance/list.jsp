<%@page import="org.json.JSONArray"%>
<%@include file="../globalsub.jsp"  %>
<%@page import="Model.Permission"%>
<%@page import="utils.PermissionUtil"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%    String pageName = "ยอดคงเหลือสินค้า";
    String pageCode = "R00000002";

    String xProviderCode24 = request.getSession().getAttribute("provider").toString();
    String xUser24 = request.getSession().getAttribute("user").toString();
    PermissionUtil pmu = new PermissionUtil(xProviderCode24);
    JSONArray pmList = pmu.getPermissUser(xUser24);
    if (!pmu.getKey(pmList, "R00000002").getBoolean("is_read")) {
        String site = new String("../index.jsp");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.sendRedirect(site);
        return;
    }
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
//    request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/balance/balance2.js", "../js/_globals.js"));

    HttpSession _sess = request.getSession();
%>
<jsp:include  page="../theme/header.jsp" flush="true" />
<style>
    body {
        color: black;
    }

</style>
<input type="hidden" value="" id="r_status">
<input type="hidden" value="<%=_sess.getAttribute("user")%>" id="userlogin">
<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=pageCode%>" id="page_code">
<input type="hidden" id="hSubLink" value="${sublink}">
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
                                <div class="col-md-2 col-sm-3 col-xs-12">
                                    <div class="form-group">
                                        <div class="input-group">
                                            <input type="text" id="txtSearchItem" class="form-control" placeholder="ค้นหาด้วยรหัสหรือชื่อสินค้า">
                                            <span class="input-group-btn">
                                                <button type="button" id="btnSearchItem" class="btn btn-primary"><i class="fa fa-search"></i></button>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-2 col-sm-3 col-xs-12 text-center" style="display: none;">
                                    <div class="form-group">
                                        <button type="button" id="btnSearchVoice" class="btn btn-primary disabled"><i class="fa fa-microphone"></i> ค้นหาด้วยเสียง</button>
                                    </div>
                                </div>
                                <div class="col-md-2 col-sm-3 col-xs-12">
                                    <div class="form-group">
                                        <div class="input-group">
                                            <input type="text" id="txtSearchBarcode" class="form-control" placeholder="ค้นหาด้วยบาร์โค้ด">
                                            <span class="input-group-btn">
                                                <button type="button" id="btnSearchBarcode" class="btn btn-primary"><i class="fa fa-search"></i></button>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--Content Table Box-->
            <div class="row" id="contentTableBox">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="x_panel">
         
                        <div class="table-responsive">
                            <table class="table table-striped text-center">
                                <thead>
                                <th class="text-center" colspan="2" style="width: 1%;"></th>
                                <th class="text-center"> <a href="javascript:void(0)" onclick="sortICode()"> รหัสสินค้า   </a> <i id="sortIc" class="fa fa-sort-down"> </i></th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortIcName()"> ชื่อสินค้า   </a> <i id="sortIcName" class="fa fa-sort-down"> </i></th>
                                <th class="text-center">หน่วยนับ</th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortBalancePO()"> สั่งซื้อค้างรับ   </a> <i id="sortBalancePO" class="fa fa-sort-down"> </i></th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortOrder()"> สั่งจองค้างส่ง   </a> <i id="sortOrder" class="fa fa-sort-down"> </i></th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortSale()"> สั่งขายค้างส่ง   </a> <i id="sortSale" class="fa fa-sort-down"> </i></th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortWH()"> สาขา/คลัง   </a> <i id="sortWH" class="fa fa-sort-down"> </i></th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortShelf()"> พื้นที่   </a> <i id="sortShelf" class="fa fa-sort-down"> </i></th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortQty()"> ยอดคงเหลือ   </a> <i id="sortQty" class="fa fa-sort-down"> </i></th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortLastIn()"> รับเข้าล่าสุด   </a> <i id="sortLastIn" class="fa fa-sort-down"> </i></th>
                                <th class="text-center"><a href="javascript:void(0)" onclick="sortLastSale()"> ขายล่าสุด   </a> <i id="sortLastSale" class="fa fa-sort-down"> </i></th>
                                </thead>
                                <tbody id="mainList">
                                    <tr id="mainRow">
                                        <td><button id="btnExpand" class="btn btn-primary btn-sm"><i class="fa fa-search"></i></button></td>
                                        <td><button id="btnChart" class="btn btn-primary btn-sm"><i class="fa fa-bar-chart"></i></button></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                        <td><h5>#</h5></td>
                                    </tr>
                                    <tr id="subRow">
                                        <td colspan="13" class="table-responsive">
                                            <table class="table">
                                                <tr id="subRowData1" style="display: none;">
                                                    <td>
                                                        <div class="x_panel">
                                                            <div class="x_title"><h3 class="text-left">สั่งซื้อค้างรับ</h3></div>
                                                            <div class="table-responsive">
                                                                <table class="table table-condensed table-striped text-center" style="margin: 0;">
                                                                    <thead style="background-color: #F5B041; color: #F8F9F9">
                                                                    <th class="text-center">วันที่เอกสาร</th>
                                                                    <th class="text-center">เวลา</th>
                                                                    <th class="text-center">เลขที่เอกสาร</th>
                                                                    <th class="text-center">สาขา</th>
                                                                    <th class="text-center">เจ้าหนี้</th>
                                                                    <th class="text-center">คลัง</th>
                                                                    <th class="text-center">ที่เก็บ</th>
                                                                    <th class="text-center">จำนวน</th>
                                                                    <th class="text-center">หน่วยนับ</th>
                                                                    <th class="text-center">ส่วนลด</th>
                                                                    <th class="text-center">มูลค่า</th>
                                                                    </thead>
                                                                    <tbody id="subList">
                                                                        <tr>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                        </tr>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr id="subRowData2" style="display: none;">
                                                    <td>
                                                        <div class="x_panel">
                                                            <div class="x_title"><h3 class="text-left">สั่งจองค้างส่ง</h3></div>
                                                            <div class="table-responsive">
                                                                <table class="table table-condensed table-striped text-center" style="margin: 0;">
                                                                    <thead style="background-color: #F5B041; color: #F8F9F9">
                                                                    <th class="text-center">วันที่เอกสาร</th>
                                                                    <th class="text-center">เวลา</th>
                                                                    <th class="text-center">ลูกค้า</th>
                                                                    <th class="text-center">สาขา</th>
                                                                    <th class="text-center">เลขที่เอกสาร</th>
                                                                    <th class="text-center">จำนวนสั่งจอง</th>
                                                                    <th class="text-center">จำนวนที่ออกบิลแล้ว</th>
                                                                    <th class="text-center">ค้างออกบิล</th>
                                                                    </thead>
                                                                    <tbody id="subList">
                                                                        <tr>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                        </tr>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr id="subRowData3" style="display: none;">
                                                    <td>
                                                        <div class="x_panel">
                                                            <div class="x_title"><h3 class="text-left">สั่งขายค้างส่ง</h3></div>
                                                            <div class="table-responsive">
                                                                <table class="table table-condensed table-striped text-center" style="margin: 0;">
                                                                    <thead style="background-color: #F5B041; color: #F8F9F9">
                                                                    <th class="text-center">วันที่เอกสาร</th>
                                                                    <th class="text-center">เวลา</th>
                                                                    <th class="text-center">ลูกค้า</th>
                                                                    <th class="text-center">สาขา</th>
                                                                    <th class="text-center">เลขที่เอกสาร</th>
                                                                    <th class="text-center">จำนวนสั่งขาย</th>
                                                                    <th class="text-center">จำนวนที่ออกบิลแล้ว</th>
                                                                    <th class="text-center">ค้างออกบิล</th>
                                                                    </thead>
                                                                    <tbody id="subList">
                                                                        <tr>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>                                                                    
                                                                        </tr>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr id="subRowData4" style="display: none;">
                                                    <td>
                                                        <div class="x_panel">
                                                            <div class="x_title"><h3 class="text-left">ซื้อล่าสุด</h3></div>
                                                            <div class="table-responsive">
                                                                <table class="table table-condensed table-striped text-center" style="margin: 0;">
                                                                    <thead style="background-color: #F5B041; color: #F8F9F9">
                                                                    <th class="text-center">วันที่เอกสาร</th>
                                                                    <th class="text-center">เวลา</th>
                                                                    <th class="text-center">เลขที่เอกสาร</th>
                                                                    <th class="text-center">สาขา</th>
                                                                    <th class="text-center">เจ้าหนี้</th>
                                                                    <th class="text-center">คลัง</th>
                                                                    <th class="text-center">ที่เก็บ</th>
                                                                    <th class="text-center">จำนวน</th>
                                                                    <th class="text-center">หน่วยนับ</th>
                                                                    <th class="text-center">ส่วนลด</th>
                                                                    <th class="text-center">มูลค่า</th>
                                                                    </thead>
                                                                    <tbody id="subList">
                                                                        <tr>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                        </tr>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr id="subRowData5" style="display: none;">
                                                    <td>
                                                        <div class="x_panel">
                                                            <div class="x_title"><h3 class="text-left">ขายล่าสุด</h3></div>
                                                            <div class="table-responsive">
                                                                <table class="table table-condensed table-striped text-center" style="margin: 0;">
                                                                    <thead style="background-color: #F5B041; color: #F8F9F9">
                                                                    <th class="text-center">วันที่เอกสาร</th>
                                                                    <th class="text-center">เวลา</th>
                                                                    <th class="text-center">เลขที่เอกสาร</th>
                                                                    <th class="text-center">สาขา</th>
                                                                    <th class="text-center">เจ้าหนี้</th>
                                                                    <th class="text-center">คลัง</th>
                                                                    <th class="text-center">ที่เก็บ</th>
                                                                    <th class="text-center">จำนวน</th>
                                                                    <th class="text-center">หน่วยนับ</th>
                                                                    <th class="text-center">ส่วนลด</th>
                                                                    <th class="text-center">มูลค่า</th>
                                                                    </thead>
                                                                    <tbody id="subList">
                                                                        <tr>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                            <td><h5>#</h5></td>
                                                                        </tr>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </tbody>
                                <tfoot>
                                    <tr id="load"><td colspan="13"><p class='text-center' style="margin-top: 1em;"><span class='fa fa-circle-o-notch fa-spin fa-2x'></span> กำลังโหลดข้อมูล...</p></td></tr>
                                </tfoot>
                            </table>
                        </div>
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