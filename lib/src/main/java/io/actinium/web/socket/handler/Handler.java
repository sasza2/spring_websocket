package io.actinium.web.socket.handler;

import io.actinium.web.socket.arg.WsArg;
import io.actinium.web.socket.handler.action.Params;

/**
 *
 * @author sasza
 */
abstract public class Handler <T extends WsArg> {

    private Handler next;

    public Object handle(Params <T> params) {
        if (type().equals(params.getArgument().getClass())) {
            return action(params);
        } else if (next != null) {
            return next.handle(params);
        } else {
            return null;
        }
    }

    public Handler next(Handler next) {
        this.next = next;
        return this;
    }

    abstract protected Object action(Params <T> params);

    abstract protected Class type();

}
