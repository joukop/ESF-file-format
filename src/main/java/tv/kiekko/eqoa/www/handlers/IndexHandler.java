package tv.kiekko.eqoa.www.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class IndexHandler implements HttpHandler {

         @Override
         public void handle(HttpExchange he) throws IOException {
                 // parse request
                 Map<String, Object> parameters = new HashMap<String, Object>();
                 URI requestedUri = he.getRequestURI();
                 String query = requestedUri.getRawQuery();
                 //parseQuery(query, parameters);

                 // send response
                 String response = "";
                 response += "<html>" +
                    "<body>" +
                    "<form method='POST' action='/esf' enctype='multipart/form-data'>" +
                    "<label>ESF File</label><br/>" +
                    "<input type='file' name='esf_file'></input><br/>" +
                    "<input type='submit'></input>" +
                    "</form>" +
                    "</body>" +
                    "</html>";

                he.sendResponseHeaders(200, response.length());
                 OutputStream os = he.getResponseBody();
                 os.write(response.toString().getBytes());

             os.close();
         }
}