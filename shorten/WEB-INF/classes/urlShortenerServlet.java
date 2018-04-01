import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class urlShortenerServlet extends HttpServlet {
    private static String url;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String shortURL; FileWriter fw; BufferedWriter bw;
        String httpBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        int localPort=request.getLocalPort();
        if(!testBody(httpBody))
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out=response.getWriter();
            out.println(response.getStatus()+" : "+"Bad request form!");
            out.close();
        }
        else{
            if(!checkUrl(formatUrl(url))) {
                fw= new FileWriter("..\\webapps\\shorten\\urls.txt",true);
                bw= new BufferedWriter(fw);
                shortURL=shorten();
                String urlAppendText=formatUrl(url)+";"+shortURL;
                bw.write(urlAppendText);
                bw.newLine();
                bw.close();
            }
            else shortURL=findShortenedUrl(formatUrl(url));
            response.setContentType("application/json");
            PrintWriter out=response.getWriter();
            if(localPort==80)
                out.println("{\"original link\":\""+url+"\",\"short link\":\"http://localhost/redirect"+shortURL+"\"}");
            else
                out.println("{\"original link\":\""+url+"\",\"short link\":\"http://localhost:"+localPort+"/redirect"+shortURL+"\"}");
            out.close();
        }}

    private static String findShortenedUrl(String url){
        BufferedReader br=null;
        FileReader fr=null;

        try {
            fr=new FileReader("..\\webapps\\shorten\\urls.txt");
            br=new BufferedReader(fr);
            String currentLine;
            while ((currentLine=br.readLine())!=null){
                if(!currentLine.equals("")){
                    String []urls=currentLine.split(";");
                    if(urls[0].equals(url))
                        return urls[1];
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
    private static String formatUrl(String url){
        if(url.matches("^http[s]?(.*)$"))
        {
            String []foo=url.split("/");
            String bar="";
            for(int i=2;i<foo.length;i++) {
                bar += foo[i];
                if (i != foo.length - 1)
                    bar += "/";
            }
            return formatUrl(bar);
        }
        else
        if(url.matches("^www\\.(.*)$"))
        {
            String []foo=url.split("\\.");
            String bar="";

            for(int i=1;i<foo.length;i++) {
                bar += foo[i];
                if (i != foo.length - 1)
                    bar += ".";
            }
            return formatUrl(bar);
        }
        else return url;
    }
    private static boolean checkUrl(String url)throws IOException{
        BufferedReader br = null;
        FileReader fr = null;
        String sCurrentLine;
        fr = new FileReader("..\\webapps\\shorten\\urls.txt");
        br = new BufferedReader(fr);
        while ((sCurrentLine = br.readLine()) != null)
            if(!sCurrentLine.equals("")){
                String foo[]=sCurrentLine.split(";");
                if(foo[0].equals(url))
                    return true;
            }
        return false;
    }
    private static String shorten(){
        String shortenedUrl="/";
        int foo;
        for(int i=0;i<8;i++) {
            if((foo= ThreadLocalRandom.current().nextInt(48,68))>57)
                foo=ThreadLocalRandom.current().nextInt(97,123);
            shortenedUrl+=(char)foo;
        }
        return shortenedUrl;
    }
    private static boolean testBody(String body){
        if(body.matches("^\\{[ ]?\"url\"[ ]?:[ ]?\"(http://|https://)?(www.)?((?!-)[A-Za-z0-9\\-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}(/[0-9A-Za-z/.\\-$@%#]+)?\"[ ]?}$")){
            String []splitBody=body.split("\"");
            url=splitBody[3];
            return true;
        }
        else
            return false;
    }
}
