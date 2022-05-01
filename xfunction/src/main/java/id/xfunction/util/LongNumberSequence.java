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
package id.xfunction.util;

import id.xfunction.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Collection which keeps track of incoming positive number sequence (1, 2, 3, 4, ...) and allows to
 * query what numbers are missing in it.
 *
 * <p>When this collection initially created all numbers of it are missing.
 *
 * <p>This collection can keep track only of N last numbers of the sequence. All numbers less than
 * "last added maximum number - N" are removed.
 *
 * <p>This collection is not thread safe.
 *
 * <h2>Examples</h2>
 *
 * <pre>{@code
 * // empty sequence of numbers between [1, 10]
 * LongNumberSequence seq = new LongNumberSequence(10);
 *
 * // prints "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]" since all numbers are missing
 * System.out.println(seq.getMissing());
 *
 * // similarly all numbers between [1, 8] are missing so it prints "[1, 2, 3, 4, 5, 6, 7, 8]"
 * System.out.println(seq.getMissing(1, 8));
 *
 * // adding few numbers
 * seq.add(5);
 * seq.add(4);
 *
 * // now 4 and 5 are not missing so it prints "[1, 2, 3, 6, 7, 8]"
 * System.out.println(seq.getMissing(1, 8));
 *
 * // adding 13
 * // since sequence size is 10 it cannot hold 13 numbers so it shifts
 * // to the right to accommodate 13 and now it is a sequence between [4, 13]
 * seq.add(13);
 *
 * // prints "[6, 7, 8, 9, 10, 11, 12]" (1, 2, 3 does not belong to the sequence anymore
 * // since it is not sequence of numbers between [1, 10] but between [4, 13]
 * System.out.println(seq.getMissing(1, 13));
 * }</pre>
 */
public class LongNumberSequence {

    private long lastMax = -1;
    private int size;
    private TreeSet<Long> missing = new TreeSet<>();

    /**
     * @param size how many N last numbers to keep. All numbers less than "last added maximum number
     *     - N" are removed.
     */
    public LongNumberSequence(int size) {
        this.size = size;
    }

    /** @return how many N last numbers can be queried for presence */
    public int size() {
        return size;
    }

    /** Returns whether there are any missing number of last N numbers or not */
    public boolean hasMissing() {
        return lastMax == -1 ? true : !missing.isEmpty();
    }

    /**
     * Add new number to the sequence.
     *
     * <p>Complexity is log(N)
     */
    public void add(long n) {
        Preconditions.isTrue(n > 0, "Only positive numbers are allowed in the sequence");
        if (lastMax < n) {
            long prevLastMax = lastMax;
            lastMax = n;
            long start = Math.max(1, lastMax - size + 1);
            while (!missing.isEmpty() && (missing.first() < start)) missing.pollFirst();
            for (long i = Math.max(start, prevLastMax + 1); i < lastMax; i++) missing.add(i);
        } else {
            missing.remove(n);
        }
    }

    /**
     * Check if certain number is missing
     *
     * <p>Complexity is log(N)
     */
    public boolean isMissing(long n) {
        return missing.contains(n);
    }

    /**
     * Returns unmodifiable collection of missing numbers from the sequence of numbers seen so far
     */
    public Collection<Long> getMissing() {
        if (lastMax == -1) {
            return getMissing(1, size);
        }
        return Collections.unmodifiableCollection(missing);
    }

    /**
     * Returns unmodifiable collection of missing numbers between [first, last] from the sequence of
     * numbers seen so far
     */
    public Collection<Long> getMissing(long first, long last) {
        if (lastMax < first) {
            return LongStream.rangeClosed(first, last).boxed().collect(Collectors.toList());
        }
        if (lastMax < last) {
            List<Long> res = new ArrayList<>((int) (last - first));
            res.addAll(missing.tailSet(first));
            for (long i = lastMax + 1; i <= last; i++) {
                res.add(i);
            }
            return Collections.unmodifiableCollection(res);
        }
        return Collections.unmodifiableCollection(missing.subSet(first, last + 1));
    }
}
