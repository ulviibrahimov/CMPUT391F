/*
 * Code taken from http://luscar.cs.ualberta.ca:8080/yuan/UploadImage.java
 * and http://commons.apache.org/proper/commons-fileupload/
 * 
 */

import java.util.*;
import java.io.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


// Following from http://jakarta.apache.org/commons/fileupload/
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.FileItem;

import org.apache.commons.io.IOUtils;


public class RestController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.write(request.toString());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
		
		String result = "";

			// Works!! (debugging)
			// Iterator<Part> parts = request.getParts().iterator();
			// Part p;
			// while (parts.hasNext()) {
			// 	p = parts.next();
			// 	result += p.getName() +" "
			// 	+p.getHeader("content-disposition")+" "
			// 	+getContent(p)
			// 	+"<br>";
			// }

		// Route the request to the appropriate function.
		Part functionPart = request.getPart("function");
		if (functionPart == null) {
			result = "No 'function' key passed into post request data";
		} else {
			String function = getContent(functionPart);
			switch (function) {
				case "uploadOne":
					result = "<br>Upload status:<br>" + this.parseAndUpload(request);
					break;
				default:
					result = "Requested function is not mapped.";
					break;
			}
		}
		
		out.write(result);
    }

    private static String getContent(Part p) throws IOException {
        Scanner s = new Scanner(p.getInputStream(), "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
	}

	private static String getFileName(Part part) {
	    String partHeader = part.getHeader("content-disposition");
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return "";
	}

    private String parseAndUpload(HttpServletRequest request) {
		try {
			if (!ServletFileUpload.isMultipartContent(request)) {
				return "Unexpected form submisssion."; // Not a file upload request
			}

			// Works!!
			Iterator<Part> parts = request.getParts().iterator();
			Part p;
			while (parts.hasNext()) {
				p = parts.next();

				switch (p.getName()) {
					case "file":
						String fileName = getFileName(p);
			        	if (fileName.length() == 0) {
			        		return "No file Selected.";
			        	}
			        	String extension = fileName.substring(fileName.length()-4).toLowerCase();
			        	//r+="&nbsp;&nbspExtension: "+extension+"<br>";
			        	if (!extension.equals(".jpg") && !extension.equals(".gif")) {
			        		return "Invalid file.  Only image files (.jpg and .gif) are accepted.";
			        	}
			        	if (uploadImage(p)) {
			        		return "Successfully uploaded "+fileName+".";
			        	} else {
			        		return "Failed to upload "+fileName+".";
			        	}
			        default:
			        	break;
				}

			}
		} catch( Exception ex ) {
		    return "Exception occurred: " + ex;
		}
		return "Failed to find a file to upload.";
	}

	private static boolean uploadImage(Part p) {

		    // File uploadedFile = new File(...);
		 //    	imageItem.write(uploadedFile);

			//     //Get the image stream
			//     InputStream instream = imageItem.getInputStream();
			    
		 //        // Connect to the database and create a statement
		 //     //    Connection conn = getConnected(drivername,dbstring, username,password);
			//     // Statement stmt = conn.createStatement();

			//     /*
			//      *  First, to generate a unique pic_id using an SQL sequence
			//      */
			//     ResultSet rset1 = stmt.executeQuery("SELECT pic_id_sequence.nextval from dual");
			//     rset1.next();
			//     pic_id = rset1.getInt(1);

			//     //Insert an empty blob into the table first. Note that you have to 
			//     //use the Oracle specific function empty_blob() to create an empty blob
			//     stmt.execute("INSERT INTO pictures VALUES("+pic_id+",'test',empty_blob())");
		 
			//     // to retrieve the lob_locator 
			//     // Note that you must use "FOR UPDATE" in the select statement
			//     String cmd = "SELECT * FROM pictures WHERE pic_id = "+pic_id+" FOR UPDATE";
			//     ResultSet rset = stmt.executeQuery(cmd);
			//     rset.next();
			//     BLOB myblob = ((OracleResultSet)rset).getBLOB(3);


			//     //Write the image to the blob object
			//     OutputStream outstream = myblob.getBinaryOutputStream();
			//     int size = myblob.getBufferSize();
			//     byte[] buffer = new byte[size];
			//     int length = -1;
			//     while ((length = instream.read(buffer)) != -1)
			// 	outstream.write(buffer, 0, length);
			//     instream.close();
			//     outstream.close();

		 //            stmt.executeUpdate("commit");
			//     response_message = " Upload OK!  ";
		 //            conn.close();
		return false;
	}

	private void connect() {
			//  change the following parameters to connect to the oracle database
		String username = "livojevi";
		String password = "****";
		String drivername = "oracle.jdbc.driver.OracleDriver";
		String dbstring ="jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
		int pic_id;

	 //    /*
	 //    /*   To connect to the specified database
	 //     */
	 //    private static Connection getConnected( String drivername,
		// 				    String dbstring,
		// 				    String username, 
		// 				    String password  ) 
		// throws Exception {
		// Class drvClass = Class.forName(drivername); 
		// DriverManager.registerDriver((Driver) drvClass.newInstance());
		// return( DriverManager.getConnection(dbstring,username,password));
		// } 
	}
}



