import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Upload extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ResourceBundle rb =
            ResourceBundle.getBundle("LocalStrings",request.getLocale());
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String title = rb.getString("helloworld.title");

        // String html = "";
        // try {
        //     html = UtilHelper.readFileToString("../../html/UploadPage.html");
        // } catch (Exception e) {
        //     System.err.println(e);
        // }
// 
        // out.println(html);

        out.println("<!DOCTYPE html><html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\" />");

        out.println("<title>" + title + "</title>");
        out.println("</head>");
        out.println("<body bgcolor=\"white\">");

        // note that all links are created to be relative. this
        // ensures that we can move the web application that this
        // servlet belongs to to a different place in the url
        // tree and not have any harmful side effects.

        // XXX
        // making these absolute till we work out the
        // addition of a PathInfo issue

        out.println("<h1>" + "welcome" + title + "</h1>");
        out.println("</body>");
        out.println("</html>");
    }

}



