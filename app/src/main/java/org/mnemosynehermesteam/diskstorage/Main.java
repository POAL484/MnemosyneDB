package org.mnemosynehermesteam.diskstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.FullKey;

import java.io.IOException;
import java.util.*;

public class Main {
  public static void main(String[] args) throws IOException {
    DiskStorage diskStorage = new DiskStorage();

    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    long ts = 1000;
    String largeText = "gugugagagugugagagugugagagugugaga".repeat(100_000);
    CacheDTO largeDto = new CacheDTO(id, userId, ts, largeText);
    ObjectMapper mapper = new ObjectMapper();
    CommonObject object = diskStorage.CommonObjWithCheckingTypes(new FullKey(id, "largetext"), largeDto);

    for (CompressionAlgorithm algorithm : CompressionAlgorithm.values()) {
      long totalBytes = 0;
      byte[] json = mapper.writeValueAsBytes(object);
      byte[] compressed = algorithm.compress(json);
      int dataBytes;
      if (algorithm == CompressionAlgorithm.NONE) {
        dataBytes = compressed.length;
      } else {
        dataBytes = Base64.getEncoder().encode(compressed).length;
      }
      totalBytes += algorithm.name().getBytes().length + 1 + dataBytes + 1;
      System.out.println(algorithm + ": " + totalBytes + " байт");
    }

    CacheDTO dto1 = new CacheDTO(id, userId, ts, new HashMap<>(Map.of("gugugaga", 2, "3", 4)));
    CacheDTO dto2 = new CacheDTO(id, userId, ts, new ArrayList<>(List.of("gugugaga", "3")));
    CacheDTO dto3 = new CacheDTO(id, userId, ts, new HashSet<>(Set.of(1, 2, 3, 4)));

    diskStorage.save(new FullKey(id, "a1"), dto1);
    diskStorage.save(new FullKey(id, "b1"), dto2);
    diskStorage.save(new FullKey(id, "c1"), dto3);

    Map<FullKey, CacheDTO> mapexample = diskStorage.loadCacheDTO();

    for (Map.Entry<FullKey, CacheDTO> entry : mapexample.entrySet()) {
      System.out.println(entry.getKey() + " " + entry.getValue());
    }
  }
}