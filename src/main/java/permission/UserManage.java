/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package permission;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import utils._global;
import utils._routine;

/**
 *
 * @author sml-dev-pcz
 */
@WebServlet(name = "UserManage", urlPatterns = {"/user-manage"})
public class UserManage extends HttpServlet {

    private String __dbname = "";
    private String __user = "";
    private String __provider = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject objResult = new JSONObject();
        objResult.put("success", false);

        HttpSession _sess = request.getSession();
        this.__user = _sess.getAttribute("user").toString();
        this.__dbname = _sess.getAttribute("dbname").toString().toLowerCase();
        this.__provider = _sess.getAttribute("provider").toString().toLowerCase();

        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {
            objResult.put("msg", "Please login");
            objResult.put("dev_msg", "Please login");
            response.getWriter().print(objResult);
            return;
        }
        String m = "";

        if (request.getParameter("m") == null || request.getParameter("m").isEmpty()) {
            objResult.put("msg", "การเข้าถึงไม่ถูกต้อง");
            objResult.put("dev_msg", "action not found");
            response.getWriter().print(objResult);
            return;
        }

        m = request.getParameter("m");
        _routine __routine = new _routine();
        Connection __conn = null;
        try {

            if (m.equals("save")) {
                objResult = permissSave(request);
            } else if (m.equals("permiss-list")) {
                objResult = permissList(request);
            } else if (m.equals("permiss-user")) {
                objResult = permissUser(request);
            }
        } catch (SQLException e) {
            objResult.put("msg", e.getMessage());
        }

        response.getWriter().print(objResult);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        processRequest(request, response);
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

    private JSONObject _Query(String __sql, int mode) {
        JSONObject objResult = new JSONObject();
        objResult.put("success", false);
        if (!__sql.isEmpty()) {
            try {

                _routine __routine = new _routine();
                Connection __conn = __routine._connect(__dbname);

                Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                if (mode == 0) {
                    __stmt.executeUpdate(__sql);
                } else {
                    ResultSet __rs = __stmt.executeQuery(__sql);
                    ResultSetMetaData _rsmd = __rs.getMetaData();
                    int _colCount = _rsmd.getColumnCount();

                    JSONArray _rsItems = new JSONArray();
                    while (__rs.next()) {
                        JSONObject _item = new JSONObject();
                        for (int __i = 1; __i <= _colCount; __i++) {
                            String _colName = _rsmd.getColumnName(__i);
                            _item.put(_colName, __rs.getObject(__i));
                        }
                        _rsItems.put(_item);
                    }
                    objResult.put("data", _rsItems);
                }
                objResult.put("success", true);
            } catch (SQLException e) {
                objResult.put("msg", "เกิดข้อผิดพลาดจากฐานข้อมูล");
                objResult.put("dev_msg", "SQLException :: " + e.getMessage());
            } catch (Exception e) {
                objResult.put("msg", "เกิดข้อผิดพลาด");
                objResult.put("dev_msg", "Exception :: " + e.getMessage());
            }
        }
        return objResult;
    }

    private JSONArray query2Array(ResultSet __rs) throws SQLException {
        ResultSetMetaData _rsMeta = __rs.getMetaData();
        int _colBodyCount = _rsMeta.getColumnCount();
        JSONArray allData = new JSONArray();
        while (__rs.next()) {
            JSONObject tmpBody = new JSONObject();
            for (int __i = 1; __i <= _colBodyCount; __i++) {
                String _colBodyName = _rsMeta.getColumnName(__i);
                tmpBody.put(_colBodyName, __rs.getObject(__i));
            }
            allData.put(tmpBody);
        }
        return allData;
    }

    private JSONObject permissSave(HttpServletRequest request) throws SQLException {
        JSONObject resultObj = new JSONObject("{'success': false}");

        _routine __routine = new _routine();
        Connection __conn = null;

        String userCode = request.getParameter("user_code").toString();
        String userAccess = request.getParameter("user_access").toString();
        String is = request.getParameter("is").toString().toLowerCase();
        int val = 0;

        try {
            val = Integer.parseInt(request.getParameter("val").toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String col = "";
        if (is.equals("read")) {
            col = "is_read";
        } else if (is.equals("create")) {
            col = "is_create";
        } else if (is.equals("edit")) {
            col = "is_update";
        } else if (is.equals("del")) {
            col = "is_delete";
        }

        if (!col.isEmpty()) {
            try {
                __conn = __routine._connect("smlerpmain" + __provider);

                String countQuery = "SELECT user_code  FROM sml_user_web WHERE web_flag = " + _global._WEB_FLAG + " AND user_access = '" + userAccess + "'  AND upper(user_code) = upper('" + userCode + "')";
                Statement stmtCount = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet _rsCount = stmtCount.executeQuery(countQuery);

                boolean permissExist = false;
                while (_rsCount.next()) {
                    permissExist = true;
                }

                String sql = "";
                if (permissExist) {
                    sql = "UPDATE sml_user_web SET " + col + " = " + val + "  WHERE user_access = '" + userAccess + "' AND upper(user_code) = upper('" + userCode + "')";

                } else {
                    sql = "INSERT INTO sml_user_web (web_flag, user_code,user_access," + col + ") VALUES (" + _global._WEB_FLAG + ", upper('" + userCode + "'),'" + userAccess + "'," + val + ")";
                }

                Statement stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                stmt.executeUpdate(sql);
                resultObj.put("success", true);
            } catch (SQLException e) {
                e.printStackTrace();
                resultObj.put("msg", e.getMessage());
            } finally {
                if (__conn != null) {
                    try {
                        __conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return resultObj;
    }

    private JSONObject permissList(HttpServletRequest request) throws SQLException {

        JSONObject resultObj = new JSONObject("{'success': false}");
        String s = "";

        _routine __routine = new _routine();
        Connection __conn = null;

        try {
            s = request.getParameter("s").toString();
        } catch (NullPointerException e) {
//            e.printStackTrace();
        };

        try {
            __conn = __routine._connect("smlerpmain" + __provider);
            String sql = "SELECT id,key_code,access_name,parent_id FROM sml_web_access WHERE is_disable <> 1 AND web_flag = " + _global._WEB_FLAG + " AND access_name LIKE '%" + s + "%' order by id asc";
            Statement stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet _rs = stmt.executeQuery(sql);

            resultObj.put("data", query2Array(_rs));
            resultObj.put("success", true);
        } catch (SQLException e) {
            e.printStackTrace();
            resultObj.put("msg", e.getMessage());
        } finally {
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resultObj;
    }

    private JSONObject permissUser(HttpServletRequest request) throws SQLException {
        JSONObject resultObj = new JSONObject("{'success': false}");
        String userCode = request.getParameter("user_code").toString();

        _routine __routine = new _routine();
        Connection __conn = null;

        try {
            __conn = __routine._connect("smlerpmain" + __provider);

            String sql = "SELECT user_code,user_access,is_create,is_read,is_update,is_delete FROM sml_user_web WHERE web_flag = " + _global._WEB_FLAG + " AND upper(user_code) = upper('" + userCode + "')";

            Statement stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet _rs = stmt.executeQuery(sql);

            resultObj.put("data", query2Array(_rs));
            resultObj.put("success", true);
        } catch (SQLException e) {
            e.printStackTrace();
            resultObj.put("msg", e.getMessage());
        } finally {
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    resultObj.put("msg", "database connect close :: " + e.getMessage());
                }
            }
        }

        return resultObj;
    }
}
