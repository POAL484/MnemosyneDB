package org.mnemosynehermesteam.diskstorage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mnemosynehermesteam.LoggerTestsUtils;
import org.mnemosynehermesteam.TestLog;
import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.FullKey;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j 
class DiskStorageTest {

  @BeforeAll static public void beforeAllDiskStorageTest() {
    LoggerTestsUtils.startTestBlock(log, "Dist Storage Test");
  }

  @AfterAll static public void afterAllDiskStorageTest() {
    LoggerTestsUtils.testBlockEnded(log, "Disk Storage Test");
  }

  @TempDir
  Path tempDir;
  @ParameterizedTest
  @EnumSource(CompressionAlgorithm.class)
  void saveAndLoadAllTypesTest(CompressionAlgorithm algorithm) {
    TestLog testLog = new TestLog(log, "Save and load all types with %s algorithm".formatted(algorithm.toString()));
    testLog.startPart("Prepare DiskStorage");
    Path testFile = tempDir.resolve("userdata.jsonl");
    DiskStorage diskStorage = new DiskStorage(testFile, algorithm);
    UUID user1 = UUID.randomUUID();
    testLog.partPassed();
    testLog.startPart("Prepare and set expected data");
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
    testLog.partPassed();

    testLog.startPart("Load cache DTO");
    Map<FullKey, CacheDTO> result = diskStorage.loadCacheDTO();
    testLog.partPassed();

    testLog.startPart("Test loaded");
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
    testLog.partPassed();
    testLog.testPassed();
  }

  @ParameterizedTest
  @EnumSource(CompressionAlgorithm.class)
  void expiredLine(CompressionAlgorithm algorithm) throws IOException {
    TestLog testLog = new TestLog(log, "Test expired lines with %s compression algorithm".formatted(algorithm.toString()));
    testLog.startPart("Prepare DiskStorage");
    Path testFile = tempDir.resolve("userdata.jsonl");
    DiskStorage diskStorage = new DiskStorage(testFile, algorithm);
    testLog.partPassed();
    testLog.startPart("Saving data");
    UUID userId = UUID.randomUUID();
    FullKey expiredKey = new FullKey(userId, "1");
    FullKey liveKey = new FullKey(userId, "2");
    CacheDTO value = new CacheDTO(UUID.randomUUID(), userId, 1L, "pupupu");

    diskStorage.save(expiredKey, value, Duration.ofMinutes(1));
    diskStorage.save(liveKey, value);
    testLog.partPassed();

    testLog.startPart("Prepare expected");
    List<String> lines = Files.readAllLines(testFile);
    String liveLine = lines.get(1);
    lines.set(0, lines.get(0).replaceFirst("^[A-Z0-9]+:[0-9]+:", algorithm.name() + ":0:"));
    Files.write(testFile, lines);
    testLog.partPassed();

    testLog.startPart("Check lines");
    assertEquals(Map.of(liveKey, value), diskStorage.loadCacheDTO());
    assertEquals(List.of(liveLine), Files.readAllLines(testFile));
    testLog.partPassed();
    testLog.testPassed();
  }
}
