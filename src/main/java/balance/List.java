package balance;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import utils.ResponeUtil;
import utils._global;
import utils._routine;

@WebServlet(name = "balanceList", urlPatterns = {"/balanceList"})
public class List extends HttpServlet {

    private String __strDatabaseName = "";
    private String __strProviderCode = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject __objResult = new JSONObject("{'success': false}");

        HttpSession session = request.getSession();
        this.__strDatabaseName = session.getAttribute("dbname").toString();
        this.__strProviderCode = session.getAttribute("provider").toString();

        if (!request.getParameterMap().containsKey("action_name")) {

        } else {
            String __strActionName = (request.getParameter("action_name") != null && !request.getParameter("action_name").isEmpty()) ? request.getParameter("action_name") : "";
            Connection conn = null;
            try {
                _routine __routine = new _routine();
                conn = __routine._connect(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));
                switch (__strActionName) {
                    case "loadList":
                        __objResult = this.loadList(conn, ResponeUtil.str2Json(request.getParameter("data")));
                        break;
                    case "loadSub":
                        __objResult = this.loadSub(conn, ResponeUtil.str2Json(request.getParameter("data")));
                        break;
                    case "loadChart":
                        __objResult = this.loadLineChart(conn, ResponeUtil.str2Json(request.getParameter("data")));
                        break;
                }
            } catch (SQLException ex) {
                __objResult.put("err_msg", ex.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                    }
                }
            }
        }

        response.getWriter().print(__objResult);
    }

    private JSONObject loadList(Connection conn, JSONObject params) throws SQLException {
        JSONObject __objTmp = new JSONObject("{'success':false}");
        String __strSearchItem = !params.isNull("search_item") && !params.getString("search_item").isEmpty() ? params.getString("search_item") : "";
        String __strSearchBarCode = !params.isNull("search_barcode") && !params.getString("search_barcode").isEmpty() ? params.getString("search_barcode") : "";

        String sort = !params.isNull("sort") && !params.getString("sort").isEmpty() ? params.getString("sort") : "";
        String sort_col = !params.isNull("sort_col") && !params.getString("sort_col").isEmpty() ? params.getString("sort_col") : "";
        
        Integer branchCount = getSyncBranchList(conn);
        String __strQueryBranchSync = "";
        if (branchCount > 0) {
            __strQueryBranchSync = " AND x1.branch_sync = ic_trans_detail.branch_sync ";
        }

        String __strQueryExtend = "";
        {
            String __strQueryTmp1 = "";
            String __strQueryTmp2 = "";
            if (__strSearchBarCode.length() > 0) {
                __strQueryExtend += " (SELECT ic_code FROM ic_inventory_barcode WHERE UPPER(barcode) LIKE '%" + __strSearchBarCode.toUpperCase() + "%' LIMIT 100) ";
            } else {
                if (__strSearchItem.length() > 0) {
                    String[] sptSearches = __strSearchItem.split(" ");
                    for (int i = 0; i < sptSearches.length; i++) {
                        if (i == 0) {
                            __strQueryTmp1 += " UPPER(code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                            __strQueryTmp2 += " UPPER(name_1) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        } else {
                            __strQueryTmp1 += " AND UPPER(code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                            __strQueryTmp2 += " AND UPPER(name_1) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        }
                    }
                } else {
                    __strQueryTmp1 += " UPPER(code) LIKE '%" + __strSearchItem.toUpperCase() + "%'";
                    __strQueryTmp2 += " UPPER(name_1) LIKE '%" + __strSearchItem.toUpperCase() + "%'";
                }
                __strQueryExtend += " (SELECT code FROM ic_inventory WHERE (" + __strQueryTmp1 + ") OR (" + __strQueryTmp2 + ") LIMIT 200) ";
            }
        }

        String __strQuery = "select * from ic_inventory limit 10";
        System.out.println("__strQuery "+__strQuery);
        Statement __stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet __result = __stmt.executeQuery(__strQuery);
        JSONArray __jsonResult = new JSONArray();
        while (__result.next()) {
            System.out.println("__result "+__result.getString("code"));
            JSONObject __objBody = new JSONObject();
            __objBody.put("ic_code", __result.getString("code"));
            __objBody.put("ic_name", __result.getString("name_1"));

            //__objBody.put("barcode", getBarcode(conn, __result.getString("ic_code")));

            __jsonResult.put(__objBody);
        }

        __objTmp.put("data", __jsonResult);
        __objTmp.put("success", true);

        return __objTmp;
    }

    private JSONObject loadSub(Connection conn, JSONObject params) throws SQLException {
        JSONObject __objTmp = new JSONObject("{'success': false}");
        String __strItemCode = !params.isNull("ic_code") && !params.getString("ic_code").isEmpty() ? params.getString("ic_code") : "";
        String __strQuery = "select *,(select name_1 from erp_branch_list where code=branch_code) as branch_name,(select name_1 from ap_supplier where code=cust_code) as cust_name from (select *,po_qty-(in_qty+cancel_qty) as balance_qty from (select *,(select cust_code from ic_trans where doc_no=x1.doc_no  and trans_flag=6) as cust_code,(select doc_date from ic_trans where doc_no=x1.doc_no  and trans_flag=6) as doc_date,(select doc_time from ic_trans where doc_no=x1.doc_no  and trans_flag=6) as doc_time,(select branch_code from ic_trans where doc_no=x1.doc_no  and trans_flag=6) as branch_code,coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=x1.item_code and ref_doc_no=x1.doc_no and trans_flag in (12,310) and last_status=0),0) as in_qty,coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=x1.item_code and ref_doc_no=x1.doc_no and trans_flag=7 and last_status=0),0) as cancel_qty from (select item_code,doc_no,sum(qty * (stand_value/divide_value)) as po_qty from ic_trans_detail as q1 where item_code='" + __strItemCode + "' and trans_flag=6 and last_status=0 group by item_code,doc_no) as x1) as x2) as x3 where balance_qty<>0 order by doc_date,doc_time";

        { // สั่งซื้อค้างรับ
            Statement __stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __result = __stmt.executeQuery(__strQuery);
            JSONArray __jsonResult = new JSONArray();
            while (__result.next()) {
                JSONObject __objBody = new JSONObject();
                __objBody.put("doc_date", __result.getString("doc_date"));
                __objBody.put("doc_time", __result.getString("doc_time"));
                __objBody.put("cust_code", __result.getString("cust_code"));
                __objBody.put("cust_name", __result.getString("cust_name"));
                __objBody.put("branch_code", __result.getString("branch_code"));
                __objBody.put("branch_name", __result.getString("branch_name"));
                __objBody.put("doc_no", __result.getString("doc_no"));
                __objBody.put("po_qty", __result.getInt("po_qty"));
                __objBody.put("in_qty", __result.getInt("in_qty"));
                __objBody.put("cancel_qty", __result.getInt("cancel_qty"));
                __objBody.put("balance_qty", __result.getInt("balance_qty"));

                String __strSubQuery = "select doc_date,doc_time,doc_no,price,qty * (stand_value/divide_value) as qty from ic_trans_detail where item_code='" + __strItemCode + "' and doc_no='" + __result.getString("doc_no") + "' and trans_flag=6 and last_status=0 order by doc_date,doc_time";
                __objBody.put("sub_data_1", getSubData(conn, __strSubQuery));
                __strSubQuery = "select item_code,doc_date,doc_time,doc_no,qty * (stand_value/divide_value) as qty from ic_trans_detail where item_code='" + __strItemCode + "' and ref_doc_no='" + __result.getString("doc_no") + "' and trans_flag in(12,310) and last_status=0 order by doc_date,doc_time";
                __objBody.put("sub_data_2", getSubData(conn, __strSubQuery));
                __jsonResult.put(__objBody);
            }

            __objTmp.put("data1", __jsonResult);
        }

        { // สั่งจองค้างส่ง
            __strQuery = "select * from (select *,po_qty-in_qty as balance_qty from (select *,(select name_1 from ar_customer where code=(select cust_code from ic_trans where doc_no=x1.doc_no)) as cust_name,(select cust_code from ic_trans where doc_no=x1.doc_no) as cust_code,(select doc_date from ic_trans where doc_no=x1.doc_no) as doc_date,(select doc_time from ic_trans where doc_no=x1.doc_no) as doc_time,(select name_1 from erp_branch_list where code=(select branch_code from ic_trans where doc_no=x1.doc_no)) as branch_name,(select branch_code from ic_trans where doc_no=x1.doc_no) as branch_code,coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=x1.item_code and ref_doc_no=x1.doc_no and trans_flag=44 and last_status=0),0) as in_qty from (select item_code,doc_no,sum(qty * (stand_value/divide_value)) as po_qty from ic_trans_detail as q1 where item_code='" + __strItemCode + "' and trans_flag=34 and last_status=0 group by item_code,doc_no) as x1) as x2) as x3 where balance_qty<>0 order by doc_date,doc_time";
            Statement __stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __result = __stmt.executeQuery(__strQuery);
            JSONArray __jsonResult = new JSONArray();
            while (__result.next()) {
                JSONObject __objBody = new JSONObject();
                __objBody.put("doc_date", __result.getString("doc_date"));
                __objBody.put("doc_time", __result.getString("doc_time"));
                __objBody.put("cust_code", __result.getString("cust_code"));
                __objBody.put("cust_name", __result.getString("cust_name"));
                __objBody.put("branch_code", __result.getString("branch_code"));
                __objBody.put("branch_name", __result.getString("branch_name"));
                __objBody.put("doc_no", __result.getString("doc_no"));
                __objBody.put("po_qty", __result.getInt("po_qty"));
                __objBody.put("in_qty", __result.getInt("in_qty"));
                __objBody.put("balance_qty", __result.getInt("balance_qty"));

                String __strSubQuery = "select doc_date,doc_time,doc_no,qty * (stand_value/divide_value) as qty from ic_trans_detail where item_code='" + __strItemCode + "' and ref_doc_no='" + __result.getString("doc_no") + "' and trans_flag=44 and last_status=0 order by doc_date,doc_time";
                __objBody.put("sub_data_1", getSubData(conn, __strSubQuery));
                __jsonResult.put(__objBody);
            }
            __objTmp.put("data2", __jsonResult);
        }

        {  // สั่งขายค้างส่ง
            __strQuery = "select * from (select *,po_qty-in_qty as balance_qty from (select *,(select cust_code from ic_trans where doc_no=x1.doc_no),(select name_1 from ar_customer where code=(select cust_code from ic_trans where doc_no=x1.doc_no)) as cust_name,(select doc_date from ic_trans where doc_no=x1.doc_no) as doc_date,(select doc_time from ic_trans where doc_no=x1.doc_no) as doc_time,(select name_1 from erp_branch_list where code=(select branch_code from ic_trans where doc_no=x1.doc_no)) as branch_name,(select branch_code from ic_trans where doc_no=x1.doc_no),coalesce((select sum(qty * (stand_value/divide_value)) from ic_trans_detail where item_code=x1.item_code and ref_doc_no=x1.doc_no and trans_flag=44 and last_status=0),0) as in_qty from (select item_code,doc_no,sum(qty * (stand_value/divide_value)) as po_qty from ic_trans_detail as q1 where item_code='" + __strItemCode + "' and trans_flag=36 and last_status=0 group by item_code,doc_no) as x1) as x2) as x3 where balance_qty<>0 order by doc_date,doc_time";
            Statement __stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __result = __stmt.executeQuery(__strQuery);
            JSONArray __jsonResult = new JSONArray();
            while (__result.next()) {
                JSONObject __objBody = new JSONObject();
                __objBody.put("doc_date", __result.getString("doc_date"));
                __objBody.put("doc_time", __result.getString("doc_time"));
                __objBody.put("cust_code", __result.getString("cust_code"));
                __objBody.put("cust_name", __result.getString("cust_name"));
                __objBody.put("branch_code", __result.getString("branch_code"));
                __objBody.put("branch_name", __result.getString("branch_name"));
                __objBody.put("doc_no", __result.getString("doc_no"));
                __objBody.put("po_qty", __result.getInt("po_qty"));
                __objBody.put("in_qty", __result.getInt("in_qty"));

                String __strSubQuery = "select doc_date,doc_time,doc_no,qty * (stand_value/divide_value) as qty from ic_trans_detail where item_code='" + __strItemCode + "' and ref_doc_no='" + __result.getString("doc_no") + "' and trans_flag=44 and last_status=0 order by doc_date,doc_time";
                __objBody.put("sub_data_1", getSubData(conn, __strSubQuery));
                __jsonResult.put(__objBody);
            }

            __objTmp.put("data3", __jsonResult);
        }
        {  // ซื้อล่าสุด
            __strQuery = "select doc_date,doc_time,doc_no,branch_code,wh_code,shelf_code,(select name_1 from erp_branch_list where code=branch_code) as branch_name,cust_code,(select name_1 from ap_supplier where code=cust_code) as cust_name,unit_code,(select name_1 from ic_unit where code=unit_code) as unit_name,qty,price,COALESCE (discount,'')as discount,sum_amount from ic_trans_detail where item_code='" + __strItemCode + "' and trans_flag=12 and last_status=0 order by doc_date desc,doc_time desc,doc_no,line_number limit 100";
            Statement __stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __result = __stmt.executeQuery(__strQuery);
            JSONArray __jsonResult = new JSONArray();
            while (__result.next()) {
                JSONObject __objBody = new JSONObject();
                __objBody.put("doc_date", __result.getString("doc_date"));
                __objBody.put("doc_time", __result.getString("doc_time"));
                __objBody.put("doc_no", __result.getString("doc_no"));
                __objBody.put("branch_code", __result.getString("branch_code"));
                __objBody.put("branch_name", __result.getString("branch_name"));
                __objBody.put("cust_code", __result.getString("cust_code"));
                __objBody.put("cust_name", __result.getString("cust_name"));
                __objBody.put("wh_code", __result.getString("wh_code"));
                __objBody.put("shelf_code", __result.getString("shelf_code"));
                __objBody.put("qty", __result.getInt("qty"));
                __objBody.put("unit_name", __result.getString("unit_name"));
                __objBody.put("unit_code", __result.getString("unit_code"));
                __objBody.put("price", __result.getInt("price"));
                __objBody.put("discount", __result.getString("discount"));
                __objBody.put("sum_amount", __result.getInt("sum_amount"));

                __jsonResult.put(__objBody);
            }

            __objTmp.put("data4", __jsonResult);
        }

        {  // ขายล่าสุด
            __strQuery = "select doc_date,doc_time,doc_no,branch_code,wh_code,shelf_code,(select name_1 from erp_branch_list where code=branch_code) as branch_name,cust_code,(select name_1 from ar_customer where code=cust_code) as cust_name,unit_code,(select name_1 from ic_unit where code=unit_code) as unit_name,qty,price,COALESCE(discount,'')as discount,sum_amount from ic_trans_detail where item_code='" + __strItemCode + "' and trans_flag=44 and last_status=0 order by doc_date desc,doc_time desc,doc_no,line_number limit 100";
            Statement __stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __result = __stmt.executeQuery(__strQuery);
            JSONArray __jsonResult = new JSONArray();
            while (__result.next()) {
                JSONObject __objBody = new JSONObject();
                __objBody.put("doc_date", __result.getString("doc_date"));
                __objBody.put("doc_time", __result.getString("doc_time"));
                __objBody.put("doc_no", __result.getString("doc_no"));
                __objBody.put("branch_code", __result.getString("branch_code"));
                __objBody.put("branch_name", __result.getString("branch_name"));
                __objBody.put("cust_code", __result.getString("cust_code"));
                __objBody.put("cust_name", __result.getString("cust_name"));
                __objBody.put("wh_code", __result.getString("wh_code"));
                __objBody.put("shelf_code", __result.getString("shelf_code"));
                __objBody.put("qty", __result.getInt("qty"));
                __objBody.put("unit_name", __result.getString("unit_name"));
                __objBody.put("unit_code", __result.getString("unit_code"));
                __objBody.put("price", __result.getInt("price"));
                __objBody.put("discount", __result.getString("discount"));
                __objBody.put("sum_amount", __result.getInt("sum_amount"));

                __jsonResult.put(__objBody);
            }

            __objTmp.put("data5", __jsonResult);
        }
        __objTmp.put("success", true);

        return __objTmp;
    }

    private Integer getSyncBranchList(Connection conn) throws SQLException {
        Integer branchCount = 0;
        String __strQuery = "SELECT count(*) AS branch_count FROM sync_branch_list";
        PreparedStatement __stmt = conn.prepareStatement(__strQuery);
        ResultSet __result = __stmt.executeQuery();
        while (__result.next()) {
            branchCount = __result.getInt("branch_count");
        }

        return branchCount;
    }

    private String getBarcode(Connection conn, String strItemCode) throws SQLException {
        String __strBarCode = "";
        String __strQuery = "SELECT barcode FROM ic_inventory_barcode WHERE ic_code='" + strItemCode + "' ORDER BY barcode";
        PreparedStatement __stmt = conn.prepareStatement(__strQuery);
        ResultSet __result = __stmt.executeQuery();
        Integer count = 0;
        while (__result.next()) {
            if (count > 0) {
                __strBarCode += ",";
            }
            __strBarCode += __result.getString("barcode");
            count++;
        }

        return __strBarCode;
    }

    private JSONArray getSubData(Connection conn, String strQuery) throws SQLException {
        JSONArray __jsonResult = new JSONArray();
        PreparedStatement __stmt = conn.prepareCall(strQuery);
        ResultSet __result = __stmt.executeQuery();

        while (__result.next()) {
            JSONObject __objBody = new JSONObject();
            __objBody.put("price", __result.getInt("price"));
            __objBody.put("doc_date", __result.getString("doc_date"));
            __objBody.put("doc_time", __result.getString("doc_time"));
            __objBody.put("doc_no", __result.getString("doc_no"));
            __objBody.put("qty", __result.getInt("qty"));

            __jsonResult.put(__objBody);
        }

        return __jsonResult;
    }

    private JSONObject loadLineChart(Connection conn, JSONObject params) throws SQLException {
        JSONObject __objTmp = new JSONObject("{'success': false}");
        String __strDateFrom = !params.isNull("from_date") && !params.getString("from_date").isEmpty() ? params.getString("from_date") : "";
        String __strDateTo = !params.isNull("to_date") && !params.getString("to_date").isEmpty() ? params.getString("to_date") : "";
        String __strWareHouse = !params.isNull("warehouse") && !params.getString("warehouse").isEmpty() ? params.getString("warehouse") : "";
        String __strItemCode = !params.isNull("ic_code") && !params.getString("ic_code").isEmpty() ? params.getString("ic_code") : "";

        String __strQueryExtend = "";
        __strQueryExtend += !__strWareHouse.equals("") ? " AND wh_code='" + __strWareHouse + "'" : "";
        __strQueryExtend += !__strItemCode.equals("") ? " AND item_code='" + __strItemCode + "'" : "";

        String __strQuery = "select * from (select doc_date,trans_flag,inquiry_type,sum(sum_amount_exclude_vat) as sum_amount_exclude_vat,sum(sum_of_cost_1) as sum_of_cost from ic_trans_detail where doc_date between " + __strDateFrom + " and " + __strDateTo + __strQueryExtend + " and (trans_flag in (44,46,12,310) and last_status=0) group by doc_date,trans_flag,inquiry_type) as temp1 where sum_amount_exclude_vat<>0 or sum_of_cost<>0 order by doc_date";
        PreparedStatement __stmt = conn.prepareStatement(__strQuery);
        ResultSet __result = __stmt.executeQuery();
        JSONArray __jsonResult = ResponeUtil.query2Array(__result);

        __objTmp.put("data", __jsonResult);
        __objTmp.put("success", true);
        return __objTmp;
    }

}
