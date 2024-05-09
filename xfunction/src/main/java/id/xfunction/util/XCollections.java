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
package id.xfunction.util;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Additions to standard {@link java.util.Collections}
 *
 * @author lambdaprime intid@protonmail.com
 */
public class XCollections {

    /** Check if two sets share one or more similar elements */
    public static <T> boolean hasIntersection(Set<T> s1, Set<T> s2) {
        return s1.stream().filter(v -> s2.contains(v)).findFirst().isPresent();
    }

    /**
     * Find equal pairs between two {@link List} "a" and "b" and consume them as pair of [indexA,
     * indexB] for which Objects.equals(a[indexA], b[indexB]) is true.
     */
    public static <T> void findEqualPairs(
            List<T> a, List<T> b, BiConsumer<Integer, Integer> pairConsumer) {
        var map =
                IntStream.range(0, a.size())
                        .boxed()
                        .collect(Collectors.toMap(i -> a.get(i), i -> i));
        IntStream.range(0, b.size())
                .forEach(
                        i2 -> {
                            var i1 = map.get(b.get(i2));
                            if (i1 != null) pairConsumer.accept(i1, i2);
                        });
    }
}
