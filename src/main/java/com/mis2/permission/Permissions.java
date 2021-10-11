package com.mis2.permission;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import utils.ResponeUtil;
import utils._global;
import utils._routine;

@WebServlet(name = "perm-list", urlPatterns = {"/perm-list"})
public class Permissions extends HttpServlet {

    private String strProviderCode;
    private String strDatabaseName;
    private String strUserCode;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject objResult = new JSONObject("{'success': false}");
        HttpSession session = request.getSession();

        strProviderCode = session.getAttribute("provider").toString();
        strDatabaseName = session.getAttribute("dbname").toString();
        strUserCode = session.getAttribute("user").toString().toUpperCase();

        if (!request.getParameterMap().containsKey("action_name")) {

        } else {
            Connection conn = null;
            _routine routine = new _routine();
            try {
                conn = routine._connect("smlerpmain" + strProviderCode, _global.FILE_CONFIG(strProviderCode));
                String strActionName = (request.getParameter("action_name") != null && !request.getParameter("action_name").isEmpty()) ? request.getParameter("action_name") : "";
                switch (strActionName) {
                    case "":
                        break;
                    case "get_perm":
                        objResult = this.loadPermissions(conn, ResponeUtil.str2Json(request.getParameter("data")));
                        break;
                }
            } catch (Exception ex) {
                objResult.put("err_title", "error");
                objResult.put("err_msg", ex.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Permissions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        response.getWriter().print(objResult);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private JSONObject loadPermissions(Connection conn, JSONObject params) throws SQLException {
        JSONObject objTmp = new JSONObject("{'success': false}");
        String strPageCode = !params.isNull("page_code") && !params.getString("page_code").trim().isEmpty() ? params.getString("page_code") : "";
        String strQuery = "SELECT "
                + "  COALESCE(is_read,false) AS is_read"
                + " ,COALESCE(is_create,false) AS is_create"
                + " ,COALESCE(is_update,false) AS is_update"
                + " ,COALESCE(is_delete,false) AS is_delete"
                + " ,COALESCE(is_re_print,false) AS is_re_print, p_code, user_code FROM sml_user_permission WHERE UPPER(user_code) = '" + strUserCode + "' AND p_code = '" + strPageCode + "'";
        PreparedStatement stmt1 = conn.prepareStatement(strQuery);
        ResultSet rsData1 = stmt1.executeQuery();
        strQuery = "SELECT "
                + " COALESCE(g_r_status,false) AS is_read"
                + ",COALESCE(g_a_status,false) AS is_create"
                + ",COALESCE(g_e_status,false) AS is_update"
                + ",COALESCE(g_d_status,false) AS is_delete"
                + ",COALESCE(g_p_status,false) AS is_re_print"
                + " FROM sml_user_and_group "
                + " INNER JOIN sml_group_permission ON sml_user_and_group.group_code = sml_group_permission.g_code "
                + " WHERE sml_group_permission.p_code = '" + strPageCode + "' "
                + " AND UPPER(sml_user_and_group.user_code) = '" + strUserCode + "' ";
        PreparedStatement stmt2 = conn.prepareStatement(strQuery);
        ResultSet rsData2 = stmt2.executeQuery();
        objTmp.put("data1", rsData1);
        objTmp.put("data2", rsData2);
        objTmp.put("success", true);
        return objTmp;
    }

}
