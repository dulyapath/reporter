
<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%    String xProviderCode16 = request.getSession().getAttribute("dbname").toString();
    String xUser16 = request.getSession().getAttribute("user").toString();

%>
<%
    request.setAttribute("title", "user permission");
    request.setAttribute("css", Arrays.asList());
    request.setAttribute("js", Arrays.asList("../js/pm/permiss.js"));

    request.setAttribute("sublink", "../");

%>
<jsp:include  page="../theme/header.jsp" flush="true" />
<div>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h1>สิทธิ์การเข้าถึง</h1>  
                    <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <br />
                    <div class="form-horizontal">
                        <div class="form-group">
                            <label class="control-label col-sm-4 col-md-2">main level</label>
                            <div class="col-sm-8 col-md-5 col-lg-4">
                                <select id="ac_parent" class="form-control">
                                    <option value="0">Root</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4 col-md-2">key code</label>
                            <div class="col-sm-8 col-md-5 col-lg-4">
                                <input id="ac_code" type="text" class="form-control">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-4 col-md-2">name</label>
                            <div class="col-sm-8 col-md-5 col-lg-4">
                                <input id="ac_name" type="text" class="form-control">
                            </div>
                        </div>
                        <div id="err_msg" class="form-group" style="display: none;">
                            <div class="col-sm-8 col-md-5 col-lg-4 col-sm-offset-4 col-md-offset-2">
                                <div class="alert alert-error" style="margin-bottom: 0;"></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-8 col-md-5 col-lg-4 col-sm-offset-4 col-md-offset-2">
                                <button id="ac_add" class="btn btn-success" data-loading-text="Processing...">เพิ่ม</button>
                                <button id="ac_edit" class="btn btn-warning" data-loading-text="Processing..." style="display: none;">แก้ไข</button>
                                <button id="ac_edit_cancel" class="btn btn-default" data-loading-text="Processing..." style="display: none;">ยกเลิก</button>
                            </div>
                        </div>
                        <div class="ln_solid"></div>
                        <div class="form-group">
                            <label class="control-label col-sm-4 col-md-2">main level</label>
                            <div class="col-sm-8 col-md-5 col-lg-4">
                                <select id="crud_parent" class="form-control">
                                    <option value="0">Root</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-8 col-md-5 col-lg-4 col-sm-offset-4 col-md-offset-2">
                                <button id="crud" class="btn btn-success">Generate CRUD</button>
                            </div>
                        </div>
                        <div class="ln_solid"></div>
                    </div>
                    <div>
                        <button id="reset" class="btn btn-danger">Reset</button>
                        <div style="width: 300px; margin-top: 5px;">
                            <select id="show_parent" class="form-control">
                                <option value="0">Root</option>
                            </select>
                        </div>

                    </div>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>id</th>
                                    <th>code name</th>
                                    <th>description</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody id="ac_list">
                                <tr>
                                    <td id="_id"></td>
                                    <td id="_code"></td>
                                    <td id="_name"></td>
                                    <td >
                                        <button id="_disable" class="btn btn-success diable-status"><span class="glyphicon glyphicon-eye-open"></span></button>
                                        <button id="_edit" class="btn btn-warning edit">Edit</button>
                                        <button id="_del" class="btn btn-danger del">Del</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include  page="../theme/footer.jsp" flush="true" />
