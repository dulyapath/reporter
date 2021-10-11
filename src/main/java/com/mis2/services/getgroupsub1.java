package com.mis2.services;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils._global;
import utils._routine;
import org.json.*;

/**
 *
 * @author SML-DEV-PC5
 */
@WebServlet(urlPatterns = {"/getGroupsub"})
public class getgroupsub1 extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession __session = request.getSession();
        String __dbname = __session.getAttribute("dbname").toString();

        String keyword = request.getParameter("code");
        PrintWriter out = response.getWriter();
        _routine __routine = new _routine();
        StringBuilder __result = new StringBuilder();
        String __providerDatabaseName = __session.getAttribute("provider").toString();

        JSONArray jsarr = new JSONArray();
        try {
            Connection __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__providerDatabaseName));
            String __query = "select code,name_1 from ic_group_sub where main_group='" + keyword + "' order by code";
            Statement __stmtCount = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsCount = __stmtCount.executeQuery(__query);

            while (__rsCount.next()) {

                JSONObject obj = new JSONObject();

                obj.put("code", __rsCount.getString("code"));
                obj.put("name_1", __rsCount.getString("name_1"));

                jsarr.put(obj);

            }

            __rsCount.close();
            __stmtCount.close();
            __conn.close();
            response.getWriter().print(jsarr);
        } catch (Exception ex) {
            response.getWriter().write("" + ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        String __dbname = request.getParameter("dbname");
        String __doc_no = request.getParameter("doc_no");
        String __doc_date = request.getParameter("doc_date");
        String __code = request.getParameter("code");
        String __con_name = request.getParameter("con_name");
        String __con_wh = request.getParameter("con_wh");
        String __con_loca = request.getParameter("con_loca");
        String __con_boro = request.getParameter("con_boro");
        String __con_retu = request.getParameter("con_retu");
        String __con_broken = request.getParameter("con_broken");
        String __cust_code = request.getParameter("cust_code");
        String __con_unit = request.getParameter("con_unit");
        String __con_price = request.getParameter("con_price");
        String __con_stand = request.getParameter("con_stand");
        String __con_divide = request.getParameter("con_divide");

        _routine __routine = new _routine();
        StringBuilder __result = new StringBuilder();
        String __providerDatabaseName = __dbname.toLowerCase();
        try {
            String __query = "select doc_no from ic_package_trans_detail_temp where doc_no='" + __doc_no + "' and item_code='" + __code + "' and item_unit='" + __con_unit + "'";
            Connection __conn = __routine._connect(__providerDatabaseName);
            Statement __stmtCount = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsCount = __stmtCount.executeQuery(__query);
            __rsCount.next();

            int row = __rsCount.getRow();

            if (row <= 0) {
                String __query2 = "insert into ic_package_trans_detail_temp (trans_flag,trans_type,doc_no,doc_date,cust_code,item_code,item_name,item_unit,qty,stand_value,divide_value,item_price,wh_code,sh_code,sendback,broken) "
                        + "values (212,2,'" + __doc_no + "','" + __doc_date + "','" + __cust_code + "','" + __code + "','" + __con_name + "','" + __con_unit + "','" + __con_boro + "','" + __con_stand + "','" + __con_divide + "','" + __con_price + "','" + __con_wh + "','" + __con_loca + "','" + __con_retu + "','" + __con_broken + "')";
                try {
                    Statement __stmtCount2 = __conn.createStatement();
                    __stmtCount2.executeUpdate(__query2);

                    __stmtCount2.close();
                } catch (Exception ex) {
                    response.getWriter().write("fail");
                }
                response.getWriter().write(__doc_no);
            } else {
                response.getWriter().write("dul");
            }

            __rsCount.close();
            __stmtCount.close();
            __conn.close();

        } catch (SQLException | IOException __ex) {
            response.getWriter().write("fail : " + __ex.getMessage());
        }

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
