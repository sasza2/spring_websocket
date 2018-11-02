package io.actinium.messaging.clients;

import io.actinium.messaging.model.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 *
 * @author sasza
 */

@Service
public class WsClients {
    
    //<id, session>
    private Map <String, Session> sessions;
    
    @PostConstruct
    public void init(){
        sessions = new ConcurrentHashMap();
    }
    
    public void add(Session session){
        sessions.put(session.getSocket().getId(), session);
    }
    
    public Session find(String id){
        return sessions.get(id);
    }
    
    public Session remove(String id){
        return sessions.remove(id);
    }
    
}
