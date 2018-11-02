package io.actinium.web.socket;

import io.actinium.messaging.model.Session;
import io.actinium.messaging.simp.WsTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 *
 * @author sasza
 */

@Service
public class MessageHandler {
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private WsTemplate wsTemplate;
    
    private ActiniumFunc func;
    
    @PostConstruct
    public void init(){
        func = new ActiniumFunc().init(wsTemplate);
                
        String [] components = context.getBeanDefinitionNames();
        for (String c : components) {
            Object component = context.getBean(c);
            Class cls = component.getClass();
            Object [] annotations = cls.getAnnotationsByType(Controller.class);
            if(annotations.length == 0){
                continue;
            }
            func.add(component);
        }
    }
    
    public void invoke(String url, Session session, Object obj){
        try {
            func.invoke(url, session, obj);
        } catch (Exception ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void invokeAfterConnect(Session session){
        func.invokeAfterConnect(session);
    }
    
    public void invokeAfterClose(Session session){
        func.invokeAfterClose(session);
    }
    
}
