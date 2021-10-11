/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import utils._global;
import utils._routine;

/**
 *
 * @author sml-dev-pcz
 */
@WebServlet(name = "PermissionManage", urlPatterns = {"/permission-manage"})
public class PermissionManage extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JSONObject objResult = new JSONObject();
        objResult.put("success", false);
           
        HttpSession _sess = request.getSession();
        String __user = _sess.getAttribute("user").toString();
        
        if(_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()){
            objResult.put("msg", "Please login");
            objResult.put("dev_msg", "Please login");
            response.getWriter().print(objResult);
            return;
        }
        String m = "";
        
        if(request.getParameter("m") == null ||request.getParameter("m").isEmpty()){
            objResult.put("msg", "การเข้าถึงไม่ถูกต้อง");
            objResult.put("dev_msg", "action not found");
            response.getWriter().print(objResult);
            return;
        }
        
        m = request.getParameter("m");
        
        int mode = 0; // 0 = no result, 1 = result 
        String _query = "";
        
        if(m.equals("add")){
            String codeID = request.getParameter("ac_code").toString();
            String accessName = request.getParameter("ac_name").toString();
            int parentID = Integer.parseInt(request.getParameter("ac_parent").toString());
            _query = "INSERT INTO sml_web_access (web_flag,key_code,access_name,parent_id) VALUES ("+_global._WEB_FLAG+",'"+codeID+"','"+accessName+"',"+parentID+")";
            mode = 0;
        }else if(m.equals("edit")){
            int id = Integer.parseInt(request.getParameter("id").toString());
            String codeID = request.getParameter("ac_code").toString();
            String accessName = request.getParameter("ac_name").toString();
            int parentID = Integer.parseInt(request.getParameter("ac_parent").toString());
            _query = "UPDATE sml_web_access SET key_code ='"+codeID+"' ,access_name = '"+accessName+"' ,parent_id = "+parentID+" WHERE id = "+id;
            mode = 0;
        }else if(m.equals("parent")){
            _query = "SELECT id,key_code,access_name,parent_id FROM sml_web_access WHERE parent_id = 0 AND web_flag = "+_global._WEB_FLAG+"";
            mode = 1;
        }else if(m.equals("list")){
            _query = "SELECT id,key_code,access_name,parent_id,is_disable FROM sml_web_access WHERE  web_flag = "+_global._WEB_FLAG+" order by id asc" ;
  
            mode = 1;
        }else if(m.equals("del")){
            int id = Integer.parseInt(request.getParameter("id").toString());
            _query = "DELETE FROM sml_web_access WHERE id = "+id;
            mode = 0;
        }else if(m.equals("disable")){
            int id = Integer.parseInt(request.getParameter("id").toString());
            int disabled = Integer.parseInt(request.getParameter("disabled").toString());
            _query = "UPDATE sml_web_access SET is_disable = "+disabled+" WHERE id = "+id;
            mode = 0;
        }else if(m.equals("reset")){
            _query = "TRUNCATE sml_web_access RESTART IDENTITY ";
            mode = 0;
        }
        
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();
        
        if(!_query.isEmpty()){
            try{

                _routine __routine = new _routine();
                Connection __conn = __routine._connect("smlerpmain"+__dbname);

                Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                if(mode == 0){
                    __stmt.executeUpdate(_query);
                }else{
                    ResultSet __rs = __stmt.executeQuery(_query );
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
                }
                objResult.put("success", true);
                }catch(SQLException e){
                    objResult.put("msg", "เกิดข้อผิดพลาดจากฐานข้อมูล");
                    objResult.put("dev_msg", "SQLException :: " + e.getMessage());
                }catch(Exception e){
                    objResult.put("msg", "เกิดข้อผิดพลาด");
                    objResult.put("dev_msg", "Exception :: " + e.getMessage()); 
            }
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
