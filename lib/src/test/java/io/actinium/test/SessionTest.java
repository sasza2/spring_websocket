package io.actinium.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.actinium.TestActinium;
import io.actinium.client.TestWebSocketClient;
import io.actinium.messaging.simp.WsRequest;
import java.lang.reflect.Type;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sasza
 */
public class SessionTest extends TestActinium {

    @Test
    public void testSum() throws Exception {
        TestWebSocketClient client = connect();
        for(int i = 0; i < 20; i++){
            if(i % 2 == 0){
                client.send("{\"to\":\"session-number\",\"body\":" + i + "}");
            } else {
                client.send("{\"to\":\"session-number2\",\"body\":" + i + "}");
            }
        }
        Thread.sleep(10);
        client.send("{\"to\":\"session-numbers-sum\"}");
        client.waitForMessage();
        
        Type type = new TypeToken<WsRequest<Integer>>(){}.getType();
        WsRequest <Integer> request = new Gson().fromJson(client.getLastMessage(), type);        
        assertEquals(190, (long)request.getBody());
    }
    
    @Test
    public void testChat() throws Exception {
        TestWebSocketClient clientA = connect();
        TestWebSocketClient clientB = connect();
        
        clientA.send("{\"to\":\"chat/join/client_aaa\"}");
        clientB.send("{\"to\":\"chat/join/client_bbb\"}");
        
        clientA.send("{\"to\":\"chat/message/client_bbb\",\"body\":\"hello aaa\"}");
        clientB.send("{\"to\":\"chat/message/client_aaa\",\"body\":\"hello bbb\"}");
        
        clientA.waitForMessage();
        clientB.waitForMessage();
        
        Type type = new TypeToken<WsRequest<String>>(){}.getType();
        WsRequest <String> requestClientA = new Gson().fromJson(clientA.getLastMessage(), type);
        WsRequest <String> requestClientB = new Gson().fromJson(clientB.getLastMessage(), type);
        
        assertEquals("hello bbb", requestClientA.getBody());
        assertEquals("hello aaa", requestClientB.getBody());
    }
    
}
