package org.mnemosynehermesteam.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mnemosynehermesteam.LoggerTestsUtils;
import org.mnemosynehermesteam.TestLog;
import org.mnemosynehermesteam.server.mhrl.JsonObjectBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class JsonObjectBuilderTest {

    @BeforeAll static public void BeforeAllJsonObjectBuilderTest() {
        LoggerTestsUtils.startTestBlock(log, "JsonObjectBuilder Test");
    }

    @AfterAll static public void AfterAllJsonObjectBuilderTest() {
        LoggerTestsUtils.testBlockEnded(log, "JsonObjectBuilder Test");
    }

    final JsonObjectTester jsonTester = new JsonObjectTester();
    
    @Test public void jsonObjectBuilderTestString() {
        TestLog testLog = new TestLog(log, "JsonObjectBuilder String test");
        jsonTester.testJsonObj = new JsonObjectBuilder().add("str", "tomat").build();
        jsonTester.testString("tomat", "str");
        testLog.testPassed();
    }

    @Test public void jsonObjectBuilderTestLong() {
        TestLog testLog = new TestLog(log, "JsonObjectBuilder Long test");
        jsonTester.testJsonObj = new JsonObjectBuilder().add("int", 67).build();
        jsonTester.testLong(67L, "int");
        testLog.testPassed();
    }

    @Test public void jsonObjectBuilderTestDouble() {
        TestLog testLog = new TestLog(log, "JsonObjectBuilder Double test");
        jsonTester.testJsonObj = new JsonObjectBuilder().add("double", 6.7).build();
        jsonTester.testDouble(6.7, "double");
        testLog.testPassed();
    }

    @Test public void jsonObjectBuilderTestBoolean() {
        TestLog testLog = new TestLog(log, "JsonObjectBuilder Boolean test");
        testLog.startPart("Test false");
        jsonTester.testJsonObj = new JsonObjectBuilder().add("bool", false).build();
        jsonTester.testBoolean(false, "bool");
        testLog.partPassed();
        testLog.startPart("Test true");
        jsonTester.testJsonObj = new JsonObjectBuilder().add("bool", true).build();
        jsonTester.testBoolean(true, "bool");
        testLog.partPassed();
        testLog.testPassed();
    }
    
    @Test public void jsonObjectBuilderTestList() {
        TestLog testLog = new TestLog(log, "JsonObjectBuilder List test");
        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        jsonTester.testJsonObj = new JsonObjectBuilder().add("list", list).build();
        jsonTester.testList(new Object[]{1, 2, 3}, "list");
        testLog.testPassed();
    }

    @Test public void jsonObjectBuilderTestSet() {
        TestLog testLog = new TestLog(log, "JsonObjectBuilder Set test");
        Set<Integer> set = new HashSet<>();
        set.add(67);
        set.add(3);
        set.add(48);
        jsonTester.testJsonObj = new JsonObjectBuilder().add("set", set).build();
        jsonTester.testSet(new Object[]{67, 48, 3}, "set");
        testLog.testPassed();
    }

    @Test public void jsonObjectBuilderTestNull() {
        TestLog testLog = new TestLog(log, "JsonObjectBuilder null test");
        testLog.startPart("Literal null");
        jsonTester.testJsonObj = new JsonObjectBuilder().add("null", null).build();
        jsonTester.testNull("null");
        testLog.partPassed();
        testLog.startPart("Non exists key");
        jsonTester.testNull("not set");
        testLog.partPassed();
        testLog.testPassed();
    }

    @Test public void jsonObjectBuilderTestMap() {
        TestLog testLog = new TestLog(log, "JsonObjectBuilder Map test");
        Map<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        jsonTester.testJsonObj = new JsonObjectBuilder().add("map", map).build();
        jsonTester.openJsonObject("map", 3);
        jsonTester.testLong(1L, "1");
        jsonTester.testLong(2L, "2");
        jsonTester.testLong(3L, "3");
        testLog.testPassed();
    }
}
