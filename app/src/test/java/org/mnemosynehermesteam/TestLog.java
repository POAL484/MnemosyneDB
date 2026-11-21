package org.mnemosynehermesteam;

import org.slf4j.Logger;

public class TestLog {
    final String testName;
    String partName = null;
    final Logger log;

    public TestLog(Logger log, String testName) {
        this.log = log;
        this.testName = testName;
        LoggerForTests.testStart(log, testName);
    }

    public void startPart(String partName) {
        this.partName = partName;
        LoggerForTests.testPartStart(log, testName, partName);
    }

    public void partPassed() {
        LoggerForTests.testPartPassed(log, testName, partName);
    }

    public void testPassed() {
        LoggerForTests.testPassed(log, testName);
    }
}
