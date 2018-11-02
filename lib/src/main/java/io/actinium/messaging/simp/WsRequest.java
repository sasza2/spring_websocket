package io.actinium.messaging.simp;

/**
 *
 * @author sasza
 */
public class WsRequest <T> {
    
    private String to;
    private T body;
    
    public WsRequest(){
        
    }
    
    public WsRequest(String to, T body){
        this.to = to;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }        

}
