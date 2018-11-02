package io.actinium.controller;

import io.actinium.TestActinium;
import io.actinium.context.annotation.WsAfterClose;
import io.actinium.context.annotation.WsFrom;
import io.actinium.messaging.model.Session;
import io.actinium.messaging.simp.WsTemplate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author sasza
 */

@Controller
public class ChatController extends TestActinium {
        
    @Autowired
    private WsTemplate wsTemplate;
    
    private Map <String, Session> users;

    @PostConstruct
    public void init(){
        users = new ConcurrentHashMap();
    }
    
    @WsFrom("chat/join/{name}")
    public void join(@PathVariable("name") String name, Session session){
        session.getAttributes().put("name", name);    
        users.put(name, session);
    }
    
    @WsFrom("chat/message/{name}")
    public void message(@PathVariable("name") String name, @RequestBody String message){
        Session to = users.get(name);
        if(to == null){
            throw new IllegalArgumentException();
        }
        
        wsTemplate.send(to, "message", message);
    }
    
    @WsAfterClose
    public void close(Session session){
        String name = (String)session.getAttributes().get("name");
        if(name != null){
            users.remove(name);
        }
    }
    
}
