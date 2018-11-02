package io.actinium.web.socket.handler;

import io.actinium.web.socket.arg.WsArgPathVariable;
import io.actinium.web.socket.handler.action.Params;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author sasza
 */
public class PathHandler extends Handler<WsArgPathVariable> {

    @Override
    protected Object action(Params <WsArgPathVariable> params) {
        PathVariable pv = params.getArgument().parameter.getAnnotation(PathVariable.class);
        if (pv == null) {
            return null;
        }

        String str = params.getLocation().get(pv.value());
        if (str == null) {
            return null;
        }

        if (params.getArgument().parameter.getType().toString().equals("int")) {
            Integer val = strToInt(str);
            if (val != null) {
                return (int)val;
            } else {
                return null;
            }
        } else if (params.getArgument().parameter.getType().equals(Integer.class)) {
            return strToInt(str);
        } else if (params.getArgument().parameter.getType().toString().equals("double")) {
            Double val = strToDouble(str);
            if (val != null) {
                return (double)val;
            } else {
                return null;
            }
        } else if (params.getArgument().parameter.getType().equals(Double.class)) {
            return strToDouble(str);
        } else {
            return str;
        }
    }

    private Integer strToInt(String s) {
        Integer val;
        try {
            val = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            val = null;
        }
        return val;
    }
    
    private Double strToDouble(String s){
        Double val;
        try {
            val = Double.parseDouble(s);
        } catch(NumberFormatException e){
            val = null;
        }
        return val;
    }

    @Override
    protected Class type() {
        return WsArgPathVariable.class;
    }

}
