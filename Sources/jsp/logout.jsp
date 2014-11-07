<html><head>
<style type="text/css"></style></head>

<body>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%
	//deleteing the cookie using session id
	Cookie[] cookies = request.getCookies();
	for(int i = 0; i < cookies.length; i++) { 
		if (cookies[i].getValue().equals(session.getId()+"")) {
			//out.println(session.getId());
			//out.println(cookies[i].getName());
			Cookie dCookie = new Cookie(cookies[i].getName(),null);
			dCookie.setMaxAge(0);
     			dCookie.setPath("/");
     			response.addCookie(dCookie);
     			//destroying current session, beacuse session id is a cookie value for each user
     			if(session!=null){
     				session.invalidate();
     		}
     		//response.sendRedirect("/CMPUT391F/login.html");
		}
	}  
%>

</body></html>

