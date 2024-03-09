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

import id.xfunction.function.ThrowingConsumer;
import id.xfunction.nio.file.XFiles;
import id.xfunction.nio.file.XPaths;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Performs string substitution according to the given mapping.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class Substitutor {

    private static class Result {
        private boolean isUpdated;
        private String text;

        public Result(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private boolean hasRegexpSupport;
    private boolean hasBackup;

    /** Allow to specify regexps in mapping */
    public Substitutor withRegexpSupport() {
        hasRegexpSupport = true;
        return this;
    }

    /**
     * Create backup of the original file when doing any changes. If no changes are done, backup
     * file is not created. If backup file already exists then it is replaced.
     */
    public Substitutor withBackup() {
        hasBackup = true;
        return this;
    }

    /**
     * Creates and returns a new list by iterating over the input list and performing line by line
     * string substitution as defined in the mapping.
     */
    public List<String> substitutePerLine(List<String> lines, Map<String, String> mapping) {
        var compiled = compile(mapping);
        return lines.stream()
                .map(l -> substitute(l, mapping, compiled))
                .map(Result::getText)
                .collect(toList());
    }

    /** Performs inplace line by line substitution in a given directory or file. */
    public List<Path> substitutePerLine(Path target, Map<String, String> mapping)
            throws IOException {
        return substitutePerLine(target, p -> true, mapping);
    }

    /**
     * Equivalent to {@link #substitutePerLine(Path, Map)} except allows to substitute only files
     * which satisfy given filter.
     */
    public List<Path> substitutePerLine(
            Path target, Predicate<Path> fileFilter, Map<String, String> mapping)
            throws IOException {
        var compiled = compile(mapping);
        var out = new ArrayList<Path>();
        forEachFile(
                target,
                fileFilter,
                file -> {
                    if (substitutePerLine(file, mapping, compiled)) {
                        out.add(file);
                    }
                });
        return out;
    }

    private boolean substitutePerLine(
            Path file, Map<String, String> mapping, Map<Pattern, String> compiledMapping)
            throws IOException {
        if (!file.toFile().isFile()) return false;
        Path tmp = Files.createTempFile(file.toAbsolutePath().getParent(), "tmp", "");
        var isChanged = false;
        try (BufferedReader r = new BufferedReader(new FileReader(file.toFile()));
                BufferedWriter w = new BufferedWriter(new FileWriter(tmp.toFile()))) {
            String l = null;
            while ((l = r.readLine()) != null) {
                var res = substitute(l, mapping, compiledMapping);
                w.write(res.text);
                w.write('\n');
                if (res.isUpdated) isChanged = true;
            }
        }
        if (hasBackup) {
            Files.move(file, getBackupFile(file), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        return isChanged;
    }

    public static boolean restoreBackup(Path file) throws IOException {
        var backupFile = getBackupFile(file);
        if (!backupFile.toFile().isFile()) return false;
        Files.move(backupFile, file, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    private static Path getBackupFile(Path file) {
        return XPaths.appendToFullFileName(file, ".bak");
    }

    /** Substitutes all values and return new string */
    public String substitute(String text, Map<String, String> mapping) {
        return substitute(text, mapping, compile(mapping)).text;
    }

    /**
     * Equivalent to {@link #substitute(Path, Map)} except allows to substitute only files which
     * satisfy given filter.
     */
    public List<Path> substitute(
            Path target, Predicate<Path> fileFilter, Map<String, String> mapping)
            throws IOException {
        var compiled = compile(mapping);
        var out = new ArrayList<Path>();
        forEachFile(
                target,
                fileFilter,
                file -> {
                    if (substitute(file, mapping, compiled)) {
                        out.add(file);
                    }
                });
        return out;
    }

    /**
     * Performs inplace substitution in a given file or in case of directory, in all files inside
     * it. It differs from {@link #substitutePerLine(Path, Map)} that it reads entire file in memory
     * as a single string and performs substitution on such string. This allows to create multiline
     * mappings and perform multiline substitutions (of course this requires appropriate Java
     * pattern which should support multiline matching, this can be done by prepending "(?s)(?m)" to
     * it).
     */
    public List<Path> substitute(Path target, Map<String, String> mapping) throws IOException {
        return substitute(target, fileFilter -> true, mapping);
    }

    private boolean substitute(
            Path file, Map<String, String> mapping, Map<Pattern, String> compiled)
            throws IOException {
        if (!file.toFile().isFile()) return false;
        var text = Files.readString(file);
        var res = substitute(text, mapping, compiled);
        if (!res.isUpdated) return false;
        if (hasBackup) {
            Files.copy(file, getBackupFile(file), StandardCopyOption.REPLACE_EXISTING);
            Files.writeString(file, res.text, StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            Files.writeString(file, res.text, StandardOpenOption.TRUNCATE_EXISTING);
        }
        return true;
    }

    private void forEachFile(
            Path target, Predicate<Path> fileFilter, ThrowingConsumer<Path, IOException> proc)
            throws IOException {
        XFiles.findFilesRecursively(target, p -> p.toFile().isFile() && fileFilter.test(p))
                .forEach(
                        file -> {
                            try {
                                proc.accept(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
    }

    private Result substitute(
            String text, Map<String, String> mapping, Map<Pattern, String> compiledMapping) {
        var result = new Result(text);
        if (hasRegexpSupport) {
            for (var e : compiledMapping.entrySet()) {
                var m = e.getKey().matcher(result.text);
                if (!m.find()) continue;
                result.isUpdated = true;
                result.text = m.replaceAll(e.getValue());
            }
        } else {
            for (var e : mapping.entrySet()) {
                var pos = text.indexOf(e.getKey());
                if (pos < 0) continue;
                result.isUpdated = true;
                result.text = result.text.replace(e.getKey(), e.getValue());
            }
        }
        return result;
    }

    private Map<Pattern, String> compile(Map<String, String> mapping) {
        if (!hasRegexpSupport) return Collections.emptyMap();
        return mapping.entrySet().stream()
                .map(e -> Map.entry(Pattern.compile(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    /** Finds all secrets in the input text and replaces them with a mask */
    public String maskSecrets(String text, List<String> secrets) {
        for (var secret : secrets) {
            text = text.replace(secret, "xxxxxxx");
        }
        return text;
    }

    /**
     * @return function which accepts input text and masks all secrets in it
     */
    public Function<String, String> maskSecretsFunc(List<String> secrets) {
        return l -> maskSecrets(l, secrets);
    }
}
