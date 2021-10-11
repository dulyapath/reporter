<%@page import="Model.Permission"%>
<%@page import="utils.PermissionUtil"%>
<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%    
    String pageName = "แก้ไขรูปภาพ";
    String pageCode = "P000000004";

    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css", "../css/select2.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/bootstrap-datetimepicker.min.js", "../js/select2.min.js", "../js/getimage/getimg.js", "../js/base64js.min.js"));

    HttpSession _sess = request.getSession();
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

<input type="hidden" value="" id="r_status">
<input type="hidden" value="<%=_sess.getAttribute("user")%>" id="userlogin">
<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=pageCode%>" id="page_code">


<div>
    <div class="page-title">
        <div class="title_left">
            <h3>แก้ไขรูปภาพ</h3>
        </div>
        <!--
                          <div class="title_right">
                            <div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
                              <div class="input-group">
                                <input type="text" class="form-control" placeholder="Search">
                                <span class="input-group-btn">
                                  <button class="btn btn-default" type="button">Go!</button>
                                </span>
                              </div>
                            </div>
                          </div>
        -->
    </div>
    <div class="clearfix"></div>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel" style="padding-bottom: 0;">
                <div class="x_content">

                    <div class="row">
                      <div class="row">    
                        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
                            <label  class="control-label">จากรหัสสินค้า</label>
                           
                            <select  id="from_item" class="form-control item_select" placeholder=""></select>
                                
                           
                        </div>
                        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
                            <label  class="control-label">ถึงรหัสสินค้า</label>
                          
                                <select  id="to_item" class="form-control item_select" placeholder=""></select>
                               
                           
                        </div>
                        <div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
                         <label  class="control-label">ความกว้างสูงสุด</label>
                                <input  id="to_width" class="form-control" type="number" value="336">
                        </div>
                          
                          <div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
                         <label  class="control-label">ความสูงสูงสุด</label>
                                <input  id="to_height" class="form-control" type="number" value="280">
                        </div>
                       
                    </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="">
                            <button type="button" id="btn_codesearch" class="btn btn-primary  ">ปรับปรุงรูปที่เลือก</button>
                            <button type="button" id="btn_allpic" class="btn btn-success  ">ปรับปรุงรูปทั้งหมด</button>
                        </div>
                    
                    </div>
                     
                </div>
            </div>
        </div>
    </div>
    <div id="load" class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel" style="padding-bottom: 0;">
                <div class="x_content">
                    <i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>
                    <span style="font-size: 27px;"> Loading...</span>
                </div>
            </div>
        </div>
    </div>
    
    <div id="" class="row">
        <div id="" class="col-md-12 col-sm-12 col-xs-12">
            <div id="resultx" class="table-responsive">
                <H3><span id="count_num"></span></H3>
                <div class="progress">
                        <div class="progress bg-green" id="progresbarz" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
                        
                        </div>
                      </div>
                <canvas id="canvasx" style="display:none" ></canvas>
                <div id="showimg">
                    <img id="previewz" >
                </div>
            
            </div>
        </div>
    </div>
    
  
</div>
<!-- Modal -->

<jsp:include  page="../theme/footer.jsp" flush="true" />

<script>
    var a = new Date();
    var secrchByItem = false;
    var secrchByBarcode = false;
    var ses = new webkitSpeechRecognition();
    ses.continuous = true;
    ses.lang = 'TH'
    ses.onresult=function (e){
            if(event.results.length>0){
                    sonuc=event.results[event.results.length-1];
                    if(sonuc.isFinal){
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
    }

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