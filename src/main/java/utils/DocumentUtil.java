package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentUtil {
    
    public static String generateDocID(String dbname, String tabaleName, String columnName, String docCode) {
        return generateDocID(dbname, tabaleName, columnName, docCode, "@ปปดด-#####" , "");
    }

    public static String generateDocID(String dbname, String tabaleName, String columnName, String docCode, String docFormat , String prefix) {
        
        docCode = docCode.toUpperCase();
        
        //docFormat = "@ปปดด-#####";
        String docNo = docFormat;
        
        String lastDocNo = "";

        _routine __routine = new _routine();
        Connection __conn = __routine._connect(dbname);

        StringBuilder sql = new StringBuilder("SELECT " + columnName + " FROM " + tabaleName + " WHERE " + columnName + " LIKE ? ORDER BY " + columnName + " DESC LIMIT 1");
//        StringBuilder sqlFormat = new StringBuilder("SELECT format FROM erp_doc_format WHERE code = ? LIMIT 1");

        try {
//            if (docCode != "YYY" && docCode != "XXX") {
//                PreparedStatement stmtFormat = __conn.prepareStatement(sqlFormat.toString());
//
//                stmtFormat.setString(1, docCode);
//                ResultSet rsFormat = stmtFormat.executeQuery();
//                while (rsFormat.next()) {
//
//                    docNo = rsFormat.getString("format");
//                    docFormat = rsFormat.getString("format");
//                }
//                
//                rsFormat.close();
//                stmtFormat.close();
//            }

            docNo = docNo.replace("ป", "y").replace("ด", "M").replace("ว", "d");
            SimpleDateFormat format = new SimpleDateFormat(docNo, new Locale("th", "TH"));

            docNo = format.format(new Date()).replace("@", docCode);

            //
            PreparedStatement stmt = __conn.prepareStatement(sql.toString());
            stmt.setString(1, docNo.replace("#", "") + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lastDocNo = rs.getString(columnName);
            }

            int docNumberLength = docFormat.length() - docFormat.replace("#", "").length();

            StringBuilder sharpText = new StringBuilder();
            int sharpLength = docNumberLength;

            do {
                sharpText.append("#");
                --sharpLength;
            } while (sharpLength > 0);

            int newDocNumber = 1;
            if (lastDocNo != "") {
                newDocNumber = Integer.parseInt(lastDocNo.replace(docCode, "@").split("-")[1]) + 1;
            }

            String docNumber = String.format("%0" + docNumberLength + "d", newDocNumber);

            docNo = prefix+docNo.replace(sharpText, docNumber);

            rs.close();
            stmt.close();
            

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (__conn != null) {
                    __conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return docNo;
    }
}
