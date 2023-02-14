/*
 * Copyright 2021 lambdaprime
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

import static java.util.stream.Collectors.toSet;

import id.xfunction.util.XCollections;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XCollectionsTests {

    @Test
    public void test_isIntersects() {
        Set<Integer> s1 = Stream.of(1, 2, 3).collect(toSet());
        Set<Integer> s2 = Stream.of(1, 4, 5).collect(toSet());
        Assertions.assertEquals(true, XCollections.hasIntersection(s1, s2));

        s2 = Stream.of(4, 5).collect(toSet());
        Assertions.assertEquals(false, XCollections.hasIntersection(s1, s2));
    }
}
