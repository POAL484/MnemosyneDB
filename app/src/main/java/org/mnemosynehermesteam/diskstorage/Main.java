package org.mnemosynehermesteam.diskstorage;

import org.mnemosynehermesteam.core.CacheDTO;

import java.util.*;

public class Main {
  public static void main(String[] args) {
    DiskStorage diskStorage = new DiskStorage();

    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    long ts = 1000;
    CacheDTO dto1 = new CacheDTO(id, userId, ts, new HashMap<>(Map.of("gugugaga", 2, "3", 4)));
    CacheDTO dto2 = new CacheDTO(id, userId, ts, new ArrayList<>(List.of("gugugaga", "3")));
    CacheDTO dto3 = new CacheDTO(id, userId, ts, new HashSet<>(Set.of(1, 2, 3, 4)));


    diskStorage.save("a1", dto1);
    diskStorage.save("b1", dto2);
    diskStorage.save("c1", dto3);

    Map<String, CacheDTO> mapexample = diskStorage.loadCacheDTO();

    for (Map.Entry<String, CacheDTO> entry : mapexample.entrySet()) {
      System.out.println(entry.getKey() + " " + entry.getValue());
    }
  }
}
