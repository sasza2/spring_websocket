package io.actinium.web.socket.arg;

import java.lang.reflect.Parameter;

/**
 *
 * @author sasza
 */
public class WsArgSession extends WsArg {

    private String name;
    private Class type;

    @Override
    public void init(Parameter parameter) {
        name = parameter.getName();
        type = parameter.getType();
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

}
