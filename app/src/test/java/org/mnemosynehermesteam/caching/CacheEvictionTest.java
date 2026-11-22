package org.mnemosynehermesteam.caching;

import org.mnemosynehermesteam.LoggerTestsUtils;
import org.mnemosynehermesteam.TestLog;
import org.mnemosynehermesteam.core.CacheDTO;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j 
class CacheEvictionTest {

    @BeforeAll static public void beforeAllCacheEvictionTest() {
        LoggerTestsUtils.startTestBlock(log, "Cache Eviction Test");
    }

    @AfterAll static public void afterAllCacheEvictionTest() {
        LoggerTestsUtils.testBlockEnded(log, "Cache Eviction Test");
    }

    // Проверяем, что LRU вытесняет List, если к Map обращались позже
    @Test
    void shouldEvictListFromLRUCache() {
        TestLog testLog = new TestLog(log, "Should evict list from LRU Cache");
        testLog.startPart("Prepare data");
        LRUCache<Object> cache = new LRUCache<>(2);

        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("list", list);

        cache.get("map");

        cache.put("set", set);
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(cache.get("list").isEmpty());
        assertEquals(map, cache.get("map").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что LRU вытесняет Map, если List использовали позже
    @Test
    void shouldEvictMapFromLRUCache(){
        TestLog testLog = new TestLog(log, "Should evict map from LRU Cache");
        testLog.startPart("Prepare data");
        LRUCache<Object> cache = new LRUCache<>(2);

        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("list", list);
        cache.get("list");
        cache.put("set", set);
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(cache.get("map").isEmpty());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что LRU вытесняет Set, если Map использовали позже
    @Test
    void shouldEvictSetFromLRUCache(){
        TestLog testLog = new TestLog(log, "Should evict set from LRU cache");
        testLog.startPart("Prepare data");
        LRUCache<Object> cache = new LRUCache<>(2);

        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("set", set);
        cache.get("map");
        cache.put("list", list);
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(cache.get("set").isEmpty());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(map, cache.get("map").orElseThrow());
        assertEquals(2, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что LFU вытесняет List, к которому обращались реже
    @Test
    void shouldEvictListFromLFUCache(){
        TestLog testLog = new TestLog(log, "Should evict list from LFU Cache");
        testLog.startPart("Prepare data");
        LFUCache<Object> cache = new LFUCache<>(2);
        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("list", list);

        for (int i = 0; i < 10; i++){
            cache.get("map");
        }
        cache.get("list");
        cache.get("list");

        cache.put("set", set);
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(cache.get("list").isEmpty());
        assertEquals(map, cache.get("map").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что LFU вытесняет Map, к которому обращались реже
    @Test
    void shouldEvictMapFromLFUCache(){
        TestLog testLog = new TestLog(log, "Should evict map from LFU Cache");
        testLog.startPart("Prepare data");
        LFUCache<Object> cache = new LFUCache<>(2);
        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("list", list);

        for (int i = 0; i < 10; i++){
            cache.get("list");
        }
        cache.get("map");
        cache.get("map");

        cache.put("set", set);
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(cache.get("map").isEmpty());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что LFU вытесняет Set, к которому обращались реже
    @Test
    void shouldEvictSetFromLFUCache(){
        TestLog testLog = new TestLog(log, "Should evict set from LFU Cache");
        testLog.startPart("Prepare data");
        LFUCache<Object> cache = new LFUCache<>(2);
        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("set", set);

        for (int i = 0; i < 10; i++){
            cache.get("map");
        }
        cache.get("set");
        cache.get("set");

        cache.put("list", list);
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(cache.get("set").isEmpty());
        assertEquals(map, cache.get("map").orElseThrow());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(2, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Если частота обращений одинаковая, удаляем самый давно использованный объект
    @Test
    void shouldEvictLeastRecentlyUsedWhenFrequencyIsEqual() {
        TestLog testLog = new TestLog(log, "Should evict least recently used when frequency is equals");
        testLog.startPart("Prepare data");
        LFUCache<Object> cache = new LFUCache<>(2);

        cache.put("map", Map.of("a", 1));
        cache.put("list", List.of(1, 2, 3));

        cache.get("map");
        cache.get("list");

        cache.put("set", Set.of(4, 5, 6));
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(cache.get("map").isEmpty());
        assertTrue(cache.get("list").isPresent());
        assertTrue(cache.get("set").isPresent());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем вытеснение CacheDTO с List через InMemoryStorage
    @Test
    void shouldEvictCacheDTOWithList() {
        TestLog testLog = new TestLog(log, "Should evict cacheDTO with list in inMemoryStorage");
        testLog.startPart("Prepare data");
        InMemoryStorage storage = new InMemoryStorage(CacheAlgorithm.LRU, 2);

        UUID userId = UUID.randomUUID();

        CacheDTO mapDTO = new CacheDTO(
                UUID.randomUUID(),
                userId,
                System.currentTimeMillis(),
                Map.of("a", 1)
        );

        CacheDTO listDTO = new CacheDTO(
                UUID.randomUUID(),
                userId,
                System.currentTimeMillis(),
                List.of(1, 2, 3)
        );

        CacheDTO setDTO = new CacheDTO(
                UUID.randomUUID(),
                userId,
                System.currentTimeMillis(),
                Set.of(4, 5, 6)
        );

        storage.save("map", mapDTO);
        storage.save("list", listDTO);

        storage.get("map");

        storage.save("set", setDTO);
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(storage.get("list").isEmpty());

        assertEquals(mapDTO, storage.get("map").orElseThrow());
        assertEquals(setDTO, storage.get("set").orElseThrow());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что TLRU удаляет просроченный Map при добавлении Set
    @Test
    void shouldRemoveExpiredObject() throws InterruptedException {
        TestLog testLog = new TestLog(log, "Should remove expired object TLRU");
        testLog.startPart("Prepare data");
        TLRUCache<Object> cache = new TLRUCache<>(2);

        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map, Duration.ofMillis(100));
        cache.put("list", list, Duration.ofSeconds(10));
        testLog.partPassed();

        testLog.startPart("Wait of 200ms");
        Thread.sleep(200);
        testLog.partPassed();

        testLog.startPart("Add some data");
        cache.put("set", set);
        testLog.partPassed();

        testLog.startPart("Test objects");
        assertTrue(cache.get("map").isEmpty());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что size() удаляет просроченные объекты перед подсчётом
    @Test
    void shouldRemoveExpiredObjectsWhenCheckingSize()
            throws InterruptedException {
        
        TestLog testLog = new TestLog(log, "Should remove expired objects when checking size TLRU");
        testLog.startPart("Prepare data");
        TLRUCache<Object> cache = new TLRUCache<>(2);

        cache.put("map", Map.of("a", 1), Duration.ofMillis(100));
        cache.put("list", List.of(1, 2, 3), Duration.ofMillis(100));

        assertEquals(2, cache.size());
        testLog.partPassed();

        testLog.startPart("Wait of 200ms");
        Thread.sleep(200);
        testLog.partPassed();

        testLog.startPart("Test is removed");
        assertEquals(0, cache.size());
        assertTrue(cache.get("map").isEmpty());
        assertTrue(cache.get("list").isEmpty());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что повторный put обновляет значение и TTL
    @Test
    void shouldUpdateTTLWhenPuttingExistingKey()
            throws InterruptedException {

        TestLog testLog = new TestLog(log, "Should update TTL when putting existing key");
        testLog.startPart("Prepare data");
        TLRUCache<Object> cache = new TLRUCache<>(2);

        Map<String, Integer> oldMap = Map.of("a", 1);
        Map<String, Integer> newMap = Map.of("b", 2);

        cache.put("map", oldMap, Duration.ofMillis(500));

        Thread.sleep(100);
        testLog.partPassed();

        testLog.startPart("Update object");
        cache.put("map", newMap, Duration.ofSeconds(10));
        testLog.partPassed();

        testLog.startPart("Wait of 500ms");
        Thread.sleep(500);
        testLog.partPassed();

        testLog.startPart("Test TTL updated");
        assertEquals(newMap, cache.get("map").orElseThrow());
        assertEquals(1, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Если TTL ни у кого не истёк, TLRU вытесняет объект по обычному LRU
    @Test
    void shouldEvictLRUWhenNoObjectsExpired() {
        TestLog testLog = new TestLog(log, "Should TLRU evict LRU when no objects expired");
        testLog.startPart("Prepare data");
        TLRUCache<Object> cache = new TLRUCache<>(2);

        cache.put("map", Map.of("a", 1), Duration.ofMinutes(10));
        cache.put("list", List.of(1, 2, 3), Duration.ofMinutes(10));

        cache.get("map");

        cache.put("set", Set.of(4, 5, 6), Duration.ofMinutes(10));
        testLog.partPassed();

        testLog.startPart("Test evict");
        assertTrue(cache.get("list").isEmpty());
        assertTrue(cache.get("map").isPresent());
        assertTrue(cache.get("set").isPresent());
        assertEquals(2, cache.size());
        testLog.partPassed();
        testLog.testPassed();
    }

    // Проверяем, что TTL работает при сохранении CacheDTO через InMemoryStorage
    @Test
    void shouldExpireCacheDTOThroughInMemoryStorage()
            throws InterruptedException {

        TestLog testLog = new TestLog(log, "Should expire CacheDTO in inMemoryStorage");
        testLog.startPart("Prepare data");
        InMemoryStorage storage =
                new InMemoryStorage(CacheAlgorithm.TLRU, 2);

        UUID userId = UUID.randomUUID();

        CacheDTO mapDTO = new CacheDTO(
                UUID.randomUUID(),
                userId,
                System.currentTimeMillis(),
                Map.of("a", 1)
        );

        storage.save(
                "map",
                mapDTO,
                Duration.ofMillis(100)
        );
        testLog.partPassed();

        testLog.startPart("Immediatly exists");
        assertEquals(mapDTO, storage.get("map").orElseThrow());
        testLog.partPassed();

        testLog.startPart("Wait of 200ms");
        Thread.sleep(200);
        testLog.partPassed();

        testLog.startPart("Check is expired");
        assertTrue(storage.get("map").isEmpty());
        testLog.partPassed();
        testLog.testPassed();
    }
}
