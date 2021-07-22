package id.xfunction;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class ResourceUtils {
    
    /**
     * Reads given resource file and returns its content as a stream of lines.
     * 
     * If you try to read resource from the module you need to "open" that module
     * to xfunction, otherwise Java will throw NPE.
     * 
     * @param absolutePath absolute path to the resource in form "xxx/xxx/.../resource"
     */
    public Stream<String> readResourceAsStream(String absolutePath) {
        try {
            return new BufferedReader(new InputStreamReader(
                ClassLoader.getSystemResourceAsStream(absolutePath))).lines();
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
        try {
            return new BufferedReader(new InputStreamReader(
                clazz.getResourceAsStream(name))).lines();
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
    
}
