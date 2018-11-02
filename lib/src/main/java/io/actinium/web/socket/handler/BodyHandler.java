package io.actinium.web.socket.handler;

import com.google.gson.Gson;
import io.actinium.web.socket.arg.WsArgBody;
import io.actinium.web.socket.handler.action.Params;

/**
 *
 * @author sasza
 */
public class BodyHandler extends Handler <WsArgBody> {

    @Override
    protected Object action(Params <WsArgBody> params){
        String json = new Gson().toJson(params.getPayload());                
        return new Gson().fromJson(json, params.getArgument().parameter.getType());
    }

    @Override
    protected Class type() {
        return WsArgBody.class;
    }

}
