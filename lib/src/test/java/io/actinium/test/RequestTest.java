package io.actinium.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.actinium.TestActinium;
import io.actinium.client.TestWebSocketClient;
import io.actinium.model.User;
import io.actinium.messaging.simp.WsRequest;
import java.lang.reflect.Type;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sasza
 */

public class RequestTest extends TestActinium {        
               
    @Test
    public void testSum() throws Exception {
        TestWebSocketClient client = connect();
        client.send("{\"to\":\"sum\",\"body\":[1,2,3,4,5]}");
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<Integer>>(){}.getType();
        WsRequest <Integer> request = new Gson().fromJson(client.getLastMessage(), type);        
        assertEquals(15, (long)request.getBody());
    }
    
    @Test
    public void testName() throws Exception {
        TestWebSocketClient client = connect();
        client.send("{\"to\":\"name/test\"}");      
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<String>>(){}.getType();
        WsRequest <String> request = new Gson().fromJson(client.getLastMessage(), type);        
        assertEquals("TEST", request.getBody());
    }
    
    @Test
    public void testSurname() throws Exception {
        TestWebSocketClient client = connect();
        client.send("{\"to\":\"name-full/aaa/bbb\"}");      
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<String>>(){}.getType();
        WsRequest <String> request = new Gson().fromJson(client.getLastMessage(), type);        
        assertEquals("aaa bbb", request.getBody());
    }
    
    @Test
    public void testUserParams() throws Exception {
        TestWebSocketClient client = connect();
        client.send("{\"to\":\"user/aaa/bbb\",\"body\":25}");      
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<String>>(){}.getType();
        WsRequest <String> request = new Gson().fromJson(client.getLastMessage(), type);        
        assertEquals("aaa bbb 25", request.getBody());
    }
    
    @Test
    public void testMultipleMessages() throws Exception {
        TestWebSocketClient client = connect();
        client.send("{\"to\":\"sum\",\"body\":[1,2]}");
        client.send("{\"to\":\"sum\",\"body\":[3,4]}");
        client.send("{\"to\":\"sum\",\"body\":[5,6]}");
        client.send("{\"to\":\"sum\",\"body\":[7,8]}");
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<Integer>>(){}.getType();
        long sum = client.getMessages()
                         .stream()
                         .map(json -> {
                             WsRequest <Integer> request = (WsRequest <Integer>)new Gson().fromJson(json, type);
                             return request.getBody();
                         })
                         .mapToInt(i -> {
                            return (int)i; 
                         })
                         .sum();
                                        
        assertEquals(36, sum);
    }
    
    @Test
    public void testUserCls() throws Exception {        
        TestWebSocketClient client = connect();        
        client.send(new Gson().toJson(new WsRequest("user-cls", new User("xx", "yy"))));
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<User>>(){}.getType();
        WsRequest <User> request = new Gson().fromJson(client.getLastMessage(), type);
        User user = request.getBody();
        
        assertEquals("XXYY", user.getName() + user.getSurname());
    }
    
    @Test
    public void testNameSet() throws Exception {
        TestWebSocketClient client = connect();        
        client.send(new Gson().toJson(new WsRequest("name-set", "test")));
        Thread.sleep(10);
        client.send(new Gson().toJson(new WsRequest("name-get", null)));
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<String>>(){}.getType();
        WsRequest <String> request = new Gson().fromJson(client.getLastMessage(), type);
        assertEquals("test", request.getBody());
    }
    
    @Test
    public void testAdd() throws Exception {
        TestWebSocketClient client = connect();
        client.send("{\"to\":\"add/55/45\"}");      
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<Integer>>(){}.getType();
        WsRequest <Integer> request = new Gson().fromJson(client.getLastMessage(), type);        
        assertEquals(100, (long)request.getBody());
    }
    
}
