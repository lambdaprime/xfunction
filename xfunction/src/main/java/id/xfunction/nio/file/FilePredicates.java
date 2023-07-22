/*
 * Copyright 2022 lambdaprime
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

/** File predicates */
public class FilePredicates {

    /**
     * Return file predicate which checks if content of a given file equals content of target file.
     */
    public static Predicate<Path> isContentEquals(Path target) {
        var targetFile = target.toFile();
        Preconditions.isTrue(targetFile.isFile(), "Not a file " + target.toAbsolutePath());
        return input -> {
            var inputFile = input.toFile();
            if (!inputFile.isFile()) return false;
            try {
                return XFiles.isContentEqual(targetFile, inputFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Return file predicate which checks if a given file extension is any from the list of target
     * extensions
     */
    public static Predicate<Path> anyExtensionOf(String... targetExtensions) {
        return inputPath -> {
            if (!inputPath.toFile().isFile()) return false;
            for (var ext : targetExtensions)
                if (inputPath.getFileName().toString().endsWith(ext)) return true;
            return false;
        };
    }

    /**
     * Return file predicate which checks if a given file name matches regular expression.
     *
     * <p>To match only image files following regexp can be used ".*\\.(png|jpg)".
     */
    public static Predicate<Path> match(String regexp) {
        return inputPath -> inputPath.getFileName().toString().matches(regexp);
    }
}
