import services.UtilHelper;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

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
