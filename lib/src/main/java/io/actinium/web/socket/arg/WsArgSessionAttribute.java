package io.actinium.web.socket.arg;

import io.actinium.context.annotation.WsAttribute;
import java.lang.reflect.Parameter;

/**
 *
 * @author sasza
 */
public class WsArgSessionAttribute extends WsArg {

    private String name;

    @Override
    public void init(Parameter parameter) {
        WsAttribute annotation = parameter.getDeclaredAnnotation(WsAttribute.class);
        name = annotation.value();
    }

    public String getName() {
        return name;
    }

}
