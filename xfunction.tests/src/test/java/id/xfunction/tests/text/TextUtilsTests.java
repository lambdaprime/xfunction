/*
 * Copyright 2026 lambdaprime
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
package id.xfunction.tests.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.text.TextUtils;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class TextUtilsTests {
    @Test
    void testResolveAll() {
        // Test with environment variables
        var data =
                Map.of(
                        "key1",
                        "hi-${env:USER}",
                        "key2",
                        "path-$(echo test)",
                        "key3",
                        "happy-${env:RAND_UNDEFINED}");

        var resolvedData = new TextUtils().expand(data, true, true);

        assertEquals("hi-" + System.getenv("USER"), resolvedData.get("key1"));
        assertEquals("path-test", resolvedData.get("key2"));
        assertEquals("happy-null", resolvedData.get("key3"));
    }
}
