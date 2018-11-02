package io.actinium.controller;

import io.actinium.context.annotation.WsAfterConnect;
import io.actinium.context.annotation.WsAttribute;
import io.actinium.context.annotation.WsFrom;
import io.actinium.context.annotation.WsTo;
import io.actinium.messaging.model.Session;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author sasza
 */

@Controller
public class SessionController {
    
    @WsAfterConnect
    public void init(Session session){
        session.getAttributes().put("numbers", new ConcurrentLinkedQueue());
    }
    
    @WsFrom("session-number")
    public void addNumber(@WsAttribute("numbers") Collection <Integer> numbers, @RequestBody int number){
        numbers.add(number);
    }
    
    @WsFrom("session-number2")
    public void addNumber(Session session, @RequestBody int number){
        Collection <Integer> numbers = (Collection<Integer>)session.getAttributes().get("numbers");
        numbers.add(number);
    }
    
    @WsFrom("session-numbers-sum")
    @WsTo("sum")
    public int sum(@WsAttribute("numbers") Collection <Integer> numbers){
        return numbers.stream()
                      .mapToInt(i -> {
                          return (int)i;
                      })
                      .sum();
    }

}
