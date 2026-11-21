package org.mnemosynehermesteam.config;

import org.mnemosynehermesteam.caching.CacheAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MnemosyneConfig {

  private static final String FILE_NAME = "mnemosyne.properties";

  private final Properties props;

  public MnemosyneConfig() {
    this(loadFromResources());
  }

  public MnemosyneConfig(Properties props) {
    this.props = props;
  }

  private static Properties loadFromResources() {
    Properties props = new Properties();
    try (InputStream in = MnemosyneConfig.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
      if (in != null) {
        props.load(in);
      }
    } catch (IOException e) {
      throw new RuntimeException("Не удалось прочитать " + FILE_NAME, e);
    }
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
}
