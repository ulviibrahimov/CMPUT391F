<html><head>


<title>Login Result</title>
<style type="text/css"></style>

</head>

<body>

<%@ page import="java.sql.*" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="services.UtilHelper" %>
<% 
		response.setContentType("text/html");
        if(request.getParameter("loginSubmit") != null)
        {
	        //get the user input from the login page
        	String userName = (request.getParameter("USERNAME")).trim();
	        String passwd = (request.getParameter("PASSWD")).trim();
			
	        //establish the connection to the underlying database
        	Connection conn = null;
	
        	try{
        		conn = UtilHelper.getConnection();

		        //select the user table from the underlying db and validate the user name and password
	        	Statement stmt = null;
		        ResultSet rset = null;
	        	String sql = "select password from users where user_name = '"+userName+"'";

	        	stmt = conn.createStatement();
		        rset = stmt.executeQuery(sql);


		        String truepwd = "";
		
	        	while(rset != null && rset.next())
		        	truepwd = (rset.getString(1)).trim();
	        	//display the result
		        if(passwd.equals(truepwd) && !truepwd.equals("")) {
		        	// On authenticate succes

		        	if (session.getAttribute("user") != null) {
						// We are already logged in, we should do something here, especially if we start storing more than the username in session
		        	}

		        	// Add username to session - indicates that the user is logged in
		            session.setAttribute("user", userName);

		            // Check for redirect
		            boolean redirect = false;
					Cookie[] cookies = request.getCookies();
					for(int i = 0; i < cookies.length; i++) { 
						if (cookies[i].getName().equals("redirect")) {
				     		redirect = true;
							String redirectAddress = URLDecoder.decode(cookies[i].getValue(), "UTF-8");

				     		// Delete the redirect cookie now that we will redirect
				     		Cookie dCookie = new Cookie("redirect",null);
							dCookie.setMaxAge(0);
			     			dCookie.setPath("/CMPUT391F");
			     			response.addCookie(dCookie);

			     			response.sendRedirect("/CMPUT391F/" + redirectAddress);
			     			return;
						}
					} 
					if (redirect == false) {
			            response.sendRedirect("/CMPUT391F");
			            return;
			        }
		    	} else{
		        	out.println("<p><b>Either your userName or Your password is inValid!</b></p>");
		        	session.setAttribute("error","Either your userName or Your password is inValid!");
					String redirectURL = "/CMPUT391F/login.html?error=invalid_login";
	        		response.sendRedirect(redirectURL);
	        	}
                conn.close();

        	} catch(Exception ex){
		        out.println("<hr>" + ex.getMessage() + "<hr>");
        	}
        }
 
%>






</body></html>
