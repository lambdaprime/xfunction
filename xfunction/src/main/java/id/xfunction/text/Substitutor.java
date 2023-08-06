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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * Performs string substitution according to the given mapping.
 *
 * @author lambdaprime intid@protonmail.com
 */
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

    /**
     * Performs inplace substitution of strings in a given directory or file
     *
     * @return list of changed files
     */
    public List<Path> substitute(
            Path target, Predicate<Path> fileFilter, Map<String, String> mapping)
            throws IOException {
        var out = new ArrayList<Path>();
        Files.find(target, Integer.MAX_VALUE, (p, a) -> fileFilter.test(p))
                .forEach(
                        file -> {
                            try {
                                if (substituteFile(file, mapping)) {
                                    out.add(file);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
        return out;
    }

    public List<Path> substitute(Path target, Map<String, String> mapping) throws IOException {
        return substitute(target, p -> p.toFile().isFile(), mapping);
    }

    private boolean substituteFile(Path file, Map<String, String> mapping) throws IOException {
        if (!file.toFile().isFile()) return false;
        Path tmp = Files.createTempFile(file.toAbsolutePath().getParent(), "tmp", "");
        var isChanged = false;
        try (BufferedReader r = new BufferedReader(new FileReader(file.toFile()));
                BufferedWriter w = new BufferedWriter(new FileWriter(tmp.toFile()))) {
            String l = null;
            while ((l = r.readLine()) != null) {
                w.write(substitute(l, mapping));
                w.write('\n');
            }
            isChanged = tmp.toFile().length() != file.toFile().length();
        }
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        return isChanged;
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
