import services.UtilHelper;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import oracle.jdbc.driver.*;
import java.text.*;
import java.net.*;

public class myPicBrowse extends PictureBrowse{

    public String setQuery(){
	String query = "select photo_id from images where owner_name='"+name+"'";
	return query;
    }
	public void setType(){
		type=1;
	}
}
