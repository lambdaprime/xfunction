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
package id.xfunction.nio.file;

import id.xfunction.function.Unchecked;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Additions to standard java.nio.file.Files */
public class XFiles {

    /** System temporary folder location if it was found */
    public static final Optional<Path> TEMP_FOLDER = findTempFolder();

    private static Optional<Path> findTempFolder() {
        try {
            var tmpFile = Files.createTempFile("test", "");
            tmpFile.toFile().deleteOnExit();
            return Optional.of(tmpFile.getParent());
        } catch (IOException e) {
            var tmpDir = System.getProperty("java.io.tmpdir");
            if (tmpDir != null) return Optional.of(Paths.get(tmpDir));
        }
        return Optional.empty();
    }

    /** Deletes directory recursively with all files and sub-directories */
    public static void deleteRecursively(Path dir) throws IOException {
        if (!dir.toFile().exists()) return;
        Files.walkFileTree(
                dir,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                            throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    /**
     * Copies directory recursively with all files and sub-directories inside.
     *
     * @param source file or directory to copy from
     */
    public static void copyRecursively(Path source, Path destination) throws IOException {
        File src = source.toFile();
        File dst = destination.toFile();
        if (src.isFile() && dst.isDirectory()) {
            dst = new File(dst, src.getName());
        }
        RecursiveCopyVisitor visitor =
                new RecursiveCopyVisitor(
                        src.toPath(), dst.toPath(), Unchecked.wrapAccept(Files::copy));
        Files.walkFileTree(src.toPath(), visitor);
    }

    /**
     * Checks if content of folder A exists in folder B
     *
     * @param a folder A
     * @param b folder B
     */
    public static boolean containsAll(Path a, Path b) throws IOException {
        return !Files.list(a)
                .map(
                        Unchecked.wrapApply(
                                expectedFile -> {
                                    String expected =
                                            Files.lines(expectedFile)
                                                    .collect(Collectors.joining("\n"));
                                    String actual =
                                            Files.lines(b.resolve(a.getFileName().toString()))
                                                    .collect(Collectors.joining("\n"));
                                    return expected.equals(actual);
                                }))
                .filter(Predicate.isEqual(Boolean.FALSE))
                .findFirst()
                .isPresent();
    }

    public static Future<Void> watchForStringInFile(Path file, String str)
            throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        file.getParent()
                .register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);
        long[] curPos = {0};
        var future = new CompletableFuture<Void>();
        var strBuf = new StringBuilder(str);
        var buf = new StringBuilder(str.length());
        ForkJoinPool.commonPool()
                .execute(
                        () -> {
                            while (!future.isDone()) {
                                try {
                                    WatchKey key = watchService.take();
                                    for (WatchEvent<?> event : key.pollEvents()) {
                                        if (!event.context().equals(file.getFileName())) continue;
                                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
                                            future.completeExceptionally(
                                                    new IOException(
                                                            "File " + file + " is deleted"));
                                        var raf = new RandomAccessFile(file.toFile(), "r");
                                        raf.seek(curPos[0]);
                                        while (raf.getFilePointer() < raf.length()) {
                                            buf.append((char) raf.read());
                                            curPos[0]++;
                                            if (buf.compareTo(strBuf) == 0) {
                                                future.complete(null);
                                                break;
                                            }
                                            if (buf.length() == str.length()) buf.deleteCharAt(0);
                                        }
                                    }
                                    if (!key.reset())
                                        future.completeExceptionally(
                                                new IOException("Could not reset the key"));
                                } catch (Exception e) {
                                    future.completeExceptionally(e);
                                }
                            }
                        });
        return future;
    }
}
