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

/**
 * Additions to standard java.nio.file.Paths
 */
public class XPaths {

    /**
     * <p>Usually extension of the file is part of its file name.
     * This function splits file name on two-value array:
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
}
