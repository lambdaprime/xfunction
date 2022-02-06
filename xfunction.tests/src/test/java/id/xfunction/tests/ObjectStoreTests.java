package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import id.xfunction.ObjectStore;

public class ObjectStoreTests {

    @Test
    public void test_happy() throws Exception {
        String obj = "enity2";
        Path file = Paths.get("/tmp/store" + System.currentTimeMillis());
        ObjectStore<String> store = new ObjectStore<>(file);
        store.save(obj);
        assertEquals(obj, store.load().get());
    }

    @Test
    public void test_happy_collection() throws Exception {
        HashSet<String> obj = new HashSet<>();
        Path file = Paths.get("/tmp/store" + System.currentTimeMillis());
        ObjectStore<HashSet<String>> store = new ObjectStore<>(file);
        obj.add("enity1");
        obj.add("enity2");
        store.save(obj);
        assertEquals(obj, store.load().get());
    }
}
