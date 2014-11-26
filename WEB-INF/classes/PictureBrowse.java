import services.UtilHelper;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import oracle.jdbc.driver.*;
import java.text.*;
import java.net.*;

/**
 * Sourse code: PictureBrowse.java from http://luscar.cs.ualberta.ca:8080/yuan/index.html
 * 
 * use servlet to query and display a list of pictures
 *
 * @author  Xiaolu Wang
 *
**/
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
	out.println("<br><br><div class=\"section hcenter\" style=\"display:none\"><center><h1> Images</h1>");
	HttpSession session = request.getSession();	
    	name = (String) session.getAttribute("user");
	if (name == null) {
		name="";
	}


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
		if (type==5 && times >4) {
			break;
		}
	       // specify the servlet for the image
               out.println("<a href=\"GetBigPic?big"+p_id+"!"+type+"\">");
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


    	out.println("<P><a href=\"/CMPUT391F/Sources/jsp/myimages.jsp\"> Return </a>");
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




