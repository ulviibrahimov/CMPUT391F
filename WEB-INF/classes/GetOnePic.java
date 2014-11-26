import services.UtilHelper;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/**
 *
 * Sourse code: GetOnePic.java from http://luscar.cs.ualberta.ca:8080/yuan/index.html
 *
 *  This servlet sends one picture stored in the table below to the client 
 *  who requested the servlet.
 *
 * images (photo_id int, owner_name varchar(24), permitted int,
 * 	subject varchar(128), place varchar(128), timing date,
 * 	description varchar(2048),thumbnail blob, photo blob)
 *
 *  The request must come with a query string as follows:
 *    GetOnePic?dis12: sends the thumbnail in images with photso_id = 12
 *    GetOnePic?big12: sends the photo in images with photo_id = 12
 *
 *  @author  Xiaolu Wang
 *
 */

public class GetOnePic extends HttpServlet 
    implements SingleThreadModel {


    public void doGet(HttpServletRequest request,
		      HttpServletResponse response)
	throws ServletException, IOException {
		String picid  = request.getQueryString();
		String query;

	if ( picid.contains("big") )  {
	    //query = "select photo from images where photo_id=" + picid.substring(3);

	    query = "select photo from images where photo_id=" + picid.substring(3);
	}
	else{
	    query = "select thumbnail from images where photo_id=" + picid.substring(3);
	}
	    ServletOutputStream out = response.getOutputStream();

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

	    	if ( rset.next() ) {
	    		response.setContentType("image/gif");
	    		InputStream input = rset.getBinaryStream(1);	    
	    		int imageByte;
	    		while((imageByte = input.read()) != -1) {
	    			out.write(imageByte);
	    		}
	    		input.close();
	    	} 
	    	else 
	    		out.println("no picture available");
	} catch( Exception ex ) {
	    	out.println(ex.getMessage() );
	}
	finally {
		try {
	    		conn.close();
	    	} catch ( SQLException ex) {
	    		System.out.println( ex.getMessage() );
	    	}
	}

    }

}
