/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Image;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import org.apache.tomcat.util.codec.binary.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import utils._routine;

/**
 *
 * @author sml-dev-pcz
 */
@WebServlet(name = "get-images", urlPatterns = {"/get-images"})
public class getimg extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet List</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet List at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder __html = new StringBuilder();

        HttpSession _sess = request.getSession();
        JSONObject objResult = new JSONObject();
        objResult.put("success", false);

        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();
        PreparedStatement __stmt = null;
        int page = 1;
        int limit = 30;
        int offset = 0;
        String search="";
        if (request.getParameter("from_item") != null && request.getParameter("from_item").equals("noting") == false && request.getParameter("to_item") != null && request.getParameter("to_item").equals("noting") == false) {
             search = "  where image_id Between '" + request.getParameter("from_item") + "' and '" + request.getParameter("to_item") + "'";
        }

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname);

            String __queryExtend = "";

            Integer branch = 0;

            String __queryHead = " select guid_code,image_id from images "+search;

            __stmt = __conn.prepareStatement(__queryHead);
            ResultSet __rsHead = __stmt.executeQuery();

            JSONArray allListX = new JSONArray();

            while (__rsHead.next()) {
                JSONObject tmpListX = new JSONObject();
                tmpListX.put("image_id", __rsHead.getString("image_id"));
                tmpListX.put("guid_code", __rsHead.getString("guid_code"));

                String query = "select image_file from images where guid_code = '" + __rsHead.getString("guid_code") + "' ";
                //System.out.println(query);
                byte[] __value = new byte[1024];
                __stmt = __conn.prepareStatement(query);
                ResultSet __rsHead1 = __stmt.executeQuery();
                ResultSetMetaData __rsmd = __rsHead1.getMetaData();
                int __colCount = __rsmd.getColumnCount();

                while (__rsHead1.next()) {

                    for (int __i = 1; __i <= __colCount; __i++) {
                        // String columnName = rsmd.getColumnName(i);
                        __value = __rsHead1.getBytes(__i);
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("data:image/png;base64,");
                    sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(__value, false)));
                    String contourChart = sb.toString();

                    byte[] bytesEncoded = Base64.encodeBase64(__value);

                    //    System.out.println("encoded: "+new String(encodedBytes));  
                    tmpListX.put("image_file", contourChart);
                    //System.out.println(__value);
                }
                allListX.put(tmpListX);
            }

            objResult.put("success", true);
            objResult.put("data", allListX);

            __rsHead.close();
            __stmt.close();

        } catch (SQLException e) {
            __html.append(e.getMessage());
            e.printStackTrace();
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
