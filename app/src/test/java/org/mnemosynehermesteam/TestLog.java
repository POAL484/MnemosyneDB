package org.mnemosynehermesteam;

import org.slf4j.Logger;

public class TestLog {
    final String testName;
    String partName = null;
    final Logger log;

    public TestLog(Logger log, String testName) {
        this.log = log;
        this.testName = testName;
        LoggerTestsUtils.testStart(log, testName);
    }

    public void startPart(String partName) {
        this.partName = partName;
        LoggerTestsUtils.testPartStart(log, testName, partName);
    }

    public void partPassed() {
        LoggerTestsUtils.testPartPassed(log, testName, partName);
    }

    public void testPassed() {
        LoggerTestsUtils.testPassed(log, testName);
    }
}
