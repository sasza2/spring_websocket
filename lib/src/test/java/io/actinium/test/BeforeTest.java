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
public class BeforeTest extends TestActinium {

    @Test
    public void testBefore() throws Exception {
        TestWebSocketClient client = connect();
        for(int i = 0; i < 20; i++){
            client.send("{\"to\":\"parity/check\",\"body\":" + i + "}");
            if(i % 2 == 0){
                client.waitForMessage();
            }
        }
        
        long sum = 0;
        Type type = new TypeToken<WsRequest<Integer>>(){}.getType();        
        for(int i = 0; i < 10; i++){
            WsRequest <Integer> request = new Gson().fromJson(client.getMessages().get(i), type);
            sum += request.getBody();
        }
        
        assertEquals(90, sum);
    }
    
}
