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

	query = "select subject, place from images where photo_id="
	        + picid.substring(3);

	//ServletOutputStream out = response.getOutputStream();
	PrintWriter out = response.getWriter();

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
            String title, place;

	    if ( rset.next() ) {
	        title = rset.getString("subject");
	        place = rset.getString("place");
		out.println("<!DOCTYPE HTML><html lang=\"en\">"+
			"<head><meta charset=\"UTF-8\">"+
			"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/jquery-1.9.1.min.js\" defer></script>"+
			"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/jquery.cookie.min.js\" defer></script>"+
			"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/buildHeader.js\" defer></script>"+
			"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/myImages.js\" defer></script>"+
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"/CMPUT391F/Sources/css/main.css\">"+
			"<title>LUX Image Hosting</title></head><body>");
                out.println("<center><h3> "+title+ " </h3>" +
			"<img src = \"GetOnePic?"+picid+"\">" +
			"<h3>" + title +"  at " + place + " </h3>" +
			"</body></html>"+
			"<P><a href=\"PictureBrowse\"> Return </a>");
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


}
