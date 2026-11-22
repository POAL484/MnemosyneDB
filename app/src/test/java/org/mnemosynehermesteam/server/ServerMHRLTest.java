package org.mnemosynehermesteam.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mnemosynehermesteam.LoggerTestsUtils;
import org.mnemosynehermesteam.TestLog;

import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class ServerMHRLTest {

    static final CoreStub coreStub = new CoreStub();
    static NettyServer server;
    final JsonObjectTester jsonTester = new JsonObjectTester();

    final Map<String, Integer> TEST_MAP = new HashMap<>();
    final Map<String, String> TEST_EMPTY_MAP = new HashMap<>();
    final Integer[] TEST_LIST = new Integer[]{1, 2, 3};
    final List<Integer> TEST_EMPTY_LIST = new ArrayList<>();
    final Set<Integer> TEST_SET = new HashSet<>();
    final Integer[] TEST_SET_EXPECTED = new Integer[]{67, 1};
    final Set<Integer> TEST_EMPTY_SET = new HashSet<>();

    ServerMHRLTest() {
        TEST_SET.add(1);
        TEST_SET.add(67);

        TEST_MAP.put("1", 1);
        TEST_MAP.put("2", 2);
        TEST_MAP.put("3", 3);
    }
    
    @BeforeAll static public void beforeAllMHRLTest() throws InterruptedException {
        LoggerTestsUtils.startTestBlock(log, "Server MHRL Test");
        log.info("Starting server for {}Server MHRL Test{}...", LoggerTestsUtils.YELLOW, LoggerTestsUtils.RESET);
        server = new NettyServer(coreStub);
        Thread serverThread = new Thread(server);
        serverThread.start();
        while ( !server.isUp() ) { Thread.sleep(100); }
        log.info("Server for {}Server MHRL Test{} started", LoggerTestsUtils.YELLOW, LoggerTestsUtils.RESET);
    }

    @AfterAll static public void afterAllMHRLTest() throws InterruptedException {
        LoggerTestsUtils.testBlockEnded(log, "Server MHRL Test");
        log.info("Closing server for {}ServerMHRLTestFull{}...", LoggerTestsUtils.YELLOW, LoggerTestsUtils.RESET);
        server.close();
        log.info("Closed server for {}ServerMHRLTestFull{}", LoggerTestsUtils.YELLOW, LoggerTestsUtils.RESET);
    }

    void testResponseValidAndData(JsonObject testJsonObj) {
        jsonTester.testJsonObj = testJsonObj;
        log.debug("testing {}valid response{}: {}", LoggerTestsUtils.BLUE, LoggerTestsUtils.RESET, testJsonObj.toString());
        jsonTester.testLong(200L, "status");
        jsonTester.testHasKey("data");
        jsonTester.testJsonObj = jsonTester.testJsonObj.get("data").getAsJsonObject();
    }

    void testResponseError(JsonObject testJsonObj, long errCode) {
        jsonTester.testJsonObj = testJsonObj;
        log.debug("testing {}error resp{} {}, expected {}", LoggerTestsUtils.BLUE, LoggerTestsUtils.RESET, testJsonObj.toString(), errCode);
        jsonTester.testLong(400L, "status");
        jsonTester.testHasKey("err");
        jsonTester.testLong(errCode, "errCode");
    }

    void opSetAndTestOK(String key, Object value) throws IOException, InterruptedException {
        testResponseValidAndData(MHRLOperations.opSet(key, value));
        jsonTester.testString("OK!", "OK");
        log.debug("Set {} : {}", key, value);
    }

    @Test public void MHRLSetGetDel() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLSetGetDel");

        testLog.startPart("Not exists");
        testResponseValidAndData(MHRLOperations.opGet("non exists"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("String");
        opSetAndTestOK("str", "tomat");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomat", "value");
        testResponseValidAndData(MHRLOperations.opDel("str"));
        jsonTester.testString("tomat", "value");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("Boolean");
        opSetAndTestOK("bool", true);
        testResponseValidAndData(MHRLOperations.opGet("bool"));
        jsonTester.testBoolean(true, "value");
        testResponseValidAndData(MHRLOperations.opDel("bool"));
        jsonTester.testBoolean(true, "value");
        testResponseValidAndData(MHRLOperations.opGet("bool"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("Int");
        opSetAndTestOK("int", 48);
        testResponseValidAndData(MHRLOperations.opGet("int"));
        jsonTester.testLong(48, "value");
        testResponseValidAndData(MHRLOperations.opDel("int"));
        jsonTester.testLong(48, "value");
        testResponseValidAndData(MHRLOperations.opGet("int"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("Double");
        opSetAndTestOK("double", 4.8);
        testResponseValidAndData(MHRLOperations.opGet("double"));
        jsonTester.testDouble(4.8, "value");
        testResponseValidAndData(MHRLOperations.opDel("double"));
        jsonTester.testDouble(4.8, "value");
        testResponseValidAndData(MHRLOperations.opGet("double"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("List");
        opSetAndTestOK("list", TEST_LIST);
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(TEST_LIST, "value");
        testResponseValidAndData(MHRLOperations.opDel("list"));
        jsonTester.testList(TEST_LIST, "value");
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("Empty list");
        opSetAndTestOK("list", TEST_EMPTY_LIST);
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(new Object[]{}, "value");
        testResponseValidAndData(MHRLOperations.opDel("list"));
        jsonTester.testList(new Object[]{}, "value");
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("Set");
        opSetAndTestOK("set", TEST_SET);
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(TEST_SET_EXPECTED, "value");
        testResponseValidAndData(MHRLOperations.opDel("set"));
        jsonTester.testSet(TEST_SET_EXPECTED, "value");
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("Empty set");
        opSetAndTestOK("set", TEST_EMPTY_SET);
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(new Object[]{}, "value");
        testResponseValidAndData(MHRLOperations.opDel("set"));
        jsonTester.testSet(new Object[]{}, "value");
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("Map");
        opSetAndTestOK("map", TEST_MAP);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 3);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        jsonTester.testLong(3, "3");
        testResponseValidAndData(MHRLOperations.opDel("map"));
        jsonTester.openJsonObject("value", 3);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        jsonTester.testLong(3, "3");
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.startPart("Empty map");
        opSetAndTestOK("map", TEST_EMPTY_MAP);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 0);
        testResponseValidAndData(MHRLOperations.opDel("map"));
        jsonTester.openJsonObject("value", 0);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.testNull("value");
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLGetItemList() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLGetItemList");
        testLog.startPart("Prepare list");
        opSetAndTestOK("list", TEST_LIST);
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(TEST_LIST, "value");
        testLog.partPassed();

        testLog.startPart("one index test");
        testResponseValidAndData(MHRLOperations.opGetItem("list", new Integer[]{1}));
        jsonTester.testList(new Integer[]{2}, "value");
        testLog.partPassed();

        testLog.startPart("two indexes test");
        testResponseValidAndData(MHRLOperations.opGetItem("list", new Integer[]{2, 0}));
        jsonTester.testList(new Integer[]{3, 1}, "value");
        testLog.partPassed();

        testLog.startPart("index out of list");
        testResponseError(MHRLOperations.opGetItem("list", new Integer[]{67}), 4);
        testLog.partPassed();

        testLog.startPart("Not exists key");
        testResponseError(MHRLOperations.opGetItem("not exist", new Integer[]{0}), 2);
        testLog.partPassed();

        testLog.startPart("Incorrect types");
        opSetAndTestOK("str", "string");
        testResponseError(MHRLOperations.opGetItem("str", new Integer[]{0}), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLGetItemMap() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLGetItemMap");
        testLog.startPart("Prepare map");
        opSetAndTestOK("map", TEST_MAP);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 3);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        jsonTester.testLong(3, "3");
        testLog.partPassed();

        testLog.startPart("One key");
        testResponseValidAndData(MHRLOperations.opGetItem("map", new String[]{"3"}));
        jsonTester.openJsonObject("value", 1);
        jsonTester.testLong(3, "3");
        testLog.partPassed();

        testLog.startPart("Two keys");
        testResponseValidAndData(MHRLOperations.opGetItem("map", new String[]{"2", "1"}));
        jsonTester.openJsonObject("value", 2);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        testLog.partPassed();

        testLog.startPart("Not exists key");
        testResponseError(MHRLOperations.opGetItem("not exists", new String[]{"1"}), 2);
        testLog.partPassed();

        testLog.startPart("Incerrect types");
        opSetAndTestOK("str", "tomat");
        testResponseError(MHRLOperations.opGetItem("str", new String[]{"1"}), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLIsMemberSet() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLIsMemberSet");
        testLog.startPart("Prepare set");
        opSetAndTestOK("set", TEST_SET);
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(new Integer[]{1, 67}, "value");
        testLog.partPassed();

        testLog.startPart("Member");
        testResponseValidAndData(MHRLOperations.opIsMember("set", 1));
        jsonTester.testBoolean(true, "isMember");
        testLog.partPassed();

        testLog.startPart("Not member same type");
        testResponseValidAndData(MHRLOperations.opIsMember("set", 48));
        jsonTester.testBoolean(false, "isMember");
        testLog.partPassed();

        testLog.startPart("Not member different type");
        testResponseValidAndData(MHRLOperations.opIsMember("set", TEST_SET));
        jsonTester.testBoolean(false, "isMember");
        testLog.partPassed();

        testLog.startPart("Not exists => not member");
        testResponseValidAndData(MHRLOperations.opIsMember("not exists", 67));
        jsonTester.testBoolean(false, "isMember");
        testLog.partPassed();

        testLog.startPart("Incorrect type");
        opSetAndTestOK("string", "tomat");
        testResponseError(MHRLOperations.opIsMember("string", 67), 1);
        testLog.partPassed();

        testLog.startPart("Empty set");
        opSetAndTestOK("set", TEST_EMPTY_SET);
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(new Object[]{}, "value");
        testResponseValidAndData(MHRLOperations.opIsMember("set", "wrrr"));
        jsonTester.testBoolean(false, "isMember");
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLIsMemberMap() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLIsMemberMap");
        testLog.startPart("Prepare map");
        opSetAndTestOK("map", TEST_MAP);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 3);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        jsonTester.testLong(3, "3");
        testLog.partPassed();

        testLog.startPart("Member");
        testResponseValidAndData(MHRLOperations.opIsMember("map", "3"));
        jsonTester.testBoolean(true, "isMember");
        testLog.partPassed();

        testLog.startPart("Not member");
        testResponseValidAndData(MHRLOperations.opIsMember("map", "wrrr"));
        jsonTester.testBoolean(false, "isMember");
        testLog.partPassed();

        testLog.startPart("Not member not string for map");
        testResponseValidAndData(MHRLOperations.opIsMember("map", TEST_MAP));
        jsonTester.testBoolean(false, "isMember");
        testLog.partPassed();

        testLog.startPart("Not exists => not member");
        testResponseValidAndData(MHRLOperations.opIsMember("not exists", "wrrr"));
        jsonTester.testBoolean(false, "isMember");
        testLog.partPassed();

        testLog.startPart("Incorrect type");
        opSetAndTestOK("string", "tomat");
        testResponseError(MHRLOperations.opIsMember("string", "wrrr"), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLSize() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLSize");
        testLog.startPart("List of 3");
        opSetAndTestOK("list", TEST_LIST);
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(TEST_LIST, "value");
        testResponseValidAndData(MHRLOperations.opSize("list"));
        jsonTester.testLong(3, "size");
        testLog.partPassed();

        testLog.startPart("Empty list");
        opSetAndTestOK("list", TEST_EMPTY_LIST);
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(new Object[]{}, "value");
        testResponseValidAndData(MHRLOperations.opSize("list"));
        jsonTester.testLong(0, "size");
        testLog.partPassed();
 
        testLog.startPart("Set of 2");
        opSetAndTestOK("set", TEST_SET);
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(TEST_SET_EXPECTED, "value");
        testResponseValidAndData(MHRLOperations.opSize("set"));
        jsonTester.testLong(2, "size");
        testLog.partPassed();

        testLog.startPart("Empty set");
        opSetAndTestOK("set", TEST_EMPTY_SET);
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(new Object[]{}, "value");
        testResponseValidAndData(MHRLOperations.opSize("set"));
        jsonTester.testLong(0, "size");
        testLog.partPassed();

        testLog.startPart("Map of 3");
        opSetAndTestOK("map", TEST_MAP);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 3);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        jsonTester.testLong(3, "3");
        testResponseValidAndData(MHRLOperations.opSize("map"));
        jsonTester.testLong(3, "size");
        testLog.partPassed();

        testLog.startPart("Empty map");
        opSetAndTestOK("map", TEST_EMPTY_MAP);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 0);
        testResponseValidAndData(MHRLOperations.opSize("map"));
        jsonTester.testLong(0, "size");
        testLog.partPassed();

        testLog.startPart("String of 6");
        opSetAndTestOK("str", "tomato");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomato", "value");
        testResponseValidAndData(MHRLOperations.opSize("str"));
        jsonTester.testLong(6, "size");
        testLog.partPassed();

        testLog.startPart("Empty string");
        opSetAndTestOK("str", "");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("", "value");
        testResponseValidAndData(MHRLOperations.opSize("str"));
        jsonTester.testLong(0, "size");
        testLog.partPassed();

        testLog.startPart("Not exists");
        testResponseError(MHRLOperations.opSize("not exists"), 2);
        testLog.partPassed();

        testLog.startPart("Incorrect types");
        opSetAndTestOK("number", 67);
        testResponseValidAndData(MHRLOperations.opGet("number"));
        jsonTester.testLong(67, "value");
        testResponseError(MHRLOperations.opSize("number"), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLIncDec() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLIncDec");
        testLog.startPart("Integer increment");
        opSetAndTestOK("n", 67);
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testLong(67, "value");
        testResponseValidAndData(MHRLOperations.opInc("n", 3));
        jsonTester.testLong(70, "value");
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testLong(70, "value");
        testLog.partPassed();

        testLog.startPart("Double on integer increment");
        testResponseValidAndData(MHRLOperations.opInc("n", 6.7));
        jsonTester.testLong(76, "value");
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testLong(76, "value");
        testLog.partPassed();

        testLog.startPart("Integer decrement");
        testResponseValidAndData(MHRLOperations.opDec("n", 6));
        jsonTester.testLong(70, "value");
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testLong(70, "value");
        testLog.partPassed();

        testLog.startPart("Double on integer decrement");
        testResponseValidAndData(MHRLOperations.opDec("n", 3.67));
        jsonTester.testLong(67, "value");
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testLong(67, "value");
        testLog.partPassed();

        testLog.startPart("Double increment");
        opSetAndTestOK("n", 0.0);
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testDouble(0.0, "value");
        testResponseValidAndData(MHRLOperations.opInc("n", 10.1));
        jsonTester.testDouble(10.1, "value");
        testLog.partPassed();

        testLog.startPart("Integer on double increment");
        testResponseValidAndData(MHRLOperations.opInc("n", 10));
        jsonTester.testDouble(20.1, "value");
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testDouble(20.1, "value");
        testLog.partPassed();

        testLog.startPart("Double decrement");
        testResponseValidAndData(MHRLOperations.opDec("n", 10.1));
        jsonTester.testDouble(20.1-10.1, "value");
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testDouble(20.1-10.1, "value");
        testLog.partPassed();

        testLog.startPart("Integer on double decrement");
        opSetAndTestOK("n", 10.1);
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testDouble(10.1, "value");
        testResponseValidAndData(MHRLOperations.opDec("n", 10));
        jsonTester.testDouble(10.1-10, "value");
        testResponseValidAndData(MHRLOperations.opGet("n"));
        jsonTester.testDouble(10.1-10, "value");
        testLog.partPassed();

        testLog.startPart("Non exists increment => new long");
        testResponseValidAndData(MHRLOperations.opInc("nInc", 67));
        jsonTester.testLong(67, "value");
        testResponseValidAndData(MHRLOperations.opGet("nInc"));
        jsonTester.testLong(67, "value");
        testLog.partPassed();

        testLog.startPart("Non exists decrement => new long");
        testResponseValidAndData(MHRLOperations.opDec("nDec", 67));
        jsonTester.testLong(-67, "value");
        testResponseValidAndData(MHRLOperations.opGet("nDec"));
        jsonTester.testLong(-67, "value");
        testLog.partPassed();

        testLog.startPart("Increment on string");
        opSetAndTestOK("str", "tomat");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomat", "value");
        testResponseError(MHRLOperations.opInc("str", 67), 1);
        testLog.partPassed();

        testLog.startPart("Decrement on string");
        testResponseError(MHRLOperations.opDec("str", 67), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLPush() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLPush");
        testLog.startPart("Prepare list");
        opSetAndTestOK("list", TEST_LIST);
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(TEST_LIST, "value");
        testLog.partPassed();

        testLog.startPart("LPush");
        testResponseValidAndData(MHRLOperations.opLPush("list", 0));
        jsonTester.testLong(4, "size");
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(new Object[]{0, 1, 2, 3}, "value");
        testLog.partPassed();

        testLog.startPart("RPush");
        testResponseValidAndData(MHRLOperations.opRPush("list", 4));
        jsonTester.testLong(5, "size");
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(new Object[]{0, 1, 2, 3, 4}, "value");
        testLog.partPassed();
        
        testLog.startPart("Not exists LPush => new list");
        testResponseValidAndData(MHRLOperations.opLPush("listLPush", 67));
        jsonTester.testLong(1, "size");
        testResponseValidAndData(MHRLOperations.opGet("listLPush"));
        jsonTester.testList(new Object[]{67}, "value");
        testLog.partPassed();

        testLog.startPart("Not exists RPush => new list");
        testResponseValidAndData(MHRLOperations.opRPush("listRPush", 67));
        jsonTester.testLong(1, "size");
        testResponseValidAndData(MHRLOperations.opGet("listRPush"));
        jsonTester.testList(new Object[]{67}, "value");
        testLog.partPassed();

        testLog.startPart("Incorrect types");
        opSetAndTestOK("str", "tomat");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomat", "value");
        testResponseError(MHRLOperations.opLPush("str", 67), 1);
        testResponseError(MHRLOperations.opRPush("str", 67), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLPop() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLPop");
        testLog.startPart("Prepare list");
        opSetAndTestOK("list", TEST_LIST);
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(TEST_LIST, "value");
        testLog.partPassed();

        testLog.startPart("LPop");
        testResponseValidAndData(MHRLOperations.opLPop("list"));
        jsonTester.testLong(1, "value");
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(new Object[]{2, 3}, "value");
        testLog.partPassed();

        testLog.startPart("RPop");
        testResponseValidAndData(MHRLOperations.opRPop("list"));
        jsonTester.testLong(3, "value");
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(new Object[]{2}, "value");
        testResponseValidAndData(MHRLOperations.opRPop("list"));
        jsonTester.testLong(2, "value");
        testResponseValidAndData(MHRLOperations.opGet("list"));
        jsonTester.testList(new Object[]{}, "value");
        testLog.partPassed();

        testLog.startPart("Pop from empty list");
        testResponseError(MHRLOperations.opLPop("list"), 3);
        testResponseError(MHRLOperations.opRPop("list"), 3);
        testLog.partPassed();

        testLog.startPart("Non exists");
        testResponseError(MHRLOperations.opLPop("non exists"), 2);
        testResponseError(MHRLOperations.opRPop("non exists"), 2);
        testLog.partPassed();

        testLog.startPart("Incorrect types");
        opSetAndTestOK("str", "tomat");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomat", "value");
        testResponseError(MHRLOperations.opLPop("str"), 1);
        testResponseError(MHRLOperations.opRPop("str"), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLSAdd() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLSAdd");
        testLog.startPart("Prepare set");
        opSetAndTestOK("set", TEST_SET);
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(TEST_SET_EXPECTED, "value");
        testLog.partPassed();

        testLog.startPart("SAdd");
        testResponseValidAndData(MHRLOperations.opSAdd("set", 48));
        jsonTester.testLong(3, "size");
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(new Object[]{1, 67, 48}, "value");
        testLog.partPassed();

        testLog.startPart("Non exists => new set");
        testResponseValidAndData(MHRLOperations.opSAdd("setSAdd", 67));
        jsonTester.testLong(1, "size");
        testResponseValidAndData(MHRLOperations.opGet("setSAdd"));
        jsonTester.testSet(new Object[]{67}, "value");
        testLog.partPassed();

        testLog.startPart("Incorrect types");
        opSetAndTestOK("str", "tomat");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomat", "value");
        testResponseError(MHRLOperations.opSAdd("str", 67), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLSRem() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLSRem");
        testLog.startPart("Prepare set");
        opSetAndTestOK("set", TEST_SET);
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(TEST_SET_EXPECTED, "value");
        testLog.partPassed();

        testLog.startPart("SRem");
        testResponseValidAndData(MHRLOperations.opSRem("set", 67));
        jsonTester.testLong(67, "value");
        jsonTester.testLong(1, "size");
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(new Object[]{1}, "value");
        testLog.partPassed();

        testLog.startPart("SRem not member");
        testResponseValidAndData(MHRLOperations.opSRem("set", 48));
        jsonTester.testNull("value");
        jsonTester.testLong(1, "size");
        testResponseValidAndData(MHRLOperations.opGet("set"));
        jsonTester.testSet(new Object[]{1}, "value");
        testLog.partPassed();

        testLog.startPart("Not exists");
        testResponseError(MHRLOperations.opSRem("non exists", 67), 2);
        testLog.partPassed();

        testLog.startPart("Incorrect types");
        opSetAndTestOK("str", "tomat");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomat", "value");
        testResponseError(MHRLOperations.opSRem("str", 67), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLHSet() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLHSet");
        testLog.startPart("Prepare map");
        opSetAndTestOK("map", TEST_MAP);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 3);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        jsonTester.testLong(3, "3");
        testLog.partPassed();

        testLog.startPart("HSet no replace");
        Map<String, Object> map = new HashMap<>();
        map.put("67", 67);
        testResponseValidAndData(MHRLOperations.opHSet("map", map));
        jsonTester.testLong(4, "size");
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 4);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        jsonTester.testLong(3, "3");
        jsonTester.testLong(67, "67");
        testLog.partPassed();

        testLog.startPart("HSet with replace");
        map.put("2", 22);
        testResponseValidAndData(MHRLOperations.opHSet("map", map));
        jsonTester.testLong(4, "size");
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 4);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(22, "2");
        jsonTester.testLong(3, "3");
        jsonTester.testLong(67, "67");
        testLog.partPassed();

        testLog.startPart("Not exists => new map");
        testResponseValidAndData(MHRLOperations.opHSet("mapHSet", map));
        jsonTester.testLong(2, "size");
        testResponseValidAndData(MHRLOperations.opGet("mapHSet"));
        jsonTester.openJsonObject("value", 2);
        jsonTester.testLong(22, "2");
        jsonTester.testLong(67, "67");
        testLog.partPassed();

        testLog.startPart("Incorrect types");
        opSetAndTestOK("str", "tomat");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomat", "value");
        testResponseError(MHRLOperations.opHSet("str", map), 1);
        testLog.partPassed();

        testLog.testPassed();
    }

    @Test public void MHRLHDel() throws IOException, InterruptedException {
        TestLog testLog = new TestLog(log, "MHRLHDel");
        testLog.startPart("Prepare map");
        opSetAndTestOK("map", TEST_MAP);
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 3);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(2, "2");
        jsonTester.testLong(3, "3");
        testLog.partPassed();

        testLog.startPart("HDel right");
        testResponseValidAndData(MHRLOperations.opHDel("map", new String[]{"2"}));
        jsonTester.openJsonObject("deleted", 1);
        jsonTester.testLong(2, "2");
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 2);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(3, "3");
        testLog.partPassed();

        testLog.startPart("HDel with not member");
        testResponseValidAndData(MHRLOperations.opHDel("map", new String[]{"1", "3", "4"}));
        jsonTester.openJsonObject("deleted", 2);
        jsonTester.testLong(1, "1");
        jsonTester.testLong(3, "3");
        testResponseValidAndData(MHRLOperations.opGet("map"));
        jsonTester.openJsonObject("value", 0);
        testLog.partPassed();

        testLog.startPart("Not exists");
        testResponseError(MHRLOperations.opHDel("non exists", new String[]{"67"}), 2);
        testLog.partPassed();

        testLog.startPart("Incorrect types");
        opSetAndTestOK("str", "tomat");
        testResponseValidAndData(MHRLOperations.opGet("str"));
        jsonTester.testString("tomat", "value");
        testResponseError(MHRLOperations.opHDel("str", new String[]{"67"}), 1);
        testLog.partPassed();

        testLog.testPassed();
    }
}
