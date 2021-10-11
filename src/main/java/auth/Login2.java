package auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import utils._global;
import utils._routine;

@WebServlet(name = "auth-login2", urlPatterns = {"/auth-login2"})
public class Login2 extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, must-revalidate"); // HTTP 1.1.
        response.setHeader("Cache-Control", "no-store"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(new JSONObject());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, must-revalidate"); // HTTP 1.1.
        response.setHeader("Cache-Control", "no-store"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject objResult = new JSONObject("{'success': false}");
        
            String __providerCode = "";
            String __userCode = "";
            String __password = "";

            

            if (request.getParameter("provider") != null) {
                __providerCode = request.getParameter("provider");
            }
            if (request.getParameter("user") != null) {
                __userCode = request.getParameter("user").toUpperCase();
            }
            if (request.getParameter("pass") != null) {
                __password = request.getParameter("pass");
            }

        boolean providerFound = false;
        try {
            Boolean __pass = false;
            _routine __routine = new _routine();
            String __providerDatabaseName = "smlerpmain" + __providerCode.toLowerCase();
            {
//                String fileConfig = "SMLConfig" + __providerCode.toUpperCase() + ".xml";
//                Connection __conn = __routine._connect(__providerDatabaseName, fileConfig);
                Connection __conn = __routine._connect(__providerDatabaseName, _global.FILE_CONFIG(__providerCode));

                if (__conn != null) {
                    String __query = "";
                    __query = "select user_code, user_name from sml_user_list where upper(user_code)='" + __userCode + "' and user_password='" + __password + "'";

                    Statement __stmt = __conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

                    ResultSet __rs = __stmt.executeQuery(__query);
                    while (__rs.next()) {
                        __pass = true;

                        HttpSession __session = request.getSession();
                        __session.setAttribute("provider", __providerCode.toLowerCase());
                        __session.setAttribute("user", __userCode);
                        __session.setAttribute("user_name", __rs.getString("user_name"));
                        __session.setMaxInactiveInterval(30 * 60);
                    }
                    __rs.close();
                    __stmt.close();
                    __conn.close();

                    providerFound = true;
                } else {
                    providerFound = false;
                    objResult.put("msg", "ไม่พบรหัสกิจการ");
                }
            }
            if (providerFound) {
                if (__pass) {
                    Cookie _cookieProvide = new Cookie("provider", __providerCode);
                    Cookie _cookieUser = new Cookie("user", __userCode);
                    _cookieProvide.setMaxAge(86400 * 365);
                    _cookieUser.setMaxAge(86400 * 365);

                    response.addCookie(_cookieProvide);
                    response.addCookie(_cookieUser);

                    JSONArray dataList = new JSONArray();

                    Connection __conn = __routine._connect(__providerDatabaseName);
                    String __query = "select data_code from sml_database_list where upper(data_code) in (select upper(data_code) from sml_database_list_user_and_group where user_or_group_status=0 and upper(user_or_group_code)='" + __userCode + "') or upper(data_code) in (select upper(data_code) from sml_database_list_user_and_group where user_or_group_status=1 and upper(user_or_group_code) in (select upper(group_code) from sml_user_and_group where upper(user_code)='" + __userCode + "')) order by data_name";

                    Statement __stmt = __conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                    ResultSet __rs = __stmt.executeQuery(__query);
                    while (__rs.next()) {

                        String lastDatabaseTest = "";
                        try {
                            String __databaseCode = __rs.getString("data_code").toLowerCase();
                            lastDatabaseTest = __databaseCode;
                            Connection __conn2 = __routine._connect(__databaseCode);
                            Statement __stmt2 = __conn2.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                            ResultSet __rs2 = __stmt2.executeQuery("select company_name_1 from erp_company_profile");
                            while (__rs2.next()) {
                                JSONObject tmpObj = new JSONObject();
                                tmpObj.put("db_code", __databaseCode);
                                tmpObj.put("company_name", __rs2.getString("company_name_1"));

                                dataList.put(tmpObj);
                            }

                            __rs2.close();
                            __stmt2.close();
                            __conn2.close();
                        } catch (SQLException ex) {
                            System.out.println("\"" + lastDatabaseTest + "\" connect failed.");
                        } catch (Exception getDbEx) {
                            objResult.put("msg", getDbEx.getMessage());
                        }
                    }
                    objResult.put("data", dataList);
                    objResult.put("success", true);

                    __rs.close();
                    __stmt.close();
                    __conn.close();

                } else {
                    objResult.put("msg", "ชื่อผู้ใช้งาน หรือ รหัสผ่านไม่ถูกต้อง");
                }
            }
        } catch (SQLException __ex) {
            objResult.put("msg", __ex.getMessage());
        }
        response.getWriter().print(objResult);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
