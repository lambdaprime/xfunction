package id.xfunction;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import id.xfunction.ObjectsStore;

public class ObjectsStorageTests {

    @Test
    public void test_happy() throws Exception {
        Path store = Paths.get("/tmp/store" + System.currentTimeMillis());
        ObjectsStore<String> pm = ObjectsStore.load(store);
        pm.add("enity1");
        pm.add("enity2");

        pm.save();

        pm = ObjectsStore.load(store);
        String actual = pm.findAll().stream()
                .sorted()
                .collect(toList())
                .toString();
        assertEquals("[enity1, enity2]", actual);
    }

    @Test
    public void test_happy_inmem() throws Exception {
        ObjectsStore<String> pm = ObjectsStore.create(new HashSet<String>());
        pm.add("enity1");
        pm.add("enity2");

        pm.save();

        String actual = pm.findAll().stream()
                .sorted()
                .collect(toList())
                .toString();
        assertEquals("[enity1, enity2]", actual);
    }
}
