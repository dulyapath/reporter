package com.mis2.permission;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.json.JSONObject;
import utils._global;
import utils._routine;

@WebServlet(name = "usergroup-list", urlPatterns = {"/usergroup-list"})
public class UserGroup extends HttpServlet {

    private JSONObject objResult;
    private String __DBNAME;
    private String __PROVIDER;
    private String __USERCODE;
    private Integer _totalPerPages = 0;
    private Integer _pageNO = 0;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UserGroup</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UserGroup at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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

        String _groupCode = "";
        if (request.getParameter("group_code") != null) {
            _groupCode = request.getParameter("group_code");
        }

        String _groupName = "";
        if (request.getParameter("group_name") != null) {
            _groupName = request.getParameter("group_name");
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
                case "get_group_list":
                    displayGroupList(__CONN, _queryExtend);
                    break;
                case "get_permission_group":
                    displayPermGroup(__CONN, _keyID);
                    break;
                case "get_permission_user_group":
                    displayPermUserGroup(__CONN, _keyID);
                    break;
                case "get_web_page":
                    displayWebPage(__CONN, _keyID);
                    break;
                case "get_userlist":
                    displayUserList(__CONN, this.__USERCODE, _keyID);
                    break;
                case "find_permission_group":
                    strQUERY = "SELECT G.is_read, G.is_create, G.is_update, G.is_delete, UG.user_code FROM sml_permission_groups AS G INNER JOIN sml_permission_user_group AS UG ON G.group_code = UG.group_code WHERE page_code='" + _keyID + "' AND UG.user_code='" + this.__USERCODE + "' AND G.web_flag='" + _global._WEB_FLAG + "'";
                    findData(__CONN, strQUERY);
                    break;
                case "find_group_list":
                    strQUERY = "SELECT group_code, group_name, web_flag FROM sml_permission_group_list WHERE group_code = '" + _keyID + "' AND web_flag = '" + _global._WEB_FLAG + "' ORDER BY group_code ";
                    findData(__CONN, strQUERY);
                    break;
                case "insert_group_list":
                    updateGroupList(__CONN, _actionName, _groupCode, _groupName);
                    break;
                case "insert_permission_group":
                    insertPermGroup(__CONN, _groupCode, _keyID);
                    break;
                case "insert_permission_user_group":
                    insertPermUserGroup(__CONN, _groupCode, _keyID);
                    break;
                case "update_group_list":
                    updateGroupList(__CONN, _actionName, _groupCode, _groupName);
                    break;
                case "update_permission":
                    updatePermUser(__CONN, _keyID, _groupCode, _updateStatus, _updateAction);
                    break;
                case "delete_group_list":
                    deleteGroupList(__CONN, _keyID);
                    break;
                case "delete_permission_group":
                    deletePermGroup(__CONN, _groupCode, _keyID);
                    break;
                case "delete_permission_user_group":
                    deletePermUserGroup(__CONN, _groupCode, _keyID);
                    break;
            }
        } catch (Exception JSONex) {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void displayGroupList(Connection connection, String strQueryExtend) {
        String __strQUERY = "SELECT group_code, group_name, web_flag FROM sml_permission_group_list WHERE web_flag = '" + _global._WEB_FLAG + "' ORDER BY group_code ";
        String __rsHTML = "";

        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(__strQUERY + strQueryExtend);
            __rsData.next();
            Integer __rowCOUNT = __rsData.getRow();

            if (__rowCOUNT > 0) {
                __rsData.previous();
                Integer __rowNumber = 1;
                while (__rsData.next()) {
                    __rsHTML += "<tr>";
                    __rsHTML += "<td><h5><strong>" + __rowNumber + "</strong></h5></td>";
                    __rsHTML += "<td><h5>" + __rsData.getString("group_code") + " ~ " + __rsData.getString("group_name") + "</h5></td>";
                    __rsHTML += "<td><button class='btn btn-primary btn-block' id='btn-gl-manage' key_id='" + __rsData.getString("group_code") + "'>จัดการสิทธิ์</button></td>";
                    __rsHTML += "<td><button class='btn btn-warning btn-block' id='btn-gl-edit' key_id='" + __rsData.getString("group_code") + "'>แก้ไข</button></td>";
                    __rsHTML += "<td><button class='btn btn-danger btn-block' id='btn-gl-delete' key_id='" + __rsData.getString("group_code") + "'>ลบ</button></td>";
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

            displayPagination(connection, __strQUERY);
        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }
    }

    private void displayPermGroup(Connection connection, String strGroupCode) {
        String __strQUERY = "SELECT *, "
                + " COALESCE ((SELECT page_name FROM sml_web_page AS WP WHERE WP.page_code = sml_permission_groups.page_code), '') AS page_name "
                + " FROM sml_permission_groups "
                + " WHERE group_code = '" + strGroupCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ORDER BY page_code ";
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
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_read' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "' checked></h5></td>";
                    } else {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_read' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "'></h5></td>";
                    }

                  /*  if (is_create) {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_create' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "' checked></h5></td>";
                    } else {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_create' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "'></h5></td>";
                    }

                    if (is_update) {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_update' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "' checked></h5></td>";
                    } else {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_update' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "'></h5></td>";
                    }

                    if (is_delete) {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_delete' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "' checked></h5></td>";
                    } else {
                        __rsHTML += "<td><h5><input type='checkbox' class='chb_is_delete' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "'></h5></td>";
                    }*/

                    __rsHTML += "<td><button class='btn btn-danger btn-block' id='btn-pm-remove' key_id='" + __rsData.getString("page_code") + "' key_code='" + __rsData.getString("group_code") + "'>นำออก</button></td>";
                    __rsHTML += "</tr>";
                    __rowNumber++;
                }
            } else {
                __rsHTML = "<tr><td colspan='7'>ไม่พบข้อมูล.</td></tr>";
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

    private void displayPermUserGroup(Connection connection, String strGroupCode) {
        String __strQUERY = "SELECT UG.group_code, UG.user_code, "
                + " COALESCE ((SELECT UL.user_name FROM sml_user_list AS UL WHERE UPPER(UL.user_code) = UG.user_code), '') AS user_name "
                + " FROM sml_permission_user_group AS UG WHERE group_code = '" + strGroupCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ORDER BY UG.user_code ";
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
                    __rsHTML += "<tr>";
                    __rsHTML += "<td><h5><strong>" + __rowNumber + "</strong></h5></td>";
                    __rsHTML += "<td><h5>" + __rsData.getString("user_code") + " ~ " + __rsData.getString("user_name") + "</h5></td>";
                    __rsHTML += "<td><button class='btn btn-danger btn-block' id='btn-ul-delete' key_id='" + __rsData.getString("user_code") + "'>นำออก</button></td>";
                    __rsHTML += "</tr>";
                    __rowNumber++;
                }
            } else {
                __rsHTML = "<tr><td colspan='3'>ยังไม่มีรายชื่อผู้ใช้งานในกลุ่ม.</td></tr>";
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

    private void displayWebPage(Connection connection, String strGroupCode) {
        String __strQUERY = "SELECT WP.page_code, WP.page_name "
                + "FROM sml_web_page AS WP "
                + "WHERE NOT EXISTS "
                + "(SELECT GP.page_code, GP.group_code FROM sml_permission_groups AS GP WHERE GP.page_code = WP.page_code AND GP.group_code = '" + strGroupCode + "')";

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
                    __rsHTML += "<tr>";
                    __rsHTML += "<td><h5><strong>" + __rowNumber + "</strong></h5></td>";
                    __rsHTML += "<td><h5>" + __rsData.getString("page_code") + " ~ " + __rsData.getString("page_name") + "</h5></td>";
                    __rsHTML += "<td><button class='btn btn-success btn-block' id='btn-wp-insert' key_id='" + __rsData.getString("page_code") + "'>นำเข้า</button></td>";
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

    private void displayUserList(Connection connection, String strUserCode, String strGroupCode) {
        String __strQUERY = "SELECT UL.user_code, UL.user_name "
                + "FROM sml_user_list AS UL "
                + "WHERE NOT EXISTS "
                + "(SELECT UG.user_code, UG.group_code FROM sml_permission_user_group AS UG WHERE UPPER(UG.user_code) = UPPER(UL.user_code) AND UG.group_code='" + strGroupCode + "') AND UPPER(UL.user_code)<>'" + strUserCode + "' ";

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
                    __rsHTML += "<tr>";
                    __rsHTML += "<td><h5><strong>" + __rowNumber + "</strong></h5></td>";
                    __rsHTML += "<td><h5>" + __rsData.getString("user_code") + " ~ " + __rsData.getString("user_name") + "</h5></td>";
                    __rsHTML += "<td><button class='btn btn-success btn-block' id='btn-ul-insert' key_id='" + __rsData.getString("user_code") + "'>นำเข้า</button></td>";
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

            __stmt.close();
            __rsData.close();

            objResult.put("success", true);
            objResult.put("data_pagination", __rsHTML);

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

    private void insertPermGroup(Connection connection, String strGroupCode, String strPageCode) {
        String __strQUERY = "INSERT INTO sml_permission_groups (group_code, page_code, is_read, is_create, is_update, is_delete, web_flag) "
                + "VALUES ('" + strGroupCode + "','" + strPageCode + "','true','false','false','false','" + _global._WEB_FLAG + "')";
        try {
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

    private void insertPermUserGroup(Connection connection, String strGroupCode, String strUserCode) {
        String __strQUERY = "INSERT INTO sml_permission_user_group (group_code, user_code, web_flag) "
                + "VALUES ('" + strGroupCode + "','" + strUserCode.toUpperCase() + "','" + _global._WEB_FLAG + "')";
        try {
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

    private void updateGroupList(Connection connection, String strActionName, String strGroupCode, String strGroupName) {
        String __strQUERY = "";
        try {
            switch (strActionName) {
                case "insert_group_list":
                    Statement __stmt0 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet __rsData0 = __stmt0.executeQuery("SELECT group_code FROM sml_permission_group_list WHERE group_code='" + strGroupCode + "' AND web_flag ='" + _global._WEB_FLAG + "' ");
                    __rsData0.next();
                    int __rowCOUNT = __rsData0.getRow();
                    if (__rowCOUNT > 0) {
                        this.objResult.put("success", false);
                        objResult.put("err_title", "ข้อความระบบ");
                        this.objResult.put("err_msg", "รหัส " + strGroupCode + " มีข้อมูลอยู่ในระบบแล้ว.");
                    } else {
                        __strQUERY = "INSERT INTO sml_permission_group_list (group_code, group_name, web_flag) VALUES ('" + strGroupCode + "','" + strGroupName + "', '" + _global._WEB_FLAG + "')";
                        Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        __stmt.executeUpdate(__strQUERY);
                        __stmt.close();
                        this.objResult.put("success", true);
                    }
                    break;
                case "update_group_list":
                    Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    __strQUERY = "UPDATE sml_permission_group_list SET group_name = '" + strGroupName + "' WHERE group_code = '" + strGroupCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ";
                    __stmt.executeUpdate(__strQUERY);
                    __stmt.close();
                    this.objResult.put("success", true);
                    break;
            }
        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
            objResult.put("err_sql", __strQUERY);
        }
    }

    private void updatePermUser(Connection connection, String strPageCode, String strGroupCode, String strUpdateStatus, String strUpdateAction) {
        String __strQUERY = "UPDATE sml_permission_groups SET " + strUpdateAction + "='" + strUpdateStatus + "' WHERE group_code='" + strGroupCode + "' AND page_code = '" + strPageCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ";
        try {
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

    private void deleteGroupList(Connection connection, String groupCode) {
        String __strQUERY0 = "DELETE FROM sml_permission_group_list WHERE group_code = '" + groupCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ";
        String __strQUERY1 = "DELETE FROM sml_permission_groups WHERE group_code = '" + groupCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ";
        String __strQUERY2 = "DELETE FROM sml_permission_user_group WHERE group_code = '" + groupCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ";
        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt.executeUpdate(__strQUERY0);
            __stmt.executeUpdate(__strQUERY1);
            __stmt.executeUpdate(__strQUERY2);
            __stmt.close();
            this.objResult.put("success", true);
        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
            objResult.put("err_sql", __strQUERY0 + "\n" + __strQUERY1 + "\n" + __strQUERY2);
        }
    }

    private void deletePermGroup(Connection connection, String strGroupCode, String strPageCode) {
        String __strQUERY = "DELETE FROM sml_permission_groups WHERE group_code = '" + strGroupCode + "' AND page_code = '" + strPageCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ";
        try {
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

    private void deletePermUserGroup(Connection connection, String strGroupCode, String strUserCode) {
        String __strQUERY = "DELETE FROM sml_permission_user_group WHERE group_code = '" + strGroupCode + "' AND user_code = '" + strUserCode + "' AND web_flag = '" + _global._WEB_FLAG + "' ";
        try {
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
