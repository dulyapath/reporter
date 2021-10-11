package permission;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
import utils._routine;

@WebServlet(name = "UserList", urlPatterns = {"/user-list"})
public class UserList extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject objResult = new JSONObject();
        objResult.put("success", false);

        HttpSession _sess = request.getSession();
        String __user = _sess.getAttribute("user").toString();

        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {
            objResult.put("msg", "Please login");
            objResult.put("dev_msg", "Please login");
            response.getWriter().print(objResult);
            return;
        }

        String __dbname = "smlerpmain" + _sess.getAttribute("provider").toString().toLowerCase();

        try {

            _routine __routine = new _routine();
            Connection __conn = __routine._connect(__dbname);

            String _query = "select user_code, user_name from sml_user_list where UPPER(user_code) <> '" + __user + "'";

            Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rs = __stmt.executeQuery(_query);
            ResultSetMetaData _rsmd = __rs.getMetaData();
            int _colCount = _rsmd.getColumnCount();

            JSONArray _rsItems = new JSONArray();
            while (__rs.next()) {
                JSONObject _item = new JSONObject();
                for (int __i = 1; __i <= _colCount; __i++) {
                    String _colName = _rsmd.getColumnName(__i);
                    _item.put(_colName, __rs.getObject(__i));
                }
                _rsItems.put(_item);
            }
            objResult.put("data", _rsItems);
            objResult.put("success", true);
        } catch (SQLException e) {
            objResult.put("msg", "เกิดข้อผิดพลาดจากฐานข้อมูล");
            objResult.put("dev_msg", "SQLException :: " + e.getMessage());
        } catch (Exception e) {
            objResult.put("msg", "เกิดข้อผิดพลาด");
            objResult.put("dev_msg", "Exception :: " + e.getMessage());
        }
        response.getWriter().print(objResult);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
