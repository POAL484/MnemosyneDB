package org.mnemosynehermesteam.server;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ToNumberPolicy;

import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class DataManipulationUtils {
    
    static final private Gson defaultGson = new GsonBuilder()
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create(); 

    public static <T> T parseGson(String data, Class<T> classOfT) {
        return parseGson(data, classOfT, defaultGson);
    }

    public static <T> T parseGson(String data, Class<T> classOfT, Gson gson) {
        try {
            return gson.fromJson(data, classOfT);
        } catch ( JsonSyntaxException e ) {
            log.debug("Json Syntax Exception while parsing: {}", data);
            return null;
        }
    }

    public static UUID authorizate(HttpHeaders headers) {
        if ( !headers.contains("user") ) {
            log.debug("No 'user' header in request");
            return null;
        }

        try {
            return UUID.fromString(headers.get("user"));
        } catch ( IllegalArgumentException e ) {
            log.debug("Provided 'user' header is not valid UUID: {}", headers.get("user"));
            return null;
        }
    }
}
