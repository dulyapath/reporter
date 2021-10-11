<%@page import="org.json.JSONArray"%>
<%@page import="Model.Permission"%>
<%@page import="utils.PermissionUtil"%>
<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%// ###################    
    String pageName = "กำหนดสิทธิ์";
    String pageCode = "R00000000";

    String xProviderCode24 = request.getSession().getAttribute("provider").toString();
    String xUser24 = request.getSession().getAttribute("user").toString();
    PermissionUtil pmu = new PermissionUtil(xProviderCode24);
    JSONArray pmList = pmu.getPermissUser(xUser24);
    if (!pmu.getKey(pmList, "R00000000").getBoolean("is_read")) {
        String site = new String("../index.jsp");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.sendRedirect(site);
        return;
    }

    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/bootstrap-datetimepicker.min.js", "../js/permission/user_list.js", "../js/_globals.js"));

    HttpSession _session = request.getSession();
%>
<jsp:include  page="../theme/header.jsp" flush="true" />
<style>
    table {
        border-collapse: collapse;
    }
    body {
        color: black;
    }
    table, th, td {
        font-size:12px;
        border: 1px solid #D3D3D3 ;
        padding:0px;
    }
    tr:hover{
        background-color: #F0F8FF;
    }
</style>

<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=pageCode%>" id="page_code">
<input type="hidden" id="hSubLink" value="${sublink}">
<div class="content-wrapper" style="background-color: #fff">
    <!-- Content Header (Page header) -->

    <div class="content-header">
        <div class="container-fluid">

       
            <!--content-search-box-->
            <div class="row clearfix" id="content-search-box">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="panel panel-primary">
                        <div class="panel-body">
                            <div class="row">
                                <!--  <div class="col-lg-6 col-md-4 col-sm-4 col-xs-12">
                                      <div class="form-horizontal">
                                          <div class="form-group" >
                                              <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                                                  <label  class="control-label">ค้นหาข้อมูล</label>
                                              </div>
                                              <div class="col-lg-9 col-md-9 col-sm-12 col-xs-12">
                                                  <div class="input-group">
                                                      <span class="input-group-btn">
                                                          <button type="button" id="btn-show-all" class="btn btn-primary">แสดงทั้งหมด</button>
                                                      </span>
                                                      <input type="text" id="txt-find-code" class="form-control" placeholder="ใส่ข้อมูลค้นหา">
                                                      <span class="input-group-btn">
                                                          <button type="button" id="btn-search-code" class="btn btn-success"><i class="fa fa-search"></i></button>
                                                      </span>
                                                  </div>
                                              </div>
                                          </div>
                                      </div>
                                  </div>-->
                              
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--content-table-->
            <div class="row clearfix" id="content-table">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="panel panel-primary">
                        <div class="table-responsive">
                            <table class="table table-striped table-condensed text-center" id="tableId">
                                <thead>
                                <th class="text-center" style="width: 8%">#</th>
                                <th class="text-center">รหัสผู้ใช้งาน ~ ชื่อผู้ใช้งาน</th>
                                <th class="text-center" style="width: 8%">จัดการ</th>
                                </thead>
                                <tbody id="content-table-list"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <!--content-pagination-->
            <div class="row" id="content-pagination">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <ul class="pagination pagination-sm pull-right" style="display: none; margin: 0;"></ul>
                </div>
            </div>
            <!--content-manage-->
            <div class="row clearfix" id="content-manage" style="display:none">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-lg-11 col-md-11 col-sm-11 col-xs-11">
                                    <h4><strong>ชื่อผู้ใช้งาน - <small id="lbl-user_code"></small></strong></h4>
                                </div>
                                <div class="col-lg-1 col-md-1 col-sm-1 col-xs-1">
                                    <button type="button" id="btn-manage-cancel" class="btn btn-danger">กลับ</button>
                                </div>
                            </div>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-striped table-condensed text-center">
                                <thead>
                                <th class="text-center" style="width: 8%">#</th>
                                <th class="text-center">รหัสเพจ ~ ชื่อเพจ</th>
                                <th class="text-center">ดูรายงาน</th>
                                </thead>
                                <tbody id="content-manage-list"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</div>

<jsp:include  page="../theme/footer.jsp" flush="true" />