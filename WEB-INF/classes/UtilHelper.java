
import java.io.*;
import java.util.*;

public class UtilHelper {

    public static String readFileToString(String fileName) throws Exception {
        File inFile = new File(fileName);
        String out = "";
        try (Scanner scanner = new Scanner(new FileInputStream(inFile))){
            out = scanner.useDelimiter("\\Z").next();
        }
        catch (Exception e){
            throw e;
        }
        
        return out;
    }
}