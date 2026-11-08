package org.mnemosynehermesteam.diskstorage;

import java.util.UUID;

public record CommonObject(
    String  key,
    UUID id,
    UUID userId,
    long ts,
    String typeOfCacheObject,
    Object cacheObject
) {}
