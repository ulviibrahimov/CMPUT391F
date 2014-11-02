<html><head>


<title>Login Result</title>
<style type="text/css"></style></head>

<body>

<%@ page import="java.sql.*" %>
<% 

        if(request.getParameter("loginSubmit") != null)
        {

	        //get the user input from the login page
        	String userName = (request.getParameter("USERNAME")).trim();
	        String passwd = (request.getParameter("PASSWD")).trim();
        	//out.println("<p>Your input User Name is "+userName+"</p>");
        	//out.println("<p>Your input password is "+passwd+"</p>");


	        //establish the connection to the underlying database
        	Connection conn = null;
	
	        String driverName = "oracle.jdbc.driver.OracleDriver";
            	String dbstring = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
	
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
		        conn = DriverManager.getConnection(dbstring,"ulvi","*******");
        		conn.setAutoCommit(false);
	        }
        	catch(Exception ex){
	        
		        out.println("<hr>" + ex.getMessage() + "<hr>");
        	}
	

	        //select the user table from the underlying db and validate the user name and password
        	Statement stmt = null;
	        ResultSet rset = null;
        	String sql = "select password from users where user_name = '"+userName+"'";
        	try{
	        	stmt = conn.createStatement();
		        rset = stmt.executeQuery(sql);
        	}
	
	        catch(Exception ex){
		        out.println("<hr>" + ex.getMessage() + "<hr>");
        	}

	        String truepwd = "";
	
        	while(rset != null && rset.next())
	        	truepwd = (rset.getString(1)).trim();
        	//display the result
	        if(passwd.equals(truepwd))
		        out.println("<p><b>Your Login is Successful!</b></p>");
        	else{
	        	out.println("<p><b>Either your userName or Your password is inValid!</b></p>");
				String redirectURL = "./login.html";
        		response.sendRedirect(redirectURL);
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
