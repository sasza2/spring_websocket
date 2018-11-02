package io.actinium.messaging.model;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author sasza
 */
public class Session {
    
    private final Attributes attributes;
    private final WebSocketSession socket;
    
    public Session(WebSocketSession socket){
        attributes = new Attributes();
        this.socket = socket;
    }
    
    public WebSocketSession getSocket(){
        return socket;
    }
    
    public Attributes getAttributes(){
        return attributes;
    }
    
    public class Attributes extends ConcurrentHashMap {
    
    }
    
}
