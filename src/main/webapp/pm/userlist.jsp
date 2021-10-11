<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String xProviderCode16 = request.getSession().getAttribute("dbname").toString();
    String xUser16 = request.getSession().getAttribute("user").toString(); 
%>
<%
    request.setAttribute("title", "ผู้ใช้งาน [รายการ]");
    request.setAttribute("css", Arrays.asList());
    request.setAttribute("js", Arrays.asList("../js/pm/userlist.js"));

    request.setAttribute("sublink", "../");
%>
<jsp:include  page="../theme/header.jsp" flush="true" />
<div>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h1>ผู้ใช้งาน <small>รายการ</small></h1>  
                    <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <br />
                    <div class="table-responsive">
                        <table class="table table-striped table-bordered">
                            <thead>
                                <tr style="background-color: #ddd;">
                                    <th>Username</th>
                                    <th>ชื่อผู้ใช้งาน</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody id="user_list">
                                <tr >
                                    <td id="user"></td>
                                    <td id="name"></td>
                                    <td id="link" class="text-center"><a href="#" style="color: #337ab7;">จัดการสิทธิ์</a></td>
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