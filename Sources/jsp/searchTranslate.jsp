<%@ page import="java.sql.*, java.util.*" %>
<%@ page import="java.io.*,javax.servlet.*" %>
<%@ page import="java.servlet.http.*,java.sql.*,oracle.jdbc.driver.*,java.text.*,java.net.*" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="services.UtilHelper" %>
<%@ page import="org.apache.commons.fileupload.*, org.apache.commons.fileupload.disk.*, org.apache.commons.fileupload.servlet.*" %>
<%@ page import="db.Database.*" %>


<%
	

	//retrieves the text field paramters from searchStart.html

	String type  = request.getQueryString();
	String keywords = request.getParameter("key");
	String dateStart = request.getParameter("dateStart");
	String dateEnd = request.getParameter("dateEnd");
	String searchtype=request.getParameter("SEARCHTYPE");
	String queryString=type+"-"+keywords+"-"+dateStart+"-"+dateEnd+"-"+searchtype;

	response.sendRedirect("/CMPUT391F/Sources/jsp/searchReturn.jsp?"+queryString);
%>

