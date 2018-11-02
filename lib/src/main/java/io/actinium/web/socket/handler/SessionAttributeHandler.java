package io.actinium.web.socket.handler;

import io.actinium.web.socket.arg.WsArgSessionAttribute;
import io.actinium.web.socket.handler.action.Params;

/**
 *
 * @author sasza
 */
public class SessionAttributeHandler extends Handler<WsArgSessionAttribute> {

    @Override
    protected Object action(Params <WsArgSessionAttribute> params){
        return params.getSession().getAttributes().get(params.getArgument().getName());
    }

    @Override
    protected Class type() {
        return WsArgSessionAttribute.class;
    }

}
