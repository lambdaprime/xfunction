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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/** Additions to standard java.nio.file.Paths */
public class XPaths {

    /**
     * Usually extension of the file is part of its file name. This function splits file name on
     * two-value array:
     *
     * <pre>array[0] = file name (without extension)
     * array[1] = file extension (or null if extension is absent)
     * </pre>
     *
     * <p>If filename starts with '.' it means it has no extension.
     */
    public static String[] splitFileName(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos <= 0) {
            return new String[] {fileName, null};
        }
        return new String[] {fileName.substring(0, pos), fileName.substring(pos + 1)};
    }

    /**
     * Append postfix into file name.
     *
     * <p>Postfix is appended to the file name excluding its extension:
     *
     * <pre>{@code
     * appendToFileName(Paths.get("/tmp/a.png"), "-cropped")
     * }</pre>
     *
     * Will produce: "/tmp/a-cropped.png"
     */
    public static Path appendToFileName(Path path, String postfix) {
        String[] fileName = splitFileName(path.getFileName().toString());
        fileName[1] = Optional.ofNullable(fileName[1]).map(ext -> "." + ext).orElse("");
        return path.resolveSibling(fileName[0] + postfix + fileName[1]);
    }

    /**
     * Append postfix into full file name.
     *
     * <p>Postfix is appended to the file name including its extension:
     *
     * <pre>{@code
     * appendToFullFileName(Paths.get("/tmp/a.png"), "-cropped")
     * }</pre>
     *
     * Will produce: "/tmp/a.png-cropped"
     */
    public static Path appendToFullFileName(Path path, String postfix) {
        var folder = path.getParent();
        var file = Paths.get(path.getFileName() + postfix);
        return (folder == null) ? file : folder.resolve(file);
    }
}
