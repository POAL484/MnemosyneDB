package org.mnemosynehermesteam.config;

import org.junit.jupiter.api.Test;
import org.mnemosynehermesteam.caching.CacheAlgorithm;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MnemosyneConfigTest {

  @Test
  void usesDefaultsWhenPropertiesAreEmpty() {
    MnemosyneConfig config = new MnemosyneConfig(new Properties());

    assertEquals(CacheAlgorithm.LRU, config.getAlgorithm());
    assertFalse(config.isDiskStorageEnabled());
  }

  @Test
  void readsValuesFromProperties() {
    Properties props = new Properties();
    props.setProperty("cache.algorithm", "LFU");
    props.setProperty("storage.disk.enabled", "true");

    MnemosyneConfig config = new MnemosyneConfig(props);

    assertEquals(CacheAlgorithm.LFU, config.getAlgorithm());
    assertTrue(config.isDiskStorageEnabled());
  }

  @Test
  void ignoresSpacesAndCaseInAlgorithm() {
    Properties props = new Properties();
    props.setProperty("cache.algorithm", "  lfu ");

    MnemosyneConfig config = new MnemosyneConfig(props);

    assertEquals(CacheAlgorithm.LFU, config.getAlgorithm());
  }

  @Test
  void throwsOnUnknownAlgorithm() {
    Properties props = new Properties();
    props.setProperty("cache.algorithm", "FIFO");

    MnemosyneConfig config = new MnemosyneConfig(props);

    assertThrows(IllegalArgumentException.class, config::getAlgorithm);
  }
}