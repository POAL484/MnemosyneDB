package org.mnemosynehermesteam.server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class ServerTest {
    HttpResponse<String> makeRequest(String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5445"))
                .header("user", UUID.randomUUID().toString())
                .method(method, BodyPublishers.ofString(reqBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test public void serverIsUpTest() throws InterruptedException {
        NettyServer server = new NettyServer();
        Thread serverThread = new Thread(server);
        serverThread.start();
        assertDoesNotThrow(() -> makeRequest("GET", ""));
        server.close();
    }

    @Test public void serverSetGetDelTest() throws JsonSyntaxException, IOException, InterruptedException {
        Gson gson = new Gson();
        NettyServer server = new NettyServer();
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Создаем новый объект
        JsonObject resp = gson.fromJson(makeRequest("POST", "{'key': 'testkey', 'val': 67}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 200);
        assertEquals(resp.getAsJsonObject("data").get("cacheObject").getAsLong(), 67);
        var uuid = resp.getAsJsonObject("data").get("id").getAsString();

        // Получаем его
        resp = gson.fromJson(makeRequest("GET", "{'key': 'testkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 200);
        assertEquals(resp.getAsJsonObject("data").get("id").getAsString(), uuid);
        assertEquals(resp.getAsJsonObject("data").get("cacheObject").getAsLong(), 67);

        // Удаляем его
        resp = gson.fromJson(makeRequest("DELETE", "{'key': 'testkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 200);
        assertEquals(resp.getAsJsonObject("data").get("id").getAsString(), uuid);
        assertEquals(resp.getAsJsonObject("data").get("cacheObject").getAsLong(), 67);

        // Проверяем удаление (не работает пока)
        // resp = gson.fromJson(makeRequest("GET", "{'key': 'testkey'}").body(), JsonObject.class);
        // assertEquals(resp.get("status").getAsLong(), 404);
        server.close();
    }

    HttpResponse<String> makeRequestInvalidHeader(String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5445"))
                .header("user", "not valid uuid")
                .method(method, BodyPublishers.ofString(reqBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    
    HttpResponse<String> makeRequestNoHeader(String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5445"))
                .method(method, BodyPublishers.ofString(reqBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test public void serverExceptionsTest() throws JsonSyntaxException, IOException, InterruptedException {
        Gson gson = new Gson();
        NettyServer server = new NettyServer();
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Для того чтобы не выдовало 404
        JsonObject resp = gson.fromJson(makeRequest("POST", "{'key': 'testkey', 'val': 67}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 200);

        // Header user есть, но неправильный
        resp = gson.fromJson(makeRequestInvalidHeader("GET", "{'key': 'testkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 401);
        resp = gson.fromJson(makeRequestInvalidHeader("POST", "{'key': 'testkey', 'val': 76}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 401);
        resp = gson.fromJson(makeRequestInvalidHeader("DELETE", "{'key': 'testkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 401);
        
        // Header user нет
        resp = gson.fromJson(makeRequestNoHeader("GET", "{'key': 'testkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 401);
        resp = gson.fromJson(makeRequestNoHeader("POST", "{'key': 'testkey', 'val': 76}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 401);
        resp = gson.fromJson(makeRequestNoHeader("DELETE", "{'key': 'testkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 401);

        // Убеждаемся что ключа "nonexistkey" не будет
        makeRequest("DELETE", "{'key': 'nonexistkey'}");
        
        // Проверка запроса несуществующего ключа
        resp = gson.fromJson(makeRequest("GET", "{'key': 'nonexistkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 404);
        resp = gson.fromJson(makeRequest("DELETE", "{'key': 'nonexistkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 404);

        // Не json в request body
        resp = gson.fromJson(makeRequest("GET", "not json").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("POST", "not json").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("DELETE", "not json").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);

        // Нулевые поля в GET и DELETE
        resp = gson.fromJson(makeRequest("GET", "{}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("GET", "{'key': null}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("DELETE", "{}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("DELETE", "{'key': null}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);

        // Нулевые поля в POST
        resp = gson.fromJson(makeRequest("POST", "{}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("POST", "{'key': 'testkey'}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("POST", "{'val': 67}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("POST", "{'key': null, 'val': 67}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("POST", "{'key': 'testkey', 'val': null}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);
        resp = gson.fromJson(makeRequest("POST", "{'key': null, 'val': null}").body(), JsonObject.class);
        assertEquals(resp.get("status").getAsLong(), 400);

        server.close();
    }
}
