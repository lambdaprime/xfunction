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
package id.xfunction.text;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** Performs string substitution according to the given mapping. */
public class Substitutor {

    private boolean hasRegexpSupport;

    /** Allow to specify regexps in mapping */
    public Substitutor withRegexpSupport() {
        hasRegexpSupport = true;
        return this;
    }

    /**
     * Creates and returns a new list by iterating over the input list and performing string
     * substitution as defined in the mapping.
     */
    public List<String> substitute(List<String> text, Map<String, String> mapping) {
        return text.stream().map(l -> substitute(l, mapping)).collect(toList());
    }

    /** Performs inplace substitution of strings in a given file */
    public void substitute(Path file, Map<String, String> mapping) throws IOException {
        Path tmp = Files.createTempFile(file.getParent(), "tmp", "");
        try (BufferedReader r = new BufferedReader(new FileReader(file.toFile()));
                BufferedWriter w = new BufferedWriter(new FileWriter(tmp.toFile()))) {
            String l = null;
            while ((l = r.readLine()) != null) {
                w.write(substitute(l, mapping));
                w.write('\n');
            }
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Substitutes all values and return new string */
    public String substitute(String text, Map<String, String> mapping) {
        for (Entry<String, String> e : mapping.entrySet()) {
            if (hasRegexpSupport) text = text.replaceAll(e.getKey(), e.getValue());
            else text = text.replace(e.getKey(), e.getValue());
        }
        return text;
    }
}
