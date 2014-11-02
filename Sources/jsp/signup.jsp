<html><head>


<title>Signup Result</title>
<style type="text/css"></style></head>

<body>

<%@ page import="java.sql.*" %>
<%@ page import="java.util.Date" %>

<% 
		
        if(request.getParameter("signupSubmit") != null)
        {

	        //get the user input from the login page
        	String userName = (request.getParameter("USERNAME")).trim();
	        String passwd = (request.getParameter("PASSWD")).trim();
	        String firstName = (request.getParameter("FIRSTNAME")).trim();
	        String lastName = (request.getParameter("LASTNAME")).trim();
	        String address = (request.getParameter("address")).trim();
	        String email = (request.getParameter("email")).trim();
	        String phone = (request.getParameter("phone")).trim();

	        //establish the connection to the underlying database
        	Connection conn = null;
	
	        String driverName = "oracle.jdbc.driver.OracleDriver";
            	String dbstring = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
	
	        try{
		        //load and register the driver
        		Class drvClass = Class.forName(driverName); 
	        	DriverManager.registerDriver((Driver) drvClass.newInstance());
        	}
	        catch(Exception ex){
		        out.println("<hr>" + ex.getMessage() + "<hr>");
	
	        }
	
        	try{
	        	//establish the connection 
		        conn = DriverManager.getConnection(dbstring,"user","******");
        		conn.setAutoCommit(false);
	        }
        	catch(Exception ex){
	        
		        out.println("<hr>" + ex.getMessage() + "<hr>");
        	}
        	
			//checking if username already exists
			
			Statement ustmt = null;
	        ResultSet urset = null;
			String usql = "select count (user_name) from users where user_name = '"+userName+"'";
			try{
	        	ustmt = conn.createStatement();
		        urset = ustmt.executeQuery(usql);
        	}
			catch(Exception ex){
		        out.println("<hr>" + ex.getMessage() + "<hr>");
        	}
        	
        	Integer uCount = 0;
        	while(urset != null && urset.next())
	        	uCount = (urset.getInt(1));
	        
	        if (uCount>0){ 
	        	String uredirectURL = "/CMPUT391F/signup.html";
        		response.sendRedirect(uredirectURL);

	        }
	        
	        //checking if email already exists
			
			Statement estmt = null;
	        ResultSet erset = null;
			String esql = "select count (email) from persons where email = '"+email+"'";
			try{
	        	estmt = conn.createStatement();
		        erset = estmt.executeQuery(esql);
        	}
			catch(Exception ex){
		        out.println("<hr>" + ex.getMessage() + "<hr>");
        	}

        	Integer eCount = 0;
        	while(erset != null && erset.next())
	        	eCount = (erset.getInt(1));
	        if (eCount>0 && uCount==0){ 
	        	String eredirectURL = "/CMPUT391F/signup.html";
        		response.sendRedirect(eredirectURL);

	        }
			
		
	        //insert values into users and persons table
        	Statement stmt = null;
	        ResultSet rset = null;
	        java.util.Date date = new java.util.Date();
      		long t = date.getTime();
        	String sqlusers = "insert into users values ('"+userName+"','"+passwd+"',"+"SYSDATE"+")";
        	String sqlpersons = "insert into persons values ('"+userName+"','"+firstName+"','"+lastName+"','"+address+"','"+email+"','"+phone+"')";
        	//out.println(sqlusers);
        	try{
	        	stmt = conn.createStatement();
		        stmt.executeUpdate(sqlusers);
		        stmt.executeUpdate(sqlpersons);
		        out.println("<p><b>Signup Successful!</b></p>");
		        response.sendRedirect("/CMPUT391F/login.html");
        	}
	
	        catch(Exception ex){
	        	out.println("<hr>" + ex.getMessage() + "<hr>");
        	}
	
	        

            try{
                    conn.close();
            }
            catch(Exception ex){
                    out.println("<hr>" + ex.getMessage() + "<hr>");
            }
        }
 
%>






</body></html>
