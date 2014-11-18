import services.UtilHelper;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;


public class GetBigPic extends HttpServlet 
    implements SingleThreadModel {



    public void doGet(HttpServletRequest request,
		      HttpServletResponse response)
	throws ServletException, IOException {
	
	//  construct the query  from the client's QueryString
	String picid  = request.getQueryString();
	String query;
	String[] parts=picid.split("!");
	String id = parts[0];
	//String name=parts[1];
	String type=parts[1];
	query = "select subject, place, timing, description, owner_name from images where photo_id="
	        + id.substring(3);

	//ServletOutputStream out = response.getOutputStream();
	PrintWriter out = response.getWriter();
	HttpSession session = request.getSession();	
    	String name = (String) session.getAttribute("user");
	if (name == null) {
		name="";
	}
	/*
	 *   to execute the given query
	 */
	Connection conn = null;
	try {
        try{
        	conn = UtilHelper.getConnection();
	}
        catch(Exception ex){
	        
		System.out.println("<hr>" + ex.getMessage() + "<hr>");
        }
	    Statement stmt = conn.createStatement();
	    ResultSet rset = stmt.executeQuery(query);
	    response.setContentType("text/html");
            String title, place, time, descrip, owner;

	    if ( rset.next() ) {
	        title = rset.getString("subject");
	        place = rset.getString("place");
		time = rset.getString("timing");
		descrip= rset.getString("description");
		owner = rset.getString("owner_name");
		out.println("<!DOCTYPE HTML><html lang=\"en\">"+
			"<head><meta charset=\"UTF-8\">"+
			"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/jquery-1.9.1.min.js\" defer></script>"+
			"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/jquery.cookie.min.js\" defer></script>"+
			"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/buildHeader.js\" defer></script>"+
			"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/myImages.js\" defer></script>"+
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"/CMPUT391F/Sources/css/main.css\">"+
			"<title>LUX Image Hosting</title></head><body><br><br><div class=\"section hcenter\">");
		//out.println("<FORM NAME=\"edit\" METHOD=\"POST\">");
                out.println("<h1><center> Subject: "+title+ "</center></h1>");
		//<INPUT TYPE=\"BUTTON\" NAME=\"editSubject\" VALUE = \"Edit\" ONCLICK=\"editSubject()\">);
		out.println("<img src = \"GetOnePic?"+id+"\">");
		out.println("<h3> Owner: " + owner + " </h3>");
		out.println("<h3> Place: " + place + " </h3>");
		out.println("<h3> Time: " + time + " </h3>");
		out.println("<h3> Description: " + descrip + " </h3>");

		if (name.compareTo(owner)==0){
			out.println("<P><a href=\"/CMPUT391F/edit.html?"+picid+"\"> Edit </a>");
		}

		if (type.contains("3"))
			out.println("<P><a href=\"publicPicBrowse\"> Return </a>");
		else if (type.contains("2"))
			out.println("<P><a href=\"groupPicBrowse\">  Return </a>");
		else
			out.println("<P><a href=\"myPicBrowse\">  Return </a>");
		out.println("</body></html>");
            }
	    else
	      out.println("<html> Pictures are not avialable</html>");
	} catch( Exception ex ) {
	    out.println(ex.getMessage() );
	}
	// to close the connection
	finally {
	    try {
		conn.close();
	    } catch ( SQLException ex) {
		out.println( ex.getMessage() );
	    }
	}
    }

/*	public void doPost(HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		if (request.getParameter("editSubject")!=null){
			System.out.println("<h3> Owner: ALALALA!!!! </h3>");
		}
	}*/
}
