package org.mnemosynehermesteam.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonObjectTester {
    
    public JsonObject testJsonObj = null;

    public void testString(String expected, String key) {
        assertNotNull(testJsonObj.get(key));
        assertEquals(expected, testJsonObj.get(key).getAsString());
    }

    public void testDouble(double expected, String key) {
        assertNotNull(testJsonObj.get(key));
        assertEquals(expected, testJsonObj.get(key).getAsDouble());
    }

    public void testLong(long expected, String key) {
        assertNotNull(testJsonObj.get(key));
        assertEquals(expected, testJsonObj.get(key).getAsLong());
    }

    public void testList(Object[] expected, String key) {
        assertNotNull(testJsonObj.get(key));
        assertEquals(expected.length, testJsonObj.get(key).getAsJsonArray().size());
        for ( int i = 0; i < expected.length; i++ ) {
            assertEquals(DataManipulationUtils.serialiseObject(expected[i]), testJsonObj.get(key).getAsJsonArray().get(i));
        }
    }

    public void testSet(Object[] expected, String key) {
        testHasKey(key);
        JsonObject shouldSet = testJsonObj.get(key).getAsJsonObject();
        assertTrue(shouldSet.has("$set"));
        List<JsonElement> set = shouldSet.get("$set").getAsJsonArray().asList();
        for ( int i = 0; i < expected.length; i++ ) {
            assertTrue(set.contains(DataManipulationUtils.serialiseObject(expected[i])));
        }
    }

    public void openJsonObject(String key, int expectedSize) {
        testHasKey(key);
        assertEquals(expectedSize, testJsonObj.get(key).getAsJsonObject().size());
        testJsonObj = testJsonObj.get(key).getAsJsonObject();
    }

    public void testNull(String key) {
        boolean isNull = false;
        JsonElement el = testJsonObj.get(key);
        if ( el != null ) {
            isNull = el.isJsonNull();
        } else isNull = true;
        assertTrue(isNull);
    }

    public void testBoolean(Boolean expected, String key) {
        assertNotNull(testJsonObj.get(key));
        assertEquals(expected, testJsonObj.get(key).getAsBoolean());
    }

    public void testHasKey(String key) {
        assertTrue(testJsonObj.has(key));
    }

}
