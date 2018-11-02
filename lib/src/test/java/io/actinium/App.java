package io.actinium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author sasza
 */

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class App {
    
    public static void main(String [] args){
        SpringApplication.run(App.class, args);        
    }
    
}