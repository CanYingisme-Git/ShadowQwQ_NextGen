package al.nya.shadowqwq.webhook;

import al.nya.shadowqwq.ShadowQwQ;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.mamoe.mirai.Bot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class WebHookHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ShadowQwQ.INSTANCE.logger.info(new String(readInputStream(exchange.getRequestBody())));
        //Ret 200
        exchange.getResponseHeaders().add("Content-Type:", "text/html;charset=utf-8");
        exchange.sendResponseHeaders(200, 0);
        byte[] responseContentByte = new byte[0];
        OutputStream out = exchange.getResponseBody();
        out.write(responseContentByte);
        out.flush();
        out.close();
    }
    public byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
