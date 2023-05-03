package controller;

import com.restfb.*;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.SendResponse;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hoandk
 */
public class Bot extends HttpServlet {

    private String AccessToken = "Pass";
    private String verifyToken = "1234";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hubToken = request.getParameter("hub.verify_token");
        String hubChallenge = request.getParameter("hub.challenge");
       
        if (verifyToken.equals(hubToken)) {
            response.getWriter().write(hubChallenge);
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            response.getWriter().write("Verificacion de contraseña incorrecta");
        }
    }

    @Override

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = request.getReader();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        JsonMapper mapper = new DefaultJsonMapper();
        
        WebhookObject webhookObj = mapper.toJavaObject(sb.toString(), WebhookObject.class);
        for (WebhookEntry entry : webhookObj.getEntryList()) {
            if (entry.getMessaging() != null) {
                for (MessagingItem mItem : entry.getMessaging()) {
                    
                    
                    if (mItem.getMessage() != null && mItem.getMessage().getText() != null) {
                        String senderId = mItem.getSender().getId();
                        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
                        System.out.println("Enviando mensaje a usario: " + mItem.getMessage().getText() + ", ID de remitente: " + senderId);
                        sendMessage(recipient, new Message(MessageProcess(mItem.getMessage().getText())));
                        return;
                    }

                }
            }
        }
        sb.delete(0, sb.length());
    }

    public static String convert(String str) {
        str = str.replaceAll("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ", "a");
        str = str.replaceAll("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ", "e");
        str = str.replaceAll("ì|í|ị|ỉ|ĩ", "i");
        str = str.replaceAll("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ", "o");
        str = str.replaceAll("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ", "u");
        str = str.replaceAll("ỳ|ý|ỵ|ỷ|ỹ", "y");
        str = str.replaceAll("đ", "d");
        return str;
    }

    static String MessageProcess(String txt) {
        String mess = "";
        if (txt.contains("Lectura de ")) {
            mess = "Bienvenido";
        }
        return mess;
    }

    void sendMessage(IdMessageRecipient recipient, Message message) {
        FacebookClient pageClient = new DefaultFacebookClient(AccessToken, Version.VERSION_11_0);
        SendResponse resp = pageClient.publish("Hola/¿Como te puedo ayudar?", SendResponse.class,
                Parameter.with("Informacion solicitada", recipient), 
                Parameter.with("Se esta analizando tu solicitud", message));

    }

    @Override
    public String getServletInfo() {
        return "A continuacion estan las siguientes opciones";
    }

}