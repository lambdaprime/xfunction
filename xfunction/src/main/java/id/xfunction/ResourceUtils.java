/*
 * Copyright 2019 lambdaprime
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
package id.xfunction;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.stream.Stream;

import id.xfunction.io.IoUtils;
import id.xfunction.lang.XRE;

public class ResourceUtils {
    
    private IoUtils ioUtils = new IoUtils();
    
    /**
     * Reads given resource file and returns its content as a stream of lines.
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param absolutePath absolute path to the resource in form "xxx/xxx/.../resource"
     */
    public Stream<String> readResourceAsStream(String absolutePath) {
        InputStream in = ClassLoader.getSystemResourceAsStream(absolutePath);
        if (in == null) throw new XRE("Resource %s is not found", absolutePath);
        try {
            return new BufferedReader(new InputStreamReader(in)).lines();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads given resource file and returns its content as a stream of lines
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param clazz class in which package resource is located
     * @param name resource name
     */
    public Stream<String> readResourceAsStream(Class<?> clazz, String name) {
        InputStream in = clazz.getResourceAsStream(name);
        if (in == null) {
            // let's try absolute name with different classloader then
            return readResourceAsStream(clazz.getPackage().getName().replace(".", "/") + "/" + name);
        }
        try {
            return new BufferedReader(new InputStreamReader(in)).lines();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads given resource file and returns its content as a string
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param absolutePath absolute path to the resource in form "xxx/xxx/.../resource"
     */
    public String readResource(String absolutePath) {
        return readResourceAsStream(absolutePath)
                .collect(joining("\n"));
    }

    /**
     * Reads given resource file and returns its content as a string
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param clazz class in which package resource is located
     * @param name resource name
     */
    public String readResource(Class<?> clazz, String name) {
        return readResourceAsStream(clazz, name)
                .collect(joining("\n"));
    }

    /**
     * Extracts resource to the following destination.
     * If file does not exist it will be created.
     * If it exist - overwritten.
     */
    public void extractResource(String absoluteResourcePath, Path destination) {
        extractResource(absoluteResourcePath, destination.toFile());
    }
    
    /**
     * Extracts resource to the following destination.
     * If file does not exist it will be created.
     * If it exist - overwritten.
     */
    public void extractResource(String absoluteResourcePath, File destination) {
        try {
            FileOutputStream out = new FileOutputStream(destination);
            ioUtils.transferTo(ClassLoader.getSystemResourceAsStream(absoluteResourcePath), out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
