<%@ page import="java.sql.*, java.util.*" %>
<%@ page import="services.UtilHelper" %>


<%
	
/**
 *
 * Sourse code: indexExample.jsp from http://luscar.cs.ualberta.ca:8080/yuan/index.html
 *
 * This servlet retrieve the parameters for search and send request to 
 * searchResult.jsp
 *
 *  @author  Xiaolu Wang
 *
 */

	String type  = request.getQueryString();
	String keywords = request.getParameter("key");
	String dateStart = request.getParameter("dateStart");
	String dateEnd = request.getParameter("dateEnd");
	String searchtype=request.getParameter("SEARCHTYPE");
	String queryString=type+"-"+keywords+"-"+dateStart+"-"+dateEnd+"-"+searchtype;

	response.sendRedirect("/CMPUT391F/Sources/jsp/searchReturn.jsp?"+queryString);
%>

