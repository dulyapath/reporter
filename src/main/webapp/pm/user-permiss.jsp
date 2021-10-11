<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%    String xProviderCode16 = request.getSession().getAttribute("dbname").toString();
    String xUser16 = request.getSession().getAttribute("user").toString();

%>
<%
    request.setAttribute("title", "Permission manage");
    request.setAttribute("css", Arrays.asList("../css/green.css"));
    request.setAttribute("js", Arrays.asList("../js/icheck.min.js", "../js/pm/user-permiss.js"));

    request.setAttribute("sublink", "../");
%>
<jsp:include  page="../theme/header.jsp" flush="true" />
<div>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h3>สิทธิ์การเข้าถึง <small >ผู้ใช้งาน [ <span id="user" data-user="<%=request.getParameter("user")%>"><%=request.getParameter("user")%></span> ]</small></h3>  
                    <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <br />
                    <div class="row">
                        <div class="col-xs-12 col-sm-10 col-md-6 col-lg-4">
                            <div id="searchBox" class="input-group">
                                <input type="text" class="form-control" placeholder="ค้นหา">
                                <span class="input-group-btn">
                                    <button class="btn btn-default"><i class="fa fa-search"></i></button>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div id="permiss" class="table-responsive" style="margin-bottom: 10px;">
                        <table class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th>ชื่อเมนู</th>
                                    <th style="width: 100px;">
                                        <label><input type="checkbox" class="flat" data-is="READ"> อ่าน</label>
                                    </th>
                                    <th style="width: 100px;">
                                        <label><input class="flat" type="checkbox" data-is="CREATE"> เพิ่ม</label>
                                    </th>
                                    <th style="width: 100px;">
                                        <label><input class="flat" type="checkbox" data-is="DEL"> ลบ</label>
                                    </th>
                                    <th style="width: 100px;">
                                        <label><input class="flat" type="checkbox" data-is="EDIT"> แก้ไข</label>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td></td>
                                    <td><input type="checkbox"></td>
                                    <td><input type="checkbox"></td>
                                    <td><input type="checkbox"></td>
                                    <td><input type="checkbox"></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div>
                        <a href="userlist.jsp" class="btn btn-lg btn-default">กลับ</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include  page="../theme/footer.jsp" flush="true" />
