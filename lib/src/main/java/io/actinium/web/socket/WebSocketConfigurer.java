package io.actinium.web.socket;

import com.google.gson.Gson;
import io.actinium.messaging.clients.WsClients;
import io.actinium.messaging.model.Session;
import io.actinium.messaging.simp.WsTemplate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author sasza
 */

@Configuration
@ComponentScan(basePackages = {"io.actinium"})
@EnableWebSocket
public class WebSocketConfigurer implements org.springframework.web.socket.config.annotation.WebSocketConfigurer {
    
    @Autowired
    private WsClients wsClients;
    
    @Autowired
    private WsTemplate wsTemplate;
    
    @Autowired
    private MessageHandler handler;

    @Bean
    public WebSocketContainerFactoryBean createWebSocketContainer() {
        WebSocketContainerFactoryBean container = new WebSocketContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(0);
        return container;
    }    
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ConnectionService(), "/ws/def")
                .setAllowedOrigins("*");
    }
    
    private class ConnectionService extends TextWebSocketHandler {
    
        @Override
        public void afterConnectionEstablished(WebSocketSession wss) throws Exception {
            super.afterConnectionEstablished(wss);
            Session session = new Session(wss);
            wsClients.add(session);
            handler.invokeAfterConnect(session);
            wsTemplate.hello(session);                        
        }

        @Override
        public void handleTextMessage(WebSocketSession wss, TextMessage message) throws Exception {
            Session session = wsClients.find(wss.getId());
            if(session != null){
                Map <String, Object> json;
                try {
                    json = new Gson().fromJson(message.getPayload(), HashMap.class);
                } catch(Exception e){
                    return;
                }
                Object url = json.get("to");
                if(url != null){
                    handler.invoke(url.toString(), session, json.get("body"));
                }
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession wss, CloseStatus status) throws Exception {
            super.afterConnectionClosed(wss, status);
            handler.invokeAfterClose(wsClients.remove(wss.getId()));            
        }        
        
    }
    
}
