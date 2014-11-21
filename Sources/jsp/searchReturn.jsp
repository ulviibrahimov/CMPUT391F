<%@ page import="java.sql.*, java.util.*" %>
<%@ page import="services.UtilHelper" %>


<%
	

	//retrieves the text field paramters from searchStart.html
	String queryString  = request.getQueryString();
	String[] parts=queryString.split("-");
	String type  = parts[0];
	String keywords = parts[1];
	String dateStart = parts[2];
	String dateEnd = parts[3];
	String searchtype=parts[4];


	//depending on which order radio button is selected, create a relevant query string for it
	String queryOrder = "images.timing DESC";
	if (searchtype!= null) {
		if (searchtype.equals("recentFirst")) {
			queryOrder = "images.timing DESC";
		} else if (searchtype.equals("recentLast")) {
			queryOrder = "images.timing ASC";
		} else if (searchtype.equals("relevant")) {
			queryOrder = "rank desc";
		}
		//if there is no order option selected, display an error
	} 
%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

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
<br><br><div class="section hcenter"><center><h1> Search Result</h1>
	
<%

	String userID = (String) session.getAttribute("user");
	Boolean display=true;
	String name = (String) session.getAttribute("user");
	String query = "select images.photo_id,images.subject,images.place,images.description";

	String canBrowse=" FROM images where images.photo_id in (select distinct i.photo_id from images i, group_lists g where (g.friend_id = '"+name+"' and g.group_id = i.permitted) or i.owner_name='"+name+"' or i.permitted=1) and ";

	if (!keywords.equals("") && !dateStart.equals("") && !dateEnd.equals("") && type.equals("3")) {
		query = query + ", ";
		String[] wordList = keywords.split(" "); //split the keyword value by space into an array for multiple keywords
		int matchNum = 0;
		for (int i = 0; i < wordList.length; i++) { //for every keyword create a query statement for it
			query = query + "6*score(" + Integer.toString(matchNum + 1)+ ")+3*score(" + Integer.toString(matchNum + 2)
						+ ")+score(" + Integer.toString(matchNum + 3)+ ") ";
			//if there are still more keywords, keep going (adds "+")
			if (i != wordList.length - 1)
				query = query + "+ ";
			matchNum = matchNum + 3;
		}
		query = query + "as rank ";
		query = query + canBrowse;
		out.println("Results of matching keywords: " + keywords+ " between dates " + dateStart + " and " + dateEnd);
		//query statement to search for a certain time period
		query = query + "images.timing between to_date('" + dateStart+ "', 'DD/MM/YYYY') AND to_date('" + dateEnd+ "', 'DD/MM/YYYY') AND ";
		//creates query statement for every key word to match with the score function
		int countNum = 0;
		for (int i = 0; i < wordList.length; i++) {
			query = query + "(contains(images.subject, '"+ wordList[i] + "', "+ Integer.toString(countNum + 1)+ ") > 0 OR contains(images.place, '"
			+ wordList[i] + "', "+ Integer.toString(countNum + 2)+ ") > 0 OR contains(images.description, '" + wordList[i]
			+ "', " + Integer.toString(countNum + 3)+ ") > 0)";
			if (i != wordList.length - 1)
				query = query + "OR ";
			countNum = countNum + 3;
		}
		query = query + "ORDER BY " + queryOrder; //order by the order query string made above
	}
	
	
	else if (!dateStart.equals("") && !dateEnd.equals("") && type.equals("2")) {
		out.println("Results of time period between " + dateStart+" and " + dateEnd);
		
		query = query + canBrowse;
		
		query = query + " images.timing between to_date('"+dateStart+ "','DD/MM/YYYY') AND to_date('" +
				dateEnd+"','DD/MM/YYYY') ORDER BY " + queryOrder;
		//out.println("<br>");
		//out.println(query);
		//out.println("<br>");

	}
	
	else if (!keywords.equals("") && type.equals("1")) {
		out.println("Resutls of matching keywords: " + keywords);
		query = query + ", ";
		String[] wordList = keywords.split(" ");
		int matchCount = 0;
		for (int i = 0; i<wordList.length; i++) {
			query = query + "6*score(" + Integer.toString(matchCount + 1)+ ")+3*score("+Integer.toString(matchCount+2)
					+")+score("+Integer.toString(matchCount+3)+")";
			if (i != wordList.length -1)
				query = query + "+ ";
			matchCount = matchCount+3;
		}
		
		query = query + " as rank ";
		
		query = query + canBrowse;
		
		int countNum = 0;
		for (int i = 0; i< wordList.length;i++) {
			query =query + "(contains(images.subject, '"+ wordList[i] + "', "+ Integer.toString(countNum + 1)+ ") > 0 OR contains(images.place, '"
					+ wordList[i] + "', "+ Integer.toString(countNum + 2)+ ") > 0 OR contains(images.description, '" + wordList[i]
					+ "', " + Integer.toString(countNum + 3)+ ") > 0)";
			if (i != wordList.length-1)
				query=query+"OR ";
			countNum = countNum+3;
		}
		query = query + "ORDER BY " + queryOrder; //order by the order query string made above
		//out.println("<br>");
		//out.println(query);
		//out.println("<br>");
	}
	else{
		out.println("Pleace enter the required parts");
		display=false;
	}
	//out.println(type);
%>

<br><center>

<%
	if (display){
		Connection conn = null;
		try{
			conn = UtilHelper.getConnection();
			conn.setAutoCommit(false);
			Statement stmt = null;
			ResultSet rset = null;
			stmt = conn.createStatement();
			rset = stmt.executeQuery(query);
			out.println("<br>");
			String p_id = "";
			int times=0;
			while (rset.next() && rset != null) {
				p_id = (rset.getObject(1)).toString();
				// specify the servlet for the image
				out.println("<a href=\"/CMPUT391F/GetBigPic?big"+p_id+"!4!"+queryString+"\">");
				// specify the servlet for the themernail
				out.println("<img src=\"/CMPUT391F/GetOnePic?dis"+p_id +
				"\"></a>");
				times+=1;
			}
			if (times==0){
				out.println("<h3> No pictures found.</h3>");
			}
			stmt.close();
			conn.close();
		} catch ( Exception e){ out.println( e.toString() );}
	}
	
%>	



<P><a href="/CMPUT391F/search.html"> Return </a></P>
</body>
</center>
</html>
