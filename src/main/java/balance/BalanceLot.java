package balance;

import java.io.IOException;
import java.sql.ResultSet;
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

@WebServlet(name = "balance-lot", urlPatterns = {"/balance-lot"})
public class BalanceLot extends HttpServlet {

    private String __strDatabaseName;
    private String __strProviderCode;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject __objResult = new JSONObject("{'success': false}");

        HttpSession __session = request.getSession();
        if (__session.getAttribute("dbname") != null && __session.getAttribute("provider") != null) {
            __strDatabaseName = __session.getAttribute("dbname").toString();
            __strProviderCode = __session.getAttribute("provider").toString();
            if (request.getParameterMap().containsKey("action_name")) {
                try {
                    switch (request.getParameter("action_name")) {
                        case "loadBranch":
                            __objResult = this.loadBranch();
                            break;
                        case "loadBalanceList":
                            __objResult = this.loadBalanceList(ResponeUtil.str2Json(request.getParameter("data")));
                            break;
                        case "loadBalanceDetail":
                            __objResult = this.loadBalanceDetail(ResponeUtil.str2Json(request.getParameter("data")));
                            break;
                        case "loadBalanceLotList":
                            __objResult = this.loadBalanceLotList(ResponeUtil.str2Json(request.getParameter("data")));
                            break;
                        case "loadBalanceLotDetail":
                            __objResult = this.loadBalanceLotDetail(ResponeUtil.str2Json(request.getParameter("data")));
                            break;
                        case "loadSearchItemList":
                            __objResult = this.loadSearchItemList();
                            break;
                        case "loadSearchWarehouseList":
                            __objResult = this.loadSearchWarehouseList();
                            break;
                        case "loadSearchShelfList":
                            __objResult = this.loadSearchShelfList();
                            break;
                        case "loadSearchGroupSubList":
                            __objResult = this.loadSearchGroupSubList();
                            break;
                        case "loadSearchGroupSub2List":
                            __objResult = this.loadSearchGroupSub2List();
                            break;
                        case "loadSearchBrandList":
                            __objResult = this.loadSearchBrandList();
                            break;
                        case "loadSearchModelList":
                            __objResult = this.loadSearchModelList();
                            break;

                        case "loadSearchCategoryList":
                            __objResult = this.loadSearchCategoryList();
                            break;
                        case "loadSearchFormatList":
                            __objResult = this.loadSearchFormatList();
                            break;
                        default:
                            __objResult.put("err_msg", "action_name not found.");
                            break;
                    }
                } catch (Exception ex) {
                    __objResult.put("err_msg", ex.getMessage());
                }
            } else {
                __objResult.put("err_msg", "action_name not found.");
            }
        } else {
            __objResult.put("err_msg", "you don\'t have permission");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(__objResult);
    }

    private JSONObject loadBranch() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1 FROM erp_branch_list ORDER BY code";

        __objResult.put("success", true);
        __objResult.put("data", __routine._excute2Array(__strQuery, null));

        return __objResult;
    }

    private JSONObject loadBalanceList(JSONObject param) throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        Integer flag = 0;

        String __itemCode = !param.isNull("search_item") ? !param.getString("search_item").isEmpty() ? param.getString("search_item") : "" : "";
        String __warehouse = !param.isNull("warehouse_list") ? !param.getString("warehouse_list").isEmpty() ? param.getString("warehouse_list") : "" : "";
        String __shelfFrom = !param.isNull("shelf_from") ? !param.getString("shelf_from").isEmpty() ? param.getString("shelf_from") : "" : "";
        String __shelfTo = !param.isNull("shelf_to") ? !param.getString("shelf_to").isEmpty() ? param.getString("shelf_to") : "" : "";
        String __groupSub = !param.isNull("group_sub") ? !param.getString("group_sub").isEmpty() ? param.getString("group_sub") : "" : "";
        String __groupSub2 = !param.isNull("group_sub2") ? !param.getString("group_sub2").isEmpty() ? param.getString("group_sub2") : "" : "";
        String __brand = !param.isNull("brand") ? !param.getString("brand").isEmpty() ? param.getString("brand") : "" : "";
        String __model = !param.isNull("model") ? !param.getString("model").isEmpty() ? param.getString("model") : "" : "";
        String __category = !param.isNull("category") ? !param.getString("category").isEmpty() ? param.getString("category") : "" : "";
        String __format = !param.isNull("format") ? !param.getString("format").isEmpty() ? param.getString("format") : "" : "";
        String __sort = !param.isNull("sort") ? !param.getString("sort").isEmpty() ? param.getString("sort") : "" : "";
        String __sortCol = !param.isNull("sort_col") ? !param.getString("sort_col").isEmpty() ? param.getString("sort_col") : "" : "";

        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strPaginationQuery = "OFFSET " + __strOffset + " LIMIT " + __strLimit;
        StringBuilder __strQuery = new StringBuilder();
        StringBuilder __strIcQuerySub = new StringBuilder();
        StringBuilder __strWarehouseQuerySub = new StringBuilder();
        StringBuilder __strShelfQuerySub = new StringBuilder();

        __strIcQuerySub.append("select string_agg( ic_inventory.code,',') as code from ic_inventory where 1=1 ");
        __strQuery.append("select ");
        __strQuery.append("ic_inventory.code as item_code, ");
        __strQuery.append("ic_inventory.name_1 as item_name, ");
        __strQuery.append(" '' as shelf_list ,");
        __strQuery.append(" '' as warehouse_list ,");
        __strQuery.append("ic_inventory.balance_qty, ");
        __strQuery.append("ic_inventory.unit_standard as unit_code, ");
        __strQuery.append("coalesce(( select ic_inventory_price.sale_price2 from ic_inventory_price where ic_inventory_price.cust_group_1 = 'GR02' and ic_inventory_price.sale_type = 0 and ic_inventory_price.status = 1 and ic_inventory_price.price_type = 2 and ic_inventory_price.price_mode = 1 and current_date between ic_inventory_price.from_date and ic_inventory_price.to_date and ic_inventory_price.ic_code = ic_inventory.code limit 1), '0') as price, ");
        __strQuery.append("coalesce(( ");
        __strQuery.append("select location from sml_ic_function_stock_balance_warehouse_location(current_date, ");
        __strQuery.append("       ic_inventory.code, ");
        __strQuery.append("       '', ");
        __strQuery.append("       '') where balance_qty > 0 order by location asc limit 1 ");
        __strQuery.append("),'') as year_weak ");
        __strQuery.append("from ");
        __strQuery.append("ic_inventory ");
        __strQuery.append("where 1=1 and ic_inventory.balance_qty > 0 ");

        if (__itemCode.trim().length() > 0) {
            __strQuery.append(" and ( lower(ic_inventory.code)  like lower('%" + __itemCode.trim().replace(" ", "%") + "%') or lower(ic_inventory.name_1) like lower('%" + __itemCode.trim().replace(" ", "%") + "%'))");
            __strIcQuerySub.append(" and ( lower(ic_inventory.code)  like lower('%" + __itemCode.trim().replace(" ", "%") + "%') or lower(ic_inventory.name_1) like lower('%" + __itemCode.trim().replace(" ", "%") + "%'))");
        }

        if (__groupSub.trim().length() > 0) {
            String[] __keyword = __groupSub.trim().split(",");
            __strQuery.append(" and ic_inventory.group_sub in ('" + String.join("','", __keyword) + "')");
            __strIcQuerySub.append(" and ic_inventory.group_sub in ('" + String.join("','", __keyword) + "')");
        }

        if (__groupSub2.trim().length() > 0) {
            String[] __keyword = __groupSub2.trim().split(",");
            __strQuery.append(" and ic_inventory.group_sub2 in ('" + String.join("','", __keyword) + "')");
            __strIcQuerySub.append(" and ic_inventory.group_sub2 in ('" + String.join("','", __keyword) + "')");
        }

        if (__brand.trim().length() > 0) {
            String[] __keyword = __brand.trim().split(",");
            __strQuery.append(" and ic_inventory.item_brand in ('" + String.join("','", __keyword) + "')");
            __strIcQuerySub.append(" and ic_inventory.item_brand in ('" + String.join("','", __keyword) + "')");
        }

        if (__model.trim().length() > 0) {
            String[] __keyword = __model.trim().split(",");
            __strQuery.append(" and ic_inventory.item_model in ('" + String.join("','", __keyword) + "')");
            __strIcQuerySub.append(" and ic_inventory.item_model in ('" + String.join("','", __keyword) + "')");
        }

        if (__category.trim().length() > 0) {
            String[] __keyword = __category.trim().split(",");
            __strQuery.append(" and ic_inventory.item_category in ('" + String.join("','", __keyword) + "')");
            __strIcQuerySub.append(" and ic_inventory.item_category in ('" + String.join("','", __keyword) + "')");
        }

        if (__format.trim().length() > 0) {
            String[] __keyword = __format.trim().split(",");
            __strQuery.append(" and ic_inventory.item_pattern in ('" + String.join("','", __keyword) + "')");
            __strIcQuerySub.append(" and ic_inventory.item_pattern in ('" + String.join("','", __keyword) + "')");
        }

        if (__warehouse.trim().length() > 0) {
            __strWarehouseQuerySub.append(__warehouse.trim());
            flag = 1;
        } else {
            __strWarehouseQuerySub.append("");
        }

        if (__shelfFrom.trim().length() > 0 && __shelfTo.trim().length() == 0) {
            __strShelfQuerySub.append(__shelfFrom.trim());
            flag = 1;
        } else if (__shelfFrom.trim().length() > 0 && __shelfTo.trim().length() > 0) {
            __strShelfQuerySub.append("select string_agg(code,',') as code from ic_shelf where code between '" + __shelfFrom.trim() + "' and '" + __shelfTo.trim() + "'");
            flag = 2;
        } else {
            __strShelfQuerySub.append("");
        }

        if (flag == 2) {

            ResultSet __shelfList = __routine._excute(__strShelfQuerySub.toString(), null);
            __strShelfQuerySub = new StringBuilder();
            while (__shelfList.next()) {
                __strShelfQuerySub.append(__shelfList.getString("code"));
            }
        }
        if (flag != 0) {
            System.out.println("__strIcQuerySub "+__strIcQuerySub);
            ResultSet __icList = __routine._excute(__strIcQuerySub.toString(), null);
            __strIcQuerySub = new StringBuilder();
            while (__icList.next()) {
                __strIcQuerySub.append(__icList.getString("code"));
            }

            if (__strIcQuerySub.toString().trim().length() == 0) {
                __strIcQuerySub.append("");
            }

            __strQuery = new StringBuilder();
            __strQuery.append("select ");
            __strQuery.append("	stk.ic_code as item_code, ");
            __strQuery.append("	stk.ic_name as item_name, ");
            __strQuery.append("	stk.ic_unit_code as unit_code, ");
            __strQuery.append(" coalesce(( select ic_inventory_price.sale_price2 from ic_inventory_price where ic_inventory_price.cust_group_1 = 'GR02' and ic_inventory_price.sale_type = 0 and ic_inventory_price.status = 1 and ic_inventory_price.price_type = 2 and ic_inventory_price.price_mode = 1 and current_date between ic_inventory_price.from_date and ic_inventory_price.to_date and ic_inventory_price.ic_code = stk.ic_code limit 1), '0') as price, ");
            __strQuery.append("	(select location from sml_ic_function_stock_balance_warehouse_location(current_date, stk.ic_code ,'@wh_code@','@shelf_code@') where balance_qty > 0 order by location asc limit 1) as year_weak ,");
            __strQuery.append(" '@shelf_code@' as shelf_list ,");
            __strQuery.append(" '@wh_code@' as warehouse_list ,");
            __strQuery.append("	sum(balance_qty) as balance_qty ");
            __strQuery.append("from ");
            __strQuery.append("	sml_ic_function_stock_balance_warehouse_location(current_date,'@ic_code@','@wh_code@','@shelf_code@') as stk ");
            __strQuery.append("where ");
            __strQuery.append("	stk.balance_qty > 0 ");
            __strQuery.append("group by ");
            __strQuery.append("	stk.ic_code , ");
            __strQuery.append("	stk.ic_name , ");
            __strQuery.append("	stk.ic_unit_code ");
            __strQuery = new StringBuilder().append(
                    __strQuery.toString()
                            .replace("@ic_code@", __strIcQuerySub.toString())
                            .replace("@wh_code@", __strWarehouseQuerySub.toString())
                            .replace("@shelf_code@", __strShelfQuerySub.toString())
            );
            if (__sortCol.equals("")) {
                __strQuery.append(" order by stk.ic_code " + __sort + " ");
            } else {
                __strQuery.append(" order by " + __sortCol + " " + __sort + " ");
            }

        } else {

            if (__sortCol.equals("")) {
                __strQuery.append(" order by ic_inventory.code " + __sort + " ");
            } else {
                __strQuery.append(" order by " + __sortCol + " " + __sort + " ");
            }
            //__strQuery.append(" order by ic_inventory.code " + __sort + " ");
        }
        System.out.println("__strPaginationQuery "+__strQuery.toString() + __strPaginationQuery);
        ResultSet __rsData = __routine._excute(__strQuery.toString() + __strPaginationQuery, null);
        JSONArray __jsResult = new JSONArray();

        while (__rsData.next()) {
            JSONObject __objData = new JSONObject();
            __objData.put("item_code", __rsData.getString("item_code"));
            __objData.put("item_name", __rsData.getString("item_name"));
            __objData.put("price", String.format("%,.2f", Float.parseFloat(__rsData.getString("price"))));
            __objData.put("balance_qty", String.format("%,.0f", Float.parseFloat(__rsData.getString("balance_qty"))));
            __objData.put("unit_code", __rsData.getString("unit_code"));
            __objData.put("year_weak", __rsData.getString("year_weak"));
            __objData.put("shelf_list", __rsData.getString("shelf_list"));
            __objData.put("warehouse_list", __rsData.getString("warehouse_list"));
            __jsResult.put(__objData);
        }

        __objResult.put("data", __jsResult);
        __objResult.put("row_count", __routine._rowCount(__strQuery.toString(), null));
        __objResult.put("success", true);

        return __objResult;
    }

    private JSONObject loadBalanceDetail(JSONObject param) throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));
        String __strItemCode = !param.isNull("item_code") ? !param.getString("item_code").isEmpty() ? param.getString("item_code") : "" : "";
        String __strShelf = !param.isNull("shelf_list") ? !param.getString("shelf_list").isEmpty() ? param.getString("shelf_list") : "" : "";
        String __warehouse = !param.isNull("warehouse_list") ? !param.getString("warehouse_list").isEmpty() ? param.getString("warehouse_list") : "" : "";

        StringBuilder __strQuery = new StringBuilder();
        __strQuery.append("select ");
        __strQuery.append("ic_code as item_code, ");
        __strQuery.append("ic_name as item_name, ");
        __strQuery.append("warehouse, ");
        __strQuery.append("location, ");
        __strQuery.append("balance_qty,   ");
        __strQuery.append("ic_unit_code as unit_code   ");
        __strQuery.append("from sml_ic_function_stock_balance_warehouse_location( ");
        __strQuery.append("current_date, ");
        __strQuery.append("'" + __strItemCode + "', ");
        __strQuery.append("'@wh_code@', ");
        __strQuery.append("'@shelf_code@')  ");
        __strQuery.append("where balance_qty > 0 ");

        if (__warehouse.trim().length() > 0) {
            __strQuery = new StringBuilder().append(__strQuery.toString().replace("@wh_code@", __warehouse.trim().toString()));
        } else {
            __strQuery = new StringBuilder().append(__strQuery.toString().replace("@wh_code@", ""));
        }

        if (__strShelf.trim().length() > 0) {
            __strQuery = new StringBuilder().append(__strQuery.toString().replace("@shelf_code@", __strShelf.trim().toString()));
        } else {
            __strQuery = new StringBuilder().append(__strQuery.toString().replace("@shelf_code@", ""));
        }
        System.out.println("__strQuery.toString() " + __strQuery.toString());
        ResultSet __rsData = __routine._excute(__strQuery.toString(), null);
        JSONArray __jsResult = new JSONArray();

        while (__rsData.next()) {

            String __strQuery2 = "select item_code,\n"
                    + " (select accrued_out_qty from ic_inventory where code=item_code)/((select unit_standard_stand_value from ic_inventory where code=item_code)/(select unit_standard_divide_value from ic_inventory where code=item_code)) as aaa\n"
                    + " from ic_trans_detail \n"
                    + " where item_code = '" + __strItemCode + "' and wh_code = '" + __rsData.getString("warehouse") + "' and shelf_code = '" + __rsData.getString("location") + "'\n"
                    + " limit 1";

            ResultSet __rsData2 = __routine._excute(__strQuery2.toString(), null);
            //System.out.println("__strQuery2.toString() " + __strQuery2.toString());
            JSONObject __objData = new JSONObject();
            __objData.put("item_code", __rsData.getString("item_code"));
            __objData.put("item_name", __rsData.getString("item_name"));
            __objData.put("balance_qty", String.format("%,.0f", Float.parseFloat(__rsData.getString("balance_qty"))));
            __objData.put("unit_code", __rsData.getString("unit_code"));
            __objData.put("warehouse", __rsData.getString("warehouse"));
            __objData.put("location", __rsData.getString("location"));
            __objData.put("overdue", "0");
            while (__rsData2.next()) {
                __objData.put("overdue", __rsData2.getString("aaa"));
            }
            __jsResult.put(__objData);
        }

        __objResult.put("data", __jsResult);
        __objResult.put("row_count", __routine._rowCount(__strQuery.toString(), null));
        __objResult.put("success", true);

        return __objResult;
    }

    private JSONObject loadBalanceLotList(JSONObject param) throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strKeyWord = !param.isNull("search_item") ? !param.getString("search_item").isEmpty() ? param.getString("search_item") : "" : "";
        String __strFromYear = !param.isNull("from_year") ? !param.getString("from_year").isEmpty() ? param.getString("from_year") : "" : "";
        String __strToYear = !param.isNull("to_year") ? !param.getString("to_year").isEmpty() ? param.getString("to_year") : "" : "";
        String __strWarehouse = !param.isNull("wh_code") ? !param.getString("wh_code").isEmpty() ? param.getString("wh_code") : "" : "";

        StringBuilder __whereSearchItem = new StringBuilder();
        String __whereSearchYear = "";
        String __whereSearchWarehouse = "";

        if (!__strFromYear.equals("") && !__strToYear.equals("")) {
            if (__strFromYear.equals(__strToYear)) {
                __whereSearchYear = " AND ((EXTRACT(year FROM mfd_date))='" + __strFromYear + "') \n";
            } else {
                __whereSearchYear = " AND ((EXTRACT(year FROM mfd_date)) BETWEEN '" + __strFromYear + "' AND '" + __strToYear + "') \n";
            }
        }

        if (!__strWarehouse.equals("")) {
            __whereSearchWarehouse = " AND wh_code='" + __strWarehouse + "'\n ";
        }

        if (__strKeyWord.trim().length() > 0) {
            String[] __fieldList = {"item_code", "item_name"};
            String[] __keyword = __strKeyWord.trim().split(",");
            for (String __fieldList1 : __fieldList) {
                if (__keyword.length > 0) {
                    if (__whereSearchItem.length() > 0) {
                        __whereSearchItem.append(" OR ");
                    } else {
                        __whereSearchItem.append(" AND ");
                    }
                    __whereSearchItem.append("(");
                    for (int __loop = 0; __loop < __keyword.length; __loop++) {
                        if (__loop > 0) {
                            __whereSearchItem.append(" OR ");
                        }
                        __whereSearchItem.append("UPPER(").append(__fieldList1).append(") LIKE \'%").append(__keyword[__loop].toUpperCase()).append("%\'");
                    }
                    __whereSearchItem.append(")\n");
                }
            }
        }

        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strPaginationQuery = "OFFSET " + __strOffset + " LIMIT " + __strLimit;
        String __strQuery = ""
                + "SELECT item_code,(select ic_inventory.name_1 from ic_inventory where ic_inventory.code=item_code) as item_name\n"
                + " ,COALESCE((SELECT unit_standard FROM ic_inventory WHERE ic_inventory.code=item_code), '') AS ic_unit_code\n"
                + "\n"
                + " ,COALESCE((SELECT price_0 FROM ic_inventory_price_formula WHERE ic_code=ic_trans_detail.item_code AND unit_code=ic_trans_detail.unit_code \n"
                + "ORDER BY price_0  fetch first 1 rows only), '0') AS price\n"
                + "\n"
                + " ,COALESCE((select ic_trans_detail_lot.mfn_name from ic_trans_detail_lot where ic_trans_detail_lot.item_code=ic_trans_detail.item_code  AND \n"
                + "((EXTRACT(year FROM mfd_date))=(EXTRACT(YEAR FROM now()))) \n"
                + " order by ic_trans_detail_lot.mfn_name desc fetch first 1 rows only), '') AS lot_year\n"
                + "\n"
                + " ,COALESCE(SUM(calc_flag*(CASE when ((trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or \n"
                + "(trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or \n"
                + "(trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) \n"
                + "and ((EXTRACT(year FROM mfd_date))=(EXTRACT(YEAR FROM now()))) and not \n"
                + "(ic_trans_detail.doc_ref <> '' and ic_trans_detail.is_pos = 1)) then qty*(stand_value / divide_value) else 0 end)),0) as balance_qty_this_year,\n"
                + "\n"
                + "(((select ic_inventory.balance_qty from ic_inventory where code=item_code)/\n"
                + "((select ic_inventory.unit_standard_stand_value from ic_inventory where code=item_code)/\n"
                + "(select ic_inventory.unit_standard_divide_value from ic_inventory where code=item_code)))-\n"
                + "COALESCE(SUM(calc_flag*(CASE when ((trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or \n"
                + "(trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or \n"
                + "(trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) and not \n"
                + "(ic_trans_detail.doc_ref <> '' and ic_trans_detail.is_pos = 1)) then qty*(stand_value / divide_value) else 0 end)),0)) as balance_qty_old_year,\n"
                + "\n"
                + "((select ic_inventory.balance_qty from ic_inventory where code=item_code)/\n"
                + "((select ic_inventory.unit_standard_stand_value from ic_inventory where code=item_code)/\n"
                + "(select ic_inventory.unit_standard_divide_value from ic_inventory where code=item_code))) as balance_qty_all\n"
                + "\n"
                + " FROM ic_trans_detail \n"
                + " WHERE last_status=0\n"
                + " AND item_type<>5 \n"
                + " AND lot_number_1 <> 'error' \n" + __whereSearchYear + __whereSearchWarehouse + __whereSearchItem
                + " AND ((EXTRACT(year FROM mfd_date))='2018') /*AND item_code='010101-MCT-0000002'*/\n"
                + " GROUP BY item_code,unit_code\n"
                + " ORDER BY lot_year DESC\n";

        System.out.println(__strQuery);

        ResultSet __rsData = __routine._excute(__strQuery + __strPaginationQuery, null);
        JSONArray __jsResult = new JSONArray();

        while (__rsData.next()) {
            JSONObject __objData = new JSONObject();
            __objData.put("item_code", __rsData.getString("item_code"));
            __objData.put("item_name", __rsData.getString("item_name"));
            __objData.put("price", String.format("%,.2f", Float.parseFloat(__rsData.getString("price"))));
            __objData.put("balance_qty_all", String.format("%,.0f", Float.parseFloat(__rsData.getString("balance_qty_all"))));
            __objData.put("balance_qty_this_year", String.format("%,.0f", Float.parseFloat(__rsData.getString("balance_qty_this_year"))));
            __objData.put("balance_qty_old_year", String.format("%,.0f", Float.parseFloat(__rsData.getString("balance_qty_old_year"))));
            __objData.put("item_lot", __rsData.getString("lot_year"));
            __objData.put("unit_code", __rsData.getString("ic_unit_code"));

            __jsResult.put(__objData);
        }

        __objResult.put("data", __jsResult);
        __objResult.put("row_count", __routine._rowCount(__strQuery, null));
        __objResult.put("success", true);

        return __objResult;
    }

    private JSONObject loadBalanceLotDetail(JSONObject param) throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strItemCode = !param.isNull("item_code") ? !param.getString("item_code").isEmpty() ? param.getString("item_code") : "" : "";
        String __strFromYear = !param.isNull("from_year") ? !param.getString("from_year").isEmpty() ? param.getString("from_year") : "" : "";
        String __strToYear = !param.isNull("to_year") ? !param.getString("to_year").isEmpty() ? param.getString("to_year") : "" : "";

        String __whereSearchYear = "";

        if (!__strFromYear.equals("") && !__strToYear.equals("")) {
            if (__strFromYear.equals(__strToYear)) {
                __whereSearchYear = " AND ((EXTRACT(year FROM mfd_date))='" + __strFromYear + "') \n";
            } else {
                __whereSearchYear = " AND ((EXTRACT(year FROM mfd_date)) BETWEEN '" + __strFromYear + "' AND '" + __strToYear + "') \n";
            }
        }

        String __strQuery = "SELECT * FROM ("
                + "SELECT wh_code\n"
                + "	,COALESCE((SELECT unit_standard FROM ic_inventory WHERE ic_inventory.code=item_code), '') AS ic_unit_code\n"
                + "	,COALESCE(SUM(calc_flag*(CASE when ((trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) and not (ic_trans_detail.doc_ref <> '' and ic_trans_detail.is_pos = 1)) then qty*(stand_value / divide_value) else 0 end)),0) as balance_qty\n"
                + "	,COALESCE((SELECT mfn_name FROM ic_trans_detail_lot WHERE ic_trans_detail_lot.item_code=ic_trans_detail.item_code AND ic_trans_detail_lot.lot_number=ic_trans_detail.lot_number_1 " + __whereSearchYear + " ORDER BY mfn_name DESC LIMIT 1), '') AS lot_year\n"
                + "FROM ic_trans_detail \n"
                + "WHERE ic_trans_detail.last_status=0 \n"
                + "	AND ic_trans_detail.item_type<>5 \n"
                + "	AND lot_number_1 <> 'error' \n" + __whereSearchYear
                + "	AND item_code='" + __strItemCode + "'\n"
                + "GROUP BY item_code,wh_code,lot_number_1,unit_code\n"
                + "ORDER BY lot_year DESC\n"
                + ") AS final_data "
                + "WHERE balance_qty > 0";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchItemList() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1,balance_qty,book_out_qty,accrued_out_qty,accrued_in_qty,unit_standard\n"
                + ",COALESCE(sign_code,'') AS sign_code\n"
                + ",COALESCE((SELECT name_1 FROM ic_unit WHERE ic_unit.code=ic_inventory.unit_standard), '') AS unit_name \n"
                + "FROM ic_inventory";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchWarehouseList() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1\n"
                + "FROM ic_warehouse";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchShelfList() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1\n"
                + "FROM ic_shelf";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchGroupSubList() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1\n"
                + "FROM ic_group_sub";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchGroupSub2List() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1\n"
                + "FROM ic_group_sub2";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchBrandList() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1\n"
                + "FROM ic_brand";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchModelList() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1\n"
                + "FROM ic_model";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchCategoryList() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1\n"
                + "FROM ic_category";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }

    private JSONObject loadSearchFormatList() throws Exception {
        JSONObject __objResult = new JSONObject("{'success': false}");
        _routine __routine = new _routine(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));

        String __strQuery = "SELECT code,name_1\n"
                + "FROM ic_pattern";

        __objResult.put("data", __routine._excute2Array(__strQuery, null));
        __objResult.put("success", true);
        return __objResult;
    }
}
