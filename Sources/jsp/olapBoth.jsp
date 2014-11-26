<%@ page import="java.sql.*" %>
<%@ page import="services.UtilHelper" %>
<%
/**
 *
 * input page for the data analysis
 * send parameters to dataAnalysis.jsp 
 *
 *  @author  Xiaolu Wang
 *
 */
%>
<!DOCTYPE HTML>
<html lang="en">
  <head>
	<meta charset="UTF-8">
	<script type="text/javascript" src="/CMPUT391F/Sources/js/jquery-1.9.1.min.js" defer></script>
	<script type="text/javascript" src="/CMPUT391F/Sources/js/jquery.cookie.min.js" defer></script>
	<script type="text/javascript" src="/CMPUT391F/Sources/js/buildHeader.js" defer></script>
	<link rel="stylesheet" type="text/css" href="/CMPUT391F/Sources/css/main.css">
	<title>LUX Image Hosting</title>
  </head>
<body>

	<br>
	<br>
	<div class="section hcenter admin-only" style="display:none">
        <h1><center>Data Analysis</center></h1>

        <form name="dataAnalysis" action="/CMPUT391F/Sources/jsp/dataAnalysis.jsp" method="post">
		
        <p>Please, fill out the form below to search the database for analysis</p>
        <table>
        <tbody><tr>
        <td>Select an user:</td>
        <td><select name="Owner">
<%      
	Connection conn = null;  
	try{
        	conn = UtilHelper.getConnection();
	        Statement stmt = null;
		ResultSet rset = null;
	        String sql = "select distinct user_name from users";

	        stmt = conn.createStatement();
		rset = stmt.executeQuery(sql);
		String name = "";
		while (rset.next() ) {
			name=(rset.getObject(1)).toString();
			out.println("<OPTION VALUE='"+name+"' SELECTED> "+name+" </OPTION>");
		}
	}
        catch(Exception ex){
		out.println("<hr>" + ex.getMessage() + "<hr>");
        }
%>
	<OPTION VALUE="" SELECTED>PLEASE SELECT AN OWNER</OPTION>
	</SELECT></TD>
	</TR>

	</td>
        </tr>

        <tr>
        <td>Sort By Owner</td>
        <td><input type="checkbox" name="SortByOwner" value ="true"></td>
        </tr>
        <tbody><tr>
        <td>Subject:</td>
        <td><input type="text" name="Subject" maxlength="24"></td>
        </tr>
        <tr>
        <td>Sort By Subject</td>
        <td><input type="checkbox" name="SortBySubject" value ="true"></td>
        </tr>
        <tbody><tr>
	<td>Time period: from</td> 
	<TD><input name="dateStart" type="text" size="50" placeholder="DD/MM/YYYY"></input></TD>
        <tbody><tr>
	<td>Time period: to</td> 
	<TD><input name="dateEnd" type="text" size="50" placeholder="DD/MM/YYYY"></input></TD>
        <tbody><tr>
	<TR>
		<TD><B>Range:</B></TD>
		<TD><SELECT NAME='Range'>
		<OPTION VALUE='week' SELECTED>Week</OPTION>
		<OPTION VALUE='month' SELECTED>Month</OPTION>
		<OPTION VALUE='year' SELECTED>Year</OPTION>
		<OPTION VALUE="" SELECTED>PLEASE SELECT A RANGE</OPTION>
		</SELECT></TD>
	</TR>
	<TR><TD><P><input type="submit" name="analysisSubmit" value="submit"></TD></TR>
	<TR><TD><a href="dataAnalysis.html"> Return </a></TD></TR>

	</div>
</body>
</html>
