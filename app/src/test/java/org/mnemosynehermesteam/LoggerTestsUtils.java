package org.mnemosynehermesteam;

import org.slf4j.Logger;

public class LoggerTestsUtils {
    public static String BLACK = "\u001B[30m";
    public static String GREEN = "\u001B[32m";
    public static String RED = "\u001B[31m";
    public static String YELLOW = "\u001B[33m";
    public static String BLUE = "\u001B[34m";
    public static String MAGENTA = "\u001B[35m";
    public static String CYAN = "\u001B[36m";
    public static String RESET = "\u001B[0m";

    public static String[] EMOJIS = new String[]{
        "┬─┬⃰͡ (ᵔᵕᵔ͜ )",
        "ʕ •`ᴥ•´ʔ",
        "☜(⌒▽⌒)☞",
        "（ ^_^）o自自o（^_^ ）",
        "ಥ‿ಥ",
        "┌(ㆆ㉨ㆆ)ʃ",
        "(ᵔᴥᵔ)",
        "(•̀ᴗ•́)و ̑̑",
        "(∩｀-´)⊃━☆ﾟ.*･｡ﾟ",
        "(◠﹏◠)",
        "~(^-^)~"
    };

    public static void testStart(Logger log, String testName) {
        log.info("{}>>{} Starting test: {}{}{}", MAGENTA, RESET, YELLOW, testName, RESET);
    }

    public static void testPartStart(Logger log, String testName, String partName) {
        log.info("{}>>>>{} {}{}{} Starting: {}{}{}", MAGENTA, RESET, YELLOW, testName, RESET, CYAN, partName, RESET);
    }

    public static void testPassed(Logger log, String testName) {
        log.info("{}<<{} Test {}{}{} passed! YAY! {}", GREEN, RESET, YELLOW, testName, RESET, EMOJIS[(int) (Math.random() * EMOJIS.length)]);
    }

    public static void testPartPassed(Logger log, String testName, String partName) {
        log.info("{}<<<<{} {}{}{} - {}{}{} passed!", GREEN, RESET, YELLOW, testName, RESET, GREEN, partName, RESET);
    }

    public static void startTestBlock(Logger log, String blockName) {
        log.info("{}>{}", MAGENTA, RESET);
        log.info("{}>{}", MAGENTA, RESET);
        log.info("{}>{} Starting {}{}{} tests!", MAGENTA, RESET, BLUE, blockName, RESET);
        log.info("{}>{}", MAGENTA, RESET);
        log.info("{}>{}", MAGENTA, RESET);
    }

    public static void testBlockEnded(Logger log, String blockName) {
        log.info("{}<{}", YELLOW, RESET);
        log.info("{}<{}", YELLOW, RESET);
        log.info("{}<{} {}{}{} tests ended!", YELLOW, RESET, BLUE, blockName, RESET);
        log.info("{}<{}", YELLOW, RESET);
        log.info("{}<{}", YELLOW, RESET);
    }
}
