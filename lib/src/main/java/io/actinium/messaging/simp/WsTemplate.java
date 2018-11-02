package io.actinium.messaging.simp;

import com.google.gson.Gson;
import io.actinium.messaging.model.Session;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

/**
 *
 * @author sasza
 */

@Service
public class WsTemplate {
    
    public void send(Collection <Session> sessions, String location, Object obj){
        String json = toJson(location, obj);
        sessions.forEach(session -> {
            sendAfterConverting(session, json);
        });
    }    
    
    public void send(List <Session> sessions, String location, Object obj){
        String json = toJson(location, obj);
        sessions.forEach(session -> {
            sendAfterConverting(session, json);
        });
    }
    
    public void send(String location, Object obj, Session ... sessions){
        send(sessions, location, obj);
    }
    
    public void send(Session [] sessions, String location, Object obj){
        String json = toJson(location, obj);
        for(Session session : sessions) {
            sendAfterConverting(session, json);
        }
    }
    
    public void send(Session session, String location, Object obj){
        sendAfterConverting(session, toJson(location, obj));
    }        
    
    public void hello(Session session){
        send(session, "/hello", new HashMap());
    }
    
    private void sendAfterConverting(Session session, String json){
        try {
            session.getSocket().sendMessage(new TextMessage(json));
        } catch (IOException ex) {
            Logger.getLogger(WsTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }        
    
    private String toJson(String location, Object obj){
        return new Gson().toJson(new WsRequest(location, obj));
    }
    
}
