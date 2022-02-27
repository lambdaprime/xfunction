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

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * Creates an InputStream from different types of inputs.
 * </p>
 * 
 */
public class XInputStream extends InputStream {

    private Iterator<Integer> iterator;

    /**
     * From list of integers.
     */
    public XInputStream(List<Integer> output) {
        this.iterator = output.iterator();
    }

    /**
     * <p>
     * From HEX string.
     * </p>
     * <p>
     * Each byte needs to be encoded with 2 symbols (padded with 0) and separated
     * with comma.
     * </p>
     * <p>
     * Example:
     * </p>
     * 
     * <pre>
     * {@code
     * new XInputStream("68, 65, 6c, 6c, 6f")
     * }
     * </pre>
     */
    public XInputStream(String hex) {
        this(Pattern.compile("\\s|,|\\n").splitAsStream(hex).map(String::trim).filter(s -> !s.isEmpty())
            .mapToInt(b -> Integer.valueOf(b, 16)).boxed().collect(toList()));
    }

    @Override
    public int read() throws IOException {
        if (!iterator.hasNext())
            return -1;
        return iterator.next();
    }
}
