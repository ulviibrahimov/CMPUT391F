<%@ page import="java.sql.*, java.util.*" %>
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
	<div class="section hcenter">
        <h1><center>Images</center></h1>

		
	<a href="/CMPUT391F/myPicBrowse?<%
		try{
			String picid = request.getQueryString().trim();
			out.println(picid);
		}

		catch(Exception e){out.println(e.getMessage());}
           %>">My Images</a>

	<a href="/CMPUT391F/publicPicBrowse?<%
		try{
			String picid = request.getQueryString().trim();
			out.println(picid);
		}

		catch(Exception e){out.println(e.getMessage());}
           %>">Public Images</a>
	<a href="/CMPUT391F/groupPicBrowse?<%
		try{
			String picid = request.getQueryString().trim();
			out.println(picid);
		}

		catch(Exception e){out.println(e.getMessage());}
           %>">Group Images</a>
    </body>
</html>
