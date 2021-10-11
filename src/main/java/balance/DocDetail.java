/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balance;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import utils._routine;

/**
 *
 * @author sml-dev-pcz
 */
@WebServlet(name = "Balance-detail", urlPatterns = {"/balance-detail"})
public class DocDetail extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DocDetail</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DocDetail at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
   
        StringBuilder __html = new StringBuilder();
        StringBuilder html2 = new StringBuilder();
        HttpSession _sess = request.getSession();
        Integer count = 0;
        DecimalFormat decim = new DecimalFormat("#,###.##");
        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {
          
            return;
        }
        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();

        String __providerCode = _sess.getAttribute("provider").toString();
        String fileConfig = "SMLConfig" + __providerCode.toUpperCase() + ".xml";

        String ic_code = "";
        if (request.getParameter("code") == null || request.getParameter("code").toString().isEmpty()) {
            response.getWriter().write("No code");
            return;
        }

        ic_code = request.getParameter("code");
        String mode = request.getParameter("mode");


        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, fileConfig);

            String __queryBody = "select *,(select name_1 from erp_branch_list where code=branch_code) as branch_name,(select name_1 from ap_supplier where code=cust_code) as cust_name from (select *,po_qty-(in_qty+cancel_qty) as balance_qty from (select *,(select cust_code from ic_trans where doc_no=x1.doc_no  and trans_flag=6) as cust_code,(select doc_date from ic_trans where doc_no=x1.doc_no  and trans_flag=6) as doc_date,(select doc_time from ic_trans where doc_no=x1.doc_no  and trans_flag=6) as doc_time,(select branch_code from ic_trans where doc_no=x1.doc_no  and trans_flag=6) as branch_code,coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=x1.item_code and ref_doc_no=x1.doc_no and trans_flag in (12,310) and last_status=0),0) as in_qty,coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=x1.item_code and ref_doc_no=x1.doc_no and trans_flag=7 and last_status=0),0) as cancel_qty from (select item_code,doc_no,sum(qty * (stand_value/divide_value)) as po_qty from ic_trans_detail as q1 where item_code='" + ic_code + "' and trans_flag=6 and last_status=0 group by item_code,doc_no) as x1) as x2) as x3 where balance_qty<>0 order by doc_date,doc_time";
          
            Statement __stmtBody = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsBody = __stmtBody.executeQuery(__queryBody);
            ResultSetMetaData _rsBodyMd = __rsBody.getMetaData();
            int _colBodyCount = _rsBodyMd.getColumnCount();
      
            while (__rsBody.next()) {
               if (count == 0) {
                        html2.append("<table width='100%' border=1>");
                        html2.append("<tr bgcolor='#EFF2FB'>");
                        html2.append("<td colspan=9 align='right'><font size=+1><b>สั่งซื้อค้างรับ</b></font></td>");
                        html2.append("</tr>");
                        html2.append("<tr bgcolor='#BCF5A9'>");
                        html2.append("<td align='center'><b>วันที่เอกสาร</b></td>");
                        html2.append("<td align='center'><b>เวลา</b></td>");
                        html2.append("<td align='center'><b>ผู้จำหน่าย</b></td>");
                        html2.append("<td align='center'><b>สาขา</b></td>");
                        html2.append("<td align='center'><b>เลขที่เอกสาร</b></td>");
                        html2.append("<td align='center'><b>จำนวนสั่งซื้อ</b></td>");
                        html2.append("<td align='center'><b>จำนวนที่รับแล้ว</b></td>");
                        html2.append("<td align='center'><b>จำนวนที่ยกเลิก</b></td>");
                        html2.append("<td align='center'><b>ค้างรับ</b></td>");
                        html2.append("</tr>");
                   
               }
                html2.append( "<tr>");
                html2.append( "<td align='center'>" + __routine.dateThai(__rsBody.getString("doc_date")) + "</td>");
                html2.append( "<td align='center'>" +__rsBody.getString("doc_time")+"</td>");
                html2.append( "<td>" +__rsBody.getString("cust_code")+"&nbsp;(" +__rsBody.getString("cust_name")+ ")</td>");
                html2.append( "<td>" +__rsBody.getString("branch_code")+ "&nbsp;(" +__rsBody.getString("branch_name")+")</td>");
                html2.append( "<td>" +__rsBody.getString("doc_no")+"</td>");
                html2.append( "<td align='right'>"+ decim.format(__rsBody.getInt("po_qty")));
                
                String query_doc = "select doc_date,doc_time,doc_no,price,qty * (stand_value/divide_value) as qty from ic_trans_detail where item_code='"  +ic_code + "' and doc_no='" + __rsBody.getString("doc_no") + "' and trans_flag=6 and last_status=0 order by doc_date,doc_time";
                Statement __stmt2 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet __rs2 = __stmt2.executeQuery(query_doc);
                Integer count_detail = 0;
                String html3  = "";
                
                 while (__rs2.next()) {
                     
                if (count_detail == 0) {
                    html3 += "<table border=0>";
                    }
                    html3 += "<tr>";
                    html3 += "<td>&nbsp;<i>";
                    if (__rs2.getInt("price")==0) {
                            html3 += "<font color='red'>แถม</font>&nbsp;";
                    }
                    html3 += __routine.dateThai(__rs2.getString("doc_date")) + "</i>&nbsp;</td>";
                    html3 += "<td>&nbsp;<i>" + __rs2.getString("doc_time")+ "</i>&nbsp;</td>";
                    html3 += "<td>&nbsp;<i>" +__rs2.getString("doc_no")+ "</i>&nbsp;</td>";
                    html3 += "<td align='right'>&nbsp;<i>" +decim.format(__rs2.getInt("qty"))+  "</i>&nbsp;</td>";
                    html3 += "</tr>";
                    count_detail++;
                }
                if (count_detail != 0) {
		html3 += "</table>";
                }
                if (count_detail > 1) {
                        html2.append(html3);
                }
                html2 .append("</td>");
                
                html2.append("<td align='right'>" +  decim.format(__rsBody.getInt("in_qty"))) ;
                html2.append("<td align='right'>" +  decim.format(__rsBody.getInt("cancel_qty"))) ;
                
                String query_doc2 = "select item_code,doc_date,doc_time,doc_no,qty * (stand_value/divide_value) as qty from ic_trans_detail where item_code='" + ic_code + "' and ref_doc_no='" +__rsBody.getString("doc_no")+ "' and trans_flag in(12,310) and last_status=0 order by doc_date,doc_time";
                Statement __stmt3 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet __rs3 = __stmt3.executeQuery(query_doc2);
                count_detail = 0;
                while (__rs3.next()) {
                    
		if (count_detail == 0) {
			html2.append("<table border=0>");
		}
		html2.append("<tr>");
		html2.append("<td>&nbsp;<i>" + __routine.dateThai(__rs3.getString("doc_date")) + "</i>&nbsp;</td>");
		html2.append("<td>&nbsp;<i>" + __rs3.getString("doc_time") + "</i>&nbsp;</td>");
		html2.append("<td>&nbsp;<i>" + __rs3.getString("doc_no") + " " + __rs3.getString("item_code")+ "</i>&nbsp;</td>");
		html2.append("<td align='right'>&nbsp;<i>" + decim.format( __rs3.getInt("qty")) + "</i>&nbsp;</td>");
		html2.append("</tr>");
		count_detail++;
                }
                if (count_detail != 0) {
		html2.append("</table>");
                }
                html2.append("</td>");

                html2.append("<td align='right'>"+ decim.format( __rsBody.getInt("balance_qty"))+ "</td>");
                html2.append("</tr>");
                count++;
               
            }
            if (count != 0) {
                html2.append("</table>");
            }
            
            // สั่งจองค้างส่ง
            //$query2 = "select * from (select *,po_qty-in_qty as balance_qty from (select cust_code,(select name_1 from ar_customer where code=cust_code) as cust_name,doc_date,doc_time,doc_no,branch_code,(select name_1 from erp_branch_list where code=branch_code) as branch_name,qty * (stand_value/divide_value) as po_qty,coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=q1.item_code and ref_doc_no=q1.doc_no and trans_flag=44 and last_status=0),0) as in_qty from ic_trans_detail as q1 where item_code='" . $ic_code . "' and trans_flag=34 and last_status=0) as x1) as x2 where balance_qty<>0 order by doc_date,doc_time";
            String query2 = "select * from (select *,po_qty-in_qty as balance_qty from (select *,(select name_1 from ar_customer where code=(select cust_code from ic_trans where doc_no=x1.doc_no)) as cust_name,(select cust_code from ic_trans where doc_no=x1.doc_no) as cust_code,(select doc_date from ic_trans where doc_no=x1.doc_no) as doc_date,(select doc_time from ic_trans where doc_no=x1.doc_no) as doc_time,(select name_1 from erp_branch_list where code=(select branch_code from ic_trans where doc_no=x1.doc_no)) as branch_name,(select branch_code from ic_trans where doc_no=x1.doc_no) as branch_code,coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=x1.item_code and ref_doc_no=x1.doc_no and trans_flag=44 and last_status=0),0) as in_qty from (select item_code,doc_no,sum(qty * (stand_value/divide_value)) as po_qty from ic_trans_detail as q1 where item_code='"+ ic_code +"' and trans_flag=34 and last_status=0 group by item_code,doc_no) as x1) as x2) as x3 where balance_qty<>0 order by doc_date,doc_time";
            count = 0;
             
            Statement __stmtquery2 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsquery2 = __stmtquery2.executeQuery(query2);
            while (__rsquery2.next()) {

                    if (count == 0) {
                            html2.append("<table width='100%' border=1>");
                            html2.append("<tr bgcolor='#EFF2FB'>");
                            html2.append("<td colspan=8 align='right'><font size=+1><b>สั่งจองค้างออกบิล</b></font></td>");
                            html2.append("</tr>");
                            html2.append("<tr bgcolor='#BCF5A9'>");
                            html2.append("<td align='center'><b>วันที่เอกสาร</b></td>");
                            html2.append("<td align='center'><b>เวลา</b></td>");
                            html2.append("<td align='center'><b>ลูกค้า</b></td>");
                            html2.append("<td align='center'><b>สาขา</b></td>");
                            html2.append("<td align='center'><b>เลขที่เอกสาร</b></td>");
                            html2.append("<td align='center'><b>จำนวนสั่งจอง</b></td>");
                            html2.append("<td align='center'><b>จำนวนที่ออกบิลแล้ว</b></td>");
                            html2.append("<td align='center'><b>ค้างออกบิล</b></td>");
                            html2.append("</tr>");
                    }
                    html2.append("<tr>");
                    html2.append("<td align='center'>" + __routine.dateThai(__rsquery2.getString("doc_date")) + "</td>");
                    html2.append("<td align='center'>" +__rsquery2.getString("doc_time")+ "</td>");
                    html2.append("<td>" +__rsquery2.getString("cust_code")+ "&nbsp;(" +__rsquery2.getString("cust_name")+ ")</td>");
                    html2.append("<td>" +__rsquery2.getString("branch_code")+ "&nbsp;(" +__rsquery2.getString("branch_name")+  ")</td>");
                    html2.append("<td>" +__rsquery2.getString("doc_no")+"</td>");
                    html2.append("<td align='right'>" + decim.format(__rsquery2.getInt("po_qty"))+ "</td>");

                    html2.append("<td align='right'>" + decim.format(__rsquery2.getInt("in_qty")));
                    
                    String query_doc = "select doc_date,doc_time,doc_no,qty * (stand_value/divide_value) as qty from ic_trans_detail where item_code='" +ic_code + "' and ref_doc_no='" + __rsquery2.getString("doc_no") + "' and trans_flag=44 and last_status=0 order by doc_date,doc_time";
                     Statement __stmtquery3 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet __rsquery3 = __stmtquery3.executeQuery(query_doc);
                    Integer count_detail = 0;
                    
                    while (__rsquery3.next()) {
                          if (count_detail == 0) {
                                    html2.append("<table border=0>");
                            }
                            html2.append("<tr>");
                            html2.append("<td>&nbsp;" + __routine.dateThai(__rsquery3.getString("doc_date")) + "&nbsp;</td>");
                            html2.append("<td>&nbsp;" + __rsquery3.getString("doc_time") + "&nbsp;</td>");
                            html2.append("<td>&nbsp;" + __rsquery3.getString("doc_no") + "&nbsp;</td>");
                            html2.append("<td align='right'>&nbsp;" + decim.format(__rsquery3.getInt("qty"))+ "&nbsp;</td>");
                            html2.append("</tr>");
                            count_detail++;   
                    }
                  
                    if (count_detail != 0) {
                            html2.append("</table>");
                    }
                    html2.append("</td>");

                    html2.append("<td align='right'>" +   decim.format(__rsquery2.getInt("balance_qty")) + "</td>");
                    html2.append("</tr>");
                    count++;
                 
            }
            if (count != 0) {
                html2.append("</table>");
            }
            
            // สั่งขายค้างส่ง
            String query33 = "select * from (select *,po_qty-in_qty as balance_qty from (select *,(select cust_code from ic_trans where doc_no=x1.doc_no),(select name_1 from ar_customer where code=(select cust_code from ic_trans where doc_no=x1.doc_no)) as cust_name,(select doc_date from ic_trans where doc_no=x1.doc_no) as doc_date,(select doc_time from ic_trans where doc_no=x1.doc_no) as doc_time,(select name_1 from erp_branch_list where code=(select branch_code from ic_trans where doc_no=x1.doc_no)) as branch_name,(select branch_code from ic_trans where doc_no=x1.doc_no),coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=x1.item_code and ref_doc_no=x1.doc_no and trans_flag=44 and last_status=0),0) as in_qty from (select item_code,doc_no,sum(qty * (stand_value/divide_value)) as po_qty from ic_trans_detail as q1 where item_code='" + ic_code + "' and trans_flag=36 and last_status=0 group by item_code,doc_no) as x1) as x2) as x3 where balance_qty<>0 order by doc_date,doc_time";
            count = 0;
            
            Statement __stmtquery33 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rs33 = __stmtquery33.executeQuery(query33);
            while (__rs33.next()) {
                    if (count == 0) {
                            html2.append("<table width='100%' border=1>");
                            html2.append("<tr bgcolor='#EFF2FB'>");
                            html2.append("<td colspan=8 align='right'><font size=+1><b>สั่งขายค้างออกบิล</b></font></td>");
                            html2.append("</tr>");
                            html2.append("<tr bgcolor='#BCF5A9'>");
                            html2.append( "<td align='center'><b>วันที่เอกสาร</b></td>");
                            html2.append("<td align='center'><b>เวลา</b></td>");
                            html2.append( "<td align='center'><b>ลูกค้า</b></td>");
                            html2.append( "<td align='center'><b>สาขา</b></td>");
                            html2.append( "<td align='center'><b>เลขที่เอกสาร</b></td>");
                            html2.append( "<td align='center'><b>จำนวนสั่งขาย</b></td>");
                            html2.append( "<td align='center'><b>จำนวนที่ออกบิลแล้ว</b></td>");
                            html2.append( "<td align='center'><b>ค้างออกบิล</b></td>");
                            html2.append( "</tr>");
                    }
                    html2.append( "<tr>");
                    html2.append(  "<td align='center'>" +__routine.dateThai(__rs33.getString("doc_date")) + "</td>");
                    html2.append(  "<td align='center'>" + __rs33.getString("doc_time") + "</td>");
                    html2.append( "<td>" + __rs33.getString("cust_code") + "&nbsp;("  + __rs33.getString("cust_name") + ")</td>");
                    html2.append( "<td>" + __rs33.getString("branch_code") +"&nbsp;(" +__rs33.getString("branch_name")+ ")</td>");
                    html2.append(  "<td>" + __rs33.getString("doc_no")+ "</td>");
                    html2.append(  "<td align='right'>" +decim.format(__rs33.getInt("po_qty"))+"</td>");
                    html2.append( "<td align='right'>"  +decim.format(__rs33.getInt("in_qty")));
                    
                    String query_doc3 = "select doc_date,doc_time,doc_no,qty * (stand_value/divide_value) as qty from ic_trans_detail where item_code='" +ic_code+ "' and ref_doc_no='" +__rs33.getString("doc_no")+ "' and trans_flag=44 and last_status=0 order by doc_date,doc_time";
                        
                    Statement __stmtquery4 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet __rsquery4 = __stmtquery4.executeQuery(query_doc3);
                
                    Integer count_detail = 0;
                    
                    
                   while (__rsquery4.next()) {
                            if (count_detail == 0) {
                                    html2.append("<table border=0>");
                            }
                           html2.append("<tr>");
                            html2.append("<td>&nbsp;" + __routine.dateThai(__rsquery4.getString("doc_date")) + "&nbsp;</td>");
                            html2.append("<td>&nbsp;" + __rsquery4.getString("doc_time") + "&nbsp;</td>");
                            html2.append("<td>&nbsp;" + __rsquery4.getString("doc_no") + "&nbsp;</td>");
                            html2.append("<td align='right'>&nbsp;" + decim.format(__rsquery4.getInt("qty"))+ "&nbsp;</td>");
                            html2.append("</tr>");
                            count_detail++;   
                    }
                    if (count_detail != 0) {
                            html2.append("</table>");
                    }
                    html2.append("</td>");

                    html2.append("<td align='right'>" +   decim.format(__rs33.getInt("balance_qty")) + "</td>");
                    html2.append("</tr>");
                    count++;
                }
                if (count != 0) {
                   html2 .append( "</table>");
                 }
                
        /*if (mode.equals("2")) {
	// แสดงรายละเอียดเพิ่มสำหรับฝ่ายจัดซื้อ
	html2.append("<table width='100%' border=1>");
	html2.append("<tr>");
	html2.append("<td>");
	html2.append("<center><font size='+1'><b>ราคาปรกติ</b></font></center>");
	html2.append("<table width='100%' border=1>");
	html2.append("<tr>");
	html2.append("<td align='center'><b>ประเภท</b></td>");
	html2.append("<td align='center'><b>ราคากลาง</b></td>");
	html2.append("<td align='center'><b>ราคาลู่ 1</b></td>");
	html2.append( "<td align='center'><b>ราคาลู่ 2</b></td>");
	html2.append("<td align='center'><b>ราคาลู่ 3</b></td>");
	html2.append("<td align='center'><b>ราคาลู่ 4</b></td>");
	html2.append("<td align='center'><b>หน่วย</b></td>");
	html2.append( "<td align='center'><b>ภาษี</b></td>");
	html2.append("</tr>");
        
	String queryz = "select case when sale_type=1 then 'สด' else 'เชื่อ' end as sale_type_name,price_0,price_1,price_2,price_3,price_4,unit_code,(select name_1 from ic_unit where code=ic_code) as unit_name,tax_type from ic_inventory_price_formula where ic_code='" + ic_code + "' order by sale_type";
	
        Statement __stmtquery5 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rsquery5 = __stmtquery5.executeQuery(queryz);
  
	while (__rsquery5.next()) {
		html2.append("<tr>");
		html2.append("<td align='center'>" +__rsquery5.getString("sale_type_name")+  "</td>");
		html2.append("<td align='right'>" + decim.format(__rsquery5.getInt("price_0"))+ "</td>");
		html2.append("<td align='right'>" + decim.format(__rsquery5.getInt("price_1"))+  "</td>");
		html2.append("<td align='right'>" + decim.format(__rsquery5.getInt("price_2"))+ "</td>");
		html2.append("<td align='right'>" + decim.format(__rsquery5.getInt("price_3"))+ "</td>");
		html2.append("<td align='right'>" + decim.format(__rsquery5.getInt("price_4"))+ "</td>");
		html2.append("<td align='center'>" +__rsquery5.getString("unit_code")+ " ("+__rsquery5.getString("unit_code")+ ")</td>");
		String tax_name = "";
		switch (__rsquery5.getInt("tax_type")) {
			case 0 : tax_name = "แยกนอก"; break;
			case 2 : tax_name = "รวมใน"; break;
			case 1 : tax_name = "ศูนย์"; break;
		}
		html2.append("<td align='center'>" + tax_name + "</td>");
		html2.append("</tr>");
	}
	html2.append("</table>");
	//
	html2.append("<center><font size='+1'><b>ราคาโปรโมชั่น</b></font></center>");
	html2.append( "<table width='100%' border=1>");
	html2.append( "<tr>");
	html2.append( "<td align='center'><b>ประเภท</b></td>");
	html2.append( "<td align='center'><b>ราคาแยกภาษี</b></td>");
	html2.append( "<td align='center'><b>ราคารวมภาษี</b></td>");
	html2.append( "<td align='center'><b>จากจำนวน</b></td>");
	html2.append( "<td align='center'><b>ถึงจำนวน</b></td>");
	html2.append( "<td align='center'><b>เริ่มต้น</b></td>");
	html2.append( "<td align='center'><b>สิ้นสุด</b></td>");
	html2.append( "<td align='center'><b>หน่วยนับ</b></td>");
	html2.append("<td align='center'><b>กลุ่มลูกค้า</b></td>");
	html2.append( "</tr>");
        
	String queryx = "select case when sale_type=1 then 'สด' else 'เชื่อ' end as sale_type_name,from_qty,to_qty,sale_price1,sale_price2,from_date,to_date,unit_code,cust_group_1 from ic_inventory_price where ic_code='" + ic_code + "'";
	
        Statement __stmtquery6 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rsquery6 = __stmtquery6.executeQuery(queryx);
        
	while (__rsquery6.next()) {
		html2.append("<tr>");
		html2.append("<td align='center'>" +__rsquery6.getString("sale_type_name")+ "</td>");
		html2.append("<td align='center'>" +decim.format(__rsquery6.getInt("sale_price1"))+ "</td>");
		html2.append("<td align='center'>" +decim.format(__rsquery6.getInt("sale_price2"))+"</td>");
		html2.append("<td align='center'>" +decim.format(__rsquery6.getInt("from_qty"))+"</td>");
		html2.append("<td align='center'>" +decim.format(__rsquery6.getInt("to_qty"))+"</td>");
		html2.append("<td align='center'>" + __routine.dateThai(__rsquery6.getString("from_date")) + "</td>");
		html2.append("<td align='center'>" + __routine.dateThai(__rsquery6.getString("to_date")) +"</td>");
		html2.append("<td align='center'>" +__rsquery6.getString("unit_code")+ "</td>");
		html2.append("<td align='center'>" +__rsquery6.getString("cust_group_1")+ "</td>");
		html2.append("</tr>");
	}
	html2 .append("</table>");
	//
	String html_cost = "";
	String html_last_buy = "";
	html2 .append("</td>");
	html2 .append("<td valign='top'>");
	Integer buy_last_price = 0;
	String buy_last_date = "";
        
	String query_branch = "select distinct branch_code from ic_trans_detail where item_code='" +ic_code + "' and trans_flag=12 and last_status=0 order by branch_code";
        //response.getWriter().write(query_branch);
        Statement __stmtquery7 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rsquery7 = __stmtquery7.executeQuery(query_branch);
        
	while (__rsquery7.next()) {
            
               if(__rsquery7.getString("branch_code").equals("")==false){
                   
               
		// ราคาซื้อล่าสุด
		String queryc = "select price,doc_date,doc_time,doc_no,qty,(select name_1 from ic_unit where code=unit_code) as unit_name,cust_code,(select name_1 from ap_supplier where code=cust_code) as cust_name from ic_trans_detail where item_code='" +ic_code +"' and trans_flag=12 and last_status=0 and branch_code = '" +__rsquery7.getString("branch_code")+ "' order by doc_date desc,doc_time desc limit 1";
		Statement __stmtquery8 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet __rsquery8 = __stmtquery8.executeQuery(queryc);
		while (__rsquery8.next()) {
                 
			html_last_buy += "สาขา&nbsp;<b>" + __rsquery7.getString("branch_code") + "</b>&nbsp;ราคาซื้อล่าสุด&nbsp;:&nbsp;<b>" + decim.format(__rsquery8.getInt("price")) + "</b>&nbsp;" + "จำนวน&nbsp;<b>" +decim.format(__rsquery8.getInt("qty")) + "</b>&nbsp;" +__rsquery8.getString("unit_name")+ "&nbsp;วันที่&nbsp;:&nbsp;<b>" +_routine.dateThai(__rsquery8.getString("doc_date")) + "</b>&nbsp;เวลา&nbsp;:&nbsp;<b>"+__rsquery8.getString("doc_time")+ "</b>&nbsp;เลขที่เอกสาร&nbsp;:&nbsp;<b>" +__rsquery8.getString("doc_no")+ "</b>&nbsp;ผู้จำหน่าย&nbsp;:&nbsp;<b>" +__rsquery8.getString("cust_name")+ "&nbsp;("  +__rsquery8.getString("cust_code")+ ")</b><br/>";
	
                }
		// ราคาทุน
		String queryv = "select *,(select name_1 from ic_unit where code=ic_unit_code) as ic_unit_name from sml_ic_function_stock_balance_warehouse(date(now()),'" + ic_code + "',(select string_agg(code,',') from ic_warehouse where branch_code like '%" +__rsquery7.getString("branch_code")+ "%'))";
		Statement __stmtquery9 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet __rsquery9 = __stmtquery9.executeQuery(queryv);
		
		while (__rsquery9.next()) {
			html_cost += "สาขา&nbsp;<b>" + __rsquery7.getString("branch_code") + "</b>&nbsp;คลัง&nbsp;<b>" + __rsquery9.getString("warehouse") + "</b>&nbsp;ราคาต้นทุน&nbsp;<b>" +decim.format( __rsquery9.getInt("average_cost") )+"</b>&nbsp;บาท&nbsp;ยอดคงเหลือ&nbsp;<b>"+decim.format( __rsquery9.getInt("balance_qty") )+  "</b>&nbsp;"+ __rsquery9.getString("ic_unit_name") + "<br/>";
		}
                }
	}

	html2 .append(html_last_buy);
	html2 .append(html_cost);
	html2 .append("</td>");
	html2 .append("</tr>");
	html2 .append("</table>");
        }*/
         
         // ซื้อล่าสุด
        String querybuy = "select doc_date,doc_time,doc_no,branch_code,wh_code,shelf_code,(select name_1 from erp_branch_list where code=branch_code) as branch_name,cust_code,(select name_1 from ap_supplier where code=cust_code) as cust_name,unit_code,(select name_1 from ic_unit where code=unit_code) as unit_name,qty,price,COALESCE (discount,'')as discount,sum_amount from ic_trans_detail where item_code='" + ic_code + "' and trans_flag=12 and last_status=0 order by doc_date desc,doc_time desc,doc_no,line_number limit 100";
        // echo $query2;
        Statement __stmtquerybuy = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rsbuy = __stmtquerybuy.executeQuery(querybuy);
        count = 0;
        while (__rsbuy.next()) {
                if (count == 0) {
                        html2 .append("<table width='100%' border=1>");
                        html2 .append("<tr bgcolor='#EFF2FB'>");
                        html2 .append("<td colspan=12 align='right'><font size=+1><b>รายการซื้อล่าสุด</b></font></td>");
                        html2 .append("</tr>");
                        html2 .append("<tr bgcolor='#BCF5A9'>");
                        html2 .append("<td align='center'><b>วันที่เอกสาร</b></td>");
                        html2 .append("<td align='center'><b>เวลา</b></td>");
                        html2 .append("<td align='center'><b>เลขที่เอกสาร</b></td>");
                        html2 .append("<td align='center'><b>สาขา</b></td>");
                        html2 .append("<td align='center'><b>เจ้าหนี้</b></td>");
                        html2 .append("<td align='center'><b>คลัง</b></td>");
                        html2 .append("<td align='center'><b>ที่เก็บ</b></td>");
                        html2 .append("<td align='center'><b>จำนวน</b></td>");
                        html2 .append("<td align='center'><b>หน่วยนับ</b></td>");
                        html2 .append("<td align='center'><b>ราคา/หน่วย</b></td>");
                        html2 .append("<td align='center'><b>ส่วนลด</b></td>");
                        html2 .append("<td align='center'><b>มูลค่า</b></td>");
                        html2 .append("</tr>");
                }
                html2 .append("<tr>");
                html2 .append("<td align='center'>" +__routine.dateThai(__rsbuy.getString("doc_date"))+ "</td>");
                html2 .append("<td align='center'>"+__rsbuy.getString("doc_time")+ "</td>");
                html2 .append("<td>" +__rsbuy.getString("doc_no")+ "</td>");
                html2 .append("<td>" +__rsbuy.getString("branch_code")+ "&nbsp;("+ __rsbuy.getString("branch_name")+ ")</td>");
                html2 .append("<td>" +__rsbuy.getString("cust_code")+"&nbsp;(" +__rsbuy.getString("cust_name")+ ")</td>");
                html2 .append("<td>" +__rsbuy.getString("wh_code")+ "</td>");
                html2 .append("<td>" +__rsbuy.getString("shelf_code")+ "</td>");
                html2 .append("<td align='right'>" + decim.format(__rsbuy.getInt("qty"))+ "</td>");
                html2 .append("<td>" +__rsbuy.getString("unit_name")+ "&nbsp;(" +__rsbuy.getString("unit_code")+ ")</td>");
                html2 .append("<td align='right'>" + decim.format(__rsbuy.getInt("price"))) ;
                html2 .append("<td>" +__rsbuy.getString("discount")+ "</td>");
                html2 .append("<td align='right'>" +decim.format(__rsbuy.getInt("sum_amount"))+ "</td>");
                html2 .append("</tr>");
                count++;
        }
        if (count != 0) {
                html2 .append("</table>");
        }
        
        // ขายล่าสุด
        String querysale = "select doc_date,doc_time,doc_no,branch_code,wh_code,shelf_code,(select name_1 from erp_branch_list where code=branch_code) as branch_name,cust_code,(select name_1 from ar_customer where code=cust_code) as cust_name,unit_code,(select name_1 from ic_unit where code=unit_code) as unit_name,qty,price,COALESCE(discount,'')as discount,sum_amount from ic_trans_detail where item_code='" + ic_code + "' and trans_flag=44 and last_status=0 order by doc_date desc,doc_time desc,doc_no,line_number limit 100";
        // echo $query2;
        Statement __stmtquerysale = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rssale = __stmtquerysale.executeQuery(querysale);
        count = 0;
        while (__rssale.next()) {
                if (count == 0) {
                        html2.append("<table width='100%' border=1>");
                        html2.append("<tr bgcolor='#EFF2FB'>");
                        html2.append("<td colspan=12 align='right'><font size=+1><b>รายการขายล่าสุด</b></font></td>");
                        html2.append("</tr>");
                        html2.append("<tr bgcolor='#BCF5A9'>");
                        html2.append("<td align='center'><b>วันที่เอกสาร</b></td>");
                        html2.append("<td align='center'><b>เวลา</b></td>");
                        html2.append("<td align='center'><b>เลขที่เอกสาร</b></td>");
                        html2.append("<td align='center'><b>สาขา</b></td>");
                        html2.append("<td align='center'><b>เจ้าหนี้</b></td>");
                        html2.append("<td align='center'><b>คลัง</b></td>");
                        html2.append("<td align='center'><b>ที่เก็บ</b></td>");
                        html2.append("<td align='center'><b>จำนวน</b></td>");
                        html2.append("<td align='center'><b>หน่วยนับ</b></td>");
                        html2.append("<td align='center'><b>ราคา/หน่วย</b></td>");
                        html2.append("<td align='center'><b>ส่วนลด</b></td>");
                        html2.append("<td align='center'><b>มูลค่า</b></td>");
                        html2.append("</tr>");
                }
                html2.append("<tr>");
                html2.append("<td align='center'>" + __routine.dateThai(__rssale.getString("doc_date"))+ "</td>");
                html2.append("<td align='center'>" +__rssale.getString("doc_time")+ "</td>");
                html2.append("<td>"+__rssale.getString("doc_no")+  "</td>");
                html2.append("<td>"+__rssale.getString("branch_code")+ "&nbsp;(" +__rssale.getString("branch_name")+ ")</td>");
                html2.append("<td>" +__rssale.getString("cust_code")+  "&nbsp;("+__rssale.getString("cust_name")+ ")</td>");
                html2.append("<td>" +__rssale.getString("wh_code")+ "</td>");
                html2.append("<td>"+__rssale.getString("shelf_code")+ "</td>");
                html2.append("<td align='right'>" + decim.format(__rssale.getInt("qty"))+"</td>");
                html2.append("<td>" +__rssale.getString("unit_name")+ "&nbsp;(" +__rssale.getString("unit_code")+  ")</td>");
                html2.append("<td align='right'>" +decim.format(__rssale.getInt("price"))) ;
                html2.append("<td>" +__rssale.getString("discount")+"</td>");
                html2.append("<td align='right'>" +decim.format(__rssale.getInt("sum_amount"))+"</td>");
                html2.append("</tr>");
                count++;
        }
        if (count != 0) {
                html2.append("</table>");
        }


        
            

            __stmtquery2.close();
            __rsquery2.close();
            __rsBody.close();
            __stmtBody.close();

          
        } catch (SQLException e) {
            response.getWriter().write("error 1 :"+e.getMessage());
            return;
        
        } catch (Exception e) {
         response.getWriter().write("error 2 :"+e.getMessage());
         return;
        } finally {
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        response.getWriter().write(html2.toString()); // +"<HR COLOR=\"orange\" WIDTH=\"100%\">"
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
