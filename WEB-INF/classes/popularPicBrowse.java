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

public class popularPicBrowse extends PictureBrowse{
	public void setType(){
		type=5;
	}
    public String setQuery(){
	String permitted = "";
	if (!name.equals("admin")){
		permitted="having i.photo_id in (select distinct i.photo_id from images i, group_lists g where (g.friend_id = '"+name+"' and g.group_id = i.permitted) or i.owner_name='"+name+"' or i.permitted=1) order by rank desc";
	}
	String query = "select i.photo_id, count(v.name) as rank from images i, viewed v where i.photo_id = v.photo_id group by i.photo_id "+permitted;

	return query;
    }
}
