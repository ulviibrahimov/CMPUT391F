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
	String query = "select photo_id from images where permitted=3";
	return query;
    }
}
