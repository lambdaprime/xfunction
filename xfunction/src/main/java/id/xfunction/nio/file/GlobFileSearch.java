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
package id.xfunction.nio.file;

import id.xfunction.function.Unchecked;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Stack;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Implementation for {@link XFiles#findFiles(String)}
 *
 * @author lambdaprime intid@protonmail.com
 */
class GlobFileSearch {
    private static final Path EOQ = Path.of("/");
    private SynchronousQueue<Path> queue = new SynchronousQueue<>();
    private Path startFrom;
    private Path path;

    public Stream<Path> findFiles(String glob) throws IOException {
        path = Path.of(glob);
        if (path.toFile().isFile()) return Stream.of(path);
        var pos =
                IntStream.range(0, path.getNameCount())
                        .filter(i -> path.getName(i).toString().contains("*"))
                        .findFirst()
                        .orElse(-1);
        if (pos == -1) {
            return Files.list(path).filter(p -> p.toFile().isFile());
        }
        if (pos == 0) startFrom = path;
        else {
            startFrom = path.subpath(0, pos);
            if (path.getRoot() != null) startFrom = path.getRoot().resolve(startFrom);
        }
        if (glob.contains("**")) {
            // in case of recursive match we visit all the directories
            var matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
            return Files.walk(startFrom, Integer.MAX_VALUE)
                    .filter(p -> matcher.matches(p))
                    .filter(p -> p.toFile().isFile());
        }

        var stream =
                Stream.iterate(
                                path,
                                p -> p != EOQ,
                                p -> {
                                    var l = Unchecked.get(queue::take);
                                    return l;
                                })
                        .skip(1);

        // do not use common pool since it can be out of threads and then we may stuck
        new Thread(this::run, "GlobFileSearch").start();
        return stream;
    }

    private void run() {
        try {
            var fs = FileSystems.getDefault();
            var matchers = new Stack<PathMatcher>();
            matchers.push(fs.getPathMatcher("glob:" + startFrom.getFileName().toString()));
            Files.walkFileTree(
                    startFrom,
                    new FileVisitor<Path>() {
                        int depth = startFrom.getNameCount() - 1;

                        @Override
                        public FileVisitResult preVisitDirectory(
                                Path dir, BasicFileAttributes attrs) throws IOException {
                            var isMatch = matchers.peek().matches(dir.getFileName());
                            if (isMatch) {
                                if (depth + 1 < path.getNameCount()) {
                                    depth++;
                                    matchers.push(
                                            fs.getPathMatcher(
                                                    "glob:" + path.getName(depth).toString()));
                                } else {
                                    return FileVisitResult.SKIP_SUBTREE;
                                }
                                return FileVisitResult.CONTINUE;
                            } else if (dir.equals(path)) return FileVisitResult.CONTINUE;
                            else return FileVisitResult.SKIP_SUBTREE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {
                            var isMatch =
                                    matchers.peek().matches(file.getFileName())
                                            || matchers.peek().matches(file.getParent());
                            if (isMatch && depth >= path.getNameCount() - 1) {
                                Unchecked.run(() -> queue.put(file));
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc)
                                throws IOException {
                            throw new RuntimeException(exc);
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                                throws IOException {
                            if (exc != null) throw new RuntimeException(exc);
                            depth--;
                            matchers.pop();
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                queue.put(EOQ);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
