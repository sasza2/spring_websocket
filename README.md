# Spring WebSocket library

Simple Spring WebSocket library with annotations:

@WsAfterClose - invoked after connections is closed,
@WsAfterConnect - invoked after connection is established, 
@WsAttribute - to extract session parameters, 
@WsBefore - method to invoke before, 
@WsFrom - sent from path, 
@WsTo - send to specified path

## Tests
All tests and examples are in lib/src/test/

```
cd lib; 
mvn "-Dexec.args=-classpath %classpath io.actinium.App" -Dexec.executable=PATH_TO_JAVA_EXECUTABLE_BIN -Dexec.classpathScope=test org.codehaus.mojo:exec-maven-plugin:1.2.1:exec
mvn test
```

## Example
Example of storing numbers in specified user session.

SessionController class:
```
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
```

index.html:
```
<script>
var socket = new WebSocket('ws://localhost:8080/ws/def');

var handlers = {
    'sum': function(number){
        console.log(number);
    },
    '/hello': function(){
        for(var i = 0; i < 20; i++){
            socket.send(JSON.stringify({to: 'session-number', body: i}));
        }
        setTimeout(function(){
            socket.send('{\"to\":\"session-numbers-sum\"}');
        }, 200);
    }
};

socket.addEventListener('message', function (event) {
    var obj     = JSON.parse(event.data);
    var handler = handlers[obj.to];
    if(handler !== undefined){
        handler(obj.body);
    }
});

</script>
```
