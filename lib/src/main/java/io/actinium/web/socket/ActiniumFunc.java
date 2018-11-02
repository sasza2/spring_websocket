package io.actinium.web.socket;

import io.actinium.context.annotation.WsAfterClose;
import io.actinium.context.annotation.WsAfterConnect;
import io.actinium.context.annotation.WsAttribute;
import io.actinium.context.annotation.WsBefore;
import io.actinium.context.annotation.WsFrom;
import io.actinium.context.annotation.WsTo;
import io.actinium.messaging.model.Session;
import io.actinium.messaging.simp.WsTemplate;
import io.actinium.web.socket.arg.*;
import io.actinium.web.socket.handler.*;
import io.actinium.web.socket.handler.action.ParamsBuilder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriTemplate;

/**
 *
 * @author sasza
 */
public class ActiniumFunc {

    private Creator creator;
    private Map<Class, List<WsMethod>> components;
    private Map<String, WsMethodSendTo> handlers;
    private Map<String, WsMethod> handlersBefore;
    private List<WsMethod> handlersConnect;
    private List<WsMethod> handlersClose;
    private Extender extender;
    private WsTemplate wsTemplate;
    private Class[] annotations = {WsAfterClose.class, WsAfterConnect.class, WsFrom.class, WsBefore.class};

    public ActiniumFunc init(WsTemplate wsTemplate) {
        creator = new Creator();

        components = new HashMap();
        for (Class annotation : annotations) {
            components.put(annotation, new ArrayList());
        }

        handlers        = new HashMap();
        handlersBefore  = new HashMap();
        handlersConnect = new ArrayList();
        handlersClose   = new ArrayList();

        extender = new Extender();
        this.wsTemplate = wsTemplate;

        return this;
    }

    public void add(Object component) {
        Class cls = component.getClass();
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            components.entrySet()
                    .stream()
                    .map(e -> {
                        return e.getKey();
                    })
                    .filter(annotation -> {
                        return method.getAnnotationsByType(annotation).length > 0;
                    })
                    .forEach(annotation -> {
                        WsMethod wsm = creator.create(cls, method, (Class)annotation);
                        if (wsm == null) {
                            return;
                        }
                        wsm.component = component;
                        if (annotation.equals(WsFrom.class)) {
                            handlers.put(method.getAnnotation(WsFrom.class).value(), (WsMethodSendTo) wsm);
                        } else if (annotation.equals(WsAfterConnect.class)) {
                            handlersConnect.add(wsm);
                        } else if (annotation.equals(WsAfterClose.class)) {
                            handlersClose.add(wsm);
                        } else if (annotation.equals(WsBefore.class)) {
                            handlersBefore.put(method.getAnnotation(WsBefore.class).value(), wsm);
                        }
                    });
        }
    }

    public Object[] invoke(String location, Session session, Object payload) throws Exception {
        for (Entry<String, WsMethodSendTo> entry : handlers.entrySet()) {
            UriTemplate template = new UriTemplate(entry.getValue().from);

            if (!template.matches(location)) {
                continue;
            }

            extender.invoke(entry.getValue(), session, template.match(location), payload);
        }
        return null;
    }

    public void invokeAfterConnect(Session session) {
        handlersConnect.forEach(method -> {
            try {
                method.parent.invoke(method.component, session);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ActiniumFunc.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void invokeAfterClose(Session session) {
        handlersClose.forEach(method -> {
            try {
                method.parent.invoke(method.component, session);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ActiniumFunc.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private class Creator {

        public WsMethod create(Class cls, Method method, Class annotation) {
            WsMethod wsm;
            WsFrom sendFrom = method.getAnnotation(WsFrom.class);
            if (sendFrom != null) {
                WsMethodSendTo wsmSendTo = new WsMethodSendTo();
                wsmSendTo.from = sendFrom.value();
                wsm = wsmSendTo;
                wsmSendTo.before = method.getAnnotation(WsFrom.class).before();
            } else {
                wsm = new WsMethod();
            }

            wsm.type = annotation;
            wsm.args = new WsArg[method.getParameterCount()];
            wsm.parent = method;

            Parameter[] parameters = method.getParameters();
            for (int p = 0; p < parameters.length; p++) {
                Parameter parameter = parameters[p];
                WsArg arg = null;
                if (parameter.getAnnotation(PathVariable.class) != null) {
                    arg = new WsArgPathVariable();
                } else if (parameter.getType().equals(Session.class)) {
                    arg = new WsArgSession();
                } else if (parameter.getAnnotation(WsAttribute.class) != null) {
                    arg = new WsArgSessionAttribute();
                } else if (parameter.getAnnotation(RequestBody.class) != null) {
                    arg = new WsArgBody();
                }

                if (arg != null) {
                    arg.init(parameter);
                    arg.parameter = parameter;
                    wsm.args[p] = arg;
                } else {
                    return null;
                }
            }

            return wsm;
        }

    }

    private class Extender {

        private Handler handler;

        public Extender() {
            handler = new SessionHandler();
            handler = new PathHandler().next(handler);
            handler = new SessionAttributeHandler().next(handler);
            handler = new BodyHandler().next(handler);
        }

        public void invoke(WsMethodSendTo method, Session session) throws Exception {
            invoke(method, session, null, null);
        }

        /*
            arguments - match location
         */
        public void invoke(WsMethodSendTo method, Session session, Map<String, String> location, Object payload) throws Exception {
            /*
                check executing method from @WsBefore,
                when it throws exception dont 
             */
            try {
                before(method, session, payload);
            } catch (Exception e) {
                return;
            }

            Object ret = method.invoke(fill(method, session, location, payload));
            if (ret != null) {
                /*
                    when invoked method return some data send it to receiper
                 */
                resend(method, session, ret);
            }
        }

        private Object[] fill(WsMethod method, Session session, Map<String, String> location, Object payload) {
            Object[] obj = new Object[method.args.length];
            for (int i = 0; i < obj.length; i++) {                
                obj[i] = handler.handle(ParamsBuilder.create()
                                                     .addArgument(method.args[i])
                                                     .addSession(session)
                                                     .addLocation(location)
                                                     .addPayload(payload)
                                                     .get());
            }
            return obj;
        }

        private void before(WsMethodSendTo method, Session session, Object payload) throws Exception {
            for(String before : method.before) {
                WsMethod wsm = handlersBefore.get(before);
                if (wsm == null) {
                    continue;
                }
                wsm.invoke(fill(wsm, session, new HashMap(), payload));
            }
        }

        private void resend(WsMethod wsm, Session session, Object ret) {
            Method method = wsm.parent;
            WsTo to = method.getAnnotation(WsTo.class);
            if (to != null) {
                wsTemplate.send(session, to.value(), ret);
            }
        }

    }

    private class WsMethod {

        public WsArg[] args;
        public Class type; //annotation
        public Method parent;
        public Object component;

        public Object invoke(Object[] objArr) throws Exception {
            return parent.invoke(component, objArr);
        }
    }

    private class WsMethodSendTo extends WsMethod {

        public String from;
        public String to;
        public String[] before;
    }

}
