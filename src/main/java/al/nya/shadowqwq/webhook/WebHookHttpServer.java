package al.nya.shadowqwq.webhook;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebHookHttpServer {
    public WebHookHttpServer(int port){
        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        httpServer.createContext("/", new WebHookHandler());
        httpServer.setExecutor(null);
        httpServer.start();
    }
}
