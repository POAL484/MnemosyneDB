package org.mnemosynehermesteam.server.endpoints;

import java.lang.reflect.Field;

import org.mnemosynehermesteam.server.Response;

import io.netty.handler.codec.http.FullHttpRequest;

public abstract class Endpoint {

    boolean checkFieldsIsnotNull(Object body) {
        for ( Field field : body.getClass().getFields() ) {
                try {
                    if ( field.get(body) == null ) {
                        return true;
                    }
                } catch (IllegalAccessException e) {}
        }
        return false;
    }
    
    public abstract Response handle(FullHttpRequest request);
}
