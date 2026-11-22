package org.mnemosynehermesteam.config;

import org.mnemosynehermesteam.caching.CacheAlgorithm;
import org.mnemosynehermesteam.diskstorage.CompressionAlgorithm;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j 
public class MnemosyneConfig {

  private static final String FILE_NAME = "mnemosyne.properties";

  private final Properties props;

  public MnemosyneConfig() {
    this(loadFromResources());
  }

  public MnemosyneConfig(Properties props) {
    this.props = props;
    log.info("Loaded props: {}", props.toString());
  }

  private static Properties loadFromResources() {
    log.debug("Starting read props from resources, file name: {}", FILE_NAME);
    Properties props = new Properties();
    try (InputStream in = MnemosyneConfig.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
      if (in != null) {
        props.load(in);
      }
    } catch (IOException e) {
      log.error("IOException while props read");
      throw new RuntimeException("Не удалось прочитать " + FILE_NAME, e);
    }
    log.debug("Successfully loaded props from resources");
    return props;
  }

  public CacheAlgorithm getAlgorithm() {
    String value = props.getProperty("cache.algorithm", "LRU");
    return CacheAlgorithm.valueOf(value.trim().toUpperCase());
  }

  public boolean isDiskStorageEnabled() {
    String value = props.getProperty("storage.disk.enabled", "false");
    return Boolean.parseBoolean(value.trim());
  }

  public CompressionAlgorithm getCompressionAlgorithm() {
    String value = props.getProperty("storage.compression.algorithm", "NONE");
    return CompressionAlgorithm.valueOf(value.trim().toUpperCase());
  }
}
