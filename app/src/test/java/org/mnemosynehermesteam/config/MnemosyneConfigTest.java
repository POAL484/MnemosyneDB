package org.mnemosynehermesteam.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mnemosynehermesteam.LoggerTestsUtils;
import org.mnemosynehermesteam.TestLog;
import org.mnemosynehermesteam.caching.CacheAlgorithm;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j 
class MnemosyneConfigTest {

  @BeforeAll static public void BeforeAllMnemosyneConfigTest() {
    LoggerTestsUtils.startTestBlock(log, "Mnemosyne Config Test");
  }

  @AfterAll static public void AfterAllMnemosyneConfigTest() {
    LoggerTestsUtils.testBlockEnded(log, "Mnemosyne Config Test");
  }

  @Test
  void usesDefaultsWhenPropertiesAreEmpty() {
    TestLog testLog = new TestLog(log, "Uses defaults when properties are empty");
    MnemosyneConfig config = new MnemosyneConfig(new Properties());

    assertEquals(CacheAlgorithm.LRU, config.getAlgorithm());
    assertFalse(config.isDiskStorageEnabled());
    testLog.testPassed();
  }

  @Test
  void readsValuesFromProperties() {
    TestLog testLog = new TestLog(log, "Reads values from properties");
    testLog.startPart("Prepare properties");
    Properties props = new Properties();
    props.setProperty("cache.algorithm", "LFU");
    props.setProperty("storage.disk.enabled", "true");
    testLog.partPassed();

    testLog.startPart("Test MnenosyneConfig");
    MnemosyneConfig config = new MnemosyneConfig(props);

    assertEquals(CacheAlgorithm.LFU, config.getAlgorithm());
    assertTrue(config.isDiskStorageEnabled());
    testLog.partPassed();
    testLog.testPassed();
  }

  @Test
  void ignoresSpacesAndCaseInAlgorithm() {
    TestLog testLog = new TestLog(log, "Ignores spaces and case in algorithm");
    testLog.startPart("Prepare properties");
    Properties props = new Properties();
    props.setProperty("cache.algorithm", "  lfu ");
    testLog.partPassed();

    testLog.startPart("Test MnemosyneConfig");
    MnemosyneConfig config = new MnemosyneConfig(props);

    assertEquals(CacheAlgorithm.LFU, config.getAlgorithm());
    testLog.partPassed();
    testLog.testPassed();
  }

  @Test
  void throwsOnUnknownAlgorithm() {
    TestLog testLog = new TestLog(log, "Throws on unknown algorithm");
    testLog.startPart("Prepare properties");
    Properties props = new Properties();
    props.setProperty("cache.algorithm", "FIFO");
    testLog.partPassed();

    testLog.startPart("Test MnemosyneConfig");
    MnemosyneConfig config = new MnemosyneConfig(props);

    assertThrows(IllegalArgumentException.class, config::getAlgorithm);
    testLog.partPassed();
    testLog.testPassed();
  }
}