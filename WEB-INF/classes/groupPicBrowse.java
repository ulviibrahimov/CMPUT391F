import services.UtilHelper;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import oracle.jdbc.driver.*;
import java.text.*;
import java.net.*;

public class groupPicBrowse extends PictureBrowse{
	public void setType(){
		type=2;
	}
    public String setQuery(){
	String query = "select i.photo_id from images i, group_lists g where g.friend_id = '" + name +"' and g.group_id = i.permitted";
	return query;
    }
}
