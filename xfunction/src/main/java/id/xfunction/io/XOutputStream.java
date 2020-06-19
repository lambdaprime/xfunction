/*
 * Copyright 2019 lambdaprime
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
package id.xfunction.io;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import id.xfunction.XByte;

/**
 * <p>Implements an OutputStream into which you can write and later
 * get all data in one of the formats available.</p>
 *  
 */
public class XOutputStream extends OutputStream {

    private List<Integer> output = new ArrayList<>();
    
    @Override
    public void write(int v) throws IOException {
        output.add(v);
    }

    /**
     * <p>Returns written data in form of List of integers. Writing "abc" will result
     * [97, 98, 99] and "hello" to [104, 101, 108, 108, 111]</p>
     * 
     */
    public List<Integer> asList() {
        return output;
    }

    /**
     * <p>Returns written data in form of HEX string. Writing "abc" will result
     * to "61, 62, 63" and "hello" to "68, 65, 6c, 6c, 6f".</p>
     * <p>Each byte always encoded with 2 symbols (padded with 0)</p>
     * 
     */
    public String asHexString() {
        return output.stream()
                .map(i -> XByte.toHex(i.byteValue()))
                .collect(joining(", "));
    }
}
