/*
 * Copyright 2025 lambdaprime
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

/**
 * @author lambdaprime intid@protonmail.com
 */
public class CircularArray {
    private double[] array;
    private int tail;
    private int size;
    private double total;

    public CircularArray(int size) {
        this.array = new double[size];
        this.tail = 0;
        this.size = 0;
    }

    public void add(double value) {
        total -= array[tail];
        array[tail] = value;
        total += value;
        if (size < array.length) size++;
        tail = (tail + 1) % array.length;
    }

    public double avg() {
        if (size == 0) return 0;
        return total / size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "CircularArray [tail=" + tail + ", size=" + size + ", total=" + total + "]";
    }
}
