/*
 * Copyright 2019 lambdaprime
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
package id.xfunction.logging;

import id.xfunction.function.Unchecked;
import id.xfunction.lang.XRE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Using {@link java.util.logging} you have to specify location of logging.properties file using
 * -Djava.util.logging.config.file=ABSOLUTE_PATH where ABSOLUTE_PATH is a local file system path. If
 * you want to put it inside of your jar it may be a problem. XLogger helps to overcome this.
 *
 * <ul>
 *   <li>If java.util.logging.config.file is set and it points to local file system file then
 *       XLogger will do nothing.
 *   <li>If it points to the resource file then it will load it and initialize JUL with it.
 *   <li>If java.util.logging.config.file is not set then it will search for resource
 *       /logging.properties and initialize JUL with it.
 *   <li>Otherwise it will do nothing
 * </ul>
 *
 * <p>Use it in same way as you would use {@link java.util.logging.Logger}
 *
 * <pre>{@code
 * static final Logger LOGGER = XLogger.getLogger(HelloWorld.class);
 * LOGGER.log(Level.FINE, "Publishers: {0}", publishers);
 * }</pre>
 *
 * <p>This class also enhance Logger with methods accepting varargs instead of Object[]. It means
 * that you don't need to provide source class name in each call. Same for info logging methods.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class XLogger extends Logger {

    /*
     * Subclassing Logger requires us to obtain instance from LogManager and
     * delegate isLoggable, log to it
     */
    private Logger logger;
    private String className;

    protected XLogger(String className) {
        super(className, null);
        this.className = className;
        logger = Logger.getLogger(className);
    }

    protected XLogger(String className, int hashCode) {
        this(className + "@" + hashCode);
    }

    static {
        load();
    }

    /** Returns {@link java.util.logging.Logger} with given class name as a logger name. */
    public static XLogger getLogger(Class<?> cls) {
        return new XLogger(cls.getName());
    }

    /** Returns {@link java.util.logging.Logger} with given logger name. */
    public static XLogger getLogger(String name) {
        return new XLogger(name);
    }

    /**
     * Returns Logger with given object class name as a logger name. It also stores object hashCode
     * as unique ID and includes it as part of the logging source class name
     */
    public static XLogger getLogger(Object obj) {
        Class<?> cls = obj.getClass();
        if (cls.isAnonymousClass()) cls = cls.getSuperclass();
        return new XLogger(cls.getName(), obj.hashCode());
    }

    public static XLogger getLogger(Class<?> clazz, TracingToken token) {
        return XLogger.getLogger(clazz.getName() + "#" + token.toString());
    }

    /**
     * Initializes {@link java.util.logging} using specified property resource.
     *
     * @param propertyResource absolute path to resource file
     */
    public static void load(String propertyResource) {
        load(propertyResource, Map.of());
    }

    /**
     * Initializes {@link java.util.logging} using specified property resource combined with
     * specified property Map (properties from {@link java.util.Map} takes priority)
     *
     * <p>Example: set {@link FileHandler} output file at a runtime:
     *
     * <pre>{@code
     * XLogger.load("logging.properties", Map.of("java.util.logging.FileHandler.pattern", "/new/path/log.txt"));
     * }</pre>
     *
     * @param propertyResource absolute path to resource file
     */
    public static void load(String propertyResource, Map<String, String> overrideProperties) {
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(propertyResource);
        var props = new Properties();
        if (inputStream != null) Unchecked.run(() -> props.load(inputStream));
        props.putAll(overrideProperties);
        load(props);
    }

    /**
     * Initializes {@link java.util.logging} using specified property file in the local file system
     */
    public static void load(Path propertyFile, Map<String, String> overrideProperties) {
        var props = new Properties();
        Unchecked.run(() -> props.load(new FileReader(propertyFile.toFile())));
        props.putAll(overrideProperties);
        load(props);
    }

    private static void load(Properties props) {
        if (props.isEmpty()) return;
        try (var baos = new ByteArrayOutputStream()) {
            Unchecked.run(() -> props.store(baos, ""));
            Unchecked.run(
                    () ->
                            LogManager.getLogManager()
                                    .readConfiguration(
                                            new ByteArrayInputStream(baos.toByteArray())));
        } catch (IOException e) {
            throw new XRE(e);
        }
    }

    /**
     * Initializes {@link java.util.logging} with default settings as described in {@link XLogger}
     *
     * <p>Allows {@link XLogger} explicitly to reset any {@link java.util.logging} configuration
     * changes.
     */
    public static void load() {
        load(System.getProperty("java.util.logging.config.file", "logging.properties"));
    }

    /** Reset {@link LogManager} to default configuration */
    public static void reset() {
        LogManager.getLogManager().reset();
    }

    @Override
    public boolean isLoggable(Level level) {
        return logger.isLoggable(level);
    }

    @Override
    public void log(LogRecord record) {
        record.setSourceClassName(className);
        logger.log(record);
    }

    public void entering(String sourceMethod) {
        super.entering(className, sourceMethod);
    }

    public void entering(String sourceMethod, Object... params) {
        super.entering(className, sourceMethod, params);
    }

    /**
     * Standard entering method requires user in case of multiple params to pass them inside
     * Object[]: entering(..., new Object[]{"a", "b"}) This method allows to pass multiple params
     * directly: entering(..., "a", "b")
     *
     * <p>Unfortunately we cannot overload existing entering methods ({@link Logger#entering(String,
     * String)}, {@link Logger#entering(String, String, Object)}) because their 2nd argument is a
     * sourceMethod. This will cause an issue that calling:
     *
     * <pre>{@code
     * entering("method1", "value")
     * }</pre>
     *
     * <p>Will result in calling {@link Logger#entering(String, String)} and mixup of the arguments
     * (className will be "method1" and sourceMethod will be "value")
     *
     * <p>With a new separate method such problem does not exist:
     *
     * <pre>{@code
     * enter("method1", "value")
     * }</pre>
     *
     * <p>With result in sourceMethod be "method1" and params be "value".
     */
    public void enter(String sourceMethod, Object... params) {
        super.entering(className, sourceMethod, params);
    }

    public void exit(String sourceMethod, Object result) {
        super.exiting(className, sourceMethod, Objects.toString(result));
    }

    public void exiting(String sourceMethod) {
        super.exiting(className, sourceMethod);
    }

    /**
     * Unfortunately original {@link java.util.logging.Logger} method does not convert result to
     * String and requires you to call it explicitly and only then pass String result. This method
     * does it for you and converts the result to String and pass it further to Logger.
     */
    public void exiting(String sourceMethod, Object result) {
        super.exiting(className, sourceMethod, Objects.toString(result));
    }

    /**
     * In {@link java.util.logging} to log {@link Level#INFO} messages you need explicitly pass the
     * corresponding log {@link Level} to log method. This method does it automatically.
     */
    @Override
    public void info(String msg) {
        super.log(Level.INFO, msg);
    }

    /**
     * In {@link java.util.logging} to log {@link Level#INFO} messages you need explicitly pass the
     * corresponding log {@link Level} to log method. This method does it automatically.
     */
    public void info(String msg, Object... param) {
        super.log(Level.INFO, msg, param);
    }

    /**
     * In {@link java.util.logging} to log {@link Level#WARNING} messages you need explicitly pass
     * the corresponding log {@link Level} to log method. This method does it automatically.
     */
    public void warning(String msg, Object... param) {
        super.log(Level.WARNING, msg, param);
    }

    /**
     * In {@link java.util.logging} to log {@link Level#SEVERE} messages you need explicitly pass
     * the corresponding log {@link Level} to log method. This method does it automatically.
     */
    public void severe(String msg, Object... param) {
        super.log(Level.SEVERE, msg, param);
    }

    /**
     * In {@link java.util.logging} to log {@link Level#SEVERE} messages you need explicitly pass
     * the corresponding log {@link Level} to log method. This method does it automatically.
     */
    public void severe(String msg, Throwable t) {
        super.log(Level.SEVERE, msg, t);
    }

    /**
     * In {@link java.util.logging} to log {@link Level#SEVERE} messages you need explicitly pass
     * the corresponding log {@link Level} to log method. This method does it automatically.
     */
    public void severe(Throwable t) {
        super.log(
                Level.SEVERE,
                Optional.ofNullable(t.getMessage()).orElse(t.getClass().getSimpleName()),
                t);
    }

    /**
     * In {@link java.util.logging} to log {@link Level#FINE} messages you need explicitly pass the
     * corresponding log {@link Level} to log method. This method does it automatically.
     */
    public void fine(String msg, Object... param) {
        super.log(Level.FINE, msg, param);
    }

    /** Log exception (with entire stacktrace) only when {@link Level#FINE} mode is active */
    public void fine(Throwable t) {
        if (isLoggable(Level.FINE)) {
            super.log(
                    Level.FINE,
                    Optional.ofNullable(t.getMessage()).orElse(t.getClass().getSimpleName()),
                    t);
        }
    }
}
