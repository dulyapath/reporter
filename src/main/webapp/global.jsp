<%
    String __userCode = "";
    String __getDatabaseName = "";
    if (session.getAttribute("user") == null) {
        String site = new String("login.jsp");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", site);
        return;
    } else {
        __userCode = session.getAttribute("user").toString();
        
        __getDatabaseName = request.getParameter("database");
        if (__getDatabaseName != null && __getDatabaseName != "") {
            HttpSession __session = request.getSession();
            __session.setAttribute("dbname", __getDatabaseName);

            __session.setMaxInactiveInterval(30 * 60);

        }
    }


%>
