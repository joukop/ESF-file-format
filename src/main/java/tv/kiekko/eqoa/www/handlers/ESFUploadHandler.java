package tv.kiekko.eqoa.www.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import tv.kiekko.eqoa.file.*;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class ESFUploadHandler implements HttpHandler {

    private class Resource {
        public final byte[] content;

        public Resource(byte[] content) {
            this.content = content;
        }
    }

    private byte[] readResource(final InputStream in, final boolean gzip) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStream gout = gzip ? new GZIPOutputStream(bout) : new DataOutputStream(bout);
        byte[] bs = new byte[4096];
        int r;
        while ((r = in.read(bs)) >= 0) {
            gout.write(bs, 0, r);
        }
        gout.flush();
        gout.close();
        in.close();
        return bout.toByteArray();
    }

    private void writeOutput(HttpExchange httpExchange, int contentLength, byte[] content, String contentType)
            throws IOException {

        httpExchange.getResponseHeaders().set("Content-Type", contentType);
        httpExchange.getResponseHeaders().set("content-disposition", "attachment");
        httpExchange.sendResponseHeaders(200, contentLength);
        httpExchange.getResponseBody().write(content);
        httpExchange.getResponseBody().close();
}

    private void serveFile(HttpExchange httpExchange, String resourcePath) throws IOException {
        File file = new File(resourcePath);
        if (file.exists()) {
            InputStream in = new FileInputStream(resourcePath);
            Resource res = new Resource(readResource(in, false));
            writeOutput(httpExchange, res.content.length, res.content, "text/plain");
        }
    }

    static void exportZone(ObjFile file) throws Exception {
        ObjExport e = new ObjExport();
        ObjInfo root = file.getRoot();
        List<ObjInfo> zones = root.getChild(ObjType.World).getChildren(ObjType.Zone);
        Zone z = (Zone) zones.get(78).getObj();
        e.addAll(z.getSpritePlacements(), file);
        e.center();
        e.write("test.obj");
    }

     @Override
     public void handle(HttpExchange t) throws IOException {
         DiskFileItemFactory d = new DiskFileItemFactory();

         try {
             ServletFileUpload up = new ServletFileUpload(d);
             List<FileItem> result = up.parseRequest(new RequestContext() {

                 @Override
                 public String getCharacterEncoding() {
                     return "UTF-8";
                 }

                 @Override
                 public int getContentLength() {
                     return 0; //tested to work with 0 as return
                 }

                 @Override
                 public String getContentType() {
                     return t.getRequestHeaders().getFirst("Content-type");
                 }

                 @Override
                 public InputStream getInputStream() throws IOException {
                     return t.getRequestBody();
                 }

             });

             String esfPath = "upload";
             File uploadedFile = new File(esfPath);
             if (uploadedFile.exists()) {
                 uploadedFile.delete();
             }

             for (FileItem fi : result) {
                 fi.write(uploadedFile);
             }
             ObjFile objFile = new ObjFile(esfPath);
             ESFUploadHandler.exportZone(objFile);
             serveFile(t, "test.obj");

         } catch (Exception e) {
             e.printStackTrace();
         }
     }
}