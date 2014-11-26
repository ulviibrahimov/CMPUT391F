<%@ page import="java.sql.*" %>
<%@ page import="services.UtilHelper" %>
<%
/**
 *
 * Retrieve parameters from olapBoth.jsp
 * Use the parameter to set up query
 * Display the result of the query
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
	<div class="section hcenter">

<%
	if (request.getParameter("analysisSubmit") == null){
		response.sendRedirect("dataAnalysis.html");
	}
%>
<%!
	public String modifyPrint(Object input){
		if (input==null)
			return "null";
		else
			return input.toString();
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


	//printStatement=printStatement+"owner = " + owner +"  subject= " +subject +"  time period= "+ dataStart +"-"+dataEnd;
	String endStatement="";
	String printStatement="Search for: ";
	if (!owner.equals("") && !subject.equals("") && !dateStart.equals("") && !dateEnd.equals("")){
		endStatement=" where i.owner_name = '"+owner+"' and i.subject = '"+ subject+"' and i.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') ";
		out.println(printStatement+"owner = " + owner +"  subject = " +subject +"  time period = ");
		out.println(dateStart);
		out.println("-");
		out.println(dateEnd);
	}
	else if (!owner.equals("") && !subject.equals("") && dateStart.equals("") && dateEnd.equals("")){
		endStatement=" where i.owner_name = '"+owner+"' and i.subject = '"+ subject+"' ";
		out.println(printStatement+"owner = " + owner +"  subject = " +subject +"  time period = ");
		out.println(dateStart);
		out.println("-");
		out.println(dateEnd);
	}
	else if (!owner.equals("") && subject.equals("") && !dateStart.equals("") && !dateEnd.equals("")){
		endStatement=" where i.owner_name = '"+owner+"' and i.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') ";
		out.println(printStatement+"owner = " + owner +"  time period = ");
		out.println(dateStart);
		out.println("-");
		out.println(dateEnd);
	}
	else if (owner.equals("") && !subject.equals("") && !dateStart.equals("") && !dateEnd.equals("")){
		endStatement=" where i.subject = '"+ subject+"' and i.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') ";
		out.println(printStatement+"subject = " +subject +"  time period = ");
		out.println(dateStart);
		out.println("-");
		out.println(dateEnd);
	}
	else if (!owner.equals("") && subject.equals("") && dateStart.equals("") && dateEnd.equals("")){
		endStatement=" where i.owner_name = '"+owner+"' ";
		out.println(printStatement+"owner = " + owner);
	}
	else if (owner.equals("") && !subject.equals("") && dateStart.equals("") && dateEnd.equals("")){
		endStatement=" where  i.subject = '"+ subject+"' ";
		out.println(printStatement+"subject = " +subject);
	}
	else if (owner.equals("") && subject.equals("") && !dateStart.equals("") && !dateEnd.equals("")){
		endStatement=" where i.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') ";
		out.println(printStatement+"time period = ");
		out.println(dateStart);
		out.println("-");
		out.println(dateEnd);
	}
	else
		endStatement="";
	//out.println(printStatement);
	out.println("<br>");
	

	if (!endStatement.equals("")){
		Connection conn = null;  
		conn = UtilHelper.getConnection();
		Statement stmt = null;
		ResultSet rset = null;

		stmt = conn.createStatement();

		int i = 0;
		String query="";
		if (subjectSort==null && ownerSort==null && rangeSort.equals("")){
			query = "SELECT count(*) from images i "+endStatement;
			rset = stmt.executeQuery(query);
			//out.println(query);
			while (rset.next() ){
				out.println((rset.getObject(1)).toString());
				out.println("<br>");
				i++;
			}
		}
		else if (subjectSort!=null && ownerSort==null && rangeSort.equals("")){
			query = "SELECT count(i.photo_id), i.subject from images i "+endStatement + " group by i.subject ";
			//out.println(query);
			rset = stmt.executeQuery(query);
			while (rset.next() ){
				out.println("Subject "+modifyPrint(rset.getObject(2))+": "+(rset.getObject(1)).toString());
				out.println("<br>");
				//out.println("<br>");
				i++;
			}

		}
		else if (subjectSort==null && ownerSort!=null && rangeSort.equals("")){
			query = "SELECT count(i.photo_id), i.owner_name from images i "+endStatement + " group by i.owner_name ";
			//out.println(query);
			rset = stmt.executeQuery(query);
			while (rset.next() ){
				out.println("Owner "+modifyPrint(rset.getObject(2))+": "+(rset.getObject(1)).toString());
				out.println("<br>");
				//out.println("<br>");
				i++;
			}
		}
		else if (subjectSort!=null && ownerSort!=null && rangeSort.equals("")){
			query = "SELECT count(i.photo_id), i.owner_name, i.subject from images i "+endStatement + " group by i.owner_name, i.subject ";
			//out.println(query);
			rset = stmt.executeQuery(query);
			while (rset.next() ){
				out.println("Owner "+modifyPrint(rset.getObject(2))+" Subject "+modifyPrint(rset.getObject(3))+": "+(rset.getObject(1)).toString());
				out.println("<br>");
				//out.println("<br>");
				i++;
			}
		}
		else if (subjectSort==null && ownerSort==null && !rangeSort.equals("")){
			//out.println(rangeSort);
			if (rangeSort.equals("year")){
				query = "SELECT count(i.photo_id), extract(year from i.timing) from images i "+endStatement + " group by extract(year from i.timing) ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println(modifyPrint(rset.getObject(2))+": "+(rset.getObject(1)).toString());
					out.println("<br>");
					//out.println("<br>");
					i++;
				}
			}
			else if (rangeSort.equals("month")){
				query = "SELECT count(i.photo_id), extract(year from i.timing), extract(month from i.timing) from images i "+endStatement + " group by extract(year from i.timing), extract(month from i.timing) ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println(modifyPrint(rset.getObject(2))+"-"+modifyPrint(rset.getObject(3))+": "+(rset.getObject(1)).toString());
					out.println("<br>");
					//out.println("<br>");
					i++;
				}
			}
			else {
				query = "SELECT count(i.photo_id), extract(year from i.timing), extract(month from i.timing), to_char(i.timing,'w') from images i "+endStatement + " group by extract(year from i.timing), extract(month from i.timing), to_char(i.timing,'w') ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println(modifyPrint(rset.getObject(2))+"-"+modifyPrint(rset.getObject(3))+"(week "+modifyPrint(rset.getObject(4))+"): "+(rset.getObject(1)).toString());
					out.println("<br>");
					//out.println("<br>");
					i++;
				}
			}

		}
		else if (subjectSort!=null && ownerSort==null && !rangeSort.equals("")){
			//out.println(rangeSort);
			if (rangeSort.equals("year")){
				query = "SELECT count(i.photo_id), extract(year from i.timing),i.subject from images i "+endStatement + " group by extract(year from i.timing),i.subject ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Subject "+modifyPrint(rset.getObject(3))+" "+modifyPrint(rset.getObject(2))+": "+(rset.getObject(1)).toString());
					out.println("<br>");
					//out.println("<br>");
					i++;
				}
			}
			else if (rangeSort.equals("month")){
				query = "SELECT count(i.photo_id), extract(year from i.timing), extract(month from i.timing),i.subject from images i "+endStatement + " group by extract(year from i.timing), extract(month from i.timing),i.subject ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Subject "+modifyPrint(rset.getObject(4))+" "+modifyPrint(rset.getObject(2))+"-"+modifyPrint(rset.getObject(3))+": "+(rset.getObject(1)).toString());
					out.println("<br>");
					//out.println("<br>");
					i++;
				}
			}
			else {
				query = "SELECT count(i.photo_id), extract(year from i.timing), extract(month from i.timing), to_char(i.timing,'w'),i.subject from images i "+endStatement + " group by extract(year from i.timing), extract(month from i.timing), to_char(i.timing,'w'),i.subject ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Subject "+modifyPrint(rset.getObject(5))+" "+modifyPrint(rset.getObject(2))+"-"+modifyPrint(rset.getObject(3))+"(week "+modifyPrint(rset.getObject(4))+"): "+(rset.getObject(1)).toString());
					out.println("<br>");
					i++;
				}
			}

		}
		else if (subjectSort==null && ownerSort!=null && !rangeSort.equals("")){
			//out.println(rangeSort);
			if (rangeSort.equals("year")){
				query = "SELECT count(i.photo_id), extract(year from i.timing), i.owner_name from images i "+endStatement + " group by extract(year from i.timing), i.owner_name ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Owner "+modifyPrint(rset.getObject(3))+" "+modifyPrint(rset.getObject(2))+": "+(rset.getObject(1)).toString());
					out.println("<br>");
					i++;
				}
			}
			else if (rangeSort.equals("month")){
				query = "SELECT count(i.photo_id), extract(year from i.timing), extract(month from i.timing), i.owner_name from images i "+endStatement + " group by extract(year from i.timing), extract(month from i.timing), i.owner_name ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Owner "+modifyPrint(rset.getObject(4))+" "+modifyPrint(rset.getObject(2))+"-"+modifyPrint(rset.getObject(3))+": "+(rset.getObject(1)).toString());
					out.println("<br>");
					i++;
				}
			}
			else {
				query = "SELECT count(i.photo_id), extract(year from i.timing), extract(month from i.timing), to_char(i.timing,'w'), i.owner_name from images i "+endStatement + " group by extract(year from i.timing), extract(month from i.timing), to_char(i.timing,'w'), i.owner_name ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Owner "+modifyPrint(rset.getObject(5))+" "+modifyPrint(rset.getObject(2))+"-"+modifyPrint(rset.getObject(3))+"(week "+modifyPrint(rset.getObject(4))+"): "+(rset.getObject(1)).toString());
					out.println("<br>");
					i++;
				}
			}

		}

		else{
			//out.println(rangeSort);
			if (rangeSort.equals("year")){
				query = "SELECT count(i.photo_id), extract(year from i.timing), i.owner_name, i.subject from images i "+endStatement + " group by extract(year from i.timing), i.owner_name, i.subject ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Subject "+modifyPrint(rset.getObject(4))+" "+"Owner "+modifyPrint(rset.getObject(3))+" "+modifyPrint(rset.getObject(2))+": "+(rset.getObject(1)).toString());
					out.println("<br>");
					i++;
				}
			}
			else if (rangeSort.equals("month")){
				query = "SELECT count(i.photo_id), extract(year from i.timing), extract(month from i.timing), i.owner_name, i.subject from images i "+endStatement + " group by extract(year from i.timing), extract(month from i.timing), i.owner_name, i.subject ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Subject "+modifyPrint(rset.getObject(5))+" "+"Owner "+modifyPrint(rset.getObject(4))+" "+modifyPrint(rset.getObject(2))+"-"+modifyPrint(rset.getObject(3))+": "+(rset.getObject(1)).toString());
					out.println("<br>");
					i++;
				}
			}
			else {
				query = "SELECT count(i.photo_id), extract(year from i.timing), extract(month from i.timing), to_char(i.timing,'w'), i.owner_name, i.subject from images i "+endStatement + " group by extract(year from i.timing), extract(month from i.timing), to_char(i.timing,'w'), i.owner_name, i.subject ";
				//out.println(query);
				rset = stmt.executeQuery(query);
				while (rset.next() ){
					out.println("Subject "+modifyPrint(rset.getObject(6))+" "+"Owner "+modifyPrint(rset.getObject(5))+" "+modifyPrint(rset.getObject(2))+"-"+modifyPrint(rset.getObject(3))+"(week "+modifyPrint(rset.getObject(4))+"): "+(rset.getObject(1)).toString());
					out.println("<br>");
					i++;
				}
			}

		}
		if (i==0)
			out.println("0");

	}
	else
		out.println("Please give the valid input!");
	
%>
	<P><a href="/CMPUT391F/Sources/jsp/olapBoth.jsp"> Return </a></P>
	</div>
</body>
</html>
