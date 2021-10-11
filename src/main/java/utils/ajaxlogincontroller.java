package utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.Cookie;
import org.json.JSONObject;
//import utils._routine;

@WebServlet(urlPatterns = {"/ajaxlogincontroller"})
public class ajaxlogincontroller extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("x-frame-options", "DENY");
        response.setHeader("Cache-Control", "no-cache, must-revalidate"); // HTTP 1.1.
        response.setHeader("Cache-Control", "no-store"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(new JSONObject());
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
        response.addHeader("x-frame-options", "DENY");
        response.setHeader("Cache-Control", "no-cache, must-revalidate"); // HTTP 1.1.
        response.setHeader("Cache-Control", "no-store"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession __session = request.getSession();

        String __providerCode = "";
        String __userCode = "";
        String __password = "";

        JSONObject objResult = new JSONObject("{'success': false}");

        if (request.getParameter("provider") != null) {
            __providerCode = request.getParameter("provider");
        }
        if (request.getParameter("user") != null) {
            __userCode = request.getParameter("user").toUpperCase();
        }
        if (request.getParameter("pass") != null) {
            __password = request.getParameter("pass");
        }

        //System.out.println(__providerCode + ',' + __userCode + ',' + __password);
        boolean providerFound = false;
        try {
            Boolean __pass = false;
            _routine __routine = new _routine();
            String __providerDatabaseName = "smlerpmain" + __providerCode.toLowerCase();
            {
                String fileConfig = "SMLConfig" + __providerCode.toUpperCase() + ".xml";
                Connection __conn = __routine._connect(__providerDatabaseName, fileConfig);
                if (__conn != null) {
                    String __query = "select user_code, user_name from sml_user_list where upper(user_code)=? and user_password=? ";

                    PreparedStatement __stmt = __conn.prepareStatement(__query);
                    __stmt.setString(1, __userCode);
                    __stmt.setString(2, __password);

                    ResultSet __rs = __stmt.executeQuery();
                    while (__rs.next()) {
                        __pass = true;

                        __session.setAttribute("user", __userCode);
                        __session.setAttribute("provider", __providerCode.toLowerCase());
                        __session.setAttribute("user_name", __rs.getString("user_name"));
                        __session.setMaxInactiveInterval(30 * 60);
                    }
                    __rs.close();
                    __stmt.close();
                    __conn.close();

                    providerFound = true;
                } else {
                    providerFound = false;
                    objResult.put("msg", "การเชื่อมต่อล้มเหลว กรุณาตรวจสอบรหัสกิจการ หรือ server");
                }

            }
            if (providerFound) {

                if (__pass) {
                    String sessionid = request.getSession().getId();
                    Cookie _cookiejsession = new Cookie("MISCOOKIE", sessionid);

                    Cookie _cookieProvide = new Cookie("provider", __providerCode);

                    Cookie _cookieUser = new Cookie("user", __userCode);

                    _cookieProvide.setMaxAge(86400 * 365);
                    _cookieUser.setMaxAge(86400 * 365);

                    response.addCookie(_cookiejsession);
                    response.addCookie(_cookieProvide);
                    response.addCookie(_cookieUser);
                    StringBuilder __html = new StringBuilder();
                    __html = __html.append("<table class='table table-bordered' width='100%' >");
                    Connection __conn = __routine._connect(__providerDatabaseName);
                    String __query = "select data_code from sml_database_list where upper(data_code) in (select upper(data_code) from sml_database_list_user_and_group where user_or_group_status=0 and upper(user_or_group_code)=?) or upper(data_code) in (select upper(data_code) from sml_database_list_user_and_group where user_or_group_status=1 and upper(user_or_group_code) in (select upper(group_code) from sml_user_and_group where upper(user_code)=?)) order by data_name";
                    // "select data_code from sml_database_list_user_and_group where upper(user_or_group_code)='" + __userCode + "' and user_or_group_status=0";
                    PreparedStatement __stmt = __conn.prepareStatement(__query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    __stmt.setString(1, __userCode);
                    __stmt.setString(2, __userCode);
                    ResultSet __rs = __stmt.executeQuery();

                    int i = 0;
                    String __databaseCode = "";
                    while (__rs.next()) {
                        try {
                            __databaseCode = __rs.getString("data_code").toLowerCase();
                            Connection __conn2 = __routine._connect(__databaseCode);
                            PreparedStatement __stmt2 = __conn2.prepareStatement("select company_name_1 from erp_company_profile", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            ResultSet __rs2 = __stmt2.executeQuery();

                            while (__rs2.next()) {

                                __html = __html.append("<tr onclick=\"window.location.href = \'verify.jsp?database=" + __databaseCode + "\'\" style='cursor: pointer;'>");
                                __html = __html.append("<td align='center'><h5>" + i + "</h5></td>");
                                __html = __html.append("<td>");
                                __html = __html.append("<h5>" + __databaseCode + "</h5>");
                                __html = __html.append("</td>");
                                __html = __html.append("<td>");
                                __html = __html.append("<h5>" + __rs2.getString("company_name_1") + "</h5>");
                                __html = __html.append("</td>");
                                __html = __html.append("</tr>");

                                i = i + 1;
                            }

                            __rs2.close();
                            __stmt2.close();
                            __conn2.close();
                        } catch (Exception getDbEx) {
                            objResult.put("msg", "\"" + getDbEx + "\" connect failed.");
                        }

                    }

                    if (i > 1) {
                        objResult.put("data", __html.toString());
                        objResult.put("type", "1");
                    } else {
                        if (__databaseCode != "") {
                            objResult.put("type", "2");
                            objResult.put("data", __databaseCode);
                        }

                    }

                    __rs.close();
                    __stmt.close();
                    __conn.close();

                    objResult.put("success", true);

                } else {
                    objResult.put("msg", " connect failed.");
                    objResult.put("success", false);
                }
            }
        } catch (SQLException __ex) {
            objResult.put("msg", __ex.getMessage());
        }

        response.getWriter().print(objResult);
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
