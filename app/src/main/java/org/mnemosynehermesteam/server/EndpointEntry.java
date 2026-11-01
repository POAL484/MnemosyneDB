package org.mnemosynehermesteam.server;

import io.netty.handler.codec.http.HttpMethod;

public record EndpointEntry(String uri, HttpMethod method) {
}
