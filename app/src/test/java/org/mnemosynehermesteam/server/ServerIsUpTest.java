package org.mnemosynehermesteam.server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

public class ServerIsUpTest {
  void testConnection() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:5445"))
        .GET()
        .build();
    client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  @Test
  public void serverIsUpTest() throws InterruptedException {
    NettyServer server = new NettyServer();
    Thread serverThread = new Thread(server);
    serverThread.start();
    assertDoesNotThrow(() -> testConnection());
    server.close();
  }
}
