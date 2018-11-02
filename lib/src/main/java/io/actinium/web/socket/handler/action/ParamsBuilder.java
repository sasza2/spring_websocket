package io.actinium.web.socket.handler.action;

import io.actinium.messaging.model.Session;
import io.actinium.web.socket.arg.WsArg;
import java.util.Map;

/**
 *
 * @author sasza
 */
public class ParamsBuilder {
    
    private Params params;
    
    private ParamsBuilder(){
        
    }
    
    public ParamsBuilder addArgument(WsArg argument){
        params.setArgument(argument);
        return this;
    }
    
    public ParamsBuilder addSession(Session session){
        params.setSession(session);
        return this;
    }
    
    public ParamsBuilder addLocation(Map <String, String> arguments){
        params.setLocation(arguments);
        return this;
    }
    
    public ParamsBuilder addPayload(Object payload){
        params.setPayload(payload);
        return this;
    }
    
    public static ParamsBuilder create(){
        ParamsBuilder builder = new ParamsBuilder();
        builder.params = new Params();
        return builder;
    }
    
    public Params get(){
        return params;
    }

}
