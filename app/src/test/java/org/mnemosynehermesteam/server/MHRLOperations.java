package org.mnemosynehermesteam.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import org.mnemosynehermesteam.server.mhrl.JsonObjectBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MHRLOperations {
    
    static final String userId = UUID.randomUUID().toString();

    static JsonObject makeRequest(JsonObject reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5445"))
                .header("user", userId)
                .method("POST", BodyPublishers.ofString(new Gson().toJson(reqBody)))
                .build();
        return new Gson().fromJson(client.send(request, HttpResponse.BodyHandlers.ofString()).body(), JsonObject.class);
    }

    static public JsonObject opGet(String key) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "GET").add("key", key).build());
    }

    static public JsonObject opSet(String key, Object value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "SET").add("key", key).add("value", value).build());
    }

    static public JsonObject opDel(String key) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "DEL").add("key", key).build());
    }

    static public JsonObject opGetItem(String key, Object[] item) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "GETITEM").add("key", key).add("item", item).build());
    }

    static public JsonObject opIsMember(String key, Object value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "ISMEMBER").add("key", key).add("value", value).build());
    }

    static public JsonObject opSize(String key) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "SIZE").add("key", key).build());
    }

    static public JsonObject opInc(String key, Object value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "INC").add("key", key).add("value", value).build());
    }

    static public JsonObject opDec(String key, Object value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "DEC").add("key", key).add("value", value).build());
    }

    static public JsonObject opLPush(String key, Object value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "LPUSH").add("key", key).add("value", value).build());
    }

    static public JsonObject opRPush(String key, Object value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "RPUSH").add("key", key).add("value", value).build());
    }

    static public JsonObject opLPop(String key) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "LPOP").add("key", key).build());
    }

    static public JsonObject opRPop(String key) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "RPOP").add("key", key).build());
    }

    static public JsonObject opSAdd(String key, Object value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "SADD").add("key", key).add("value", value).build());
    }

    static public JsonObject opSRem(String key, Object value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "SREM").add("key", key).add("value", value).build());
    }

    static public JsonObject opHSet(String key, Map<String, Object> value) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "HSET").add("key", key).add("value", value).build());
    }

    static public JsonObject opHDel(String key, String[] mapKeys) throws IOException, InterruptedException {
        return makeRequest(new JsonObjectBuilder().add("op", "HDEL").add("key", key).add("mapKeys", mapKeys).build());
    }

}
