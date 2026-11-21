package org.mnemosynehermesteam.diskstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.config.MnemosyneConfig;
import org.mnemosynehermesteam.core.FullKey;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
public class DiskStorage {

  private final Path filePath;
  private final ObjectMapper mapper = new ObjectMapper();
  private final CompressionAlgorithm compressionAlgorithm;

  public DiskStorage() {
    MnemosyneConfig config = new MnemosyneConfig();

    this.filePath = Path.of("userdata.jsonl");
    compressionAlgorithm = config.getCompressionAlgorithm();
  }

  DiskStorage(Path filePath, CompressionAlgorithm compressionAlgorithm) {
    this.filePath = filePath;
    this.compressionAlgorithm = compressionAlgorithm;
  }

  private String compress(String json, Long timeLimit) throws IOException {
    String header = compressionAlgorithm.name();
    if (timeLimit != null) {
      header += ":" + timeLimit;
    }
    if (compressionAlgorithm == CompressionAlgorithm.NONE) {
      return header + ":" + json;
    }

    byte[] compressed = compressionAlgorithm.compress(json.getBytes());
    return header + ":" + Base64.getEncoder().encodeToString(compressed);
  }

  private String decompress(CompressionAlgorithm algorithm, String data) throws IOException {
    if (algorithm == CompressionAlgorithm.NONE) {
      return data;
    }

    byte[] compressed = Base64.getDecoder().decode(data);
    byte[] decompressed = algorithm.decompress(compressed);

    return new String(decompressed);
  }

  CommonObject CommonObjWithCheckingTypes(
      FullKey fullKey,
      CacheDTO cacheDTO
  ) {
    if (cacheDTO.cacheObject() instanceof Long) {
      return new CommonObject(fullKey, cacheDTO, "Long");

    } else if (cacheDTO.cacheObject() instanceof Set<?>) {
      return new CommonObject(fullKey, cacheDTO, "Set");
    }

    return new CommonObject(fullKey, cacheDTO, "AnotherType");
  }

  public void save(FullKey fullKey, CacheDTO cacheDTO) {
    save(fullKey, cacheDTO, null);
  }

  public void save(FullKey fullKey, CacheDTO cacheDTO, Duration ttl) {
    if (ttl != null && (ttl.isZero() || ttl.isNegative())) {
      throw new IllegalArgumentException("TTL must be positive");
    }

    Long timeLimit = null;
    if (ttl != null) {
      timeLimit = Instant.now().plus(ttl).toEpochMilli();
    }
    try {
      CommonObject commonObject =
          CommonObjWithCheckingTypes(fullKey, cacheDTO);
      String json = mapper.writeValueAsString(commonObject);
      String line = compress(json, timeLimit);

      Files.writeString(
          filePath,
          line + "\n",
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND
      );
    } catch (IOException e) {
      log.error("Disk storage save caused IOException", e);
    }
  }

  CacheDTO createValueObj(CommonObject commonObject) {
    Object value;

    if (commonObject.typeOfCacheObject().equals("Long")) {
      value = mapper.convertValue(
          commonObject.cacheObject(),
          Long.class
      );

    } else if (commonObject.typeOfCacheObject().equals("Set")) {
      value = mapper.convertValue(
          commonObject.cacheObject(),
          Set.class
      );

    } else {
      value = commonObject.cacheObject();
    }

    return new CacheDTO(
        commonObject.id(),
        commonObject.userId(),
        commonObject.ts(),
        value
    );
  }

  public Map<FullKey, CacheDTO> loadCacheDTO() {
    Map<FullKey, CacheDTO> cacheMap = new HashMap<>();
    if (!Files.exists(filePath)) {
      return cacheMap;
    }

    Path path = filePath.toAbsolutePath();
    long now = Instant.now().toEpochMilli();
    boolean hasExpiredLines = false;

    try {
      List<String> newLines = new ArrayList<>();
      for (String line : Files.readAllLines(path)) {
        int separator = line.indexOf(':');

        CompressionAlgorithm algorithm = CompressionAlgorithm.valueOf(line.substring(0, separator));
        String data = line.substring(separator + 1);
        int nextSeparator = data.indexOf(':');
        if (nextSeparator > 0 && !data.startsWith("{")) {
          long timeLimit = Long.parseLong(data.substring(0, nextSeparator));
          if (timeLimit <= now) {
            hasExpiredLines = true;
            continue;
          }
          data = data.substring(nextSeparator + 1);
        }

        String json = decompress(algorithm, data);
        CommonObject commonObject = mapper.readValue(json, CommonObject.class);
        cacheMap.put(commonObject.fullKey(), createValueObj(commonObject));
        newLines.add(line);
      }

      if (hasExpiredLines) {
        Path temp = path.resolveSibling("userdata.tmp");
        Files.write(temp, newLines, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        try {
          Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
          Files.deleteIfExists(temp);
        }
      }
    } catch (IOException | IllegalArgumentException e) {
      e.printStackTrace();
    }

    return cacheMap;
  }
}
