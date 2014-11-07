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

			Cookie[] cookies = request.getCookies();
		    boolean foundCookie = false;

		    for(int i = 0; i < cookies.length; i++) { 
		        Cookie c = cookies[i];
		        if (c.getName().equals(userName)) {
		            foundCookie = true;
		            //planning to redirect to home page
		        }
		    }  
			//if there is no cookie with the current user name, create new one
		    if (!foundCookie) {
		        Cookie c = new Cookie(userName, session.getId()+"");
		        c.setMaxAge(24*60*60);
		        c.setPath("/");
		        response.addCookie(c); 
		    }

			
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
		        conn = DriverManager.getConnection(dbstring,"ulvi","6755711ibrahimov");
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
				String redirectURL = "/CMPUT391F/login.html";
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
