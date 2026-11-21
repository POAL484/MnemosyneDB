package org.mnemosynehermesteam.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mnemosynehermesteam.server.mhrl.JsonObjectBuilder;

public class JsonObjectBuilderTest {

    final JsonObjectTester jsonTester = new JsonObjectTester();
    
    @Test public void jsonObjectBuilderTestString() {
        jsonTester.testJsonObj = new JsonObjectBuilder().add("str", "tomat").build();
        jsonTester.testString("tomat", "str");
    }

    @Test public void jsonObjectBuilderTestLong() {
        jsonTester.testJsonObj = new JsonObjectBuilder().add("int", 67).build();
        jsonTester.testLong(67L, "int");
    }

    @Test public void jsonObjectBuilderTestDouble() {
        jsonTester.testJsonObj = new JsonObjectBuilder().add("double", 6.7).build();
        jsonTester.testDouble(6.7, "double");
    }

    @Test public void jsonObjectBuilderTestBoolean() {
        jsonTester.testJsonObj = new JsonObjectBuilder().add("bool", false).build();
        jsonTester.testBoolean(false, "bool");
        jsonTester.testJsonObj = new JsonObjectBuilder().add("bool", true).build();
        jsonTester.testBoolean(true, "bool");
    }
    
    @Test public void jsonObjectBuilderTestList() {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        jsonTester.testJsonObj = new JsonObjectBuilder().add("list", list).build();
        jsonTester.testList(new Object[]{1, 2, 3}, "list");
    }

    @Test public void jsonObjectBuilderTestSet() {
        Set<Integer> set = new HashSet<>();
        set.add(67);
        set.add(3);
        set.add(48);
        jsonTester.testJsonObj = new JsonObjectBuilder().add("set", set).build();
        jsonTester.testSet(new Object[]{67, 48, 3}, "set");
    }

    @Test public void jsonObjectBuilderTestNull() {
        jsonTester.testJsonObj = new JsonObjectBuilder().add("null", null).build();
        jsonTester.testNull("null");
        jsonTester.testNull("not set");
    }

    @Test public void jsonObjectBuilderTestMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        jsonTester.testJsonObj = new JsonObjectBuilder().add("map", map).build();
        jsonTester.openJsonObject("map", 3);
        jsonTester.testLong(1L, "1");
        jsonTester.testLong(2L, "2");
        jsonTester.testLong(3L, "3");
    }
}
