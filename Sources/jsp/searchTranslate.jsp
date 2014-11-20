<%@ page import="java.sql.*, java.util.*" %>
<%@ page import="services.UtilHelper" %>


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

