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

import id.xfunction.Preconditions;
import id.xfunction.function.Unchecked;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Additions to standard java.nio.file.Files
 *
 * @author lambdaprime intid@protonmail.com
 */
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
        var visitor =
                new RecursiveCopyFileVisitor(
                        src.toPath(), dst.toPath(), Unchecked.wrapAccept(Files::copy));
        Files.walkFileTree(src.toPath(), visitor);
    }

    /**
     * Checks, non recursively, if all files of folder A exists in folder B and their content is
     * equal (byte-to-byte comparison).
     *
     * @param a folder A
     * @param b folder B
     */
    public static boolean containsAll(Path a, Path b) throws IOException {
        return containsAll(a, b, 1);
    }

    /**
     * Checks, recursively, if all files of folder A and its subfolders exists in folder B and their
     * content is equal (byte-to-byte comparison).
     *
     * @param a folder A
     * @param b folder B
     */
    public static boolean containsAllRecursively(Path a, Path b) throws IOException {
        return containsAll(a, b, Integer.MAX_VALUE);
    }

    /**
     * @see #containsAllRecursively(Path, Path)
     * @param maxDepth the maximum number of directory levels to visit
     */
    public static boolean containsAll(Path a, Path b, int maxDepth) throws IOException {
        var iter = Files.walk(a, maxDepth).iterator();
        while (iter.hasNext()) {
            var expectedPath = iter.next();
            if (!expectedPath.toFile().isFile()) continue;
            if (!isContentEqual(
                    expectedPath.toFile(),
                    b.resolve(expectedPath.subpath(a.getNameCount(), expectedPath.getNameCount()))
                            .toFile())) return false;
        }
        return true;
    }

    /**
     * Setup a watcher on a file for a particular line.
     *
     * <p>It polls file with given delay for all new incoming lines. Once it finds a line which
     * matches the predicate it completes the future with that line.
     *
     * <p>Initially this function was implemented with {@link WatchService}. This was more efficient
     * since we avoided unnecessary polls when file is not changed. Unfortunately in certain
     * conditions {@link WatchService} could stuck waiting for events forever although the file was
     * changed already multiple of times. Querying the file size somehow affects this and unblocks
     * the events.
     *
     * <p>Such behavior was observed in Windows10, Java 17 with log file which was actively written
     * but WatchService was not emiting anything.
     */
    public static Future<String> watchForLineInFile(
            Path filePath, Predicate<String> matchPredicate, Duration pollDelay)
            throws IOException, InterruptedException {
        File file = filePath.toFile();
        Preconditions.isTrue(file.isFile(), "Not a file " + filePath);
        long[] curPos = {0};
        var future = new CompletableFuture<String>();
        var buf = new StringBuilder();
        var separator = System.lineSeparator();
        // do not use common pool since it can be out of threads and then we may stuck
        new Thread(
                        () -> {
                            while (!future.isDone()) {
                                try {
                                    Thread.sleep(pollDelay.toMillis());
                                    if (!file.exists()) {
                                        future.completeExceptionally(
                                                new IOException(
                                                        "File " + file + " does not exist"));
                                        return;
                                    }
                                    if (curPos[0] == file.length()) continue;
                                    try (var raf = new RandomAccessFile(file, "r")) {
                                        raf.seek(curPos[0]);
                                        int separatorLenSoFar = 0;
                                        while (raf.getFilePointer() < raf.length()) {
                                            var ch = (char) raf.read();
                                            curPos[0]++;
                                            if (ch == separator.charAt(separatorLenSoFar)) {
                                                separatorLenSoFar++;
                                                if (separatorLenSoFar == separator.length()) {
                                                    var line = buf.toString();
                                                    if (matchPredicate.test(line)) {
                                                        future.complete(line);
                                                        break;
                                                    }
                                                    buf.setLength(0);
                                                    separatorLenSoFar = 0;
                                                }
                                            } else {
                                                if (separatorLenSoFar != 0) {
                                                    buf.append(
                                                            separator.substring(
                                                                    0, separatorLenSoFar));
                                                    separatorLenSoFar = 0;
                                                }
                                                buf.append(ch);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    future.completeExceptionally(e);
                                }
                            }
                        },
                        "watchForLineInFile")
                .start();
        return future;
    }

    /** Compares two files byte-to-byte */
    public static boolean isContentEqual(File a, File b) throws IOException {
        if (!a.isFile()) return false;
        if (!b.isFile()) return false;
        try (var streamA = new BufferedInputStream(new FileInputStream(a));
                var streamB = new BufferedInputStream(new FileInputStream(b))) {
            while (streamA.read() == streamB.read()) {
                if (streamA.available() == 0 && streamB.available() == 0) return true;
            }
        }
        return false;
    }

    /**
     * Expands to:
     *
     * <pre>{@code
     * Files.find(root, Integer.MAX_VALUE, (p, a) -> fileFilter.test(p));
     * }</pre>
     */
    public static Stream<Path> findFilesRecursively(Path root, Predicate<Path> fileFilter)
            throws IOException {
        return Files.find(root, Integer.MAX_VALUE, (p, a) -> fileFilter.test(p));
    }

    /**
     * Find files which satisfy given glob.
     *
     * <p>Glob examples:
     *
     * <ul>
     *   <li>"a" - if "a" is a file then return it, if "a" is a folder then return all files inside
     *       it
     *   <li>"a/**" - return all files under folder "a" subtree (it includes folder "a" and
     *       recursively all its subfolders)
     *   <li>"a/*.html" - return all HTML files inside folder "a"
     *   <li>"a/b*.html" - return all HTML files inside folder "a" which names starts with "b"
     *   <li>"a/&#42;&#42;/*.html" - return all HTML files inside folder "a" subtree
     *   <li>"a/&#42;/static/*.html" - return all HTML files inside all "static" folders which are 1
     *       level below folder "a"
     * </ul>
     *
     * <p>This function does not expand the entire subtree and then filter all files under it. Such
     * algorithm would be slow for very big subtrees. Instead, this function expands only parts of
     * the subtree which satisfy the glob expression. For example, for input glob "/a/&#42;/c/*",
     * this function will expand folder "/a/b/c/" and not folder "/a/b/d/" or any other folder which
     * does not satisfy the expression "/a/&#42;/c".
     *
     * @param glob glob expression which covers entire path of searched files, for example
     *     "a/&#42;/23". It differs from Java standard {@link Files#newDirectoryStream(Path,
     *     String)}, where glob covers only file name part of the path (passing something like
     *     "a/&#42;/23" to it will not find anything).
     * @return
     * @throws IOException
     */
    public static Stream<Path> findFiles(String glob) throws IOException {
        return new GlobFileSearch().findFiles(glob);
    }
}
