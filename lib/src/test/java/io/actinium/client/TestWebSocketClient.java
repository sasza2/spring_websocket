package io.actinium.client;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 *
 * @author sasza
 */
@ClientEndpoint
public class TestWebSocketClient {
    
    private final List <String> messages;
    private Session session;
    private int messagesCount;
    
    public TestWebSocketClient(){
        messages = new ArrayList();
    }

    @OnOpen
    public void onOpen(final Session session){
        this.session = session;
    }
    
    @OnMessage
    public void onMessage(final String message){
        Map <String, Object> obj = new Gson().fromJson(message, HashMap.class);        
        String to = (String)obj.get("to");
        if(to == null){
            throw new NullPointerException();
        }
        if(!"/hello".equals(to)){
            messages.add(message);
        }
    }
    
    public Session getSession(){
        return session;
    }
    
    public void send(String json) throws IOException {
        session.getBasicRemote().sendText(json);        
    }
    
    public void send(Object obj) throws IOException {
        send((String)new Gson().toJson(obj));
    }
    
    public void waitForMessages(int expected) throws Exception {
        int c = 0;
        while(messages.size() < expected){
            Thread.sleep(5);
            c++;
            if(c > 200){
                throw new IllegalStateException();
            }
        }
        messagesCount = messages.size();
    }
    
    public void waitForMessage() throws Exception {
        waitForMessages(messagesCount + 1);
    }
    
    public List <String> getMessages(){
        return messages;
    }
    
    public String getLastMessage(){
        if(messages.isEmpty()){
            throw new IndexOutOfBoundsException();
        }
        return messages.get(messages.size()-1);
    }
    
}