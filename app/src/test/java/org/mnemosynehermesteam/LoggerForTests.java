package org.mnemosynehermesteam;

import org.slf4j.Logger;

public class LoggerForTests {
    public static String BLACK = "\u001B[30m";
    public static String GREEN = "\u001B[32m";
    public static String RED = "\u001B[31m";
    public static String YELLOW = "\u001B[33m";
    public static String BLUE = "\u001B[34m";
    public static String MAGENTA = "\u001B[35m";
    public static String CYAN = "\u001B[36m";
    public static String RESET = "\u001B[0m";

    public static void testStart(Logger log, String testName) {
        log.info("{}>>{} Starting test: {}{}{}", MAGENTA, RESET, YELLOW, testName, RESET);
    }

    public static void testPartStart(Logger log, String testName, String partName) {
        log.info("{}>>>>{} {}{}{} Starting: {}{}{}", MAGENTA, RESET, YELLOW, testName, RESET, CYAN, partName, RESET);
    }

    public static void testPassed(Logger log, String testName) {
        log.info("{}<<{} Test {}{}{} passed! ", GREEN, RESET, YELLOW, testName, RESET);
    }

    public static void testPartPassed(Logger log, String testName, String partName) {
        log.info("{}<<<<{} {}{}{} - {}{}{} passed!", GREEN, RESET, YELLOW, testName, RESET, GREEN, partName, RESET);
    }
}
