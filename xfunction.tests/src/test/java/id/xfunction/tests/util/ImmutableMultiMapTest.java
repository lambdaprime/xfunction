/*
 * Copyright 2024 lambdaprime
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
package id.xfunction.tests.util;

import id.xfunction.util.ImmutableMultiMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImmutableMultiMapTest {

    @Test
    public void test() {
        Assertions.assertEquals("{}", ImmutableMultiMap.of().toString());
        Assertions.assertEquals(
                "{1=[a, c], 2=[b]}", ImmutableMultiMap.of(1, "a", 2, "b", 1, "c").toString());
        Assertions.assertEquals(
                "Optional[a]",
                ImmutableMultiMap.of(1, "a", 2, "b", 1, "c").getFirstParameter(1).toString());
    }
}
