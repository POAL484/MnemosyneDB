package org.mnemosynehermesteam.diskstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.config.MnemosyneConfig;
import org.mnemosynehermesteam.core.FullKey;

import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

  private String compress(String json) throws IOException {

    if (compressionAlgorithm == CompressionAlgorithm.NONE) {
      return "NONE:" + json;
    }

    byte[] bytes = json.getBytes();
    byte[] compressed = compressionAlgorithm.compress(bytes);

    return compressionAlgorithm.name() + ":"
        + Base64.getEncoder().encodeToString(compressed);
  }

  private String decompress(String encoded) throws IOException {

    int separator = encoded.indexOf(':');

    String algorithmName = encoded.substring(0, separator);
    String data = encoded.substring(separator + 1);

    CompressionAlgorithm algorithm;
    algorithm = CompressionAlgorithm.valueOf(algorithmName);

    if (algorithm == CompressionAlgorithm.NONE) {
      return data;
    }

    byte[] compressed = Base64.getDecoder().decode(data);
    byte[] decompressed = algorithm.decompress(compressed);

    return new String(decompressed);
  }

  CommonObject createCommonObject(
      FullKey fullKey,
      CacheDTO cacheDTO,
      String typeOfCacheObject
  ) {
    return new CommonObject(
        fullKey.key(),
        cacheDTO.id(),
        fullKey.userId(),
        cacheDTO.ts(),
        typeOfCacheObject,
        cacheDTO.cacheObject()
    );
  }

  CommonObject CommonObjWithCheckingTypes(
      FullKey fullKey,
      CacheDTO cacheDTO
  ) {
    if (cacheDTO.cacheObject() instanceof Long) {
      return createCommonObject(fullKey, cacheDTO, "Long");

    } else if (cacheDTO.cacheObject() instanceof Set<?>) {
      return createCommonObject(fullKey, cacheDTO, "Set");
    }

    return createCommonObject(fullKey, cacheDTO, "AnotherType");
  }

  public void save(FullKey fullKey, CacheDTO cacheDTO) {
    try {
      CommonObject commonObject =
          CommonObjWithCheckingTypes(fullKey, cacheDTO);

      String json = mapper.writeValueAsString(commonObject);
      String compressedjson = compress(json);

      Files.writeString(
          filePath,
          compressedjson + "\n",
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND
      );

    } catch (IOException e) {
      e.printStackTrace();
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

  public void delete(FullKey fullKey) {
    Path tempFile = null;

    try {
      if (!Files.exists(filePath)) {
        return;
      }

      List<String> lines = Files.readAllLines(filePath);
      List<String> remainingLines = new ArrayList<>();

      for (String line : lines) {
        String json = decompress(line);

        CommonObject commonObject =
            mapper.readValue(json, CommonObject.class);

        FullKey currentKey = new FullKey(
            commonObject.userId(),
            commonObject.key()
        );

        if (!currentKey.equals(fullKey)) {
          remainingLines.add(line);
        }
      }

      Path absolutePath = filePath.toAbsolutePath();

      tempFile = Files.createTempFile(
          absolutePath.getParent(),
          "userdata-",
          ".tmp"
      );

      Files.write(tempFile, remainingLines);
      Files.move(tempFile, absolutePath);

    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      if (tempFile != null) {
        try {
          Files.deleteIfExists(tempFile);

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Map<FullKey, CacheDTO> loadCacheDTO() {
    Map<FullKey, CacheDTO> cacheMap = new HashMap<>();

    try {
      if (Files.exists(filePath)) {
        List<String> lines = Files.readAllLines(filePath);

        for (String line : lines) {
          String json = decompress(line);

          CommonObject commonObject =
              mapper.readValue(json, CommonObject.class);

          CacheDTO value = createValueObj(commonObject);

          FullKey fullKey = new FullKey(
              commonObject.userId(),
              commonObject.key()
          );

          cacheMap.put(fullKey, value);
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return cacheMap;
  }
}