package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Permission;
import org.json.JSONArray;
import org.json.JSONObject;

public class PermissionUtil {

    private final int WEB_FLAG = 1;
    private String provider;
    public Permission pmx;

    public PermissionUtil(String dbname) {
        this.provider = dbname.toLowerCase();
    }

    public PermissionUtil(String provider, String user, String menuCode) {
        this.provider = provider.toLowerCase();
        this.pmx = this.getPermissMenu(user, menuCode);
    }

    public Permission getPermissMenu(String user, String menuCode) {
        _routine routine = new _routine();
        Connection __conn = null;
        Permission pmx = new Permission();
        try {
            __conn = routine._connect("smlerpmain" + this.provider, "SMLConfig" + this.provider.toUpperCase() + ".xml");
            String _query = "SELECT is_create, is_read, is_update, is_delete FROM sml_user_web WHERE web_flag = " + WEB_FLAG + " AND lower(user_access) = lower('" + menuCode + "') AND upper(user_code) = upper('" + user + "')";

            Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rs = __stmt.executeQuery(_query);

            pmx.setMenuCode(menuCode);
            while (__rs.next()) {
                pmx.setCreate(num2bool(__rs.getInt("is_create")));
                pmx.setRead(num2bool(__rs.getInt("is_read")));
                pmx.setUpdate(num2bool(__rs.getInt("is_update")));
                pmx.setDelete(num2bool(__rs.getInt("is_delete")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return pmx;
    }

    public JSONArray getPermissUser(String user) throws SQLException {
        _routine routine = new _routine();
        Connection __conn = null;
        JSONArray PermArr = new JSONArray();
        
        try {
            __conn = routine._connect("smlerpmain" + this.provider, _global.FILE_CONFIG(provider));
            String _query = "select page_code from sml_web_page;";
            Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rs = __stmt.executeQuery(_query);

            while (__rs.next()) {
                JSONObject objResult = new JSONObject();
                if (user.equals("SUPERADMIN")) {
                    objResult.put("page_code", __rs.getString("page_code"));
                    objResult.put("is_create", true);
                    objResult.put("is_read", true);
                    objResult.put("is_update", true);
                    objResult.put("is_delete", true);
                } else {
                    objResult.put("page_code", __rs.getString("page_code"));
                    objResult.put("is_create", false);
                    objResult.put("is_read", false);
                    objResult.put("is_update", false);
                    objResult.put("is_delete", false);

                    String _querySub = "select * from sml_permission_user_list where upper(user_code) = upper('"+user+"')  and upper(page_code) =  upper('" + __rs.getString("page_code") + "');";
                    Statement __stmt2 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet __rs2 = __stmt2.executeQuery(_querySub);
                    //System.out.println("_querySub :" + _querySub);
                    while (__rs2.next()) {
                        objResult.put("page_code", __rs.getString("page_code"));
                        objResult.put("is_create", __rs2.getBoolean("is_create"));
                        objResult.put("is_read", __rs2.getBoolean("is_read"));
                        objResult.put("is_update", __rs2.getBoolean("is_update"));
                        objResult.put("is_delete", __rs2.getBoolean("is_delete"));
                    }

                    String _querySub2 = "select * from sml_permission_groups where group_code = (SELECT group_code FROM sml_permission_user_group WHERE upper(user_code) = upper('"+user+"')) and upper(page_code) = upper('" + __rs.getString("page_code") + "');";
                    Statement __stmt3 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet __rs3 = __stmt3.executeQuery(_querySub2);
                    // System.out.println("_querySub2 :" + _querySub2);
                    while (__rs3.next()) {
                        objResult.put("page_code", __rs.getString("page_code"));
                        if (!objResult.getBoolean("is_create")) {
                            objResult.put("is_create", __rs3.getBoolean("is_create"));
                        }
                        if (!objResult.getBoolean("is_read")) {
                            objResult.put("is_read", __rs3.getBoolean("is_read"));
                        }
                        if (!objResult.getBoolean("is_update")) {
                            objResult.put("is_update", __rs3.getBoolean("is_update"));
                        }
                        if (!objResult.getBoolean("is_delete")) {
                            objResult.put("is_delete", __rs3.getBoolean("is_delete"));
                        }
                    }

                }
                //System.out.println("objResult :" + objResult);
                PermArr.put(objResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return PermArr;
    }

    public JSONObject getKey(JSONArray array, String key) {
        JSONObject value = null;
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            if (item.getString("page_code").equals(key)) {
                value = item;
                break;
            }

        }

        return value;
    }

    public Permission getPermissByList(List<Permission> pmList, String menuCode) {
        Permission pmx = new Permission();

        for (Permission pm : pmList) {
            if (pm.getMenuCode().equals(menuCode)) {
                return pm;
            }
        }
        return pmx;
    }

    public boolean checkPermissAdmin(String user) throws SQLException {
        _routine routine = new _routine();
        Connection __conn = routine._connect("smlerpmain" + this.provider, "SMLConfig" + this.provider.toUpperCase() + ".xml");

        String _query = "SELECT user_level FROM sml_user_list WHERE upper(user_code) = upper('" + user + "') AND user_level = 2 ";
        Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rs = __stmt.executeQuery(_query);

        while (__rs.next()) {
            return true;
        }

        return false;
    }

    public boolean checkByList(List<Integer> pmx, List<Integer> pmList) {
        for (int _chk : pmList) {
            if (pmx.contains(_chk)) {
                return true;
            }
        }
        return false;
    }

    private boolean num2bool(int num) {
        return num == 1 ? true : false;
    }
}
