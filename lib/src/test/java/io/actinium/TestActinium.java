package io.actinium;

import io.actinium.client.TestWebSocketClient;
import java.net.URI;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author sasza
 */

@RunWith(SpringRunner.class)
abstract public class TestActinium {
        
    public TestWebSocketClient connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        TestWebSocketClient client = new TestWebSocketClient();
        Session session = container.connectToServer(client , URI.create("ws://localhost:8080/ws/def"));        
        
        int c = 0;
        while(!session.isOpen()){
            Thread.sleep(10);
            c++;
            if(c > 100){
                throw new IllegalStateException();
            }
        }
        
        return client;
    }
    
}
