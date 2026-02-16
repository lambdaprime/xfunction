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

import id.xfunction.Preconditions;
import id.xfunction.function.Unchecked;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Pattern;
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
    private GlobPath globPath;

    /**
     * Provides basic functionality similar to {@link Path} with glob expressions
     *
     * <p>{@link Path} in Windows does not support glob expressions, for example:
     *
     * {@snippet :
     * jshell> Path.of("c:\\gg*")
     * Exception java.nio.file.InvalidPathException: Illegal char <*> at index 5: c:\gg*
     * }
     *
     * @author lambdaprime intid@protonmail.com
     */
    private static class GlobPath {
        private String[] pathElements;
        private Optional<Path> root;

        public GlobPath(String globPath) {
            this.root = extractRoot(globPath);
            if (root.isPresent()) globPath = globPath.substring(root.get().toString().length());
            this.pathElements = globPath.split(Pattern.quote(File.separator));
            Preconditions.isTrue(pathElements.length > 0);
        }

        private Optional<Path> extractRoot(String globPath) {
            return Optional.ofNullable(Path.of(globPath.replace("*", "X")).getRoot());
        }

        public int getNameCount() {
            return pathElements.length;
        }

        public Object getName(int i) {
            if (i < 0 || i >= pathElements.length) {
                throw new IndexOutOfBoundsException(
                        "Index: " + i + ", Size: " + pathElements.length);
            }
            return pathElements[i];
        }

        public Path subpath(int i, int pos) {
            if (i < 0 || pos > pathElements.length || i > pos) {
                throw new IndexOutOfBoundsException("Invalid range: [" + i + ", " + pos + "]");
            }

            // Create a new array for the subpath
            String[] subElements = new String[pos - i];
            System.arraycopy(pathElements, i, subElements, 0, pos - i);

            // Join the elements back into a path string
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < subElements.length; j++) {
                if (j > 0) sb.append("/");
                sb.append(subElements[j]);
            }

            return Path.of(sb.toString());
        }

        public Path getRoot() {
            return root.orElse(null);
        }
    }

    public Stream<Path> findFiles(String globExpression) throws IOException {
        globPath = new GlobPath(globExpression);
        var pos =
                IntStream.range(0, globPath.getNameCount())
                        .filter(i -> globPath.getName(i).toString().contains("*"))
                        .findFirst()
                        .orElse(-1);
        if (pos == -1) {
            var filePath = Path.of(globExpression);
            if (filePath.toFile().isFile()) return Stream.of(filePath);
            return Files.list(filePath).filter(p -> p.toFile().isFile());
        }
        if (pos == 0) startFrom = Path.of("");
        else {
            startFrom = globPath.subpath(0, pos);
            if (globPath.getRoot() != null) startFrom = globPath.getRoot().resolve(startFrom);
        }
        if (globExpression.contains("**")) {
            globExpression = globExpression.replace('\\', '/');
            // in case of recursive match we visit all the directories
            var matcher = FileSystems.getDefault().getPathMatcher("glob:" + globExpression);
            var isAbsolute = startFrom.isAbsolute();
            return Files.walk(startFrom, Integer.MAX_VALUE)
                    .filter(p -> matcher.matches(isAbsolute ? p.toAbsolutePath() : p))
                    .filter(p -> p.toFile().isFile());
        } else {
            // in case of non recursive match we visit only directories which satisfy glob
            // expression
            // this optimization helps to do search efficiently on big file trees by pruning
            // unnecessary subtrees
            var stream =
                    Stream.iterate(
                                    Path.of(""),
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
    }

    private void run() {
        try {
            var fs = FileSystems.getDefault();
            var matchers = new Stack<PathMatcher>();
            var globExpression = "glob:" + getFileName(startFrom);
            matchers.push(fs.getPathMatcher(globExpression));
            // System.out.println(globExpression);
            Files.walkFileTree(
                    startFrom,
                    new FileVisitor<Path>() {
                        int depth = Math.max(1, startFrom.getNameCount()) - 1;

                        @Override
                        public FileVisitResult preVisitDirectory(
                                Path dir, BasicFileAttributes attrs) throws IOException {
                            var isMatch = matchers.peek().matches(getFileName(dir));
                            if (isMatch) {
                                if (depth + 1 < globPath.getNameCount()) {
                                    depth++;
                                    var globExpression =
                                            "glob:" + globPath.getName(depth).toString();
                                    matchers.push(fs.getPathMatcher(globExpression));
                                    // System.out.println(globExpression);
                                } else {
                                    return FileVisitResult.SKIP_SUBTREE;
                                }
                                return FileVisitResult.CONTINUE;
                            } else if (dir.equals(globPath)) return FileVisitResult.CONTINUE;
                            else return FileVisitResult.SKIP_SUBTREE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {
                            var isMatch =
                                    matchers.peek().matches(file.getFileName())
                                            || matchers.peek().matches(file.getParent());
                            if (isMatch && depth >= globPath.getNameCount() - 1) {
                                Unchecked.run(() -> queue.put(file));
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc)
                                throws IOException {
                            exc.printStackTrace();
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                                throws IOException {
                            if (exc != null) exc.printStackTrace();
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

    private Path getFileName(Path path) {
        var name = path.getFileName();
        return name == null ? path : name;
    }
}
