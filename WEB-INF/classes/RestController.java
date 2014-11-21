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
				result = getGroupsOwned(request);
			} else if (function.equals("groupBelongNotOwn")) {
				result = getGroupsBelong(request, false);
			} else if (function.equals("groupAdmin")) {
				result = getGroupsAdmin(request);
			} else if (function.equals("getGroup")) {
				result = getGroup(request, map);
			} else if (function.equals("singleImage")) {
				result = getSingleImage(request, map);
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
		    } else if (function.equals("editPic")) {
				result = editPic(userName, map);
			} else if (function.equals("createGroup")) {
				result = createGroup(userName, map);
			} else if (function.equals("leaveGroup")) {
				result = leaveGroup(userName, map);
			} else if (function.equals("addUserToGroup")) {
				result = addUserToGroup(userName, map);
			} else if (function.equals("kickUser")) {
				result = kickUser(userName, map);
			} else if (function.equals("disbandGroup")) {
				result = disbandGroup(userName, map);
			} else if (function.equals("transferGroupOwnership")) {
				result = transferGroupOwnership(userName, map);
			} else if (function.equals("changeGroupName")) {
				result = changeGroupName(userName, map);
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

	/*
	 * Attempts to edit a picture's description
	 */
	private static String editPic(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "Save failed.  User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			int picId = Integer.parseInt(getTextValue("photo-id", map));
			int groupId = Integer.parseInt(getTextValue("group-id", map));
			String subject = getTextValue("subject", map);
			String dateTemp = getTextValue("date",map);
			java.sql.Date date = null;
			if (!dateTemp.equals("")) {
				date = java.sql.Date.valueOf(dateTemp);
			}
			String location = getTextValue("location", map);
			String description = getTextValue("description", map);

			// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			PreparedStatement stm;
			ResultSet rset;

			// Get picture owner.
			stm = conn.prepareStatement("SELECT owner_name "
				+ "FROM images "
				+ "WHERE photo_id = ?");
		    stm.setInt(1, picId);
		    rset = stm.executeQuery();

		    // Confirm user has permission to edit pic
			String owner = "";
		    while (rset.next() == true) {
		    	owner = (String) rset.getString("owner_name");
		    }
		    if (!userName.equals(owner)) {
				return "Save failed.  User not picture owner.";
	    	}

			// Update image.
		    stm = conn.prepareStatement("UPDATE images "
		    	+ "SET PERMITTED=?, SUBJECT=?, PLACE=?, TIMING=?, DESCRIPTION=? "
		    	+ "WHERE photo_id = ?");
		    stm.setInt(1, groupId);
	    	stm.setString(2, subject);
	    	stm.setString(3, location);
	    	stm.setDate(4, date);
	    	stm.setString(5, description);
	    	stm.setInt(6, picId);
	    	stm.executeUpdate();

            conn.close();

		} catch( Exception ex ) {
		    return result + "Save failed.  Exception occurred: " + ex;
		}

		return result + "Save successful.";
	}

    private static String createGroup(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "Group Creation Failed. User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			String groupName = getTextValue("group-name", map);
			String notice = getTextValue("notice", map);
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
	    	stm.setString(4, notice);
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
	 * Attempts to remove a user from a group.
	 */
	private static String leaveGroup(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			int groupId = Integer.parseInt(getTextValue("groupId", map));

			if (groupId <= 2) {
				return "User does not have permission to modify the public/private group.";
			}

			removeUserFromGroup(userName, groupId, null);

            result = groupId + "";

		} catch (Exception ex) {
		    return result + "Exception occurred: " + ex;
		}

		return result;
	}

	/*
	 * Attempts to remove a user from a group.
	 */
	private static String kickUser(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			String member = getTextValue("user", map);
			int groupId = Integer.parseInt(getTextValue("group", map));

			if (groupId <= 2) {
				return "User does not have permission to modify the public/private group.";
			}

			removeUserFromGroup(member, groupId, userName);

            result = "success";

		} catch (Exception ex) {
		    return result + "Exception occurred: " + ex;
		}

		return result;
	}

	/*
	 * Attempts to revome specified user from group_list.  Optionally checks if the group owner matches expectedOwner.  
	 * Pass null to expectedOwner if you wish to bypass this check.
	 */
	private static void removeUserFromGroup(String userName, int groupId, String expectedOwner) throws Exception{

		// Connect to the oracle database
		Connection conn = UtilHelper.getConnection();
		PreparedStatement stm;
		ResultSet rset;

		stm = conn.prepareStatement("SELECT user_name FROM groups WHERE group_id = ?");
	    stm.setInt(1, groupId);
    	rset = stm.executeQuery();

	    rset.next();
	    String owner = rset.getString("user_name");

		// Ensure user to be removed is not group leader
	    if (owner.equals(userName)) {
	    	throw new Exception("User is group owner.  Please transfer leadership first or disband group.");
	    }

	    // Ensure owner is as espected
	    if (expectedOwner != null && !expectedOwner.equals("admin") && !expectedOwner.equals(owner)) {
	    	throw new Exception("User does not have persmission to remove " + userName + ".");
	    }

	    // First remove their pictures from the group
	    pictureToPrivate(groupId, userName);

	    // Then remove the user.
    	stm = conn.prepareStatement("DELETE FROM group_lists WHERE group_id = ? AND friend_id = ?");
	    stm.setInt(1, groupId);
	    stm.setString(2, userName);
    	stm.executeUpdate();

        conn.close();
	}

	/*
	 * Attempts to delete a group.
	 */
	private static String disbandGroup(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			int groupId = Integer.parseInt(getTextValue("group", map));

			if (groupId <= 2) {
				return "User does not have permission to modify the public/private group.";
			}
			// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			PreparedStatement stm;
			ResultSet rset;

			if (!userName.equals("admin")) {
				stm = conn.prepareStatement("SELECT user_name FROM groups WHERE group_id = ?");
			    stm.setInt(1, groupId);
		    	rset = stm.executeQuery();

			    rset.next();
			    String owner = rset.getString("user_name");

			    // Enusre that it is the owner attempting to disband.
			    if (!owner.equals(userName)) {
			    	return "User is not group owner.";
			    }
			}

			// First change permissions on pictures associated with the group.
			pictureToPrivate(groupId, null);

	    	stm = conn.prepareStatement("DELETE FROM group_lists WHERE group_id = ?");
		    stm.setInt(1, groupId);
		    stm.executeUpdate();

	    	stm = conn.prepareStatement("DELETE FROM groups WHERE group_id = ?");
			stm.setInt(1, groupId);
			stm.executeUpdate();

            conn.close();

            result = "success";

		} catch (Exception ex) {
		    return result + "Exception occurred: " + ex;
		}

		return result;
	}

	/*
	 * Attempts to change matched pictures' permission id to private.
	 * Pass null to userName if you wish to change all pictures in a group.
	 */
	private static void pictureToPrivate(int groupId, String userName) throws Exception {

		// Connect to the oracle database
		Connection conn = UtilHelper.getConnection();
		PreparedStatement stm;
		ResultSet rset;

		String baseStatement = "UPDATE images SET permitted = ? WHERE permitted = ?";
		if (userName != null) {
			baseStatement += " AND owner_name = ?";
		}
		stm = conn.prepareStatement(baseStatement);
	    stm.setInt(1, 2);
	    stm.setInt(2, groupId);
	    if (userName != null) {
			stm.setString(3, userName);
		}
    	stm.executeUpdate();

        conn.close();
	}

	/*
	 * Attempts to transfer ownership of a group.
	 */
	private static String transferGroupOwnership(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			String newOwner = getTextValue("user", map);
			int groupId = Integer.parseInt(getTextValue("group", map));

			if (groupId <= 2) {
				return "User does not have permission to modify the public/private group.";
			}

			// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			PreparedStatement stm;
			ResultSet rset;

			if (!userName.equals("admin")) {
				stm = conn.prepareStatement("SELECT user_name FROM groups WHERE group_id = ?");
			    stm.setInt(1, groupId);
		    	rset = stm.executeQuery();

			    rset.next();
			    String owner = rset.getString("user_name");

			    // Ensure that it is the current owner attempting to transfer.
			    if (!owner.equals(userName)) {
			    	return "User is not group owner.";
			    }
			}

	    	stm = conn.prepareStatement("UPDATE groups SET user_name = ? WHERE group_id = ?");
			stm.setString(1, newOwner);
			stm.setInt(2, groupId);
			stm.executeUpdate();

            conn.close();

            result = "success";

		} catch (Exception ex) {
		    return result + "Exception occurred: " + ex;
		}

		return result;
	}

	/*
	 * Attempts to change the name of a group.
	 */
	private static String changeGroupName(String userName, Map<String,List<FileItem>> map) {
    	if (userName == "") {
    		return "User not logged in.";
    	}

		String result = "";
    	try {
			// Get all input fields
			String newName = getTextValue("name", map);
			int groupId = Integer.parseInt(getTextValue("group", map));

			if (groupId <= 2) {
				return "User does not have permission to modify the public/private group.";
			}

			// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			PreparedStatement stm;
			ResultSet rset;

			if (!userName.equals("admin")) {
				stm = conn.prepareStatement("SELECT user_name FROM groups WHERE group_id = ?");
			    stm.setInt(1, groupId);
		    	rset = stm.executeQuery();

			    rset.next();
			    String owner = rset.getString("user_name");

			    // Ensure that it is the current owner attempting to transfer.
			    if (!owner.equals(userName)) {
			    	return "User is not group owner.";
			    }
			}

	    	stm = conn.prepareStatement("UPDATE groups SET group_name = ? WHERE group_id = ?");
			stm.setString(1, newName);
			stm.setInt(2, groupId);
			stm.executeUpdate();

            conn.close();

            result = "success";

		} catch (Exception ex) {
		    return result + "Exception occurred: " + ex;
		}

		return result;
	}

	/*
	 * Adds a user to a group.
	 */
	private static String addUserToGroup(String requester, Map<String,List<FileItem>> map) {
		String result = "";
    	
    	try {
    		int groupId = Integer.parseInt(getTextValue("group", map));
    		String user = getTextValue("user", map);
    		String notice = getTextValue("notice", map);
    		String dateTemp = getTextValue("date", map);
			java.sql.Date date = null;
			if (!dateTemp.equals("")) {
				date = java.sql.Date.valueOf(dateTemp);
			}

    		// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			PreparedStatement stm;
			ResultSet rset;

			if (!requester.equals("admin")) {
				// Verify username
				stm = conn.prepareStatement("SELECT user_name FROM groups WHERE group_id = ?");
			    stm.setInt(1, groupId);
			    rset = stm.executeQuery();
			    if (rset.next() == false) {
			    	throw new Exception("Invalid user name.");
			    }
			}
			
			// Verify user exists
			stm = conn.prepareStatement("SELECT user_name FROM users WHERE user_name = ?");
		    stm.setString(1, user);
		    rset = stm.executeQuery();
		    if (rset.next() == false) {
		    	throw new Exception("User, <b>" + user + "</b>, does not exist.");
		    }

		    // Add user
			stm = conn.prepareStatement("INSERT INTO group_lists (group_id, friend_id, date_added, notice) "
				+ "VALUES (?, ?, ?, ?) ");
		    stm.setInt(1, groupId);
		    stm.setString(2, user);
		    stm.setDate(3, date);
		    stm.setString(4, notice);
		    stm.executeUpdate();

		    conn.close();

		    result = "success";
	    } catch( Exception ex ) {
		    return result + "Exception occurred: " + ex;
		}

		return result;
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
		    	result += "<option value='" + (String) rset.getString("group_id") + "'>" + (String) rset.getString("group_name") + " (Owner: " + (String) rset.getString("user_name") + ")</option>";
		    }

		    conn.close();

	    } catch( Exception ex ) {
		    return result + "Exception occurred: " + ex;
		}

		return result;
	}
/*
     * returns a json object containing groups owned by current user.
     */
	private static String getGroupsOwned(HttpServletRequest request) {
		JSONObject result = new JSONObject();

    	String userName = getUserName(request);
    	if (userName == "") {
    		result.append("result", "fail");
    		result.append("reason", "User not logged in.");
    		return result.toString();
    	}
		
    	try {
    		Map<String, String> groups = getUserOwnedGroups(userName);
		    for (String k : groups.keySet()) {
		    	result.append(k, groups.get(k));
		    }
		    result.append("result", "success");

	    } catch( Exception ex ) {
		    result.append("result", "fail");
    		result.append("reason", "Exception Occurred: " + ex);
    		return result.toString();
		}

		return result.toString();
	}

	/*
     * returns a json object containing groups that current user belongs to but does not own.
     */
	private static String getGroupsBelong(HttpServletRequest request, boolean includeOwned) {
		JSONObject result = new JSONObject();

    	String userName = getUserName(request);
    	if (userName == "") {
    		result.append("result", "fail");
    		result.append("reason", "User not logged in.");
    		return result.toString();
    	}
		
    	try {
			Map<String, String> groups = getUserGroups(userName, includeOwned);
			for (String k : groups.keySet()) {
		    	result.append(k, groups.get(k));
		    }
		    result.append("result", "success");

	    } catch( Exception ex ) {
		    result.append("result", "fail");
    		result.append("reason", "Exception Occurred: " + ex);
    		return result.toString();
		}

		return result.toString();
	}

	/*
     * returns a map of groups that the user belongs to.
     */
	private static Map<String, String> getUserGroups(String userName, boolean includeOwned) throws Exception {
		Map<String, String> groups = new HashMap<String, String>();

		// Connect to the oracle database
		Connection conn = UtilHelper.getConnection();

	    // Get groups that user is a part of
		String baseStatement = "SELECT groups.group_id, group_name, user_name "
			+ "FROM (groups INNER JOIN group_lists ON groups.group_id = group_lists.group_id) "
			+ "WHERE friend_id = ?";
		if (!includeOwned) {
			baseStatement += " AND user_name <> ?";
		}

		PreparedStatement stm = conn.prepareStatement(baseStatement);
	    stm.setString(1, userName);
	    if (!includeOwned) {
			stm.setString(2, userName);
		}

	    // Build the result
	    ResultSet rset = stm.executeQuery();
	    while (rset.next() == true) {
	    	groups.put((String) rset.getString("group_id"), ((String) rset.getString("group_name")) + " (Owner: " + ((String) rset.getString("user_name")) + ")");
	    } 

	    conn.close();

		return groups;
	}

	/*
     * returns a map of groups that the user owns.
     */
	private static Map<String, String> getUserOwnedGroups(String userName) throws Exception {
		Map<String, String> groups = new HashMap<String, String>();

		// Connect to the oracle database
		Connection conn = UtilHelper.getConnection();

	    // Get groups that user is a part of
		PreparedStatement stm = conn.prepareStatement("SELECT group_id, group_name "
			+ "FROM groups "
			+ "WHERE user_name = ?");
	    stm.setString(1, userName);

	    // Build the result
	    ResultSet rset = stm.executeQuery();
	    while (rset.next() == true) {
	    	groups.put((String) rset.getString("group_id"), (String) rset.getString("group_name"));
	    }

	    conn.close();

		return groups;
	}

	/*
     * returns a map of groups that the user owns.
     */
	private static String getGroupsAdmin(HttpServletRequest request) {
		JSONObject result = new JSONObject();

		String userName = getUserName(request);
    	if (!userName.equals("admin")) {
    		result.append("result", "fail");
    		result.append("reason", "User not admin.");
    		return result.toString();
    	}

		try {
			// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();

		    // Get all groups
			PreparedStatement stm = conn.prepareStatement("SELECT group_id, group_name, user_name "
				+ "FROM groups "
				+ "WHERE group_id <> 1 AND group_id <> 2");

		    // Build the result
		    JSONObject groups = new JSONObject();
		    ResultSet rset = stm.executeQuery();
		    while (rset.next() == true) {
		    	groups.append((String) rset.getString("group_id"), ((String) rset.getString("group_name")) + " (Owner: " + ((String) rset.getString("user_name")) + ")");
		    }
		    result.append("groups", groups);
		    result.append("result", "success");

		    conn.close();
		} catch (Exception ex) {
			result.append("result", "fail");
    		result.append("reason", "Exception Occurred: " + ex);
    		return result.toString();
		}

		return result.toString();
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
			String owner = "";
		    while (rset.next() == true) {
		    	owner = (String) rset.getString("user_name");
		    	if (userName.equals(owner) || userName.equals("admin")) {
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
	    		JSONObject userObj = new JSONObject();
	    		String user = (String) rset.getString("friend_id");
	    		if (user.equals(owner)) {
	    			userObj.append("owner", "true");
	    		}
	    		userObj.append("user", user);
	    		userObj.append("notice", (String) rset.getString("notice"));
	    		result.append("members", userObj);
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
	private static String getSingleImage(HttpServletRequest request, Map<String, String[]> map) {
		JSONObject result = new JSONObject();

    	String userName = getUserName(request);
    	if (userName == "") {
    		result.append("result", "fail");
    		result.append("reason", "User not logged in.");
    		return result.toString();
    	}
		
    	try {
			// Get params
			int picId = Integer.parseInt(getParamValue("id", map)[0]);

    		// Connect to the oracle database
			Connection conn = UtilHelper.getConnection();
			PreparedStatement stm;
			ResultSet rset;

			// Get picture data.
			stm = conn.prepareStatement("SELECT photo_id, owner_name, permitted, subject, place, timing, description, photo "
				+ "FROM images "
				+ "WHERE photo_id = ?");
		    stm.setInt(1, picId);

			rset = stm.executeQuery();
			String owner = "";
		    while (rset.next() == true) {
		    	owner = (String) rset.getString("owner_name");
		    	// Confirm user has permission to edit pic
		    	if (userName.equals(owner)) {
		    		JSONObject pic = new JSONObject();
		    		pic.append("photo_id", (String) rset.getString("photo_id"));
		    		pic.append("permitted", (String) rset.getString("permitted"));
		    		pic.append("subject", (String) rset.getString("subject"));
		    		pic.append("place", (String) rset.getString("place"));
		    		pic.append("timing", (String) rset.getString("timing"));
		    		pic.append("description", (String) rset.getString("description"));

		    		result.append("pic", pic);
		    	} else {
					result.append("result", "fail");
		    		result.append("reason", "User not picture owner.");
		    		return result.toString();
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



