/*
 * Copyright 2019 lambdaprime
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
package id.xfunction.tests.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.lang.XRE;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XRuntimeExceptionTest {

    @Test
    public void test_throw() {
        RuntimeException ex =
                Assertions.assertThrows(
                        XRE.class,
                        () -> {
                            throw new XRE("msg %s %d", "a1", 3);
                        });
        assertEquals("id.xfunction.lang.XRuntimeException: msg a1 3", ex.getMessage());
    }
}
