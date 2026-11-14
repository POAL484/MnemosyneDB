package org.mnemosynehermesteam.server.mhrloperations;

import java.util.List;

public record MHRLRequestBody(
    String op,
    String key,
    Object value,
    List<Object> item,
    List<String> mapKeys
) {}
