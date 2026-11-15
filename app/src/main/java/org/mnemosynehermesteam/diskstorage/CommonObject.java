package org.mnemosynehermesteam.diskstorage;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.FullKey;

import java.util.UUID;

public record CommonObject(
    String  key,
    UUID id,
    UUID userId,
    long ts,
    String typeOfCacheObject,
    Object cacheObject
) {
  CommonObject(FullKey fullKey, CacheDTO cacheDTO, String typeOfCacheObject) {
      this(
          fullKey.key(),
          cacheDTO.id(),
          fullKey.userId(),
          cacheDTO.ts(),
          typeOfCacheObject,
          cacheDTO.cacheObject()
      );
  }
}
