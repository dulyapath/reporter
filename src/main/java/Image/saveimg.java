/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Image;


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
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import utils._routine;

/**
 *
 * @author sml-dev-pcz
 */
@WebServlet(name = "save-img", urlPatterns = {"/save-img"})
public class saveimg extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject objResult = new JSONObject();
        objResult.put("success", false);

        HttpSession _sess = request.getSession();

        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {
            objResult.put("msg", "Please login");
            objResult.put("dev_msg", "Please login");
            response.getWriter().print(objResult);
            return;
        }
        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();

        int page = 1;
        int limit = 30;
        int offset = 0;

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname);

            String strFromDate = "";

            DecimalFormat decim = new DecimalFormat("#,###.##");

            if (request.getParameter("image_id") == null) {
                return;
            }
            if (request.getParameter("image_file") == null) {
                return;
            }
            if (request.getParameter("guid_code") == null) {
                return;
            }

            String imgreplaced = request.getParameter("image_file").replace("data:image/png;base64,", "");
            String imgreplaced2 = imgreplaced.replace("data:image/gif;base64,", "");
            String imgreplaced3 = imgreplaced2.replace("data:image/jpg;base64,", "");
            String imgreplaced4 = imgreplaced3.replace("data:image/bmp;base64,", "");
            String imgreplaced5 = imgreplaced4.replace("data:image/jpeg;base64,", "");

            StringBuilder __result = new StringBuilder();
 
            byte[] bytesDecoded = Base64.decodeBase64(imgreplaced5.getBytes());

            String __queryGetInventory = "update images set image_file=? ,create_date_time_now=now() where  guid_code='" + request.getParameter("guid_code") + "' and image_id='" + request.getParameter("image_id") + "'";

            try {
                java.sql.PreparedStatement __pstmt = __conn.prepareStatement(__queryGetInventory);
            
                __pstmt.setBytes(1, bytesDecoded);
                __pstmt.executeUpdate();
                  //System.out.println("Success:" + __queryGetInventory);
                __pstmt.close();
            } catch (Exception __ex) {
                //System.out.println("_query:" + __ex.getMessage() + ":" + __queryGetInventory);
            }
   
            objResult.put("success", true);

        } catch (Exception e) {
            objResult.put("msg", "เกิดข้อผิดพลาด");
            objResult.put("dev_msg", "Exception :: " + e.getMessage());
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

        response.getWriter().print(objResult);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject objResult = new JSONObject();
        objResult.put("success", false);

        HttpSession _sess = request.getSession();

        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {
            objResult.put("msg", "Please login");
            objResult.put("dev_msg", "Please login");
            response.getWriter().print(objResult);
            return;
        }
        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();

        int page = 1;
        int limit = 30;
        int offset = 0;

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname);

            String strFromDate = "";

            DecimalFormat decim = new DecimalFormat("#,###.##");

            String _Image_id = "";

            //_Image_id = request.getParameter("image_id");
            String __queryHead = "select * from ic_inventory limit 1";

            //System.err.println(__queryHead);
            Statement __stmtHead = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmtHead.executeQuery(__queryHead);
            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();

            JSONArray allList = new JSONArray();

            while (__rsHead.next()) {
                JSONObject tmpList = new JSONObject();

                tmpList.put("images", "1234");

                allList.put(tmpList);
            }

            __rsHead.close();
            __stmtHead.close();
            //objResult.put("query", __queryHead);
            objResult.put("success", true);
            objResult.put("data", allList);

        } catch (SQLException e) {

            objResult.put("msg", "เกิดข้อผิดพลาดจากฐานข้อมูล");
            objResult.put("dev_msg", "SQLException :: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            objResult.put("msg", "เกิดข้อผิดพลาด");
            objResult.put("dev_msg", "Exception :: " + e.getMessage());
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

}
