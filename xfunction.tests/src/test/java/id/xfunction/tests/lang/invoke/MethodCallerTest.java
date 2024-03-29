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
package id.xfunction.tests.lang.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.lang.invoke.MethodCaller;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MethodCallerTest {

    @Test
    public void test_no_method() throws Exception {
        var caller = new MethodCaller(MethodHandles.lookup(), "hello world");
        Assertions.assertThrows(NoSuchMethodException.class, () -> caller.call("sadf"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_happy() throws Throwable {
        var list = new ArrayList();
        list.add("hello");
        var caller = new MethodCaller(MethodHandles.lookup(), list);
        assertEquals("[hello]", caller.call("toString"));
    }

    @Test
    public void test_void() throws Throwable {
        var caller = new MethodCaller(MethodHandles.lookup(), "hello  ");
        assertEquals("hello", caller.call("trim"));
    }
}
