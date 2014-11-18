
import services.UtilHelper;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import oracle.jdbc.driver.*;
import java.text.*;
import java.net.*;


public class PictureBrowse extends HttpServlet implements SingleThreadModel {
    
    public String name;
    public int type=0;
    public void doGet(HttpServletRequest request,
		      HttpServletResponse res)
	throws ServletException, IOException {


	setType();
	//  send out the HTML file
	res.setContentType("text/html");
	PrintWriter out = res.getWriter ();
        out.println("<!DOCTYPE HTML><html lang=\"en\">"+
		"<head><meta charset=\"UTF-8\">"+
		"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/jquery-1.9.1.min.js\" defer></script>"+
		"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/jquery.cookie.min.js\" defer></script>"+
		"<script type=\"text/javascript\" src=\"/CMPUT391F/Sources/js/buildHeader.js\" defer></script>"+
		"<link rel=\"stylesheet\" type=\"text/css\" href=\"/CMPUT391F/Sources/css/main.css\">"+
		"<title>LUX Image Hosting</title></head><body>");
	out.println("<br><br><div class=\"section hcenter\"><center><h3> Images</h3>");
	name=request.getQueryString().trim();

	if (!name.contains("public") || type==3){

	/*
	 *   to execute the given query
	 */
	try {
	    String query = setQuery();
       	    Connection conn = null;
        try{
        	conn = UtilHelper.getConnection();
        }
        catch(Exception ex){ 
        	System.out.println("<hr>" + ex.getMessage() + "<hr>");
        }

	    Statement stmt = conn.createStatement();
	    ResultSet rset = stmt.executeQuery(query);
	    String p_id = "";
		int times=0;
	    while (rset.next() ) {
		p_id = (rset.getObject(1)).toString();

	       // specify the servlet for the image
               out.println("<a href=\"GetBigPic?big"+p_id+"!"+name+"!"+type+"\">");
	       // specify the servlet for the themernail
	       out.println("<img src=\"GetOnePic?dis"+p_id +
	                   "\"></a>");
		times+=1;
	    }
		if (times==0){
			out.println("<h3> No pictures found.</h3>");
		}
	    stmt.close();
	    conn.close();
	} catch ( Exception ex ){ out.println(ex.toString() );}
	}
	else{
	out.println("<h3> You are not logged in.</h3>");
	}
    	out.println("<P><a href=\"/CMPUT391F/Sources/jsp/myimages.jsp?"+name+"\"> Return </a>");
	out.println("</body>");
	out.println("</html>");
    }
    public String setQuery(){
	String query = "select photo_id from images";
	return query;
    }

	public void setType(){
		this.type=0;
	}

}




