/*
 * Code taken from http://luscar.cs.ualberta.ca:8080/yuan/UploadImage.java
 * and http://commons.apache.org/proper/commons-fileupload/
 * 
 */

import java.util.*;
import java.io.*;

import java.sql.*;
import oracle.sql.*;
import oracle.jdbc.*;

// import javax.servlet.*;
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

        out.write("Get is kinda empty");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
		
		

		if (!ServletFileUpload.isMultipartContent(request)) {
			out.write("Unexpected form submisssion."); // Not a file upload request
			return;
		}

		try {
			// TODO verify session

			// Get map of request parts
		    Map<String,List<FileItem>> map = (new ServletFileUpload(new DiskFileItemFactory())).parseParameterMap(request);

		    String function = getTextValue("function", map);

		    // Route the request to the appropriate function.
			String result;
		    if (function.equals("uploadOne")) {
				result = "<br>Upload status:<br>" + uploadFile(map);
			} else {
				result = "Requested function is not mapped.";
			}
			out.write(result);

		} catch (Exception e) {
			out.write("Exception occurred: " + e);
			return;
		}
    }

    /*
     * Gets a string from the form field represented by key.  The key value pair is removed from the map.
     */
	private static String getTextValue(String key, Map<String,List<FileItem>> map) throws Exception{
    	List<FileItem> funcItems = map.remove(key);

	    // Check that key does exist
	    if (funcItems == null) {
	    	throw new Exception("No '"+key+"'' key passed into post request data.");
		}

		// Check for key value
	    Iterator<FileItem> i = funcItems.iterator();
	    if (!i.hasNext()) {
	    	throw new Exception("Malformed '"+key+"'value.");
	    }
	    FileItem item = i.next();
	    if (!item.isFormField()) {
	    	throw new Exception("Unexpected value type for '"+key+"'.  Expected: simple text");
	    }
	    return item.getString();
    }

	/*
     * Gets the InputStream for a file from a form represented by key.
     */
	private static InputStream getFileValue(String key, Map<String,List<FileItem>> map) throws Exception{
    	List<FileItem> funcItems = map.get(key);

	    // Check that key does exist
	    if (funcItems == null) {
	    	throw new Exception("No '"+key+"'' key passed into post request data.");
		}

		// Check for key value
	    Iterator<FileItem> i = funcItems.iterator();
	    if (!i.hasNext()) {
	    	throw new Exception("Malformed '"+key+"'value.");
	    }
	    FileItem item = i.next();
	    if (item.isFormField()) {
	    	throw new Exception("Unexpected value type for '"+key+"'.  Expected: file content");
	    }
	    return item.getInputStream();
    }

    private String uploadFile(Map<String,List<FileItem>> map) {
    	String result = "";
		try {
			InputStream inStream = getFileValue("selected-file", map);

			//  connect to the oracle database
			// 1 gywnne DB but must enable tunnel first (can't have both sql developer and java connected)
			// 2 Local db
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			// Generate a unique pic_id using an SQL sequence
		    ResultSet rset1 = stmt.executeQuery("SELECT pic_id_seq.nextval from dual");
		    rset1.next();
		    int pic_id = rset1.getInt(1);

		    // TODO add real username

			//Insert an empty blob into the table first. Note that you have to 
		    //use the Oracle specific function empty_blob() to create an empty blob
		    stmt.execute("INSERT INTO images (PHOTO_ID, OWNER_ID, PHOTO) VALUES("+pic_id+",'test',empty_blob())");

		    // to retrieve the lob_locator 
		    // Note that you must use "FOR UPDATE" in the select statement
		    String cmd = "SELECT * FROM images WHERE photo_id = "+pic_id+" FOR UPDATE";
		    ResultSet rset = stmt.executeQuery(cmd);
		    rset.next();
		    BLOB myBlob = ((OracleResultSet)rset).getBLOB("PHOTO");

		    //Write the image to the blob object
		    OutputStream outStream = myBlob.setBinaryStream(1);
		    int size = myBlob.getBufferSize();
		    byte[] buffer = new byte[size];
		    int length = -1;
		    while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
		    inStream.close();
		    outStream.close();

            stmt.executeUpdate("commit");

            conn.close();
		} catch( Exception ex ) {
		    return result + "Exception occurred: " + ex;
		}
		return "Upload successful.";
	}

	/*
     *   To connect to the database specified by connectionConfigNum
     */
    public static Connection getConnection() throws Exception {
        String configParam[] = UtilHelper.getConfiguration();

        String dbstring = configParam[0];
        String username = configParam[1];
        String password = configParam[2];
        String drivername = "oracle.jdbc.driver.OracleDriver";

        return getConnected(drivername,dbstring, username,password);
    }

    /*
     *   To connect to the specified database
     */
    public static Connection getConnected(String drivername, String dbstring, String username, String password) throws Exception {
        Class drvClass = Class.forName(drivername); 
        DriverManager.registerDriver((Driver) drvClass.newInstance());
        return (DriverManager.getConnection(dbstring,username,password));
    }
}



