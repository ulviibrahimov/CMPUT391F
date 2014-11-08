<html><head>
<style type="text/css"></style></head>

<body>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%
	if(session!=null){
		session.invalidate();
	}
	response.sendRedirect("/CMPUT391F");
%>

</body></html>

