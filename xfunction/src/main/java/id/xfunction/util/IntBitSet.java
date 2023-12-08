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

/**
 * BitSet implementation which uses ints to represent words.
 *
 * <p>Java standard {@link java.util.BitSet} uses long as a word which means if you need to obtain
 * all words as an array of integers you will need to create a new int array and copy all bits from
 * BitSet.
 *
 * <p>When this is done very often it can decrease performance.
 *
 * <p>This class is using int as word so it is possible to obtain array of integers with no cost.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class IntBitSet {

    private static final int LEN = 32;
    private int[] array;
    private int len;

    /**
     * @param len number of bits which are going to be stored
     */
    public IntBitSet(int len) {
        this.len = len;
        this.array = new int[(len + LEN - 1) / LEN];
    }

    /**
     * @param array creates new {@link IntBitSet} backed up by given array
     */
    public IntBitSet(int[] array) {
        this.len = array.length;
        this.array = array;
    }

    /** Flips all bits between [fromIndex, toIndex) */
    public void flip(int fromIndex, int toIndex) {
        Preconditions.isLess(fromIndex, len, "Out of range");
        Preconditions.isLessOrEqual(toIndex, len, "Out of range");
        if (fromIndex == toIndex) return;
        int idxA = index(fromIndex);
        int len = toIndex - fromIndex;
        if (len == 0) {
            flip(fromIndex);
            return;
        }
        int idxB = index(fromIndex + len - 1);
        Preconditions.isLessOrEqual(idxA, idxB, "Negative value");
        Preconditions.isLess(idxA, array.length, "Out of range");
        Preconditions.isLess(idxB, array.length, "Out of range");
        if (idxA == idxB) {
            array[idxA] = flip(array[idxA], fromIndex % LEN, toIndex % LEN);
            return;
        }
        array[idxA] = flip(array[idxA], fromIndex % LEN, LEN);
        for (int i = idxA + 1; i < idxB; i++) {
            array[i] = ~array[i];
        }
        array[idxB] = flip(array[idxB], 0, toIndex);
    }

    public int flip(int word, int fromIndex, int toIndex) {
        int mask = -1 >>> LEN - toIndex;
        mask &= -1 << fromIndex;
        word ^= mask;
        return word;
    }

    /** Flip single bit by its position */
    public void flip(int i) {
        Preconditions.isLess(i, len, "Out of range");
        int idx = index(i);
        int n = array[idx];
        n ^= 1 << (i % LEN);
        array[idx] = n;
    }

    /**
     * @return internal int array
     */
    public int[] intArray() {
        return array;
    }

    private int index(int bitPosition) {
        return bitPosition / LEN;
    }

    /**
     * Returns the index of the first bit that is set to true that occurs on or after the specified
     * starting index. If no such bit exists then return -1
     */
    public int nextSetBit(int fromIndex) {
        int idx = index(fromIndex);
        while (idx < array.length) {
            int n = array[idx];
            n &= 1 << (fromIndex % LEN);
            if (n != 0) return fromIndex;
            fromIndex++;
            idx = index(fromIndex);
        }
        return -1;
    }

    public String toBinaryString() {
        int i = array.length - 1;
        while (i >= 0 && array[i] == 0) i--;
        if (i < 0) return "0";
        StringBuilder buf = new StringBuilder();
        while (i >= 0) {
            String s = Integer.toBinaryString(array[i]);
            if (buf.length() != 0 && LEN - s.length() != 0) {
                buf.append(String.format("%" + (LEN - s.length()) + "s", "0").replace(" ", "0"));
            }
            buf.append(s);
            i--;
        }
        return buf.toString();
    }
}
