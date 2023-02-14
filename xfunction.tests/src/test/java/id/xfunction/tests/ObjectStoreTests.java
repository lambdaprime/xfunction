/*
 * Copyright 2022 lambdaprime
 * 
 * Website: https://github.com/lambdaprime/xfunction
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.ObjectStore;
import id.xfunction.nio.file.XFiles;
import java.nio.file.Path;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class ObjectStoreTests {

    @Test
    public void test_happy() throws Exception {
        String obj = "enity2";
        Path file = XFiles.TEMP_FOLDER.get().resolve("store" + System.currentTimeMillis());
        ObjectStore<String> store = new ObjectStore<>(file);
        store.save(obj);
        assertEquals(obj, store.load().get());
    }

    @Test
    public void test_happy_collection() throws Exception {
        HashSet<String> obj = new HashSet<>();
        Path file = XFiles.TEMP_FOLDER.get().resolve("store" + System.currentTimeMillis());
        ObjectStore<HashSet<String>> store = new ObjectStore<>(file);
        obj.add("enity1");
        obj.add("enity2");
        store.save(obj);
        assertEquals(obj, store.load().get());
    }
}
