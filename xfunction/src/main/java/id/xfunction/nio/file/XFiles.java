/*
 * Copyright 2021 lambdaprime
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
package id.xfunction.nio.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import id.xfunction.function.Unchecked;

/**
 * Additions to standard java.nio.file.Files
 */
public class XFiles {

    /**
     * Deletes directory recursively with all files and sub-directories
     */
    public static void deleteRecursively(Path dir) throws IOException {
        if (!dir.toFile().exists())
            return;
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Copies directory recursively with all files and sub-directories inside.
     * 
     * @param source file or directory to copy from
     * @throws IOException 
     */
    public static void copyRecursively(Path source, Path destination) throws IOException {
        File src = source.toFile();
        File dst = destination.toFile();
        if (src.isFile() && dst.isDirectory()) {
            dst = new File(dst, src.getName());
        }
        RecursiveCopyVisitor visitor = new RecursiveCopyVisitor(src.toPath(), dst.toPath(),
            Unchecked.wrapAccept(Files::copy));
        Files.walkFileTree(src.toPath(), visitor);
    }
    
    /**
     * Checks if content of folder A exists in folder B
     * @param a folder A
     * @param b folder B
     * @throws IOException
     */
    public boolean containsAll(Path a, Path b) throws IOException {
        return !Files.list(a)
            .map(Unchecked.wrapApply(expectedFile -> {
                String expected = Files.lines(expectedFile)
                        .collect(Collectors.joining("\n"));
                String actual = Files.lines(b.resolve(a.getFileName().toString()))
                        .collect(Collectors.joining("\n"));
                return expected.equals(actual);
            }))
            .filter(Predicate.isEqual(Boolean.FALSE))
            .findFirst()
            .isPresent();
    }
}
