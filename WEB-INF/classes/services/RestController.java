/*
 * Code taken from http://luscar.cs.ualberta.ca:8080/yuan/UploadImage.java
 * and http://commons.apache.org/proper/commons-fileupload/
 * 
 */
package services;

import java.util.*;
import java.io.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.sql.*;
import java.sql.PreparedStatement;
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

    private String uploadFile(Map<String,List<FileItem>> map) {
    	String result = "";
    	
    	try {
			// Get all input fields
			int groupId = Integer.parseInt(getTextValue("group-id", map));
			String subject = getTextValue("subject", map);
			java.sql.Date date = java.sql.Date.valueOf(getTextValue("date", map));
			String location = getTextValue("location", map);
			String description = getTextValue("description", map);
			
			// Get images			
			String fileType = getFileType("selected-file", map);
			if (!fileType.equals("jpg") && !fileType.equals("gif")) {
	    		throw new Exception("Invalid file.  Only image files (.jpg and .gif) are accepted.");
	    	}
	    	InputStream inStream = getFileValue("selected-file", map);
			BufferedImage photo = ImageIO.read(inStream);
		    BufferedImage thumbnail = shrink(photo, 10);
			inStream.close();

			//  connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			Statement stmt = conn.createStatement();

			// Generate a unique pic_id using an SQL sequence
		    ResultSet rset1 = stmt.executeQuery("SELECT pic_id_seq.nextval from dual");
		    rset1.next();
		    int pic_id = rset1.getInt(1);

		    // TODO add real username

			// Insert row into table with an empty blob.
		    PreparedStatement stm = conn.prepareStatement("INSERT INTO images (PHOTO_ID, OWNER_NAME, PERMITTED, SUBJECT, PLACE, TIMING, DESCRIPTION, THUMBNAIL, PHOTO) "
		    	+ "VALUES(?, ?, ?, ?, ?, ?, ?, empty_blob(), empty_blob())");
		    stm.setInt(1, pic_id);
	    	stm.setString(2, "test");
	    	stm.setInt(3, groupId);
	    	stm.setString(4, subject);
	    	stm.setString(5, location);
	    	stm.setDate(6, date);
	    	stm.setString(7, description);
	    	stm.executeUpdate();

		    // Retrieve the lob_locator 
		    // Note that you must use "FOR UPDATE" in the select statement
		    String cmd = "SELECT * FROM images WHERE photo_id = "+pic_id+" FOR UPDATE";
		    ResultSet rset = stmt.executeQuery(cmd);
		    rset.next();
		    BLOB photoBlob = ((OracleResultSet)rset).getBLOB("PHOTO");
		    BLOB thumbBlob = ((OracleResultSet)rset).getBLOB("THUMBNAIL");
			
			OutputStream outStream;

			// Write photo
			outStream = photoBlob.setBinaryStream(1);
		    ImageIO.write(photo, fileType, outStream);
		    outStream.close();

		    // Write thumbnail
		    outStream = thumbBlob.setBinaryStream(1);
		    ImageIO.write(thumbnail, fileType, outStream);
		    outStream.close();

            stmt.executeUpdate("commit");
            conn.close();

		} catch( Exception ex ) {
		    return result + "Exception occurred: " + ex;
		}

		return result + "Upload successful.";
	}

    /*
     * shrink image by a factor of n, and return the shrinked image
     */
    public static BufferedImage shrink(BufferedImage image, int n) {

        int w = image.getWidth() / n;
        int h = image.getHeight() / n;

        BufferedImage shrunkImage =
            new BufferedImage(w, h, image.getType());

        for (int y=0; y < h; ++y)
            for (int x=0; x < w; ++x)
                shrunkImage.setRGB(x, y, image.getRGB(x*n, y*n));

        return shrunkImage;
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
     * Gets the filetype for a file from a form represented by key.
     */
	private static String getFileType(String key, Map<String,List<FileItem>> map) throws Exception{
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
	    String name = item.getName();
    	if (name.length() == 0) {
    		throw new Exception("Invalid file name for key: "+key);
    	}
    	return name.substring(name.lastIndexOf(".")+1).toLowerCase();
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

}



