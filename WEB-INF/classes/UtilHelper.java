
import java.io.*;
import java.util.*;

public class UtilHelper {

    /**
     * Reads a file from the html directory and returns a String.
     * Outputs an html formatted error in the case of an exception.
     */
    public static String readHtmlFile(String fileName) {
        String out = "";
        try {
            out = readFile("webapps/CMPUT391F/html/"+fileName);    
        } catch (Exception e){
            out = "</p>404 - Failed to read resource: " + (new File("webapps/CMPUT391F/html/"+fileName)).getAbsolutePath() + "<br>" + e;
        }
        return out;
    }

    /**
     * Reads a file relative the Tomcat directory and returns a String.
     */
    public static String readFile(String fileName) throws Exception {
        File inFile = new File(fileName);
        String out = "";
        try (Scanner scanner = new Scanner(new FileInputStream(inFile))){
            out = scanner.useDelimiter("\\Z").next();
        } catch (Exception e){
            throw e;
        }
        return out;
    }
}