<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@include file="../globalsub.jsp" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Model.Permission"%>
<%@page import="utils.PermissionUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="utils.RandomID"%>

<%    List css = request.getAttribute("css") == null ? new ArrayList<String>() : (List) request.getAttribute("css");
    String v = "?_=" + RandomID.rand();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Reporter | ${title}</title>

        <link rel="stylesheet" href="${sublink}plugins/fontawesome-free/css/all.min.css">
        <!-- Ionicons -->
        <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
        <!-- Tempusdominus Bbootstrap 4 -->
        <link rel="stylesheet" href="${sublink}plugins/tempusdominus-bootstrap-4/css/tempusdominus-bootstrap-4.min.css">
        <!-- iCheck -->
        <link rel="stylesheet" href="${sublink}plugins/icheck-bootstrap/icheck-bootstrap.min.css">
        <!-- JQVMap -->
        <link rel="stylesheet" href="${sublink}plugins/jqvmap/jqvmap.min.css">
        <!-- Theme style -->
        <link rel="stylesheet" href="${sublink}dist/css/adminlte.min.css">
        <!-- overlayScrollbars -->
        <link rel="stylesheet" href="${sublink}plugins/overlayScrollbars/css/OverlayScrollbars.min.css">
        <!-- Daterange picker -->
        <link rel="stylesheet" href="${sublink}plugins/daterangepicker/daterangepicker.css">
        <!-- summernote -->
        <link rel="stylesheet" href="${sublink}plugins/summernote/summernote-bs4.css">
        <link href="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/css/select2.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="${sublink}/css/select2-bootstrap.css">
        <link rel="stylesheet" href="${sublink}/css/sweetalert.css">
       <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs4/jszip-2.5.0/dt-1.10.25/b-1.7.1/b-colvis-1.7.1/b-html5-1.7.1/b-print-1.7.1/datatables.min.css"/>

        <!-- Google Font: Source Sans Pro -->
        <link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700" rel="stylesheet">
        <%
            if (css.size() > 0) {
                for (int i = 0; i < css.size(); i++) {
                    out.print("<link rel=\"stylesheet\" href=\"" + css.get(i).toString() + v + "\">");
                }
            }
        %>
        <style type="text/css">
            .overlay {
                position: absolute;
                top: 0;
                left: 0;
                height: 100%;
                width: 100%;
                background-color: rgba(0,0,0,0.5);
                z-index: 10;
            }
        </style>

    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">

            <!-- Navbar -->
            <nav class="main-header navbar navbar-expand navbar-white navbar-light">
                <!-- Left navbar links -->
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link" data-widget="pushmenu" href="#"><i class="fas fa-bars"></i></a>
                    </li>
                    <li class="nav-item d-none d-sm-inline-block">
                        <a href="#" class="nav-link" style="color:#000">${title}</a>
                    </li>
                </ul>

            </nav>
            <!-- /.navbar -->

            <!-- Main Sidebar Container -->
            <aside class="main-sidebar sidebar-light-primary elevation-4">
                <!-- Brand Logo -->
                <a href="${sublink}index.jsp" class="brand-link">
                    <img src="${sublink}dist/img/logo.png" alt="Logo" class="brand-image img-circle elevation-3"
                         >
                    <span class="brand-text font-weight">Reporter</span>
                </a>

                <!-- Sidebar -->
                <div class="sidebar">
                    <!-- Sidebar user panel (optional) -->
                    <div class="user-panel mt-3 pb-3 mb-3 d-flex">
                        <div class="image">
                            <img src="${sublink}images/icon-user.png" class="img-circle elevation-2" alt="User Image">
                        </div>
                        <div class="info">
                            <a href="#" class="d-block">${user_name}</a>
                        </div>
                    </div>


                    <%
                        String xProviderCode24 = request.getSession().getAttribute("provider").toString();
                        String xUser24 = request.getSession().getAttribute("user").toString();
                        PermissionUtil pmu = new PermissionUtil(xProviderCode24);
                        JSONArray pmList = pmu.getPermissUser(xUser24);
                        boolean isAdmin = pmu.checkPermissAdmin(xUser24);
                    %>
                    <nav class="mt-2">
                        <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">
                            <li class="nav-header">DB:${dbname}</li>
                            <li class="nav-item">
                                <a href="${sublink}index.jsp" class="nav-link">
                                    <i class="nav-icon fa fa-home"></i>
                                    <p>
                                        เมนูหลัก
                                    </p>
                                </a>
                            </li>



                            <li class="nav-header">รายงาน</li>
                                <%
                                    if (pmu.getKey(pmList, "R00000001").getBoolean("is_read")) {
                                %>
                            <li class="nav-item">
                                <a href="${sublink}report/report1.jsp" class="nav-link">
                                    <i class="nav-icon fas fa-file"></i>
                                    <p>
                                        สินค้าคงเหลือเรียงตามคลัง
                                    </p>
                                </a>
                            </li>


                            <%
                                }

                            %>
                            <%                                    if (pmu.getKey(pmList, "R00000002").getBoolean("is_read")) {
                            %>
                            <li class="nav-item">
                                <a href="${sublink}report/report2.jsp" class="nav-link">
                                    <i class="nav-icon fas fa-file"></i>
                                    <p>
                                        ประมาณการสั่งสินค้า
                                    </p>
                                </a>
                            </li>


                            <%
                                }

                            %>
                            <%                                if (pmu.getKey(pmList, "R00000003").getBoolean("is_read")) {
                            %>
                            <li class="nav-item">
                                <a href="${sublink}report/report3.jsp" class="nav-link">
                                    <i class="nav-icon fas fa-file"></i>
                                    <p>
                                        วิเคราะห์การขาย กำไรขั้นต้น
                                    </p>
                                </a>
                            </li>

                            <%
                                }

                            %>
                            <li class="nav-header">ตั้งค่า</li>
                                <% if (pmu.getKey(pmList, "R00000000").getBoolean("is_read")) {
                                %>
                            <li class="nav-item">
                                <a href="${sublink}permission/user_list.jsp" class="nav-link">
                                    <i class="nav-icon fas fa-user "></i>
                                    <p class="text">กำหนดสิทธิ์</p>
                                </a>
                            </li>

                            <%
                                }

                            %>



                            <li class="nav-item">
                                <a href="${sublink}logout.jsp" class="nav-link">
                                    <i class="nav-icon fas fa-sign-out-alt text-danger"></i>
                                    <p>ออกจากระบบ</p>
                                </a>
                            </li>
                        </ul>
                    </nav>
                    <!-- /.sidebar-menu -->
                </div>
                <!-- /.sidebar -->
            </aside>