package io.actinium.web.socket.handler;

import io.actinium.web.socket.arg.WsArgSession;
import io.actinium.web.socket.handler.action.Params;

/**
 *
 * @author sasza
 */
public class SessionHandler extends Handler<WsArgSession> {

    @Override
    protected Object action(Params <WsArgSession> params) {
        return params.getSession();
    }

    @Override
    protected Class type() {
        return WsArgSession.class;
    }

}
