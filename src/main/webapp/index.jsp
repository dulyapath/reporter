
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="global.jsp"  %>
<%@page import="java.util.Arrays"%>
<%@page import="utils.MigrateDatabase"%>
<%@page import="java.util.Arrays"%>
<%    String title = "Dashboard";
    request.setAttribute("title", title);
    request.setAttribute("sublink", "./");
%>

<jsp:include  page="theme/header.jsp" flush="true" />
<%

%>
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper" style="background-color: #fff;padding-top:  1rem;">
    <!-- Main content -->
    <section class="content" >
        <div class="container-fluid" >
            <!--<div class="row">
                <div class="col-sm-6">
                    <div class="card text-white bg-info">
                        <div class="card-body" style="cursor: pointer">
                            <div class="row">
                                <div class="col-6">
                                    <h3>บันทึกรายการขาย</h3>
                                </div> 
                                <div class="col-6 text-right" style="padding-right: 25px">
                                    <h1 style="font-size: 64px">6</h1>
                                </div>  
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6">
                    <div class="card text-white bg-success ">
                        <div class="card-body" style="cursor: pointer">
                            <div class="row">
                                <div class="col-6">
                                    <h3>รายการรออนุมัติ</h3>
                                </div> 
                                <div class="col-6 text-right" style="padding-right: 25px">
                                    <h1 style="font-size: 64px">6</h1>
                                </div>  
                            </div>
                        </div>
                    </div>
                </div>
            </div>-->



        </div><!-- /.container-fluid -->
    </section>
    <!-- /.content -->
</div>
<jsp:include  page="theme/footer.jsp" flush="true" />

