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

        <title>SML MIS V.1 | ${title}</title>

        <link href="${sublink}assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="${sublink}assets/font-awesome/css/font-awesome.min.css" rel="stylesheet">
        <link href="${sublink}css/custom.min.css" rel="stylesheet">
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
    <body class="nav-md">
        <input type="hidden" id="sublinkx" value="${sublink}">
        <div class="overlay" style="display:none"></div>
        <div class="container body">
            <div class="main_container">
                <div class="col-md-3 left_col">
                    <div class="left_col scroll-view">
                        <div class="navbar nav_title" style="border: 0;">
                            <a href="${sublink}index.jsp" class="site_title">SML <span>MIS V.1</span></a>
                        </div>

                        <div class="clearfix"></div>

                        <div class="profile">
                            <div class="profile_pic">
                                <img src="${sublink}images/icon-user.png" alt="..." class="img-circle profile_img">
                            </div>
                            <div class="profile_info">
                                <span>ยินดีต้อนรับ,</span>
                                <h2>${user_name}</h2>
                            </div>
                        </div>

                        <br />

                        <!-- sidebar menu -->
                        <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
                            <div class="menu_section">
                                <h3>${dbname}</h3>
                                <ul class="nav side-menu">
                                    <li><a href="${sublink}index.jsp"><i class="fa fa-home"></i> หน้าหลัก </a></li>
                                        <%
                                            String xProviderCode24 = request.getSession().getAttribute("provider").toString();
                                            String xUser24 = request.getSession().getAttribute("user").toString();
                                            PermissionUtil pmu = new PermissionUtil(xProviderCode24);
                                            List<Permission> pmList = pmu.getPermissUser(xUser24);
                                            boolean isAdmin = pmu.checkPermissAdmin(xUser24);
                                        %>
                                    <li class=""><a><i class="fa fa-file-text-o"></i> ยอดคงเหลือ <span class="fa fa-chevron-down"></span></a>
                                        <ul class="nav child_menu">
                                            <li ><a href="${sublink}balance/list.jsp"><i class="fa fa-file-text-o"></i>ยอดคงเหลือสินค้า</a></li>
                                        </ul>
                                    </li>

                                    <li class=""><a><i class="fa fa-file-text-o"></i> สถานะ <span class="fa fa-chevron-down"></span></a>
                                        <ul class="nav child_menu">
                                            <li ><a href="${sublink}arcustomer/list.jsp"><i class="fa fa-file-text-o"></i>ลูกหนี้</a></li>
                                        </ul>
                                    </li>
                                    <li class=""><a><i class="fa fa-file-text-o"></i> รายงาน <span class="fa fa-chevron-down"></span></a>
                                        <ul class="nav child_menu">
                                            <li ><a href="${sublink}report/report1.jsp"><i class="fa fa-file-text-o"></i>รายงาน</a></li>
                                        </ul>
                                    </li>

                                    <li class=""><a><i class="fa fa-file-text-o"></i> วิเคราะห์ขาย <span class="fa fa-chevron-down"></span></a>
                                        <ul class="nav child_menu">
                                            <li ><a href="${sublink}sale-summary/list.jsp"><i class="fa fa-file-text-o"></i>ภาพรวม</a></li>
                                        </ul>
                                    </li>
                                    <li class=""><a><i class="fa fa-user"></i> กำหนดสิทธิ์ <span class="fa fa-chevron-down"></span></a>
                                        <ul class="nav child_menu">
                                            <li class=""><a href="${sublink}permission/user_list.jsp">แบบเดี่ยว</a></li>
                                            <li class=""><a href="${sublink}permission/user_group.jsp">แบบกลุ่ม</a></li>
                                        </ul>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <!-- /sidebar menu -->
                    </div>
                </div>

                <div class="top_nav">
                    <div class="nav_menu">
                        <nav>
                            <div class="nav toggle">
                                <a id="menu_toggle"><i class="fa fa-bars"></i></a>
                            </div>

                            <ul class="nav navbar-nav navbar-right">
                                <li class="">
                                    <a href="javascript:;" class="user-profile dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                                        <img src="${sublink}images/icon-user.png" alt="">${user_name}
                                        <span class=" fa fa-angle-down"></span>
                                    </a>
                                    <ul class="dropdown-menu dropdown-usermenu pull-right">
                                        <li><a href="${sublink}logout.jsp"><i class="fa fa-sign-out pull-right"></i> ออกจากระบบ</a></li>
                                    </ul>
                                </li>
                            </ul>
                        </nav>
                    </div>
                </div>
                <div class="right_col" role="main">