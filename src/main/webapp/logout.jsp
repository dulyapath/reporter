<%
    session.invalidate();
    String site = new String("login.jsp");
    response.setStatus(response.SC_MOVED_TEMPORARILY);
    response.setHeader("Location", site);
    return;
%>