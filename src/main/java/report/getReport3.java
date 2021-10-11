package report;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils._global;
import utils._routine;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import org.json.*;

/**
 *
 * @author SML-DEV-PC5
 */
@WebServlet(urlPatterns = {"/getReporter3"})
public class getReport3 extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession __session = request.getSession();
        String search = request.getParameter("search_item");
        String group_mail = request.getParameter("group_mail");
        String group_sub = request.getParameter("group_sub");
        String group_sub2 = request.getParameter("group_sub2");
        String branch_code = request.getParameter("branchcode");
        String department_code = request.getParameter("department_code");
        String item_brand = request.getParameter("itembrand");
        String item_pattern = request.getParameter("itempattern");
        String category = request.getParameter("category");
        String emp_code = request.getParameter("empcode");
        String ar_code = request.getParameter("arcode");
        String date_end = request.getParameter("date_end");
        String date_begin = request.getParameter("date_begin");

        String __dbname = __session.getAttribute("dbname").toString();
        String __providerDatabaseName = __session.getAttribute("provider").toString();

        PrintWriter out = response.getWriter();
        _routine __routine = new _routine();

        StringBuilder __result = new StringBuilder();

        String where_item_like = "";
        String where_group_main = "";
        String where_group_sub = "";
        String where_group_sub2 = "";
        String where_branch_code = "";
        String where_category = "";
        String where_item_brand = "";
        String where_item_pattern = "";
        String where_emp_code = "";
        String where_ar_code = "";
        String where_department_code = "";

        if (!category.equals("")) {
            where_category = " and (select item_category from ic_inventory where code=item_code) in (" + category + ") ";
        }

        if (!search.equals("")) {
            String[] sptSearches = search.split(" ");
            if (sptSearches.length > 1) {

                where_item_like += "and (";

                for (int i = 0; i < sptSearches.length; i++) {
                    if (i == 0) {
                        where_item_like += " (UPPER(item_code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        where_item_like += " or UPPER(item_name) LIKE UPPER('%" + sptSearches[i] + "%')) ";

                    } else {
                        where_item_like += " and ( UPPER(item_code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        where_item_like += " or UPPER(item_name) LIKE UPPER('%" + sptSearches[i] + "%')) ";

                    }
                }
                where_item_like += " )";
            } else {
                where_item_like = " and item_code LIKE upper('%" + search + "%') or item_name LIKE upper('%" + search + "%') ";
            }
        }

        if (!group_mail.equals("")) {
            where_group_main = " and (select group_main from ic_inventory where code=item_code) = '" + group_mail + "' ";
        }
        if (!group_sub.equals("")) {
            where_group_sub = " and (select group_sub from ic_inventory where code=item_code) = '" + group_sub + "' ";
        }
        if (!group_sub2.equals("")) {
            where_group_sub2 = " and (select group_sub2 from ic_inventory where code=item_code) = '" + group_sub2 + "' ";
        }
        if (!branch_code.equals("")) {
            where_branch_code = " and (select ic_trans.branch_code from ic_trans where ic_trans.doc_no = ic_trans_detail.doc_no) in  (" + branch_code + ") ";
        }

        if (!item_brand.equals("")) {
            where_item_brand = " and (select item_brand from ic_inventory where code=item_code) in (" + item_brand + ") ";
        }
        if (!item_pattern.equals("")) {
            where_item_pattern = " and (select item_pattern from ic_inventory where code=item_code) in (" + item_pattern + ") ";
        }
        if (!department_code.equals("")) {
            where_department_code = " and (select ic_trans.department_code from ic_trans where ic_trans.doc_no = ic_trans_detail.doc_no) in (" + department_code + ") ";
        }
        if (!emp_code.equals("")) {
            where_emp_code = " and (select ic_trans.sale_code from ic_trans where ic_trans.doc_no = ic_trans_detail.doc_no) in (" + emp_code + ") ";
        }
        if (!ar_code.equals("")) {
            where_ar_code = " and cust_code in (" + ar_code + ") ";
        }
        JSONArray jsarr = new JSONArray();
        String __Where = "select trans_flag,\n"
                + "concat(\n"
                + "sale_code,' : ',\n"
                + "(select name_1 from erp_user where code=sale_code)) as sale_name,\n"
                + "concat(\n"
                + "(select item_category from ic_inventory where code=item_code),' : ',\n"
                + "(select name_1 from ic_category where code=(select item_category from ic_inventory where code=item_code))\n"
                + ") as category_name,\n"
                + "\n"
                + "concat(\n"
                + "(select group_main from ic_inventory where code=item_code),' : ',\n"
                + "(select name_1 from ic_group where code=(select group_main from ic_inventory where code=item_code)) \n"
                + ")as group_main_name,\n"
                + "concat(\n"
                + "(select group_sub from ic_inventory where code=item_code) ,' : ',\n"
                + "(select name_1 from ic_group_sub where code=(select group_sub from ic_inventory where code=item_code))\n"
                + ") as group_sub_name,\n"
                + "concat(\n"
                + "(select group_sub2 from ic_inventory where code=item_code) ,' : ',\n"
                + "(select name_1 from ic_group_sub2 where code=(select group_sub2 from ic_inventory where code=item_code)) \n"
                + ")as group_sub_name2,\n"
                + "concat(\n"
                + "(select item_pattern from ic_inventory where code=item_code) ,' : ',\n"
                + "(select name_1 from ic_pattern where code=(select item_pattern from ic_inventory where code=item_code))\n"
                + ") as pattern_name,\n"
                + "concat(\n"
                + "(select item_brand from ic_inventory where code=item_code) ,' : ',\n"
                + "(select name_1 from ic_brand where code=(select item_brand from ic_inventory where code=item_code)) \n"
                + ")as brand_name,\n"
                + "concat(\n"
                + "(select dimension_2 from ic_inventory_detail where ic_code=item_code) ,' : ',\n"
                + "(select name_1 from ic_dimension where code=(select dimension_2 from ic_inventory_detail where ic_code=item_code)) \n"
                + ")as color_name,\n"
                + "\n"
                + "--มิติสินค้า 1\n"
                + "doc_date,doc_no,\n"
                + "concat(\n"
                + "(select ic_trans.department_code from ic_trans where ic_trans.doc_no = ic_trans_detail.doc_no),' : ',\n"
                + "(select name_1 from erp_department_list where code=(select ic_trans.department_code from ic_trans where ic_trans.doc_no = ic_trans_detail.doc_no)) \n"
                + ")as department_name,\n"
                + "concat(\n"
                + "(select area_code from ar_customer_detail where ar_code=cust_code),' : ',\n"
                + "(select name_1 from ar_sale_area where code=(select area_code from ar_customer_detail where ar_code=cust_code))\n"
                + ") as area_name,\n"
                + "concat(\n"
                + "cust_code,' : ',(select name_1 from ar_customer where code=cust_code)\n"
                + ") as cust_name,\n"
                + "concat(\n"
                + "item_code,' : ',(select name_1 from ic_inventory where code=item_code)) as item_name,\n"
                + "concat(wh_code,':',shelf_code) as wh_shelf,(select name_1 from ic_unit where code=unit_code) as unit_name,\n"
                + "(select name_1 from transport_type where code=(select ic_trans.transport_code from ic_trans where ic_trans.doc_no=ic_trans_detail.doc_no)) as transport_name,\n"
                + " CASE\n"
                + "  WHEN (trans_flag in (44) and last_status = 0) THEN ((qty)*1)\n"
                + "  WHEN (trans_flag in (44) and last_status = 1) THEN ((qty)*0)\n"
                + "  WHEN (trans_flag in (46) and last_status = 0) THEN ((qty)*1)\n"
                + "  WHEN (trans_flag in (46) and last_status = 1) THEN ((qty)*0)\n"
                + "  WHEN (trans_flag in (48) and last_status = 0) THEN ((qty)*-1)\n"
                + "  WHEN (trans_flag in (48) and last_status = 1) THEN ((qty)*0)\n"
                + " END AS qty_t,\n"
                + " CASE\n"
                + "  WHEN (trans_flag in (44) and last_status = 0) THEN ((sum_amount_exclude_vat)*1)\n"
                + "  WHEN (trans_flag in (44) and last_status = 1) THEN ((sum_amount_exclude_vat)*0)\n"
                + "  WHEN (trans_flag in (46) and last_status = 0) THEN ((sum_amount_exclude_vat)*1)\n"
                + "  WHEN (trans_flag in (46) and last_status = 1) THEN ((sum_amount_exclude_vat)*0)\n"
                + "  WHEN (trans_flag in (48) and last_status = 0) THEN ((sum_amount_exclude_vat)*-1)\n"
                + "  WHEN (trans_flag in (48) and last_status = 1) THEN ((sum_amount_exclude_vat)*0)\n"
                + " END AS sum_amount_exclude_vat_t,\n"
                + " CASE\n"
                + "  WHEN (trans_flag in (44) and last_status = 0) THEN ((sum_of_cost_1)*1)\n"
                + "  WHEN (trans_flag in (44) and last_status = 1) THEN ((sum_of_cost_1)*0)\n"
                + "  WHEN (trans_flag in (46) and last_status = 0) THEN ((sum_of_cost_1)*1)\n"
                + "  WHEN (trans_flag in (46) and last_status = 1) THEN ((sum_of_cost_1)*0)\n"
                + "  WHEN (trans_flag in (48) and last_status = 0) THEN ((sum_of_cost_1)*-1)\n"
                + "  WHEN (trans_flag in (48) and last_status = 1) THEN ((sum_of_cost_1)*0)\n"
                + " END AS sum_of_cost_1_t,\n"
                + " CASE\n"
                + "  WHEN (trans_flag in (44) and last_status = 0) THEN (((sum_amount_exclude_vat-sum_of_cost_1))*1)\n"
                + "  WHEN (trans_flag in (44) and last_status = 1) THEN (((sum_amount_exclude_vat-sum_of_cost_1))*0)\n"
                + "  WHEN (trans_flag in (46) and last_status = 0) THEN (((sum_amount_exclude_vat-sum_of_cost_1))*1)\n"
                + "  WHEN (trans_flag in (46) and last_status = 1) THEN (((sum_amount_exclude_vat-sum_of_cost_1))*0)\n"
                + "  WHEN (trans_flag in (48) and last_status = 0) THEN (((sum_amount_exclude_vat-sum_of_cost_1))*-1)\n"
                + "  WHEN (trans_flag in (48) and last_status = 1) THEN (((sum_amount_exclude_vat-sum_of_cost_1))*0)\n"
                + " END AS GP,\n"
                + "(select ic_trans.branch_code from ic_trans where ic_trans.doc_no = ic_trans_detail.doc_no) as branch_code,\n"
                + " CASE\n"
                + "  WHEN ((trans_flag = 44) and last_status = 0) THEN 'ขาย'\n"
                + "  WHEN ((trans_flag = 44) and last_status = 1) THEN 'ยกเลิกขาย'\n"
                + "  WHEN ((trans_flag = 46) and last_status = 0) THEN 'เพิ่มหนี้'\n"
                + "  WHEN ((trans_flag = 46) and last_status = 1) THEN 'ยกเลิกเพิ่มหนี้'\n"
                + "  WHEN ((trans_flag = 48) and last_status = 0) THEN 'ลดหนี้'\n"
                + "  WHEN ((trans_flag = 48) and last_status = 1) THEN 'ยกเลิกลดหนี้'\n"
                + " END AS status_name\n"
                + "\n"
                + "from ic_trans_detail \n"
                + "\n"
                + "where trans_flag in (44,45,46,47,48,49) \n"
                + "and doc_date between '" + date_begin + "' and '" + date_end + "'\n"
                + " and item_type <> 3 " + where_branch_code + where_ar_code + where_item_like + where_group_main + where_group_sub + where_group_sub2 + where_category + where_item_brand + where_item_pattern + where_department_code + where_emp_code
                + " order by doc_date,doc_no,roworder";

        try {
            System.out.println("__query " + __Where);
            Connection __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__providerDatabaseName));

            PreparedStatement __stmtCount = __conn.prepareStatement(__Where, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsCount = __stmtCount.executeQuery();
            int i = 0;
            while (__rsCount.next()) {
                DecimalFormat formatter = new DecimalFormat("#,###.00");

                JSONObject obj = new JSONObject();

                obj.put("branch_code", __rsCount.getString("branch_code"));
                obj.put("sale_name", __rsCount.getString("sale_name"));
                obj.put("doc_date", __rsCount.getString("doc_date"));
                obj.put("doc_no", __rsCount.getString("doc_no"));
                obj.put("cust_name", __rsCount.getString("cust_name"));
                obj.put("item_name", __rsCount.getString("item_name"));
                obj.put("wh_shelf", __rsCount.getString("wh_shelf"));
                obj.put("unit_name", __rsCount.getString("unit_name"));
                obj.put("qty_t", __rsCount.getString("qty_t") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_t"))) : "");
                obj.put("sum_amount_exclude_vat_t", __rsCount.getString("sum_amount_exclude_vat_t") != null ? formatter.format(Double.parseDouble(__rsCount.getString("sum_amount_exclude_vat_t"))) : "");
                obj.put("sum_of_cost_1_t", __rsCount.getString("sum_of_cost_1_t") != null ? formatter.format(Double.parseDouble(__rsCount.getString("sum_of_cost_1_t"))) : "");
                obj.put("gp", __rsCount.getString("gp") != null ? formatter.format(Double.parseDouble(__rsCount.getString("gp"))) : "");
                obj.put("gpx", formatter.format((Double.parseDouble(__rsCount.getString("gp")) / Double.parseDouble(__rsCount.getString("sum_of_cost_1_t"))) * 100));

                jsarr.put(obj);

            }

            // find inactive item check
            __rsCount.close();
            __stmtCount.close();
            __conn.close();
            //System.out.println(jsarr);
            response.getWriter().print(jsarr);
        } catch (Exception ex) {
            response.getWriter().write("" + ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
