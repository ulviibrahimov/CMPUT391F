<%@ page import="java.sql.*, java.util.*" %>
<%@ page import="services.UtilHelper" %>
<!DOCTYPE HTML>
<html lang="en">
  <head>
	<meta charset="UTF-8">
	<script type="text/javascript" src="/CMPUT391F/Sources/js/jquery-1.9.1.min.js" defer></script>
	<script type="text/javascript" src="/CMPUT391F/Sources/js/jquery.cookie.min.js" defer></script>
	<script type="text/javascript" src="/CMPUT391F/Sources/js/buildHeader.js" defer></script>
	<script type="text/javascript" src="/CMPUT391F/Sources/js/myImages.js" defer></script>
	<link rel="stylesheet" type="text/css" href="/CMPUT391F/Sources/css/main.css">
	<title>LUX Image Hosting</title>
  </head>
<body>

	<br>
	<br>
	<div class="section hcenter" style="display:none">
        <h1><center>Images</center></h1>

		
	<a href="/CMPUT391F/myPicBrowse">My Images</a>

	<a href="/CMPUT391F/publicPicBrowse">Public Images</a>
	<a href="/CMPUT391F/groupPicBrowse">Group Images</a>

<% 
	String name = (String) session.getAttribute("user");
        if(name.equals("admin"))
        {
	        out.println("<a href=\"/CMPUT391F/PictureBrowse\">All images</a>");
	}
%>

    </body>
</html>
