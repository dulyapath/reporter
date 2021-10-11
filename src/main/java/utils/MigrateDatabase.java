package utils;

import Model.MigrateTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import Model.MigrateTableModel;
import Model.MigrateColumnModel;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

public class MigrateDatabase {

    Vector<MigrateTableModel> tables;
    Vector<MigrateTableModel> tablesProvider;
    HttpSession session = null;

    StringBuilder logVerify = new StringBuilder();

    private void structTable() {
        tables = new Vector<MigrateTableModel>();
        tablesProvider = new Vector<MigrateTableModel>();

        // ################### ic_inventory_price_list #########################
        MigrateTableModel ICInventoryPriceList = new MigrateTableModel("ic_inventory_price_list");

        ICInventoryPriceList.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("branch_code", "character varying", 255, "NOT NULL"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("item_code", "character varying", 255, "NOT NULL"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("unit_code", "character varying", 50, "NOT NULL"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("department_code", "character varying", 255, "NOT NULL"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("sale_type", "smallint"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("date_begin", "date"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("date_end", "date"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_0", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_1", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_2", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_3", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_4", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_5", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_6", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_7", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_8", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_9", "character varying", 50));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("vat_type", "smallint"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_guid", "character varying", 255));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("is_use", "smallint", "DEFAULT 1"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("qty_begin", "numeric"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("qty_end", "numeric"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("is_promotion", "smallint", "DEFAULT 0"));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("price_promotion", "character varying", 255));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("from_ar_group", "character varying", 255));
        ICInventoryPriceList.addColumns(new MigrateColumnModel("to_ar_group", "character varying", 255));
        ICInventoryPriceList.addConstraint("ic_inventory_price_list_pkey PRIMARY KEY (roworder)");
        tables.add(ICInventoryPriceList);

        // ################### sml_web_page ####################################
        MigrateTableModel smlWebPage = new MigrateTableModel("sml_web_page");

        smlWebPage.addColumns(new MigrateColumnModel("page_code", "character varying", 255, "NOT NULL"));
        smlWebPage.addColumns(new MigrateColumnModel("page_name", "character varying", 255, "NOT NULL"));
        smlWebPage.addColumns(new MigrateColumnModel("web_flag", "integer", "NOT NULL DEFAULT 0"));
        smlWebPage.addConstraint("sml_web_page_pkey PRIMARY KEY (page_code)");
        smlWebPage.addAfterScript("INSERT INTO sml_web_page (page_code,page_name,web_flag) VALUES ('R00000000', 'กำหนดสิทธ์','0')");
        smlWebPage.addAfterScript("INSERT INTO sml_web_page (page_code,page_name,web_flag) VALUES ('R00000001', 'รายงานสินค้าคงเหลือเรียงตามคลัง','1')");
        smlWebPage.addAfterScript("INSERT INTO sml_web_page (page_code,page_name,web_flag) VALUES ('R00000002', 'รายงานประมาณการสั่งสินค้า','1')");
        smlWebPage.addAfterScript("INSERT INTO sml_web_page (page_code,page_name,web_flag) VALUES ('R00000003', 'รายงานวิเคราะห์การขายและกำไรขั้นต้นALL','1')");

        tablesProvider.add(smlWebPage);

        // ################### sml_permission_user_list ########################
        MigrateTableModel smlPermUserList = new MigrateTableModel("sml_permission_user_list");

        smlPermUserList.addColumns(new MigrateColumnModel("roworder", "serial"));
        smlPermUserList.addColumns(new MigrateColumnModel("user_code", "character varying", 255, "NOT NULL"));
        smlPermUserList.addColumns(new MigrateColumnModel("page_code", "character varying", 255, "NOT NULL"));
        smlPermUserList.addColumns(new MigrateColumnModel("web_flag", "integer", "NOT NULL DEFAULT 0"));
        smlPermUserList.addColumns(new MigrateColumnModel("is_read", "boolean"));
        smlPermUserList.addColumns(new MigrateColumnModel("is_create", "boolean"));
        smlPermUserList.addColumns(new MigrateColumnModel("is_update", "boolean"));
        smlPermUserList.addColumns(new MigrateColumnModel("is_delete", "boolean"));
        smlPermUserList.addConstraint("sml_permission_user_list_pkey PRIMARY KEY (roworder)");
        tablesProvider.add(smlPermUserList);

        // ################### sml_permission_group_list ########################
        MigrateTableModel smlPermGroupList = new MigrateTableModel("sml_permission_group_list");

        smlPermGroupList.addColumns(new MigrateColumnModel("group_code", "character varying", 255, "NOT NULL"));
        smlPermGroupList.addColumns(new MigrateColumnModel("group_name", "character varying", 255, "NOT NULL"));
        smlPermGroupList.addColumns(new MigrateColumnModel("web_flag", "integer", "NOT NULL DEFAULT 0"));
        smlPermGroupList.addConstraint("sml_permission_group_list_pkey PRIMARY KEY (group_code)");
        tablesProvider.add(smlPermGroupList);

        // ################### sml_permission_groups ########################
        MigrateTableModel smlPermGroup = new MigrateTableModel("sml_permission_groups");

        smlPermGroup.addColumns(new MigrateColumnModel("roworder", "serial"));
        smlPermGroup.addColumns(new MigrateColumnModel("group_code", "character varying", 255, "NOT NULL"));
        smlPermGroup.addColumns(new MigrateColumnModel("page_code", "character varying", 255, "NOT NULL"));
        smlPermGroup.addColumns(new MigrateColumnModel("web_flag", "integer", "NOT NULL DEFAULT 0"));
        smlPermGroup.addColumns(new MigrateColumnModel("is_read", "boolean"));
        smlPermGroup.addColumns(new MigrateColumnModel("is_create", "boolean"));
        smlPermGroup.addColumns(new MigrateColumnModel("is_update", "boolean"));
        smlPermGroup.addColumns(new MigrateColumnModel("is_delete", "boolean"));
        smlPermGroup.addConstraint("sml_permission_groups_pkey PRIMARY KEY (roworder)");
        tablesProvider.add(smlPermGroup);

        // ################### sml_permission_user_group ########################
        MigrateTableModel smlPermUserGroup = new MigrateTableModel("sml_permission_user_group");

        smlPermUserGroup.addColumns(new MigrateColumnModel("roworder", "serial"));
        smlPermUserGroup.addColumns(new MigrateColumnModel("user_code", "character varying", 255, "NOT NULL"));
        smlPermUserGroup.addColumns(new MigrateColumnModel("group_code", "character varying", 255, "NOT NULL"));
        smlPermUserGroup.addColumns(new MigrateColumnModel("web_flag", "integer", "NOT NULL DEFAULT 0"));
        smlPermUserGroup.addConstraint("sml_permission_user_group_pkey PRIMARY KEY (roworder)");
        tablesProvider.add(smlPermUserGroup);
    }

    public void verify(String provider, String dbname, HttpSession session) {
        this.session = session;
        verify(provider, dbname);
    }

    public void verify(String provider, String dbname) {
        Connection __conn = null;
        Connection __connProvider = null;

        structTable();
        dbname = dbname.toLowerCase();
        provider = provider.toLowerCase();

        _routine __routine = new _routine();

        __conn = __routine._connect(dbname, "SMLConfig" + provider.toUpperCase() + ".xml");
        __connProvider = __routine._connect("smlerpmain" + provider);

        try {

            for (MigrateTableModel table : tables) {
                verifyTable(__conn, table);
            }

            for (MigrateTableModel table : tablesProvider) {
                verifyTable(__connProvider, table);
            }
            afterScript(__connProvider);

        } finally {
            logStatus("complete", "", 1);

            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (__connProvider != null) {
                try {
                    __connProvider.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    private void afterScript(Connection __conn) {
        try {
            String sqlCheck = "SELECT module_code FROM sml_web_module WHERE module_code = 'settings'";

            PreparedStatement stmt = __conn.prepareStatement(sqlCheck);
            ResultSet rs = stmt.executeQuery();

            boolean valNotExists = true;
            while (rs.next()) {
                valNotExists = false;
            }

            if (valNotExists) {
                String afterScript = "DELETE FROM sml_web_module WHERE web_flag=1;";
                afterScript += "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'cargroup', 'รหัสรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'carmaster', 'รายละเอียดรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'carstatus', 'สถานะรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'cartype', 'ประเภทรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'tmsroute', 'จัดการเส้นทาง', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'drivermaster', 'จัดการพนักงานขนส่ง', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'maintanance', 'Maintanance', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'oil', 'Oil', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'routedetails', 'การเดินรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipment', 'คิวส่งสินค้า', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentapprove', 'ปล่อยรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentcancel', 'ยกเลิก', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentfinished', 'ปิดจ๊อบ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'trucktracking', 'ติดตามรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentreport', 'รายงานตรวจสอบ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonque', 'เหตุผลจัดคิว', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonapprove', 'เหตุผลปล่อยรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonclosejob', 'เหตุผลปิดjob', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'permission', 'จัดการสิทธิ์', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'group', 'กลุ่มผู้ใช้งาน', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'settings', 'ตั้งค่า', '0', '1');";

                PreparedStatement stmtBeforeScript = __conn.prepareStatement(afterScript);
                stmtBeforeScript.executeUpdate();
            }

        } catch (SQLException ex) {
//            ex.printStackTrace();
        }
    }

    private void verifyTable(Connection __conn, MigrateTableModel table) {

        try {
            PreparedStatement stmtBeforeScript = __conn.prepareStatement(table.getAfterScript());
            stmtBeforeScript.executeUpdate();
        } catch (SQLException ex) {

        }

        try {

            String sqlCheckTable = "";

            JSONObject logTable = new JSONObject();

            sqlCheckTable = "SELECT table_name FROM information_schema.tables WHERE table_name = '" + table.getTableName() + "';";
            PreparedStatement stmt = __conn.prepareStatement(sqlCheckTable);
            ResultSet rs = stmt.executeQuery();

            table.setExists(false);
            while (rs.next()) {
                table.setExists(true);
            }
            rs.close();
            stmt.close();

            if (table.isExists()) {
                logStatus("t", table.getTableName(), 1);
                verifyColumn(__conn, table);
            } else {

                PreparedStatement stmtTable = __conn.prepareStatement(table.getCreateTableScript());
                stmtTable.executeUpdate();
                stmtTable.close();

                PreparedStatement stmtAfterScript = __conn.prepareStatement(table.getAfterScript());
                stmtAfterScript.executeUpdate();

                logStatus("t", table.getTableName(), 1);
            }

            String indexsScript = table.getAddIndexScript();

            if (!indexsScript.equals("")) {
                try {
                    PreparedStatement stmtIndexs = __conn.prepareStatement(indexsScript);
                    stmtIndexs.executeUpdate();
                } catch (SQLException e) {

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logStatus("t", table.getTableName(), 0);
            logStatus("sql_err", e.getMessage(), 0);
        }
    }

    private void verifyColumn(Connection __conn, MigrateTableModel table) {

        String columnLastCheck = "";
        try {

            String sqlCheckColumn = "";

            sqlCheckColumn = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + table.getTableName() + "'";
            Statement stmtCol = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rsCol = stmtCol.executeQuery(sqlCheckColumn);

            Vector<String> colExists = new Vector<String>();

            while (rsCol.next()) {
                colExists.add(rsCol.getString("column_name"));
            }

            rsCol.close();
            stmtCol.close();

            for (MigrateColumnModel column : table.getColumns()) {
                column.setExists(colExists.contains(column.getName()));
                if (!column.isExists()) {
                    columnLastCheck = column.getName();

                    Statement stmtColAdd = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stmtColAdd.executeUpdate(column.getAddColumnScript(table.getTableName()));
                    stmtColAdd.close();

                    logStatus("c", column.getName(), 1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logStatus("c", columnLastCheck, 0);
            logStatus("sql_err", e.getMessage(), 0);
        }
    }

    private void sessionLog() {
        if (session != null) {
            session.setAttribute("verify_log", logVerify.toString());
        }
    }

    private void logStatus(String code, String msg, int status) {
        logVerify.append(code + "," + msg + "," + status + "\n");
        sessionLog();
    }

}
