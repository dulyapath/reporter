<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="utils.RandomID"%>
<%
    List js = request.getAttribute("js") == null ? new ArrayList<String>() : (List) request.getAttribute("js");
    String v = "?_=" + RandomID.rand();
%>

<script src="${sublink}js/jquery-3.2.1.min.js"></script>
<!--<script src="${sublink}js/_globals.js"></script>-->
<script src="${sublink}js/jquery.cookie.js"></script>
<script src="${sublink}assets/bootstrap/js/bootstrap.min.js"></script>
<script src="${sublink}js/fastclick.js"></script>
<script src="${sublink}js/moment.min.js"></script>
<script src="${sublink}js/locale/th.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/v/bs4/jszip-2.5.0/dt-1.10.25/b-1.7.1/b-colvis-1.7.1/b-html5-1.7.1/b-print-1.7.1/datatables.min.js"></script>

<script src="${sublink}js/custom.min.js${v}"></script>
<!-- jQuery UI 1.11.4 -->
<script src="${sublink}plugins/jquery-ui/jquery-ui.min.js"></script>
<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<!-- Bootstrap 4 -->
<script src="${sublink}plugins/bootstrap/js/bootstrap.bundle.min.js"></script>

<!-- AdminLTE App -->
<script src="${sublink}dist/js/adminlte.js"></script>
<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/js/select2.min.js"></script>

<%
    if (js.size() > 0) {
        for (int i = 0; i < js.size(); i++) {
            out.print("<script src=\"" + js.get(i).toString() + v + "\"></script>");
        }
    }
%>
</body>
</html>
