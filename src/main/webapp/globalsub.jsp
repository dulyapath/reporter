<%
    if (session.getAttribute("user") == null) {
        String site = new String("../login.jsp");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.sendRedirect(site);
        return;
    }

%>