package io.actinium.controller;

import io.actinium.TestActinium;
import io.actinium.context.annotation.WsBefore;
import io.actinium.context.annotation.WsFrom;
import io.actinium.context.annotation.WsTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author sasza
 */

@Controller
public class BeforeController extends TestActinium {
    
    @WsBefore("parity")
    public void before(@RequestBody int number){
        if(number % 2 > 0){
            throw new IllegalArgumentException();
        }
    }

    @WsFrom(value = "parity/check", before = "parity")
    @WsTo("parity")
    public int parity(@RequestBody int number){
        return number;
    }    
    
}
