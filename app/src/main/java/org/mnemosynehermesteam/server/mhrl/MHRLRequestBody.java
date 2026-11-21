package org.mnemosynehermesteam.server.mhrl;

import java.util.List;

public record MHRLRequestBody(
    String op,
    String key,
    Object value,
    List<Object> item,
    List<String> mapKeys
) {}
