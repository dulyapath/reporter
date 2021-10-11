package utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import models.SaleSummaryLine1;
import org.json.JSONObject;

public class WebUtil {

    public String loadSaleSummaryLine(Connection conn, JSONObject params) throws SQLException, ParseException {
        SimpleDateFormat __dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
        SimpleDateFormat __dateFormatData = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);

        StringBuilder __strResult = new StringBuilder();
        __strResult.append("var success = true;");
        __strResult.append("function create_line() { line1 = ");

        String __strFromDate = !params.isNull("from_date") && !params.getString("from_date").isEmpty() ? params.getString("from_date") : "";
        String __strToDate = !params.isNull("to_date") && !params.getString("to_date").isEmpty() ? params.getString("to_date") : "";
        String __strWarehouse = !params.isNull("warehouse") && !params.getString("warehouse").isEmpty() ? params.getString("warehouse") : "";
        String __strItemCode = !params.isNull("item_code") && !params.getString("item_code").isEmpty() ? params.getString("item_code") : "";

        Date __dateFrom = __dateFormat.parse(__strFromDate);
        Date __dateTo = __dateFormat.parse(__strToDate);
        Calendar __dateCalendar = Calendar.getInstance();
        __dateCalendar.setTime(__dateFrom);

        Boolean __showAmount = !params.isNull("check_amount") ? params.getBoolean("check_amount") : false;
        Boolean __showCost = !params.isNull("check_cost") ? params.getBoolean("check_cost") : false;
        Boolean __showProfit = !params.isNull("check_profit") ? params.getBoolean("check_profit") : false;
        Boolean __showAbove = !params.isNull("check_above") ? params.getBoolean("check_above") : false;
        Boolean __showCash = !params.isNull("check_cash") ? params.getBoolean("check_cash") : false;
        Boolean __showCredit = !params.isNull("check_credit") ? params.getBoolean("check_credit") : false;
        Boolean __showPurchase = !params.isNull("check_purchase") ? params.getBoolean("check_purchase") : false;

        StringBuilder __chartValueAmount = new StringBuilder();
        StringBuilder __chartValueCost = new StringBuilder();
        StringBuilder __chartValueProfit = new StringBuilder();
        StringBuilder __chartValueCash = new StringBuilder();
        StringBuilder __chartValueCredit = new StringBuilder();
        StringBuilder __chartValuePurchase = new StringBuilder();
        StringBuilder __chartLabel = new StringBuilder();

        Locale __dateLocal = new Locale("th", "TH");
        SimpleDateFormat __fmtThai = new SimpleDateFormat("d/MM/yy", __dateLocal);

        String __strQueryExtend = "";
        __strQueryExtend += !__strWarehouse.equals("") ? " AND wh_code='" + __strWarehouse + "'" : "";
        __strQueryExtend += !__strItemCode.equals("") ? " AND item_code='" + __strItemCode + "'" : "";

        if (__strFromDate.equals(__strToDate)) {
            __strQueryExtend += " AND doc_date = '" + __dateFormatData.format(__dateFrom) + "' ";
        } else {
            __strQueryExtend += " AND doc_date BETWEEN '" + __dateFormatData.format(__dateFrom) + "' AND '" + __dateFormatData.format(__dateTo) + "' ";
        }

        String __strQuery = "SELECT * FROM (SELECT doc_date,trans_flag,inquiry_type,SUM(sum_amount_exclude_vat) AS sum_amount_exclude_vat,SUM(sum_of_cost_1) AS sum_of_cost from ic_trans_detail WHERE (trans_flag in (44,46,12,310) AND last_status=0) " + __strQueryExtend + " AND COALESCE(set_ref_line,'')='' GROUP BY doc_date,trans_flag,inquiry_type) AS temp1 WHERE sum_amount_exclude_vat<>0 OR sum_of_cost<>0 ORDER BY doc_date";
        PreparedStatement __stmt = conn.prepareStatement(__strQuery);
        ResultSet __result = __stmt.executeQuery();

        int __count = 0;
        Date __docDateLast = new Date();
        __docDateLast.setTime(0);
        ArrayList<SaleSummaryLine1> __data = new ArrayList<>();
        while (__dateCalendar.getTime().compareTo(__dateTo) <= 0) {
            SaleSummaryLine1 __newData = new SaleSummaryLine1();
            __newData._docDate = __dateCalendar.getTime();
            __newData._amount = new BigDecimal(0.0);
            __newData._cash = new BigDecimal(0.0);
            __newData._cost = new BigDecimal(0.0);
            __newData._credit = new BigDecimal(0.0);
            __newData._profit = new BigDecimal(0.0);
            __newData._purchase = new BigDecimal(0.0);
            __data.add(__newData);
            __dateCalendar.add(Calendar.DATE, 1);
        }

        Comparator<SaleSummaryLine1> __comparator = new Comparator<SaleSummaryLine1>() {
            @Override
            public int compare(SaleSummaryLine1 u1, SaleSummaryLine1 u2) {
                return u1.getDocDate().compareTo(u2.getDocDate());
            }
        };
        int countRow = 0;
        while (__result.next()) {
            String __docDateStr = __result.getString("doc_date");
            Date __docDate = __dateFormatData.parse(__docDateStr);
            SaleSummaryLine1 __find = new SaleSummaryLine1();
            __find._docDate = __docDate;
            int __addr = Collections.binarySearch(__data, __find, __comparator);
            if (__addr != -1) {
                SaleSummaryLine1 __found = __data.get(__addr);
                int __transFlag = __result.getInt("trans_flag");
                int __inquiryType = __result.getInt("inquiry_type");
                BigDecimal __sum_amount_exclude_vat_value_get = __result.getBigDecimal("sum_amount_exclude_vat");
                if (__transFlag == 12 || __transFlag == 310) {
                    // ซื้อ
                    __found._purchase = __found._purchase.add(__sum_amount_exclude_vat_value_get);
                } else {
                    // ขาย
                    if (__inquiryType == 0 || __inquiryType == 2) {
                        __found._credit = __found._credit.add(new BigDecimal(__sum_amount_exclude_vat_value_get.doubleValue()));
                    } else {
                        __found._cash = __found._cash.add(new BigDecimal(__sum_amount_exclude_vat_value_get.doubleValue()));
                    }
                    BigDecimal __sum_of_cost_value_get = __result.getBigDecimal("sum_of_cost");
                    if (__sum_of_cost_value_get == null) {
                        __sum_of_cost_value_get = new BigDecimal(0.0);
                    }
                    // รวมวัน
                    __found._amount = __found._amount.add(__sum_amount_exclude_vat_value_get);
                    __found._cost = __found._cost.add(__sum_of_cost_value_get);
                }
                __data.set(__addr, __found);
            }
            countRow++;
        }
        __result.close();
        __stmt.close();
        if (countRow > 0) {
            // ประมวลตามวัน
            for (int dayCount = 0; dayCount < __data.size(); dayCount++) {
                SaleSummaryLine1 __found = __data.get(dayCount);
                __count++;
                if (__chartLabel.length() > 0) {
                    __chartLabel.append(",");
                    __chartValueAmount.append(",");
                    __chartValueCost.append(",");
                    __chartValueProfit.append(",");
                    __chartValueCash.append(",");
                    __chartValueCredit.append(",");
                    __chartValuePurchase.append(",");
                }
                __chartLabel.append("\'");
                if (__count % 2 == 0) {
                    __chartLabel.append("\\r\\n");
                }
                __chartLabel.append(__fmtThai.format(__found._docDate)).append("\'");
                __chartValueAmount.append(__found._amount.setScale(2, BigDecimal.ROUND_HALF_UP));
                __chartValueCost.append(__found._cost.setScale(2, BigDecimal.ROUND_HALF_UP));
                BigDecimal __profit = new BigDecimal(0.0);
                __profit = __profit.add(__found._amount.setScale(2, BigDecimal.ROUND_HALF_UP)).subtract(__found._cost.setScale(2, BigDecimal.ROUND_HALF_UP));
                if (__profit.compareTo(new BigDecimal(0.0)) > 0) {
                    __chartValueProfit.append(__profit.toString());
                } else {
                    __chartValueProfit.append("0.0");
                }
                __chartValueCash.append(__found._cash);
                __chartValueCredit.append(__found._credit);
                __chartValuePurchase.append(__found._purchase);
            }
            // ประกอบ

            __strResult.append(" new RGraph.Line('lineList',");
            StringBuilder __value = new StringBuilder();
            if (__showAmount) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                __value.append("[").append(__chartValueAmount).append("]");
            }

            if (__showCost) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                __value.append("[").append(__chartValueCost).append("]");
            }

            if (__showProfit) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                __value.append("[").append(__chartValueProfit).append("]");
            }

            if (__showCash) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                __value.append("[").append(__chartValueCash).append("]");
            }

            if (__showCredit) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                __value.append("[").append(__chartValueCredit).append("]");
            }

            if (__showPurchase) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                __value.append("[").append(__chartValuePurchase).append("]");
            }

            __strResult.append(__value).append(");\n");
            __strResult.append(
                    "line1.Set('chart.colors', [");
            StringBuilder __color = new StringBuilder();

            if (__showAmount) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'blue'");
            }

            if (__showCost) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'red'");
            }

            if (__showProfit) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'green'");
            }

            if (__showCash) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'magenta'");
            }

            if (__showCredit) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'cyan'");
            }

            if (__showPurchase) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'yellow'");
            }
            __strResult.append(__color).append("]);\n");

            if (__showAbove) {
                __strResult.append("line1.Set('chart.labels.above', true);\n");
            }

            __strResult.append(
                    "line1.Set('chart.key', [");
            StringBuilder __label = new StringBuilder();

            if (__showAmount) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'ยอดขาย : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }

            if (__showCost) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'ต้นทุน : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }

            if (__showProfit) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'กำไร : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }

            if (__showCash) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'ยอดขายสด : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }

            if (__showCredit) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'ยอดขายเชื่อ : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }

            if (__showPurchase) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'ยอดซื้อ : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }

            __strResult.append(__label).append("]);\n");
//        __result.append("line1.Set('chart.xaxispos', 'center');\n");
            __strResult.append("line1.Set('chart.yaxispos', 'right');\n");
            __strResult.append("line1.Set('chart.hmargin', 15);\n");
            __strResult.append("line1.Set('chart.tickmarks', 'endcircle');\n");
            __strResult.append("line1.Set('chart.labels', [").append(__chartLabel).append("]);\n");
            __strResult.append("line1.Set('chart.linewidth', 3);\n");
            __strResult.append("line1.Set('chart.shadow', true);\n");
            __strResult.append("line1.Set('chart.gutter.top', 25);\n");
            __strResult.append("line1.Set('chart.gutter.bottom', 45);\n");
            __strResult.append("line1.Set('chart.gutter.right', 80);\n");
            __strResult.append("line1.Set('chart.gutter.left', 15);\n");
            __strResult.append("line1.Set('chart.outofbounds', 'true');\n");
            __strResult.append("resize_canvas_line();\n");
            __strResult.append("}");
        } else {
            __strResult.setLength(0);
            __strResult.append("var success = false;");
            __strResult.append("function hide_line() {");
            __strResult.append("$('#contentLineList').hide();");
            __strResult.append("}");
        }

        return __strResult.toString();
    }

    public String loadSaleSummaryBar(Connection conn, JSONObject params) throws SQLException, ParseException {
        SimpleDateFormat __dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
        SimpleDateFormat __dateFormatData = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);

        StringBuilder __strResult = new StringBuilder();
        __strResult.append("var success = true;");
        __strResult.append("function create_bar() { bar1 = ");

        String __strFromDate = !params.isNull("from_date") && !params.getString("from_date").isEmpty() ? params.getString("from_date") : "";
        String __strToDate = !params.isNull("to_date") && !params.getString("to_date").isEmpty() ? params.getString("to_date") : "";
        String __strWarehouse = !params.isNull("warehouse") && !params.getString("warehouse").isEmpty() ? params.getString("warehouse") : "";
        String __strItemCode = !params.isNull("item_code") && !params.getString("item_code").isEmpty() ? params.getString("item_code") : "";

        Date __dateFrom = __dateFormat.parse(__strFromDate);
        Date __dateTo = __dateFormat.parse(__strToDate);
        Calendar __dateCalendar = Calendar.getInstance();
        __dateCalendar.setTime(__dateFrom);

        Boolean __showAmount = !params.isNull("check_amount") ? params.getBoolean("check_amount") : false;
        Boolean __showCost = !params.isNull("check_cost") ? params.getBoolean("check_cost") : false;
        Boolean __showProfit = !params.isNull("check_profit") ? params.getBoolean("check_profit") : false;

        StringBuilder __chartValue = new StringBuilder();
        StringBuilder __chartLabel = new StringBuilder();

        String __strQueryExtend = "";
        __strQueryExtend += !__strWarehouse.equals("") ? " AND wh_code='" + __strWarehouse + "'" : "";
        __strQueryExtend += !__strItemCode.equals("") ? " AND item_code='" + __strItemCode + "'" : "";

        if (__strFromDate.equals(__strToDate)) {
            __strQueryExtend += " AND doc_date = '" + __dateFormatData.format(__dateFrom) + "' ";
        } else {
            __strQueryExtend += " AND doc_date BETWEEN '" + __dateFormatData.format(__dateFrom) + "' AND '" + __dateFormatData.format(__dateTo) + "' ";
        }

        String __strQuery = "select * from (select sale_code,coalesce((select name_1 from erp_user where erp_user.code=sale_code),sale_code) as sale_name,sum(sum_amount_exclude_vat) as sum_amount_exclude_vat,sum(sum_of_cost_1) as sum_of_cost from ic_trans_detail where trans_flag=44 " + __strQueryExtend + "  AND COALESCE(set_ref_line,'')='' and last_status=0 and sale_code<>'' and sale_code is not null group by sale_code,sale_name) as temp1 where sum_amount_exclude_vat<>0 or sum_of_cost<>0 order by sale_code";
        PreparedStatement __stmt = conn.prepareStatement(__strQuery);
        ResultSet __result = __stmt.executeQuery();
        int __count = 0;
        while (__result.next()) {
            __count++;
            String __saleName = __result.getString("sale_name");
            BigDecimal __sum_amount_exclude_vat_value = __result.getBigDecimal("sum_amount_exclude_vat");
            BigDecimal __sum_of_cost_value = __result.getBigDecimal("sum_of_cost");
            if (__sum_of_cost_value == null) {
                __sum_of_cost_value = new BigDecimal(0.0);
            }
            //
            __sum_amount_exclude_vat_value = __sum_amount_exclude_vat_value.setScale(2, BigDecimal.ROUND_HALF_UP);
            __sum_of_cost_value = __sum_of_cost_value.setScale(2, BigDecimal.ROUND_HALF_UP);
//
            if (__chartLabel.length() > 0) {
                __chartLabel.append(",");
                __chartValue.append(",");
            }
            __chartLabel.append("\'");
            if (__count % 2 == 0) {
                __chartLabel.append("\\r\\n");
            }
            __chartLabel.append(__saleName).append("\'");
            // ตัวเลข
            StringBuilder __value = new StringBuilder();
            if (__showAmount) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                __value.append(__sum_amount_exclude_vat_value);
            }
            if (__showCost) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                __value.append(__sum_of_cost_value);
            }
            if (__showProfit) {
                if (__value.length() > 0) {
                    __value.append(",");
                }
                BigDecimal __profit = new BigDecimal(0.0);
                __profit = __profit.add(__sum_amount_exclude_vat_value).subtract(__sum_of_cost_value);
                __value.append(__profit.toString());
            }
            __chartValue.append("[").append(__value).append("]");
        }
        __result.close();
        __stmt.close();

        if (__count > 0) {
            // ประกอบ
            __strResult.append(" new RGraph.Bar('barList',[\n");
            __strResult.append(__chartValue).append("\n");
            __strResult.append("]);\n");
//            __strResult.append("bar1.Set('chart.background.barcolor1', 'white');\n");
//            __strResult.append("bar1.Set('chart.background.barcolor2', 'white');\n");
            __strResult.append("bar1.Set('chart.labels', [").append(__chartLabel).append("]);\n");
            __strResult.append("bar1.Set('chart.labels.above', true);\n");
            __strResult.append("bar1.Set('chart.key', [");
            StringBuilder __label = new StringBuilder();
            if (__showAmount) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'ยอดขาย : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }
            if (__showCost) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'ต้นทุน : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }
            if (__showProfit) {
                if (__label.length() > 0) {
                    __label.append(",");
                }
                __label.append("'กำไร : จากวันที่ ").append(__strFromDate).append(" ถึง ").append(__strToDate).append("'");
            }
            __strResult.append(__label).append("]);\n");
            __strResult.append("bar1.Set('chart.key.position.y', 35);\n");
            __strResult.append("bar1.Set('chart.key.position', 'gutter');\n");
            __strResult.append("bar1.Set('chart.key.background', 'rgb(255,255,255)');\n");
            __strResult.append("bar1.Set('chart.colors', [");
            StringBuilder __color = new StringBuilder();
            if (__showAmount) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'blue'");
            }
            if (__showCost) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'red'");
            }
            if (__showProfit) {
                if (__color.length() > 0) {
                    __color.append(",");
                }
                __color.append("'green'");
            }
            __strResult.append(__color).append("]);\n");
            __strResult.append("bar1.Set('chart.shadow', true);\n");
            __strResult.append("bar1.Set('chart.shadow.blur', 15);\n");
            __strResult.append("bar1.Set('chart.shadow.offsetx', 0);\n");
            __strResult.append("bar1.Set('chart.shadow.offsety', 0);\n");
            __strResult.append("bar1.Set('chart.shadow.color', '#aaa');\n");
            __strResult.append("bar1.Set('chart.yaxispos', 'right');\n");
            __strResult.append("bar1.Set('chart.strokestyle', 'rgba(0,0,0,0)');\n");
            __strResult.append("bar1.Set('chart.gutter.left', 15);\n");
            __strResult.append("bar1.Set('chart.gutter.right', 80);\n");
            __strResult.append("bar1.Set('chart.gutter.bottom', 50);\n");
            __strResult.append("resize_canvas_bar();\n");
            __strResult.append("}");
        } else {
            __strResult.setLength(0);
            __strResult.append("var success = false;");
            __strResult.append("function hide_bar() {");
            __strResult.append("$('#contentBarList').hide();");
            __strResult.append("}");
        }

        return __strResult.toString();
    }
}
