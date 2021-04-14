package tv.kiekko.eqoa;


import com.sun.net.httpserver.HttpServer;
import tv.kiekko.eqoa.www.handlers.ESFUploadHandler;
import tv.kiekko.eqoa.www.handlers.IndexHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App
{
    public static void main( String[] args ) throws IOException {
        int port = 9000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("server started at " + port);
        server.createContext("/", new IndexHandler());
        server.createContext("/esf", new ESFUploadHandler());
        server.setExecutor(null);
        server.start();
    }
}
