package org.mnemosynehermesteam.diskstorage;

import org.mnemosynehermesteam.core.CacheDTO;

import java.util.*;

public class Main {
  public static void main(String[] args) {
    DiskStorage diskStorage = new DiskStorage();

    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    long ts = 1000;
    CacheDTO dto = new CacheDTO(id, userId, ts, new HashMap<>(Map.of("згзгзг", 2, "3", 4)));

    diskStorage.save("a1", dto);
    diskStorage.save("b1", dto);
    diskStorage.save("c1", dto);

    Map<String, CacheDTO> mapexample = diskStorage.loadAllCacheDTO();

    for (Map.Entry<String, CacheDTO> entry : mapexample.entrySet()) {
      System.out.println(entry.getKey() + " " + entry.getValue());
    }
  }
}
