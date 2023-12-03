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
package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.XUtils;
import id.xfunction.function.Unchecked;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class XUtilsTests {

    int m() throws Exception {
        return 0;
    }

    @Test
    public void testSafe() {
        Unchecked.get(this::m);
    }

    @Test
    public void test_quote() throws Exception {
        assertEquals("\"ggg\"", XUtils.quote("\"ggg\""));
        assertEquals("\"\"", XUtils.quote(""));
        assertEquals(" \"", XUtils.quote(" \""));
        assertEquals("  \"\"", XUtils.quote("  \"\""));
        assertEquals("\"sdsdsd  ", XUtils.quote("\"sdsdsd  "));
        assertEquals("\"sdsdsd", XUtils.quote("\"sdsdsd"));
        assertEquals("\"sd\"", XUtils.quote("sd  "));
        assertEquals("\"sd\"", XUtils.quote("  sd  "));
        assertEquals("  \"sd\"  ", XUtils.quote("  \"sd\"  "));
    }

    @Test
    public void test_unquote() throws Exception {
        assertEquals("ggg", XUtils.unquote("\"ggg\""));
        assertEquals("", XUtils.unquote(""));
        assertEquals(" \"", XUtils.unquote(" \""));
        assertEquals("", XUtils.unquote("  \"\""));
        assertEquals("\"sdsdsd  ", XUtils.unquote("\"sdsdsd  "));
        assertEquals("\"sdsdsd", XUtils.unquote("\"sdsdsd"));
        assertEquals("sd", XUtils.unquote("\"sd\"  "));
        assertEquals("sd", XUtils.unquote("  \"sd\"  "));
        assertEquals("  sd  ", XUtils.unquote("  sd  "));
    }

    public static void main(String[] args) {
        XUtils.printMemoryConsumption(100);
        new XUtilsTests().testSafe();

        byte[] b = "test".getBytes();
        System.out.println("fff");
        System.out.println(Arrays.toString(b));
    }
}
