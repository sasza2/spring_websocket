package io.actinium.web.socket.arg;

import java.lang.reflect.Parameter;

/**
 *
 * @author sasza
 */
abstract public class WsArg {

    public Parameter parameter;

    abstract public void init(Parameter parameter);
    
}
