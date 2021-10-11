<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="global.jsp"  %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Verify</title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    </head>
    <body>
        <%            
            String USER_CODE = request.getSession().getAttribute("user").toString();
            String DB_CODE = request.getParameter("database");
        %>
        <div class="container">
            <input type="hidden" id="db_code" value="<%=DB_CODE%>">
            <input type="hidden" id="user_code" value="<%=USER_CODE%>">
            <div class="row">
                <div class="col-lg-6 col-md-8 col-sm-10 col-lg-offset-3 col-md-offset-2 col-sm-offset-1">
                    <div class="panel panel-default" style="margin-top: 55px;">
                        <div class="panel-heading">
                            <div class="panel-title">Verify</div>
                        </div>
                        <div class="panel-body" style="padding: 5px 5px 15px 5px;">
                            <pre id="log" data-stop="${param.stop}" style="border-radius: 0;padding-bottom: 0; margin-bottom: 0"></pre>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="js/jquery-3.2.1.min.js"></script>
        <script src="js/verify.js"></script>
    </body>
</html>
