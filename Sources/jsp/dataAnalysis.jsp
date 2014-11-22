<%@ page import="java.sql.*" %>
<%@ page import="services.UtilHelper" %>

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
	<div class="section hcenter" style="display:none">

<%
	if (request.getParameter("analysisSubmit") == null){
		response.sendRedirect("dataAnalysis.html");
	}
%>
        <h1><center>Data Analysis Result:</center></h1>
<%
	String type  = request.getQueryString();
	String subject = request.getParameter("Subject");
	String ownerSort = request.getParameter("SortByOwner");
	String subjectSort = request.getParameter("SortBySubject");
	String owner=request.getParameter("Owner");
	String dateStart = request.getParameter("dateStart");
	String dateEnd = request.getParameter("dateEnd");
	String rangeSort = request.getParameter("Range");
	//String queryString=type+"-"+keywords+"-"+dateStart+"-"+dateEnd+"-"+searchtype;

	//out.println(subject+owner+dateStart+dateEnd);

	String endStatement="";
	if (!owner.equals("") && !subject.equals("") && !dateStart.equals("") && !dateEnd.equals(""))
		endStatement=" where i.owner_name = '"+owner+"' and i.subject = '"+ subject+"' and i.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') ";
	else if (!owner.equals("") && !subject.equals("") && dateStart.equals("") && dateEnd.equals(""))
		endStatement=" where i.owner_name = '"+owner+"' and i.subject = '"+ subject+"' ";
	else if (!owner.equals("") && subject.equals("") && !dateStart.equals("") && !dateEnd.equals(""))
		endStatement=" where i.owner_name = '"+owner+"' and i.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') ";
	else if (owner.equals("") && !subject.equals("") && !dateStart.equals("") && !dateEnd.equals(""))
		endStatement=" where i.subject = '"+ subject+"' and i.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') ";
	else if (!owner.equals("") && subject.equals("") && dateStart.equals("") && dateEnd.equals(""))
		endStatement=" where i.owner_name = '"+owner+"' ";
	else if (owner.equals("") && !subject.equals("") && dateStart.equals("") && dateEnd.equals(""))
		endStatement=" where  i.subject = '"+ subject+"' ";
	else
		endStatement=" where i.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') ";

	Connection conn = null;  
        conn = UtilHelper.getConnection();
	Statement stmt = null;
	ResultSet rset = null;

	stmt = conn.createStatement();


	String query="";
	if (subjectSort==null && ownerSort==null && rangeSort.equals("")){
		query = "SELECT count(*) from images i "+endStatement;
		rset = stmt.executeQuery(query);
		//out.println(query);
		if (rset.next() )
			out.println((rset.getObject(1)).toString());
		else
			out.println("something wrong");
	}
	else if (subjectSort!=null && ownerSort==null && rangeSort.equals("")){
		query = "SELECT count(i.photo_id), i.subject from images i "+endStatement + " group by i.subject ";
		//out.println(query);
		rset = stmt.executeQuery(query);
		while (rset.next() )
			out.println((rset.getObject(2)).toString()+": "+(rset.getObject(1)).toString());
			//out.println("<br>");
			//out.println("<br>");

	}
	else if (subjectSort==null && ownerSort!=null && rangeSort.equals("")){
		query = "SELECT count(i.photo_id), i.owner_name from images i "+endStatement + " group by i.owner_name ";
		//out.println(query);
		rset = stmt.executeQuery(query);
		while (rset.next() )
			out.println((rset.getObject(2)).toString()+": "+(rset.getObject(1)).toString());
			//out.println("<br>");
			//out.println("<br>");

	}


%>
	</div>
</body>
</html>
