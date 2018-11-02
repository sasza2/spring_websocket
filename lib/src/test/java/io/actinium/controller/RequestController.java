package io.actinium.controller;

import io.actinium.model.User;
import io.actinium.TestActinium;
import io.actinium.context.annotation.WsFrom;
import io.actinium.context.annotation.WsTo;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author sasza
 */

@Controller
public class RequestController extends TestActinium {
    
    private String name;
            
    @WsFrom("sum")
    @WsTo("sum")
    public int sum(@RequestBody List <Double> numbers){
        return numbers.stream()
                      .filter(t -> {
                          return t != null;
                      })
                      .mapToInt(t -> {
                          return (int)(double)t;
                      })
                      .sum();        
    }
    
    @WsFrom("name-full/{name}/{surname}")
    @WsTo("name")
    public String nameFull(@PathVariable("name") String name, @PathVariable("surname") String surname){
        return name + " " + surname;
    }
    
    @WsFrom("user/{name}/{surname}")
    @WsTo("name")
    public String nameFull(@PathVariable("name") String name, @PathVariable("surname") String surname, @RequestBody int age){
        return name + " " + surname + " " + age;
    }
    
    @WsFrom("name/{name}")
    @WsTo("name")
    public String name(@PathVariable("name") String name){
        return name.toUpperCase();
    }        
    
    @WsFrom("user-cls")
    @WsTo("user")
    public User userCls(@RequestBody User user){
        user.setName(user.getName().toUpperCase());
        user.setSurname(user.getSurname().toUpperCase());
        return user;
    }
    
    @WsFrom("name-set")
    public void userSet(@RequestBody String name){
        this.name = name;
    }
    
    @WsFrom("name-get")
    @WsTo("name-get")
    public String userGet(){
        return name;
    }
    
    @WsFrom("add/{x}/{y}")
    @WsTo("add")
    public int add(@PathVariable("x") int x, @PathVariable("y") int y){
        return x + y;
    }
    
}
