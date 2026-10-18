package org.mnemosynehermesteam.core;

import java.util.UUID;

public record CacheDTO(UUID id, UUID userId, long ts, Object cacheObject) {}
