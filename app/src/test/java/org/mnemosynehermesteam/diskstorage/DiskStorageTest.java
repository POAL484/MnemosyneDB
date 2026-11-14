package org.mnemosynehermesteam.diskstorage;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.FullKey;

import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DiskStorageTest {
  @TempDir
  Path tempDir;
  @ParameterizedTest
  @EnumSource(CompressionAlgorithm.class)
  void saveAndLoadAllTypesTest(CompressionAlgorithm algorithm) {
    Path testFile = tempDir.resolve("userdata.jsonl");
    DiskStorage diskStorage = new DiskStorage(testFile, algorithm);
    UUID user1 = UUID.randomUUID();
    Map<FullKey, CacheDTO> expected = new LinkedHashMap<>();

    expected.put(
        new FullKey(user1, "long"),
        new CacheDTO(UUID.randomUUID(), user1, 1, 100L)
    );

    expected.put(
        new FullKey(user1, "integer"),
        new CacheDTO(UUID.randomUUID(), user1, 2, 67)
    );

    expected.put(
        new FullKey(user1, "string"),
        new CacheDTO(UUID.randomUUID(), user1, 100L, "gugugaga")
    );

    expected.put(
        new FullKey(user1, "set"),
        new CacheDTO(
            UUID.randomUUID(),
            user1,
            1000L,
            Set.of("one", "two", "three")
        )
    );

    expected.put(
        new FullKey(user1, "list"),
        new CacheDTO(
            UUID.randomUUID(),
            user1,
            1000L,
            List.of(1, 2, 3)
        )
    );

    expected.put(
        new FullKey(user1, "map"),
        new CacheDTO(
            UUID.randomUUID(),
            user1,
            1000L,
            Map.of(
                "one", "1",
                "two", "2"
            )
        )
    );

    for (Map.Entry<FullKey, CacheDTO> entry : expected.entrySet()) {
      diskStorage.save(entry.getKey(), entry.getValue());
    }

    Map<FullKey, CacheDTO> result = diskStorage.loadCacheDTO();

    assertEquals(expected.size(), result.size());

    for (Map.Entry<FullKey, CacheDTO> entry : expected.entrySet()) {

      FullKey fullKey = entry.getKey();
      CacheDTO expectedDTO = entry.getValue();

      assertTrue(result.containsKey(fullKey));

      CacheDTO actualDTO = result.get(fullKey);

      assertEquals(expectedDTO.id(), actualDTO.id());

      assertEquals(expectedDTO.ts(), actualDTO.ts());

      assertEquals(
          expectedDTO.cacheObject(),
          actualDTO.cacheObject()
      );
    }
  }
}