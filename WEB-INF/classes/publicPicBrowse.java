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
 * extend PictureBrowse.java
 * use servlet to query and display a list of pictures
 *
 * @author  Xiaolu Wang
 *
**/

public class publicPicBrowse extends PictureBrowse{
	public void setType(){
		type=3;
	}
    public String setQuery(){
	String query = "select photo_id from images where permitted=1";
	return query;
    }

}
