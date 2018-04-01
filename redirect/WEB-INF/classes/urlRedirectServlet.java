import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class urlRedirectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUri=request.getRequestURI();
        String responseUrl=findUrl(requestUri);
        if(responseUrl==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            PrintWriter out=response.getWriter();
            out.println(response.getStatus()+" : "+"Requested (short) URL not found");
            out.close();
        }
        else{
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            responseUrl="https://www."+responseUrl;
            response.setHeader("Location",responseUrl);
            PrintWriter pw=response.getWriter();
            pw.println("");
            pw.close();
        }
    }
    private static String findUrl(String shortenedUrl){
        BufferedReader br=null; FileReader fr=null;
        String foo[]=shortenedUrl.split("/");
        shortenedUrl="/"+foo[2];
        try {
            fr=new FileReader("..\\webapps\\shorten\\urls.txt");
            br=new BufferedReader(fr);
            String currentLine;
            while ((currentLine=br.readLine())!=null){
                if(!currentLine.equals("")){
                    String []urls=currentLine.split(";");
                    if(urls[1].equals(shortenedUrl))
                    {
                        return urls[0];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(br!=null)
                    br.close();
                if(fr!=null)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
