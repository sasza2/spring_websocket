package io.actinium.web.socket.handler.action;

import io.actinium.messaging.model.Session;
import io.actinium.web.socket.arg.WsArg;
import java.util.Map;

/**
 *
 * @author sasza
 */
public class Params <T extends WsArg> {

    private T argument;
    private Session session;
    private Map <String, String> location;
    private Object payload;

    public T getArgument() {
        return argument;
    }

    public void setArgument(T argument) {
        this.argument = argument;
    }   

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Map <String, String> getLocation() {
        return location;
    }

    public void setLocation(Map<String, String> arguments) {
        this.location = arguments;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
            
}
