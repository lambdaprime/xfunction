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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiConsumer;

import id.xfunction.lang.XRE;

public class RecursiveCopyVisitor implements FileVisitor<Path> {
    private Path srcPath;
    private Path dstPath;
    private BiConsumer<Path, Path> copier;
    
    public RecursiveCopyVisitor(Path srcPath, Path dstPath, BiConsumer<Path, Path> copier) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        this.copier = copier;
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        copier.accept(file, relativeToDestination(file));
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Files.createDirectories(relativeToDestination(dir));
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc)
            throws IOException {
        throw new XRE("Failed to open %s: %s", file, exc.getMessage());
    }
    
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
            throws IOException {
        return FileVisitResult.CONTINUE;
    }

    private Path relativeToDestination(Path file) {
        return dstPath.resolve(srcPath.relativize(file));
    }

}