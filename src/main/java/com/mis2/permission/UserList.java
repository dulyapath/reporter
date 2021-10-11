package com.mis2.permission;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils._global;
import utils._routine;

@WebServlet(name = "userlist-list", urlPatterns = {"/userlist-list"})
public class UserList extends HttpServlet {

    private JSONObject objResult;
    private String __DBNAME;
    private String __PROVIDER;
    private String __USERCODE;
    private Integer _totalPerPages;
    private Integer _pageNO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.objResult = new JSONObject();
        this.objResult.put("success", false);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession __SESSION = request.getSession();

        if (__SESSION.getAttribute("dbname") != null) {
            this.__DBNAME = "smlerpmain" + __SESSION.getAttribute("provider").toString().toLowerCase();
        }

        if (__SESSION.getAttribute("provider") != null) {
            this.__PROVIDER = __SESSION.getAttribute("provider").toString();
        }

        if (__SESSION.getAttribute("user") != null) {
            this.__USERCODE = __SESSION.getAttribute("user").toString().toUpperCase();
        }

        String _actionName = "";
        if (request.getParameter("action_name") != null) {
            _actionName = request.getParameter("action_name");
        }

        String _keyID = "";
        if (request.getParameter("key_id") != null) {
            _keyID = request.getParameter("key_id");
        }

        String _keyCode = "";
        if (request.getParameter("key_code") != null) {
            _keyCode = request.getParameter("key_code");
        }

        String _updateStatus = "";
        if (request.getParameter("update_status") != null) {
            _updateStatus = request.getParameter("update_status");
        }

        String _updateAction = "";
        if (request.getParameter("update_action") != null) {
            _updateAction = request.getParameter("update_action");
        }

        String _queryExtend = "";
        if (request.getParameter("page_no") != null && request.getParameter("total_page") != null) {
            this._pageNO = Integer.parseInt(request.getParameter("page_no"));
            this._totalPerPages = Integer.parseInt(request.getParameter("total_page"));

            int _startPostion;
            if (this._pageNO > 0) {
                _startPostion = (this._pageNO - 1) * this._totalPerPages;

                _queryExtend += " LIMIT " + this._totalPerPages + " OFFSET " + _startPostion;
            }
        }

        Connection __CONN = null;
        String strQUERY;
        try {
            _routine __ROUTINE = new _routine();
            __CONN = __ROUTINE._connect(this.__DBNAME, _global.FILE_CONFIG(__PROVIDER));

            switch (_actionName) {
                case "get_userlist":
                    displayUserList(__CONN, this.__USERCODE, _queryExtend);
                    break;
                case "find_userlist":
                    displayUserPerm(__CONN, _keyID);
                    break;
                case "get_web_pages":
                    strQUERY = "SELECT page_code FROM sml_web_page WHERE web_flag = '" + _global._WEB_FLAG + "' ORDER BY page_code";
                    findData(__CONN, strQUERY);
                    break;
                case "update_permission":
                    updateUserPermission(__CONN, _keyID, _keyCode, _updateStatus, _updateAction);
                    break;
                case "find_permission_user":
                    strQUERY = "SELECT is_read, is_create, is_update, is_delete FROM sml_permission_user_list WHERE UPPER(user_code)='" + this.__USERCODE + "' AND page_code='" + _keyID + "' AND web_flag='" + _global._WEB_FLAG + "'";
                    findData(__CONN, strQUERY);
                    break;
            }
        } catch (JSONException JSONex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", JSONex.getMessage());
        } finally {
            if (__CONN != null) {
                try {
                    __CONN.close();
                } catch (SQLException SQLex) {
                    objResult.put("err_title", "ข้อความระบบ");
                    objResult.put("err_msg", SQLex.getMessage());
                }
            }
        }

        response.getWriter().print(objResult);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void displayUserList(Connection connection, String userCode, String strQueryExtend) {
        String __strQUERY = "SELECT user_code, user_name FROM sml_user_list ";
        String __rsHTML = "";

        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(__strQUERY );
            __rsData.next();
            Integer __rowCOUNT = __rsData.getRow();

            if (__rowCOUNT > 0) {
                __rsData.previous();
                Integer __rowNumber = 1;
                while (__rsData.next()) {
                    __rsHTML += "<tr>";
                    __rsHTML += "<td><h5><strong>" + __rowNumber + "</strong></h5></td>";
                    __rsHTML += "<td><h5>" + __rsData.getString("user_code") + " ~ " + __rsData.getString("user_name") + "</h5></td>";
                    __rsHTML += "<td><button class='btn btn-primary btn-block' id='btn-manage' key_id='" + __rsData.getString("user_code") + "'>จัดการสิทธิ์</button></td>";
                    __rsHTML += "</tr>";

                    __rowNumber++;
                }
            } else {
                __rsHTML = "<tr><td colspan='3'>ไม่พบข้อมูล.</td></tr>";
            }

            objResult.put("success", true);
            objResult.put("data", __rsHTML);

           

        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }

    }

    private void displayPagination(Connection connection, String __strQUERY) {
        String __rsHTML;

        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(__strQUERY);
            __rsData.last();
            int _totalRecords = __rsData.getRow();
            int _currentPages = 1;
            int _totalPages = 0;

            if (_totalRecords > 0) {
                _totalPages = (int) Math.ceil(_totalRecords / this._totalPerPages);
            }

            if (_totalPages == 0) {
                _totalPages = 1;
            } else {
                Double _totalPages2 = Double.parseDouble(String.valueOf(_totalRecords)) / Double.parseDouble(String.valueOf(this._totalPerPages));
                DecimalFormat df = new DecimalFormat();
                df.applyPattern("0.00");
                String[] arrTotalPages2 = String.valueOf(df.format(_totalPages2)).split("\\.");

                if (Integer.parseInt(arrTotalPages2[1]) > 0) {
                    _totalPages += 1;
                }
            }

            if (_pageNO > 1) {
                _currentPages = _pageNO;
            }

            if (_currentPages != 1) {
                __rsHTML = "<li><a href='#' page_no ='" + (_currentPages - 1) + "' class='pagination_link'><span>&laquo;</span></></li>";
            } else {
                __rsHTML = "<li class='disabled pagination_link'><a href='#'><span>&laquo;</span></></li>";
            }

            for (int i = 1; i <= _totalPages; i++) {
                if (i == _currentPages) {
                    __rsHTML += "<li><a href='#' page_no='" + i + "' style='color: red;' class='pagination_link'><span>" + i + "</span></a></li>";
                } else {
                    __rsHTML += "<li><a href='#' page_no='" + i + "' class='pagination_link'><span>" + i + "</span></a></li>";
                }
            }

            if (_currentPages != _totalPages) {
                __rsHTML += "<li><a href='#' page_no='" + (_currentPages + 1) + "' class='pagination_link'><span>&raquo;</span></a></li>";
            } else {
                __rsHTML += "<li class='disabled pagination_link'><a href='#'><span>&raquo;</span></a></li>";
            }

            objResult.put("success", true);
            objResult.put("data_pagination", __rsHTML);

        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }
    }

    private void displayUserPerm(Connection connection, String userCode) {
        String __strQUERY = "SELECT WP.page_code, WP.page_name , "
                + " COALESCE ((SELECT is_read FROM sml_permission_user_list AS UP WHERE WP.page_code=UP.page_code AND UPPER(UP.user_code)='" + userCode.toUpperCase() + "'), 'f') AS is_read, "
                + " COALESCE ((SELECT is_create FROM sml_permission_user_list AS UP WHERE WP.page_code=UP.page_code AND UPPER(UP.user_code)='" + userCode.toUpperCase() + "'), 'f') AS is_create, "
                + " COALESCE ((SELECT is_update FROM sml_permission_user_list AS UP WHERE WP.page_code=UP.page_code AND UPPER(UP.user_code)='" + userCode.toUpperCase() + "'), 'f') AS is_update, "
                + " COALESCE ((SELECT is_delete FROM sml_permission_user_list AS UP WHERE WP.page_code=UP.page_code AND UPPER(UP.user_code)='" + userCode.toUpperCase() + "'), 'f') AS is_delete, "
                + " COALESCE ((SELECT roworder FROM sml_permission_user_list AS UP WHERE WP.page_code=UP.page_code AND UPPER(UP.user_code)='" + userCode.toUpperCase() + "'), 0) AS roworder "
                + " FROM sml_web_page AS WP where wp.page_code like '%R00%' ORDER BY WP.page_code";
        String __rsHTML = "";

        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(__strQUERY);
            __rsData.next();
            Integer __rowCOUNT = __rsData.getRow();

            if (__rowCOUNT > 0) {
                __rsData.previous();
                Integer __rowNumber = 1;
                while (__rsData.next()) {

                    Boolean is_read = __rsData.getBoolean("is_read");
                    Boolean is_create = __rsData.getBoolean("is_create");
                    Boolean is_update = __rsData.getBoolean("is_update");
                    Boolean is_delete = __rsData.getBoolean("is_delete");

                    __rsHTML += "<tr>";
                    __rsHTML += "<td><h5><strong>" + __rowNumber + "</strong></h5></td>";
                    __rsHTML += "<td><h5>" + __rsData.getString("page_code") + " ~ " + __rsData.getString("page_name") + "</h5></td>";

                    if (is_read) {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_read' key_id='" + __rsData.getString("page_code") + "' checked></h5></td>";
                    } else {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_read' key_id='" + __rsData.getString("page_code") + "'></h5></td>";
                    }

                  /*  if (is_create) {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_create' key_id='" + __rsData.getString("page_code") + "' checked></h5></td>";
                    } else {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_create' key_id='" + __rsData.getString("page_code") + "'></h5></td>";
                    }

                    if (is_update) {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_update' key_id='" + __rsData.getString("page_code") + "' checked></h5></td>";
                    } else {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_update' key_id='" + __rsData.getString("page_code") + "'></h5></td>";
                    }

                    if (is_delete) {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_delete' key_id='" + __rsData.getString("page_code") + "' checked></h5></td>";
                    } else {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_delete' key_id='" + __rsData.getString("page_code") + "'></h5></td>";
                    }*/
                    __rsHTML += "</tr>";

                    __rowNumber++;
                }
            } else {
                __rsHTML = "<tr><td colspan='3'>ไม่พบข้อมูล.</td></tr>";
            }

            __stmt.close();
            __rsData.close();

            objResult.put("success", true);
            objResult.put("data", __rsHTML);

        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }
    }

    private void findData(Connection connection, String strQUERY) {
        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(strQUERY);
            ResultSetMetaData __rsMData = __rsData.getMetaData();
            int __colCOUNT = __rsMData.getColumnCount();

            JSONArray arrJSList = new JSONArray();

            while (__rsData.next()) {
                JSONObject objData = new JSONObject();
                for (int i = 1; i <= __colCOUNT; i++) {
                    String __colNAME = __rsMData.getColumnName(i);
                    objData.put(__colNAME, __rsData.getObject(i));
                }
                arrJSList.put(objData);
            }

            __rsData.close();
            __stmt.close();

            this.objResult.put("success", true);
            this.objResult.put("data", arrJSList);
        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }
    }

    private void updateUserPermission(Connection connection, String strPageCode, String strUserCode, String strUpdateStatus, String strUpdateAction) {
        String __strQUERY = "UPDATE sml_permission_user_list SET " + strUpdateAction + "='" + strUpdateStatus + "' WHERE UPPER(user_code)='" + strUserCode.toUpperCase() + "' AND page_code = '" + strPageCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ";
        try {
            Statement __stmt0 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData0 = __stmt0.executeQuery("SELECT page_code FROM sml_permission_user_list WHERE UPPER(user_code)='" + strUserCode.toUpperCase() + "' AND page_code = '" + strPageCode + "'");
            __rsData0.next();
            int __rowCOUNT = __rsData0.getRow();

            if (__rowCOUNT <= 0) {
                Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                __stmt.executeUpdate("INSERT INTO sml_permission_user_list (page_code, user_code, is_read, is_create, is_update, is_delete, web_flag) VALUES ('" + strPageCode + "', '" + strUserCode.toUpperCase() + "','f','f','f','f','" + _global._WEB_FLAG + "')");
                __stmt.close();
            }
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt.executeUpdate(__strQUERY);
            __stmt.close();
            this.objResult.put("success", true);
        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
            objResult.put("err_sql", __strQUERY);
        }
    }

    private void showLog(String value) {
        System.out.println(value);
    }

}
