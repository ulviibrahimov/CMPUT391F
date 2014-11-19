/*
 * Code taken from http://luscar.cs.ualberta.ca:8080/yuan/UploadImage.java
 * and http://commons.apache.org/proper/commons-fileupload/
 * 
 */
import services.UtilHelper;

import org.json.JSONObject;
import org.json.JSONArray;

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
// String result = "";
		try {
			// @SuppressWarnings("unchecked")
			Map<String, String[]> map = request.getParameterMap();

			String function = getParamValue("function", map)[0];

		    // Route the request to the appropriate function.
			String result = "";
		    if (function.equals("userName")) {
				result = getUserName(request);
			} else if (function.equals("groupOptions")) {
				result = getGroupOptions(request);
			} else if (function.equals("groupOwned")) {

			} else if (function.equals("groupBelong")) {

			} else if (function.equals("getGroup")) {
				result = getGroup(request, map);
			} else if (function.equals("singleImage")) {
				result = getSingleImage(request);
			} else {
				result = "Requested function is not mapped.";
			}
			out.write(result);

		} catch (Exception e) {
			out.write("Exception occurred!!!: " + e);
			return;
		}
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
			// Get username for passing
			String userName = getUserName(request);

			// Get map of request parts
		    Map<String,List<FileItem>> map = (new ServletFileUpload(new DiskFileItemFactory())).parseParameterMap(request);

		    String function = getTextValue("function", map);

		    // Route the request to the appropriate function.
			String result;
		    if (function.equals("uploadOne")) {
				result = "<br>Upload status:<br>" + uploadFile(userName, map);
			} else if (function.equals("createGroup")) {
				result = createGroup(userName, map);
			} else {
				result = "Requested function is not mapped.";
			}
			out.write(result);

		} catch (Exception e) {
			out.write("!Exception occurred: " + e);
			return;
		}
    }

    private static String uploadFile(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "Upload Failed. User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			int groupId = Integer.parseInt(getTextValue("group-id", map));
			String subject = getTextValue("subject", map);
			String dateTemp = getTextValue("date",map);
			java.sql.Date date = null;
			if (!dateTemp.equals("")) {
				date = java.sql.Date.valueOf(dateTemp);
			}
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

			// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			Statement stmt = conn.createStatement();

			// Generate a unique pic_id using an SQL sequence
		    ResultSet rset1 = stmt.executeQuery("SELECT pic_id_seq.nextval from dual");
		    rset1.next();
		    int pic_id = rset1.getInt(1);

			// Insert row into table with an empty blob.
		    PreparedStatement stm = conn.prepareStatement("INSERT INTO images (PHOTO_ID, OWNER_NAME, PERMITTED, SUBJECT, PLACE, TIMING, DESCRIPTION, THUMBNAIL, PHOTO) "
		    	+ "VALUES(?, ?, ?, ?, ?, ?, ?, empty_blob(), empty_blob())");
		    stm.setInt(1, pic_id);
	    	stm.setString(2, userName);
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

    private static String createGroup(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "Group Creation Failed. User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			String groupName = getTextValue("group-name", map);
			String dateTemp = getTextValue("date", map);
			java.sql.Date date = null;
			if (!dateTemp.equals("")) {
				date = java.sql.Date.valueOf(dateTemp);
			}

			// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			Statement stmt = conn.createStatement();

			// Generate a unique pic_id using an SQL sequence
		    ResultSet rset1 = stmt.executeQuery("SELECT group_id_seq.nextval from dual");
		    rset1.next();
		    int group_id = rset1.getInt(1);

			// Insert row into table
		    PreparedStatement stm = conn.prepareStatement("INSERT INTO groups (GROUP_ID, USER_NAME, GROUP_NAME, DATE_CREATED) VALUES(?, ?, ?, ?)");
		    stm.setInt(1, group_id);
	    	stm.setString(2, userName);
	    	stm.setString(3, groupName);
	    	stm.setDate(4, date);
	    	stm.executeUpdate();

	    	stm = conn.prepareStatement("INSERT INTO group_lists (GROUP_ID, FRIEND_ID, DATE_ADDED, NOTICE) VALUES(?, ?, ?, ?)");
		    stm.setInt(1, group_id);
	    	stm.setString(2, userName);
	    	stm.setDate(3, date);
	    	stm.setString(4, "Current Owner");
	    	stm.executeUpdate();

            conn.close();

            // Success!
            result = group_id + "";

		} catch (SQLIntegrityConstraintViolationException intEx) {
			return result + "* Group name already in use.";
		} catch (Exception ex) {
		    return result + "* Group Creation Failed.  Exception occurred: " + ex;
		}

		return result;
	}

	/*
	 * Adds a user to a group.
	 */
	private static String addUserToGroup () {
		// Verify username
			// PreparedStatement stm = conn.prepareStatement("SELECT user_name FROM users WHERE user_name = ?");
		 //    stm.setString(1, userName);
		 //    ResultSet rset2 = stm.executeQuery();
		 //    if (rset2.next() == false) {
		 //    	throw new Exception("Invalid user name.");
		 //    }
		return "";
	}

	/*
     * returns an html option list contain group ids and names.
     */
	private static String getGroupOptions(HttpServletRequest request) {
		String result = "";
    	String userName = getUserName(request);
    	
    	try {
    		// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();

		    // Get groups that user is a part of
			PreparedStatement stm = conn.prepareStatement("SELECT groups.group_id, group_name, user_name "
				+ "FROM (groups INNER JOIN group_lists ON groups.group_id = group_lists.group_id) "
				+ "WHERE friend_id = ?");
		    stm.setString(1, userName);

		    // Build the result
		    ResultSet rset = stm.executeQuery();
		    while (rset.next() == true) {
		    	result += "<option value='" + (String) rset.getString("group_id") + "'>" + (String) rset.getString("group_name") + " (Owner: " + (String) rset.getString("user_name") + "</option>";
		    }

		    conn.close();

	    } catch( Exception ex ) {
		    return result + "Exception occurred: " + ex;
		}

		return result;
	}

	/*
     * returns a json object containing group data.
     */
	private static String getGroup(HttpServletRequest request, Map<String, String[]> map) {
		JSONObject result = new JSONObject();

    	String userName = getUserName(request);
    	if (userName == "") {
    		result.append("result", "fail");
    		result.append("reason", "User not logged in.");
    		return result.toString();
    	}
		
    	try {
			// Get params
			int groupId = Integer.parseInt(getParamValue("groupId", map)[0]);

    		// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			PreparedStatement stm;
			ResultSet rset;

			// Confirm user has permission to manage group
			stm = conn.prepareStatement("SELECT user_name, group_name "
				+ "FROM groups "
				+ "WHERE group_id = ?");
		    stm.setInt(1, groupId);

			rset = stm.executeQuery();
		    while (rset.next() == true) {
		    	String owner = (String) rset.getString("user_name");
		    	if (owner.equals(userName)) {
		    		result.append("groupName", (String) rset.getString("group_name"));	
		    	} else {
					result.append("result", "fail");
		    		result.append("reason", "User not group owner.");
		    		return result.toString();
		    	}
		    }


		    // Get group members
			stm = conn.prepareStatement("SELECT friend_id, notice "
				+ "FROM group_lists "
				+ "WHERE group_id = ?");
		    stm.setInt(1, groupId);

		    // Build array of members
		    rset = stm.executeQuery();
		    while (rset.next() == true) {
		    	String user = (String) rset.getString("friend_id");
		    	if (!user.equals(userName)) {
		    		JSONObject userObj = new JSONObject();
		    		userObj.append("user", user);
		    		userObj.append("notice", (String) rset.getString("notice"));
		    		result.append("members", userObj);
		    	}
		    }

		    conn.close();

		    result.append("result", "success");

	    } catch( Exception ex ) {
		    result.append("result", "fail");
    		result.append("reason", "Exception Occurred: " + ex);
    		return result.toString();
		}

		return result.toString();
	}

	/*
     * returns privledge level for the signed in user and data about the image.
     */
	private static String getSingleImage(HttpServletRequest request) {
		return "Whee";
	}

    /*
     * shrink image by a factor of n, and return the shrinked image
     */
    private static BufferedImage shrink(BufferedImage image, int n) {

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
	    	throw new Exception("No '"+key+"' key passed into post request data.");
		}

		// Check for key value
	    Iterator<FileItem> i = funcItems.iterator();
	    if (!i.hasNext()) {
	    	throw new Exception("Malformed '"+key+"' value.");
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

    /*
     * Gets the String array value for the specified key.
     */
	private static String[] getParamValue(String key, Map<String, String[]> map) throws Exception{
    	String[] value = map.get(key);

	    // Check that key does exist
	    if (value == null) {
	    	throw new Exception("No '"+key+"' key passed into request data.");
		}

		// Check for key value
	    if (value.length == 0) {
	    	throw new Exception("No value found for key, '"+key+"'.");
	    }
	    return value;
    }

    private static String getUserName(HttpServletRequest request) {
		HttpSession session = request.getSession();	
    	String name = (String) session.getAttribute("user");
		if (name == null) {
			return "";
		}
		return name;
	}
}



