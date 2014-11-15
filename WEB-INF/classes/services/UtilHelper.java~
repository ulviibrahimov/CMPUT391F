package services;

import java.io.*;
import java.util.*;
import java.sql.*;

public class UtilHelper {

    /**
     * Reads a file from the html directory and returns a String.
     * Outputs an html formatted error in the case of an exception.
     */
    public static String readHtmlFile(String fileName) {
        String out = "";
        try {
            out = readFile("Sources/html/"+fileName+".html");    
        } catch (Exception e){
            out = "</p>404 - Failed to read resource: " + (new File("webapps/CMPUT391F/html/"+fileName+".html")).getAbsolutePath() + "<br>" + e;
        }
        return out;
    }

    /**
     * Reads a file relative to the CMPUT391 directory and returns a String.
     */
    public static String readFile(String fileName) throws IOException {
        File inFile = new File("catalina/webapps/CMPUT391F/"+fileName);
	//throw new IOException("EXPECTED FILE: "+inFile.getAbsolutePath());

        String out = "";
        Scanner scanner = new Scanner(new FileInputStream(inFile));
        out = scanner.useDelimiter("\\Z").next();
        return out;
    }


    /*
     * Returns the 3 lines following START found in "CMPUT391F/connectionConfig.txt"
     */
    public static String[] getConfiguration() throws IOException {
        String config = UtilHelper.readFile("connectionConfig.txt");

        // Retrieve configuration specified by con
        StringTokenizer st = new StringTokenizer(config, System.getProperty("line.separator"));
        String token;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals("START")) {
                // Found our config
                break;
            }
        }

        String[] out = new String[3];

        for (int i = 0; i < out.length; i++) {
            out[i] = st.nextToken();
        }

        return out;
    }

    /*
     *   To connect to the database specified in "CMPUT391F/connectionConfig.txt"
     */
    public static Connection getConnection() throws Exception {
        String configParam[] = getConfiguration();

        String dbstring = configParam[0];
        String username = configParam[1];
        String password = configParam[2];
        String drivername = "oracle.jdbc.driver.OracleDriver";
        Class drvClass = Class.forName(drivername); 
        DriverManager.registerDriver((Driver) drvClass.newInstance());
        return (DriverManager.getConnection(dbstring,username,password));
    }
}
