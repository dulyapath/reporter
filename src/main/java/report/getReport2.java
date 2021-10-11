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
@WebServlet(urlPatterns = {"/getReporter2"})
public class getReport2 extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession __session = request.getSession();
        String search = request.getParameter("search_item");
        String balance_date = request.getParameter("balance_date");
        String doc_po = request.getParameter("doc_po");
        String group_mail = request.getParameter("group_mail");
        String group_sub = request.getParameter("group_sub");
        String group_sub2 = request.getParameter("group_sub2");
        String branch_code = request.getParameter("branchcode");
        String wh_code = request.getParameter("whcode");
        String item_brand = request.getParameter("itembrand");
        String item_pattern = request.getParameter("itempattern");
        String emp_code = request.getParameter("empcode");
        String ar_code = request.getParameter("arcode");
        String sale_date_end = request.getParameter("sale_date_end");
        String sale_date_begin = request.getParameter("sale_date_begin");
        String send_time = request.getParameter("send_time");
        String percent = request.getParameter("percent");
        String stock_age = request.getParameter("stock_age");

        String __dbname = __session.getAttribute("dbname").toString();
        String __providerDatabaseName = __session.getAttribute("provider").toString();

        PrintWriter out = response.getWriter();
        _routine __routine = new _routine();

        StringBuilder __result = new StringBuilder();
        String where_doc_po = "";
        String where_item_like = "";
        String where_group_main = "";
        String where_group_sub = "";
        String where_group_sub2 = "";
        String where_branch_code = "";
        String where_wh_code = "";
        String where_item_brand = "";
        String where_item_pattern = "";
        String where_emp_code = "";
        String where_ar_code = "";
        if (!doc_po.equals("")) {
            where_doc_po = " and exists(select item_code from ic_trans_detail b where ic_trans_detail.item_code = b.item_code and b.trans_flag = 6 b.doc_no = '" + doc_po + "') ";
        }
        if (!search.equals("")) {
            String[] sptSearches = search.split(" ");
            if (sptSearches.length > 1) {

                where_item_like += "and (";

                for (int i = 0; i < sptSearches.length; i++) {
                    if (i == 0) {
                        where_item_like += " ( UPPER(item_code) LIKE UPPER('%" + sptSearches[i] + "%') ";
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
            where_group_main = " and (select group_main from ic_inventory where ic_inventory.code = ic_trans_detail.item_code) = '" + group_mail + "' ";
        }
        if (!group_sub.equals("")) {
            where_group_sub = " and (select group_sub from ic_inventory where ic_inventory.code = ic_trans_detail.item_code) = '" + group_sub + "' ";
        }
        if (!group_sub2.equals("")) {
            where_group_sub2 = " and (select group_sub2 from ic_inventory where ic_inventory.code = ic_trans_detail.item_code) = '" + group_sub2 + "' ";
        }
        if (!branch_code.equals("")) {
            where_branch_code = " and branch_code in (" + branch_code + ") ";
        }
        if (!wh_code.equals("")) {
            where_wh_code = " and wh_code in (" + wh_code + ") ";
        }
        if (!item_brand.equals("")) {
            where_item_brand = " and (select item_brand from ic_inventory where ic_inventory.code = ic_trans_detail.item_code) in (" + item_brand + ") ";
        }
        if (!item_pattern.equals("")) {
            where_item_pattern = " and (select item_pattern from ic_inventory where ic_inventory.code = ic_trans_detail.item_code) in (" + item_pattern + ") ";
        }
        if (!emp_code.equals("")) {
            where_emp_code = " and (select sale_code from ic_trans where ic_trans.doc_no = ic_trans_detail.doc_no and ic_trans.trans_flag = ic_trans_detail.trans_flag) in (" + emp_code + ") ";
        }
        if (!ar_code.equals("")) {
            where_ar_code = " and cust_code in (" + ar_code + ") ";
        }
        JSONArray jsarr = new JSONArray();
        String __Where = "with balance_stock as \n"
                + "(select ic_code, ic_name, ic_unit_code, balance_qty/unit_standard_ratio as balance_qty\n"
                + "from (select coalesce((select stand_value/divide_value from ic_unit_use where ic_unit_use.ic_code=temp2.ic_code and ic_unit_use.code=temp2.ic_unit_code),1) as unit_standard_ratio\n"
                + ",ic_code, ic_name, balance_qty, ic_unit_code\n"
                + "from (select ic_code, ic_name, balance_qty, ic_unit_code, (select unit_standard_stand_value/unit_standard_divide_value from ic_inventory where ic_inventory.code=temp1.ic_code) as unit_ratio\n"
                + "from (select item_code as ic_code, (select name_1 from ic_inventory where ic_inventory.code=item_code) as ic_name\n"
                + ", (select unit_standard from ic_inventory where ic_inventory.code=item_code) as ic_unit_code\n"
                + ", coalesce(sum(calc_flag*(case when ((trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) \n"
                + "	or (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) \n"
                + "	or (trans_flag=311 and inquiry_type=0)) and not (ic_trans_detail.doc_ref <> '' and ic_trans_detail.is_pos = 1)) \n"
                + "	then qty*(stand_value / divide_value) else 0 end)),0) as balance_qty\n"
                + "from ic_trans_detail \n"
                + "where ic_trans_detail.last_status=0  and ic_trans_detail.item_type<>5  \n"
                + "and (select item_type from ic_inventory where ic_inventory.code = ic_trans_detail.item_code) not in (1,3)  and doc_date_calc<='" + balance_date + "' \n"
                + where_doc_po
                + where_item_like + where_item_pattern + where_group_main + where_group_sub + where_group_sub2 + where_branch_code + where_wh_code + where_item_brand + where_emp_code + where_ar_code
                + " group by item_code\n"
                + ") as temp1\n"
                + ") as temp2  \n"
                + "where ( balance_qty<>0)\n"
                + ") as final \n"
                + "order by ic_code)\n"
                + "\n"
                + ",acc_in_balance as (\n"
                + "select item_code,sum(acc_in_balance) as acc_in_balance \n"
                + "from(select doc_no, item_code, acc_in_balance from (\n"
                + "select doc_no, item_code, qty\n"
                + "- coalesce((\n"
                + "select sum(qty * (stand_value/divide_value)) from ic_trans_detail\n"
                + "where ( trans_flag in (12,310)\n"
                + "or\n"
                + "\n"
                + "( ic_trans_detail.trans_flag = 7 and (select cancel_type from ic_trans where ic_trans.doc_no = ic_trans_detail.doc_no and ic_trans_detail.trans_flag = ic_trans.trans_flag) = 2)\n"
                + ") and last_status = 0 and ic_trans_detail.ref_doc_no = temp1.doc_no and ic_trans_detail.item_code = temp1.item_code\n"
                + "), 0) as acc_in_balance\n"
                + "from (\n"
                + "select doc_no, item_code\n"
                + ", sum(qty * (stand_value/divide_value)) as qty\n"
                + "from ic_trans_detail where trans_flag=6 and last_status = 0 and doc_date <= '" + sale_date_end + "'\n"
                + where_doc_po
                + where_item_like + where_item_pattern + where_group_main + where_group_sub + where_group_sub2 + where_branch_code + where_wh_code + where_item_brand + where_emp_code + where_ar_code
                + "group by doc_no, item_code\n"
                + ") as temp1\n"
                + "\n"
                + ") as temp2 where acc_in_balance <> 0\n"
                + ")as temp3\n"
                + "group by item_code\n"
                + "order by acc_in_balance\n"
                + ")\n"
                + "---------------------------\n"
                + "\n"
                + "select item_code,barcode,item_name,age_stock,time_stock,qty,balance_qty,unit_code,sellingday\n"
                + ",sd,sd*age_stock as stockaging,sd*time_stock as leadtime,growth\n"
                + ",(sd*age_stock)+(sd*time_stock) as requireqty\n"
                + ",waitreceive\n"
                + ",((sd*age_stock)+(sd*time_stock))-waitreceive-balance_qty as estorder\n"
                + ",'' as orderqty\n"
                + ",(select minimum_qty from ic_inventory_detail where ic_inventory_detail.ic_code = temp2.item_code) as min\n"
                + ",(select maximum_qty from ic_inventory_detail where ic_inventory_detail.ic_code = temp2.item_code) as max\n"
                + ",(select description from ic_inventory where ic_inventory.code = temp2.item_code) as remarkpurchase\n"
                + ",(select acc_in_balance from acc_in_balance where acc_in_balance.item_code = temp2.item_code) as pr\n"
                + ",coalesce((select case when coalesce(price_2,'')<>'' then price_2 \n"
                + "		when coalesce(price_0,'')<>'' then price_0 \n"
                + "		else '0' end \n"
                + "	from ic_inventory_price_formula where ic_inventory_price_formula.ic_code = temp2.item_code)::float,0) as salesprice\n"
                + "from(select item_code,barcode,item_name,age_stock,time_stock,qty\n"
                + ",(select balance_qty from balance_stock where balance_stock.ic_code = temp1.item_code) as balance_qty\n"
                + ",unit_code,sellingday\n"
                + ",case when sellingday = 0 then 0 else ((qty*(100+" + Double.parseDouble(percent) + "))/100)/sellingday end as sd,growth,waitreceive\n"
                + "from(select item_code,barcode,item_name\n"
                + "," + stock_age + " as age_stock," + send_time + " as time_stock\n"
                + ",sum(qty) as qty,0 as balance_qty\n"
                + ",unit_code\n"
                + ",case when split_part((to_timestamp('" + sale_date_end + "' ||' '|| '00:00','YYYY-MM-dd HH24:MI:SS') - to_timestamp('" + sale_date_begin + "' ||' '|| '00:00','YYYY-MM-dd'))::text, ' day', 1) = '00:00:00' then 1 \n"
                + "	else split_part((to_timestamp('" + sale_date_end + "' ||' '|| '00:00','YYYY-MM-dd HH24:MI:SS') - to_timestamp('" + sale_date_begin + "' ||' '|| '00:00','YYYY-MM-dd'))::text, ' day', 1)::int+1 end as sellingday\n"
                + "," + percent + " as growth\n"
                + ",0 as waitreceive\n"
                + "from ic_trans_detail \n"
                + "where trans_flag = 44 and last_status = 0 and doc_date between '" + sale_date_begin + "' and '" + sale_date_end + "'\n"
                + where_doc_po
                + where_item_like + where_item_pattern + where_group_main + where_group_sub + where_group_sub2 + where_branch_code + where_wh_code + where_item_brand + where_emp_code + where_ar_code
                + "group by item_code,barcode,item_name,unit_code\n"
                + "order by item_code\n"
                + ") as temp1\n"
                + ") as temp2";

        try {
            System.out.println("__query " + __Where);
            Connection __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__providerDatabaseName));

            PreparedStatement __stmtCount = __conn.prepareStatement(__Where, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsCount = __stmtCount.executeQuery();
            int i = 0;
            while (__rsCount.next()) {
                DecimalFormat formatter = new DecimalFormat("#,###.00");

                JSONObject obj = new JSONObject();

                obj.put("barcode", __rsCount.getString("barcode") != null ? __rsCount.getString("barcode") : __rsCount.getString("item_code"));
                obj.put("item_code", __rsCount.getString("item_code"));
                obj.put("item_name", __rsCount.getString("item_name"));
                obj.put("unit_code", __rsCount.getString("unit_code"));
                obj.put("age_stock", __rsCount.getString("age_stock"));
                obj.put("time_stock", __rsCount.getString("time_stock"));
                obj.put("qty", __rsCount.getString("qty") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty"))) : "");
                obj.put("balance_qty", __rsCount.getString("balance_qty") != null ? formatter.format(Double.parseDouble(__rsCount.getString("balance_qty"))) : "");
                obj.put("sellingday", __rsCount.getString("sellingday"));
                obj.put("sd", __rsCount.getString("sd") != null ? formatter.format(Double.parseDouble(__rsCount.getString("sd"))) : "");
                obj.put("stockaging", __rsCount.getString("stockaging") != null ? formatter.format(Double.parseDouble(__rsCount.getString("stockaging"))) : "");
                obj.put("leadtime", __rsCount.getString("leadtime") != null ? formatter.format(Double.parseDouble(__rsCount.getString("leadtime"))) : "");
                obj.put("growth", __rsCount.getString("growth") != null ? __rsCount.getString("growth") : "");
                obj.put("requireqty", __rsCount.getString("requireqty") != null ? formatter.format(Double.parseDouble(__rsCount.getString("requireqty"))) : "");
                obj.put("waitreceive", __rsCount.getString("waitreceive") != null ? formatter.format(Double.parseDouble(__rsCount.getString("waitreceive"))) : "");
                obj.put("estorder", __rsCount.getString("estorder") != null ? formatter.format(Double.parseDouble(__rsCount.getString("estorder"))) : "");
                obj.put("orderqty", __rsCount.getString("orderqty"));
                obj.put("min", __rsCount.getString("min") != null ? formatter.format(Double.parseDouble(__rsCount.getString("min"))) : "");
                obj.put("max", __rsCount.getString("max") != null ? formatter.format(Double.parseDouble(__rsCount.getString("max"))) : "");
                obj.put("remarkpurchase", __rsCount.getString("remarkpurchase"));
                obj.put("pr", __rsCount.getString("pr") != null ? formatter.format(Double.parseDouble(__rsCount.getString("pr"))) : "");
                obj.put("salesprice", __rsCount.getString("salesprice"));
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
