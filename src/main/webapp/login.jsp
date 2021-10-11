<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <meta http-equiv="X-UA-Compatible" content="IE=edge">

        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Login</title>
        <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">

        <style>
            .table {
                cursor: pointer;
            }
            .table-bordered > thead > tr > th,
            .table-bordered > tbody > tr > th,
            .table-bordered > tfoot > tr > th,
            .table-bordered > thead > tr > td,
            .table-bordered > tbody > tr > td,
            .table-bordered > tfoot > tr > td {
                cursor: pointer;
            }
            html{
                background-color: #8EC5FC;
                background-image: linear-gradient(62deg, #8EC5FC 0%, #E0C3FC 100%);

            }
        </style>


    </head>
    <body >
        <div class="top-content" style="margin-top:6em">
            <div class="col-xs-12 col-sm-4 col-sm-offset-4 form-box" id="tab-login">
                <div class="form-top" >
                    <div class="form-top-center" style="text-align:center">
                        <h2 id="text-header">เข้าสู่ระบบ</h2>
                    </div>
                </div>
                <div class="form-bottom" >
                    <div class="form-group">
                        รหัสกิจการ
                        <input type="text" placeholder="กรุณาป้อนรหัสกิจการ..." class=" form-control" id="form-provider" value="data">
                    </div>
                    <div class="form-group">
                        รหัสพนักงาน
                        <input type="text" placeholder="กรุณาป้อนรหัสพนักงาน..." class=" form-control" id="form-plkiz" value="superadmin">
                    </div>
                    <div class="form-group">
                        รหัสผ่าน
                        <input type="password" placeholder="กรุณาป้อนรหัสผ่าน..." class=" form-control" id="form-jzuys" value="mFptH455">
                    </div>
                    <button class="btn btn-info btn-block btn-lg" id="btn-login">เข้าสู่ระบบ</button>

                </div>
                <div id="error" style="margin-top: 10px;"></div>
            </div>
            <div class="col-xs-12 col-sm-4 col-sm-offset-4 form-box" id="tab-table" style="display:none">
                <div class="card" style="background-color: #fff;padding: 6px;">
                    <h3 class="card-header text-center">เลือกฐานข้อมูล</h3>
                    <div class="card-body">
                        <table class="table table-hover ">
                            <thead>
                            <th>#</th>
                            <th>ชื่อฐานข้อมูล</th>
                            <th>ชื่อบริษัท</th>
                            </thead>
                            <tbody id="table-detail">

                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <!-- Javascript -->
        <script src="js/jquery-3.2.1.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script>
            $(document).ready(function ()
            {
                $('#btn-login').click(function ()
                {
                    var providerCode = $("#form-provider").val();
                    var plkiz = $("#form-plkiz").val();
                    var jzuys = $("#form-jzuys").val();
                    if ($.trim(providerCode).length > 0 && $.trim(plkiz).length > 0 && $.trim(jzuys).length > 0)
                    {


                        $.post("ajaxlogincontroller", {provider: providerCode, user: plkiz, pass: jzuys}, function (responseText) {
                            //console.log(responseText);
                            if (responseText.success) {
                                if (responseText.type == "1") {

                                    $("#error").html("");
                                    $("#table-detail").html(responseText.data);
                                    $('#form-login').hide();
                                    $('#text-header').text('เลือกฐานข้อมูล');
                                    $('#tab-table').show();
                                    $('#tab-login').hide();
                                } else if (responseText.type == "2") {
                                    window.location.href = 'verify.jsp?database=' + responseText.data;
                                }

                            } else {
                                $("#error").html("<br/><span style='color:#cc0000'>ผิดพลาด :</span> " + responseText.msg);
                                $("#form-plkiz").focus();
                                $("#form-plkiz").select();
                                $("#form-jzuys").val("");
                                $('#form-login').show();
                                $('#tab-table').hide();
                                $('#tab-login').show();
                                $('#text-header').text('เข้าสู่ระบบ');
                            }
                        }).always(function () {

                        });

                    }
                    return false;
                });
            });
        </script>

    </body>
</html>
