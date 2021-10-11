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
@WebServlet(urlPatterns = {"/getReporter1"})
public class getReport1 extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession __session = request.getSession();
        String search = request.getParameter("search");
        String date_begin = request.getParameter("date_begin");
        String wh_code = request.getParameter("wh_code");
        String shelf_code = request.getParameter("shelf_code");
        String group_mail = request.getParameter("group_mail");
        String group_sub = request.getParameter("group_sub");
        String group_sub2 = request.getParameter("group_sub2");
        String item_brand = request.getParameter("itembrand");
        String item_pattern = request.getParameter("itempattern");
        String item_model = request.getParameter("itemmodel");
        String item_design = request.getParameter("itemdesign");
        String item_grade = request.getParameter("itemgrade");
        String item_class = request.getParameter("itemclass");
        String category = request.getParameter("category");
        String balance_type = request.getParameter("balance_type");

        String __dbname = __session.getAttribute("dbname").toString();
        String __providerDatabaseName = __session.getAttribute("provider").toString();

        PrintWriter out = response.getWriter();
        _routine __routine = new _routine();

        StringBuilder __result = new StringBuilder();

        String _where_date = "";
        String _where_whcode = "";
        String _where_shelfcode = "";
        String _where_category = "";
        String _where_group_mail = "";
        String _where_group_sub = "";
        String _where_group_sub2 = "";
        String _where_item_brand = "";
        String _where_item_grade = "";
        String _where_item_model = "";
        String _where_item_design = "";
        String _where_item_pattern = "";
        String _where_item_branch = "";
        String _where_currency = "";
        String _where_item_class = "";
        String _where_balance_type = "";
        String _where_balance_type2 = "";
        String __wherelike = "";
        String __wherelike2 = "";
        if (!category.equals("")) {
            _where_category = " and item_category in (" + category + ") ";
        }
        if (!item_class.equals("")) {
            _where_item_class = " and item_class in (" + item_class + ") ";
        }
        if (!item_grade.equals("")) {
            _where_item_grade = " and item_grade in (" + item_grade + ") ";
        }
        if (!item_design.equals("")) {
            _where_item_design = " and item_design in (" + item_design + ") ";
        }

        if (!item_model.equals("")) {
            _where_item_model = " and item_model in (" + item_model + ") ";
        }
        if (!item_pattern.equals("")) {
            _where_item_pattern = " and item_pattern in (" + item_pattern + ") ";
        }

        if (!item_brand.equals("")) {
            _where_item_brand = " and item_brand in (" + item_brand + ") ";
        }

        if (!group_mail.equals("")) {
            _where_group_mail = " and group_main in ('" + group_mail + "') ";
        }

        if (!group_sub.equals("")) {
            _where_group_sub = " and group_sub in ('" + group_sub + "') ";
        }

        if (!group_sub2.equals("")) {
            _where_group_sub2 = " and group_sub2 in ('" + group_sub2 + "') ";
        }

        if (!wh_code.equals("")) {
            _where_whcode = " and wh_code in (" + wh_code + ") ";
        }
        if (!shelf_code.equals("")) {
            _where_shelfcode = " and shelf_code = '" + shelf_code + "' ";
        }
        if (!search.equals("")) {
            String[] sptSearches = search.split(" ");
            if (sptSearches.length > 1) {

                __wherelike += "and (";
                __wherelike2 += "and (";
                for (int i = 0; i < sptSearches.length; i++) {
                    if (i == 0) {
                        __wherelike += " (UPPER(item_code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        __wherelike += " or UPPER(item_name) LIKE UPPER('%" + sptSearches[i] + "%')) ";
                        __wherelike2 += " (UPPER(code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        __wherelike2 += " or UPPER(name_1) LIKE UPPER('%" + sptSearches[i] + "%')) ";
                    } else {
                        __wherelike += " and (UPPER(item_code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        __wherelike += " or UPPER(item_name) LIKE UPPER('%" + sptSearches[i] + "%')) ";
                        __wherelike2 += " and ( UPPER(code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        __wherelike2 += " or UPPER(name_1) LIKE UPPER('%" + sptSearches[i] + "%')) ";
                    }
                }
                __wherelike += " )";
                __wherelike2 += " )";
            } else {
                __wherelike = " and UPPER(item_code) LIKE upper('%" + search + "%') or UPPER(item_name) LIKE upper('%" + search + "%') ";
                __wherelike2 = " and UPPER(code) LIKE upper('%" + search + "%') or UPPER(name_1) LIKE upper('%" + search + "%') ";
            }
        }
        if (!balance_type.equals("")) {
            if (balance_type.equals("0")) {
                _where_balance_type = " and 1=1 ";
            } else if (balance_type.equals("1")) {
                _where_balance_type = " and balance_qty>0 ";
            } else if (balance_type.equals("2")) {
                _where_balance_type = " and balance_qty=0 ";
            } else if (balance_type.equals("3")) {
                _where_balance_type = " and balance_qty<0 ";
            } else if (balance_type.equals("4")) {
                _where_balance_type = " and balance_qty<>0 ";
            }

        }
        if (!balance_type.equals("")) {
            if (balance_type.equals("0")) {
                _where_balance_type2 = "  1=1 ";
            } else if (balance_type.equals("1")) {
                _where_balance_type2 = "  (rcps10.balance_qty >0 or rcps2.balance_qty >0 or rcps3.balance_qty >0 or rcps4.balance_qty >0 or rcps5.balance_qty >0 or rcps8.balance_qty >0 or rcps9.balance_qty >0 or rcps91.balance_qty >0 \n"
                        + "	or temp.balance_qty >0 or wang.balance_qty >0 or wdkt.balance_qty >0 or wdud.balance_qty >0 or wh01.balance_qty >0 or wh11.balance_qty >0 or wh12.balance_qty >0 or wh14.balance_qty >0 or wh15.balance_qty >0 \n"
                        + "	or whls.balance_qty >0 or whnp.balance_qty >0 or wjon.balance_qty >0 or wkls.balance_qty >0 or wknr.balance_qty >0 or wksm.balance_qty >0 or wktl.balance_qty >0 or wnkp.balance_qty >0 or wphk.balance_qty >0 \n"
                        + "	or wpmh.balance_qty >0 or wpplk.balance_qty >0 or wpsl.balance_qty >0 or wrcp1.balance_qty >0 or wrcp2.balance_qty >0 or wrss.balance_qty >0 or wsae.balance_qty >0 or wsbk.balance_qty >0 or wsbp1.balance_qty >0 \n"
                        + "	or wscp1.balance_qty >0 or wsdd.balance_qty >0 or wshk.balance_qty >0 or wskl1.balance_qty >0 or wskn.balance_qty >0 or wsks.balance_qty >0 or wslp.balance_qty >0 or wsns1.balance_qty >0 or wsny.balance_qty >0 \n"
                        + "	or wspk.balance_qty >0 or wspp1.balance_qty >0 or wspt.balance_qty >0 or wsri.balance_qty >0 or wsrn.balance_qty >0 or wsrs.balance_qty >0 or wssk.balance_qty >0 or wssn.balance_qty >0 or wstp.balance_qty >0 \n"
                        + "	or wswc.balance_qty >0 or wtat.balance_qty >0 or wtpn.balance_qty >0 or wvsp.balance_qty >0 or wwny.balance_qty >0 or wwsm.balance_qty >0 or wyst.balance_qty >0) ";
            } else if (balance_type.equals("2")) {
                _where_balance_type2 = "  (rcps10.balance_qty = 0 or rcps2.balance_qty = 0 or rcps3.balance_qty = 0 or rcps4.balance_qty = 0 or rcps5.balance_qty = 0 or rcps8.balance_qty = 0 or rcps9.balance_qty = 0 or rcps91.balance_qty = 0 \n"
                        + "	or temp.balance_qty = 0 or wang.balance_qty = 0 or wdkt.balance_qty = 0 or wdud.balance_qty = 0 or wh01.balance_qty = 0 or wh11.balance_qty = 0 or wh12.balance_qty = 0 or wh14.balance_qty = 0 or wh15.balance_qty = 0 \n"
                        + "	or whls.balance_qty = 0 or whnp.balance_qty = 0 or wjon.balance_qty = 0 or wkls.balance_qty = 0 or wknr.balance_qty = 0 or wksm.balance_qty = 0 or wktl.balance_qty = 0 or wnkp.balance_qty = 0 or wphk.balance_qty = 0 \n"
                        + "	or wpmh.balance_qty = 0 or wpplk.balance_qty = 0 or wpsl.balance_qty = 0 or wrcp1.balance_qty = 0 or wrcp2.balance_qty = 0 or wrss.balance_qty = 0 or wsae.balance_qty = 0 or wsbk.balance_qty = 0 or wsbp1.balance_qty = 0 \n"
                        + "	or wscp1.balance_qty = 0 or wsdd.balance_qty = 0 or wshk.balance_qty = 0 or wskl1.balance_qty = 0 or wskn.balance_qty = 0 or wsks.balance_qty = 0 or wslp.balance_qty = 0 or wsns1.balance_qty = 0 or wsny.balance_qty = 0 \n"
                        + "	or wspk.balance_qty = 0 or wspp1.balance_qty = 0 or wspt.balance_qty = 0 or wsri.balance_qty = 0 or wsrn.balance_qty = 0 or wsrs.balance_qty = 0 or wssk.balance_qty = 0 or wssn.balance_qty = 0 or wstp.balance_qty = 0 \n"
                        + "	or wswc.balance_qty = 0 or wtat.balance_qty = 0 or wtpn.balance_qty = 0 or wvsp.balance_qty = 0 or wwny.balance_qty = 0 or wwsm.balance_qty = 0 or wyst.balance_qty = 0) ";
            } else if (balance_type.equals("3")) {
                _where_balance_type2 = "  (rcps10.balance_qty <0 or rcps2.balance_qty <0 or rcps3.balance_qty <0 or rcps4.balance_qty <0 or rcps5.balance_qty <0 or rcps8.balance_qty <0 or rcps9.balance_qty <0 or rcps91.balance_qty <0 \n"
                        + "	or temp.balance_qty <0 or wang.balance_qty <0 or wdkt.balance_qty <0 or wdud.balance_qty <0 or wh01.balance_qty <0 or wh11.balance_qty <0 or wh12.balance_qty <0 or wh14.balance_qty <0 or wh15.balance_qty <0 \n"
                        + "	or whls.balance_qty <0 or whnp.balance_qty <0 or wjon.balance_qty <0 or wkls.balance_qty <0 or wknr.balance_qty <0 or wksm.balance_qty <0 or wktl.balance_qty <0 or wnkp.balance_qty <0 or wphk.balance_qty <0 \n"
                        + "	or wpmh.balance_qty <0 or wpplk.balance_qty <0 or wpsl.balance_qty <0 or wrcp1.balance_qty <0 or wrcp2.balance_qty <0 or wrss.balance_qty <0 or wsae.balance_qty <0 or wsbk.balance_qty <0 or wsbp1.balance_qty <0 \n"
                        + "	or wscp1.balance_qty <0 or wsdd.balance_qty <0 or wshk.balance_qty <0 or wskl1.balance_qty <0 or wskn.balance_qty <0 or wsks.balance_qty <0 or wslp.balance_qty <0 or wsns1.balance_qty <0 or wsny.balance_qty <0 \n"
                        + "	or wspk.balance_qty <0 or wspp1.balance_qty <0 or wspt.balance_qty <0 or wsri.balance_qty <0 or wsrn.balance_qty <0 or wsrs.balance_qty <0 or wssk.balance_qty <0 or wssn.balance_qty <0 or wstp.balance_qty <0 \n"
                        + "	or wswc.balance_qty <0 or wtat.balance_qty <0 or wtpn.balance_qty <0 or wvsp.balance_qty <0 or wwny.balance_qty <0 or wwsm.balance_qty <0 or wyst.balance_qty <0) ";
            } else if (balance_type.equals("4")) {
                _where_balance_type2 = "  (rcps10.balance_qty <> 0 or rcps2.balance_qty <> 0 or rcps3.balance_qty <> 0 or rcps4.balance_qty <> 0 or rcps5.balance_qty <> 0 or rcps8.balance_qty <> 0 or rcps9.balance_qty <> 0 or rcps91.balance_qty <> 0 \n"
                        + "	or temp.balance_qty <> 0 or wang.balance_qty <> 0 or wdkt.balance_qty <> 0 or wdud.balance_qty <> 0 or wh01.balance_qty <> 0 or wh11.balance_qty <> 0 or wh12.balance_qty <> 0 or wh14.balance_qty <> 0 or wh15.balance_qty <> 0 \n"
                        + "	or whls.balance_qty <> 0 or whnp.balance_qty <> 0 or wjon.balance_qty <> 0 or wkls.balance_qty <> 0 or wknr.balance_qty <> 0 or wksm.balance_qty <> 0 or wktl.balance_qty <> 0 or wnkp.balance_qty <> 0 or wphk.balance_qty <> 0 \n"
                        + "	or wpmh.balance_qty <> 0 or wpplk.balance_qty <> 0 or wpsl.balance_qty <> 0 or wrcp1.balance_qty <> 0 or wrcp2.balance_qty <> 0 or wrss.balance_qty <> 0 or wsae.balance_qty <> 0 or wsbk.balance_qty <> 0 or wsbp1.balance_qty <> 0 \n"
                        + "	or wscp1.balance_qty <> 0 or wsdd.balance_qty <> 0 or wshk.balance_qty <> 0 or wskl1.balance_qty <> 0 or wskn.balance_qty <> 0 or wsks.balance_qty <> 0 or wslp.balance_qty <> 0 or wsns1.balance_qty <> 0 or wsny.balance_qty <> 0 \n"
                        + "	or wspk.balance_qty <> 0 or wspp1.balance_qty <> 0 or wspt.balance_qty <> 0 or wsri.balance_qty <> 0 or wsrn.balance_qty <> 0 or wsrs.balance_qty <> 0 or wssk.balance_qty <> 0 or wssn.balance_qty <> 0 or wstp.balance_qty <> 0 \n"
                        + "	or wswc.balance_qty <> 0 or wtat.balance_qty <> 0 or wtpn.balance_qty <> 0 or wvsp.balance_qty <> 0 or wwny.balance_qty <> 0 or wwsm.balance_qty <> 0 or wyst.balance_qty <> 0) ";
            }

        }
        JSONArray jsarr = new JSONArray();
        String __Where = "with balance_qty as (select *,balance_qty*price as balance_amount\n"
                + "from(select ic_code as item_code,warehouse,shelf_code, ic_name, ic_unit_code, balance_qty/unit_standard_ratio as balance_qty\n"
                + ",(select case when coalesce(price_2,'')='' then replace(price_0,',','')  else replace(price_2,',','') end from ic_inventory_price_formula where ic_inventory_price_formula.ic_code = final.ic_code)::float as price\n"
                + "from (select coalesce((select stand_value/divide_value from ic_unit_use where ic_unit_use.ic_code=temp2.ic_code and ic_unit_use.code=temp2.ic_unit_code),1) as unit_standard_ratio\n"
                + ",ic_code,warehouse,shelf_code, ic_name, balance_qty, ic_unit_code\n"
                + "from (select ic_code,warehouse,shelf_code, ic_name, balance_qty, ic_unit_code, (select unit_standard_stand_value/unit_standard_divide_value from ic_inventory where ic_inventory.code=temp1.ic_code) as unit_ratio\n"
                + "from (select item_code as ic_code,wh_code as warehouse,shelf_code, (select name_1 from ic_inventory where ic_inventory.code=item_code) as ic_name\n"
                + ", (select unit_standard from ic_inventory where ic_inventory.code=item_code) as ic_unit_code\n"
                + ", coalesce(sum(calc_flag*(case when ((trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) \n"
                + "	or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) and not (ic_trans_detail.doc_ref <> '' and ic_trans_detail.is_pos = 1)) \n"
                + "	then qty*(stand_value / divide_value) else 0 end)),0) as balance_qty\n"
                + "from ic_trans_detail \n"
                + "where ic_trans_detail.last_status=0  and ic_trans_detail.item_type<>5  and (select item_type from ic_inventory where ic_inventory.code = ic_trans_detail.item_code) not in (1,3)  and doc_date_calc<='" + date_begin + "' \n"
                + __wherelike + _where_whcode + _where_shelfcode + " "
                + "group by item_code, wh_code,shelf_code\n"
                + ") as temp1\n"
                + ") as temp2  \n"
                + "where ( balance_qty<>0)\n"
                + ") as final \n"
                + "order by ic_code,warehouse\n"
                + ") as temp3\n"
                + "where 1=1 " + _where_balance_type + " \n"
                + "order by item_code,warehouse\n"
                + ") \n"
                + "\n"
                + "\n"
                + "select code,name_1,unit_cost,rcps10.balance_qty as qty_rcps10,rcps2.balance_qty as qty_rcps2,rcps3.balance_qty as qty_rcps3,rcps4.balance_qty as qty_rcps4,\n"
                + "rcps5.balance_qty as qty_rcps5,rcps8.balance_qty as qty_rcps8,rcps9.balance_qty as qty_rcps9,rcps91.balance_qty as qty_rcps91,temp.balance_qty as qty_temp,wang.balance_qty as qty_wang,wdkt.balance_qty as qty_wdkt,\n"
                + "wdud.balance_qty as qty_wdud,wh01.balance_qty as qty_wh01,wh11.balance_qty as qty_wh11,wh12.balance_qty as qty_wh12,wh14.balance_qty as qty_wh14,wh15.balance_qty as qty_wh15,whls.balance_qty as qty_whls,whnp.balance_qty as qty_whnp,wjon.balance_qty as qty_wjon,\n"
                + "wkls.balance_qty as qty_wkls,wknr.balance_qty as qty_wknr,wksm.balance_qty as qty_wksm,wktl.balance_qty as qty_wktl,wnkp.balance_qty as qty_wnkp,wphk.balance_qty as qty_wphk,wpmh.balance_qty as qty_wpmh,wpplk.balance_qty as qty_wpplk,\n"
                + "wpsl.balance_qty as qty_wpsl,wrcp1.balance_qty as qty_wrcp1,wrcp2.balance_qty as qty_wrcp2,wrss.balance_qty as qty_wrss,wsae.balance_qty as qty_wsae,wsbk.balance_qty as qty_wsbk,wsbp1.balance_qty as qty_wsbp1,wscp1.balance_qty as qty_wscp1,\n"
                + "wsdd.balance_qty as qty_wsdd,wshk.balance_qty as qty_wshk,wskl1.balance_qty as qty_wskl1,wskn.balance_qty as qty_wskn,wsks.balance_qty as qty_wsks,wslp.balance_qty as qty_wslp,wsns1.balance_qty as qty_wsns1,wsny.balance_qty as qty_wsny,\n"
                + "wspk.balance_qty as qty_wspk,wspp1.balance_qty as qty_wspp1,wspt.balance_qty as qty_wspt,wsri.balance_qty as qty_wsri,wsrn.balance_qty as qty_wsrn,wsrs.balance_qty as qty_wsrs,wssk.balance_qty as qty_wssk,wssn.balance_qty as qty_wssn,wstp.balance_qty as qty_wstp,\n"
                + "wswc.balance_qty as qty_wswc,wtat.balance_qty as qty_wtat,wtpn.balance_qty as qty_wtpn,wvsp.balance_qty as qty_wvsp,wwny.balance_qty as qty_wwny,wwsm.balance_qty as qty_wwsm,wyst.balance_qty as qty_wyst\n"
                + ",(coalesce(rcps10.balance_qty,0) + coalesce(rcps2.balance_qty,0) + coalesce(rcps3.balance_qty,0) + coalesce(rcps4.balance_qty,0) + coalesce(rcps5.balance_qty,0) + coalesce(rcps8.balance_qty,0)\n"
                + " + coalesce(rcps9.balance_qty,0) + coalesce(rcps91.balance_qty,0) + coalesce(temp.balance_qty,0) + coalesce(wang.balance_qty,0) + coalesce(wdkt.balance_qty,0) + coalesce(wdud.balance_qty,0)\n"
                + " + coalesce(wh01.balance_qty,0) + coalesce(wh11.balance_qty,0) + coalesce(wh12.balance_qty,0) + coalesce(wh14.balance_qty,0) + coalesce(wh15.balance_qty,0) + coalesce(whls.balance_qty,0)\n"
                + " + coalesce(whnp.balance_qty,0) + coalesce(wjon.balance_qty,0) + coalesce(wkls.balance_qty,0) + coalesce(wknr.balance_qty,0) + coalesce(wksm.balance_qty,0) + coalesce(wktl.balance_qty,0)\n"
                + " + coalesce(wnkp.balance_qty,0) + coalesce(wphk.balance_qty,0) + coalesce(wpmh.balance_qty,0) + coalesce(wpplk.balance_qty,0) + coalesce(wpsl.balance_qty,0) + coalesce(wrcp1.balance_qty,0)\n"
                + " + coalesce(wrcp2.balance_qty,0) + coalesce(wrss.balance_qty,0) + coalesce(wsae.balance_qty,0) + coalesce(wsbk.balance_qty,0) + coalesce(wsbp1.balance_qty,0) + coalesce(wscp1.balance_qty,0) \n"
                + " + coalesce(wsdd.balance_qty,0) + coalesce(wshk.balance_qty,0) + coalesce(wskl1.balance_qty,0) + coalesce(wskn.balance_qty,0) + coalesce(wsks.balance_qty,0) + coalesce(wslp.balance_qty,0) \n"
                + " + coalesce(wsns1.balance_qty,0) + coalesce(wsny.balance_qty,0) + coalesce(wspk.balance_qty,0) + coalesce(wspp1.balance_qty,0) + coalesce(wspt.balance_qty,0) + coalesce(wsri.balance_qty,0) \n"
                + " + coalesce(wsrn.balance_qty,0) + coalesce(wsrs.balance_qty,0) + coalesce(wssk.balance_qty,0) + coalesce(wssn.balance_qty,0) + coalesce(wstp.balance_qty,0) + coalesce(wswc.balance_qty,0) \n"
                + " + coalesce(wtat.balance_qty,0) + coalesce(wtpn.balance_qty,0) + coalesce(wvsp.balance_qty,0) + coalesce(wwny.balance_qty,0) + coalesce(wwsm.balance_qty,0) + coalesce(wyst.balance_qty,0) \n"
                + " ) as sum_qty\n"
                + ",coalesce((select price_0::float from ic_inventory_price_formula where ic_inventory_price_formula.ic_code = ic_inventory.code and ic_inventory_price_formula.unit_code = ic_inventory.unit_cost limit 1),0) as price\n"
                + ",(coalesce(rcps10.balance_qty,0) + coalesce(rcps2.balance_qty,0) + coalesce(rcps3.balance_qty,0) + coalesce(rcps4.balance_qty,0) + coalesce(rcps5.balance_qty,0) + coalesce(rcps8.balance_qty,0)\n"
                + " + coalesce(rcps9.balance_qty,0) + coalesce(rcps91.balance_qty,0) + coalesce(temp.balance_qty,0) + coalesce(wang.balance_qty,0) + coalesce(wdkt.balance_qty,0) + coalesce(wdud.balance_qty,0)\n"
                + " + coalesce(wh01.balance_qty,0) + coalesce(wh11.balance_qty,0) + coalesce(wh12.balance_qty,0) + coalesce(wh14.balance_qty,0) + coalesce(wh15.balance_qty,0) + coalesce(whls.balance_qty,0)\n"
                + " + coalesce(whnp.balance_qty,0) + coalesce(wjon.balance_qty,0) + coalesce(wkls.balance_qty,0) + coalesce(wknr.balance_qty,0) + coalesce(wksm.balance_qty,0) + coalesce(wktl.balance_qty,0)\n"
                + " + coalesce(wnkp.balance_qty,0) + coalesce(wphk.balance_qty,0) + coalesce(wpmh.balance_qty,0) + coalesce(wpplk.balance_qty,0) + coalesce(wpsl.balance_qty,0) + coalesce(wrcp1.balance_qty,0)\n"
                + " + coalesce(wrcp2.balance_qty,0) + coalesce(wrss.balance_qty,0) + coalesce(wsae.balance_qty,0) + coalesce(wsbk.balance_qty,0) + coalesce(wsbp1.balance_qty,0) + coalesce(wscp1.balance_qty,0) \n"
                + " + coalesce(wsdd.balance_qty,0) + coalesce(wshk.balance_qty,0) + coalesce(wskl1.balance_qty,0) + coalesce(wskn.balance_qty,0) + coalesce(wsks.balance_qty,0) + coalesce(wslp.balance_qty,0) \n"
                + " + coalesce(wsns1.balance_qty,0) + coalesce(wsny.balance_qty,0) + coalesce(wspk.balance_qty,0) + coalesce(wspp1.balance_qty,0) + coalesce(wspt.balance_qty,0) + coalesce(wsri.balance_qty,0) \n"
                + " + coalesce(wsrn.balance_qty,0) + coalesce(wsrs.balance_qty,0) + coalesce(wssk.balance_qty,0) + coalesce(wssn.balance_qty,0) + coalesce(wstp.balance_qty,0) + coalesce(wswc.balance_qty,0) \n"
                + " + coalesce(wtat.balance_qty,0) + coalesce(wtpn.balance_qty,0) + coalesce(wvsp.balance_qty,0) + coalesce(wwny.balance_qty,0) + coalesce(wwsm.balance_qty,0) + coalesce(wyst.balance_qty,0) \n"
                + " )\n"
                + "*coalesce((select price_0::float from ic_inventory_price_formula where ic_inventory_price_formula.ic_code = ic_inventory.code and ic_inventory_price_formula.unit_code = ic_inventory.unit_cost limit 1),0)\n"
                + "as sum_amount_price\n"
                + "from ic_inventory\n"
                + "\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'RCPS10') as rcps10 on ic_inventory.code = rcps10.item_code\n"
                + "\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'RCPS2') as rcps2 on ic_inventory.code = rcps2.item_code\n"
                + "\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'RCPS3') as rcps3 on ic_inventory.code = rcps3.item_code\n"
                + "\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'RCPS4') as rcps4 on ic_inventory.code = rcps4.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'RCPS5') as rcps5 on ic_inventory.code = rcps5.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'RCPS8') as rcps8 on ic_inventory.code = rcps8.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'RCPS9') as rcps9 on ic_inventory.code = rcps9.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'RCPS91') as rcps91 on ic_inventory.code = rcps91.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'TEMP') as temp on ic_inventory.code = temp.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WANG') as wang on ic_inventory.code = wang.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WDKT') as wdkt on ic_inventory.code = wdkt.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WDUD') as wdud on ic_inventory.code = wdud.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WH01') as wh01 on ic_inventory.code = wh01.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WH11') as wh11 on ic_inventory.code = wh11.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WH12') as wh12 on ic_inventory.code = wh12.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WH14') as wh14 on ic_inventory.code = wh14.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WH15') as wh15 on ic_inventory.code = wh15.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WHLS') as whls on ic_inventory.code = whls.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WHNP') as whnp on ic_inventory.code = whnp.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WJON') as wjon on ic_inventory.code = wjon.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WKLS') as wkls on ic_inventory.code = wkls.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WKNR') as wknr on ic_inventory.code = wknr.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WKSM') as wksm on ic_inventory.code = wksm.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WKTL') as wktl on ic_inventory.code = wktl.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WNKP') as wnkp on ic_inventory.code = wnkp.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WPHK') as wphk on ic_inventory.code = wphk.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WPMH') as wpmh on ic_inventory.code = wpmh.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WPPLK') as wpplk on ic_inventory.code = wpplk.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WPSL') as wpsl on ic_inventory.code = wpsl.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WRCP1') as wrcp1 on ic_inventory.code = wrcp1.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WRCP2') as wrcp2 on ic_inventory.code = wrcp2.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WRSS') as wrss on ic_inventory.code = wrss.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSAE') as wsae on ic_inventory.code = wsae.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSBK') as wsbk on ic_inventory.code = wsbk.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSBP1') as wsbp1 on ic_inventory.code = wsbp1.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSCP1') as wscp1 on ic_inventory.code = wscp1.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSDD') as wsdd on ic_inventory.code = wsdd.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSHK') as wshk on ic_inventory.code = wshk.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSKL1') as wskl1 on ic_inventory.code = wskl1.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSKN') as wskn on ic_inventory.code = wskn.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSKS') as wsks on ic_inventory.code = wsks.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSLP') as wslp on ic_inventory.code = wslp.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSNS1') as wsns1 on ic_inventory.code = wsns1.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSNY') as wsny on ic_inventory.code = wsny.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSPK') as wspk on ic_inventory.code = wspk.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSPP1') as wspp1 on ic_inventory.code = wspp1.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSPT') as wspt on ic_inventory.code = wspt.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSRI') as wsri on ic_inventory.code = wsri.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSRN') as wsrn on ic_inventory.code = wsrn.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSRS') as wsrs on ic_inventory.code = wsrs.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSSK') as wssk on ic_inventory.code = wssk.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSSN') as wssn on ic_inventory.code = wssn.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSTP') as wstp on ic_inventory.code = wstp.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WSWC') as wswc on ic_inventory.code = wswc.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WTAT') as wtat on ic_inventory.code = wtat.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WTPN') as wtpn on ic_inventory.code = wtpn.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WVSP') as wvsp on ic_inventory.code = wvsp.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WWNY') as wwny on ic_inventory.code = wwny.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WWSM') as wwsm on ic_inventory.code = wwsm.item_code\n"
                + "left join (select item_code,balance_qty from balance_qty where warehouse = 'WYST') as wyst on ic_inventory.code = wyst.item_code\n"
                + "\n"
                + "where item_type not in (1,3)\n"
                + "and  " + _where_balance_type2 + " \n"
                + __wherelike2 + _where_currency + _where_item_class + _where_group_mail + _where_group_sub + _where_group_sub2 + _where_category + _where_item_brand + _where_item_grade + _where_item_model + _where_item_design + _where_item_pattern + _where_item_branch
                + "order by code";

        try {
            System.out.println("__query " + __Where);
            Connection __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__providerDatabaseName));

            PreparedStatement __stmtCount = __conn.prepareStatement(__Where, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsCount = __stmtCount.executeQuery();
            int i = 0;
            while (__rsCount.next()) {
                DecimalFormat formatter = new DecimalFormat("#,###.00");

                JSONObject obj = new JSONObject();

                obj.put("item_code", __rsCount.getString("code"));
                obj.put("item_name", __rsCount.getString("name_1"));
                obj.put("unit_code", __rsCount.getString("unit_cost"));
                obj.put("rcps10", __rsCount.getString("qty_rcps10") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_rcps10"))) : "");
                obj.put("rcps2", __rsCount.getString("qty_rcps2") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_rcps2"))) : "");
                obj.put("rcps3", __rsCount.getString("qty_rcps3") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_rcps3"))) : "");
                obj.put("rcps4", __rsCount.getString("qty_rcps4") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_rcps4"))) : "");
                obj.put("rcps5", __rsCount.getString("qty_rcps5") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_rcps5"))) : "");
                obj.put("rcps8", __rsCount.getString("qty_rcps8") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_rcps8"))) : "");
                obj.put("rcps9", __rsCount.getString("qty_rcps9") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_rcps9"))) : "");
                obj.put("rcps91", __rsCount.getString("qty_rcps91") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_rcps91"))) : "");
                obj.put("temp", __rsCount.getString("qty_temp") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_temp"))) : "");
                obj.put("wang", __rsCount.getString("qty_wang") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wang"))) : "");
                obj.put("wdkt", __rsCount.getString("qty_wdkt") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wdkt"))) : "");
                obj.put("wdud", __rsCount.getString("qty_wdud") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wdud"))) : "");
                obj.put("wh01", __rsCount.getString("qty_wh01") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wh01"))) : "");
                obj.put("wh11", __rsCount.getString("qty_wh11") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wh11"))) : "");
                obj.put("wh12", __rsCount.getString("qty_wh12") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wh12"))) : "");
                obj.put("wh14", __rsCount.getString("qty_wh14") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wh14"))) : "");
                obj.put("wh15", __rsCount.getString("qty_wh15") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wh15"))) : "");
                obj.put("whls", __rsCount.getString("qty_whls") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_whls"))) : "");
                obj.put("whnp", __rsCount.getString("qty_whnp") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_whnp"))) : "");
                obj.put("wjon", __rsCount.getString("qty_wjon") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wjon"))) : "");
                obj.put("wkls", __rsCount.getString("qty_wkls") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wkls"))) : "");
                obj.put("wknr", __rsCount.getString("qty_wknr") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wknr"))) : "");
                obj.put("wksm", __rsCount.getString("qty_wksm") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wksm"))) : "");
                obj.put("wktl", __rsCount.getString("qty_wktl") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wktl"))) : "");
                obj.put("wnkp", __rsCount.getString("qty_wnkp") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wnkp"))) : "");
                obj.put("wphk", __rsCount.getString("qty_wphk") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wphk"))) : "");
                obj.put("wpmh", __rsCount.getString("qty_wpmh") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wpmh"))) : "");
                obj.put("wpplk", __rsCount.getString("qty_wpplk") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wpplk"))) : "");
                obj.put("wpsl", __rsCount.getString("qty_wpsl") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wpsl"))) : "");
                obj.put("wrcp1", __rsCount.getString("qty_wrcp1") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wrcp1"))) : "");
                obj.put("wrcp2", __rsCount.getString("qty_wrcp2") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wrcp2"))) : "");
                obj.put("wrss", __rsCount.getString("qty_wrss") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wrss"))) : "");
                obj.put("wsae", __rsCount.getString("qty_wsae") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsae"))) : "");
                obj.put("wsbk", __rsCount.getString("qty_wsbk") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsbk"))) : "");
                obj.put("wsbp1", __rsCount.getString("qty_wsbp1") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsbp1"))) : "");
                obj.put("wscp1", __rsCount.getString("qty_wscp1") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wscp1"))) : "");
                obj.put("wsdd", __rsCount.getString("qty_wsdd") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsdd"))) : "");
                obj.put("wshk", __rsCount.getString("qty_wshk") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wshk"))) : "");
                obj.put("wskl1", __rsCount.getString("qty_wskl1") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wskl1"))) : "");
                obj.put("wskn", __rsCount.getString("qty_wskn") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wskn"))) : "");
                obj.put("wsks", __rsCount.getString("qty_wsks") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsks"))) : "");
                obj.put("wslp", __rsCount.getString("qty_wslp") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wslp"))) : "");
                obj.put("wsns1", __rsCount.getString("qty_wsns1") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsns1"))) : "");
                obj.put("wsny", __rsCount.getString("qty_wsny") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsny"))) : "");
                obj.put("wspk", __rsCount.getString("qty_wspk") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wspk"))) : "");
                obj.put("wspp1", __rsCount.getString("qty_wspp1") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wspp1"))) : "");
                obj.put("wspt", __rsCount.getString("qty_wspt") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wspt"))) : "");
                obj.put("wsri", __rsCount.getString("qty_wsri") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsri"))) : "");
                obj.put("wsrn", __rsCount.getString("qty_wsrn") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsrn"))) : "");
                obj.put("wsrs", __rsCount.getString("qty_wsrs") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wsrs"))) : "");
                obj.put("wssk", __rsCount.getString("qty_wssk") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wssk"))) : "");
                obj.put("wssn", __rsCount.getString("qty_wssn") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wssn"))) : "");
                obj.put("wstp", __rsCount.getString("qty_wstp") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wstp"))) : "");
                obj.put("wswc", __rsCount.getString("qty_wswc") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wswc"))) : "");
                obj.put("wtat", __rsCount.getString("qty_wtat") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wtat"))) : "");

                obj.put("wtpn", __rsCount.getString("qty_wtpn") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wtpn"))) : "");
                obj.put("wvsp", __rsCount.getString("qty_wvsp") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wvsp"))) : "");
                obj.put("wwny", __rsCount.getString("qty_wwny") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wwny"))) : "");
                obj.put("wwsm", __rsCount.getString("qty_wwsm") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wwsm"))) : "");
                obj.put("wyst", __rsCount.getString("qty_wyst") != null ? formatter.format(Double.parseDouble(__rsCount.getString("qty_wyst"))) : "");
                obj.put("sum_qty", __rsCount.getString("sum_qty") != null ? formatter.format(Double.parseDouble(__rsCount.getString("sum_qty"))) : "0");
                obj.put("price", __rsCount.getString("price") != null ? formatter.format(Double.parseDouble(__rsCount.getString("price"))) : "0");
                obj.put("sum_amount_price", __rsCount.getString("sum_amount_price") != null ? __rsCount.getString("sum_amount_price") : "0");

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
