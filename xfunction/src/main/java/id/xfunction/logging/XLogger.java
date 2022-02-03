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
package id.xfunction.logging;

import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import id.xfunction.function.Unchecked;

/**
 * <p>Using JUL you have to specify location of logging.properties
 * file using -Djava.util.logging.config.file=ABSOLUTE_PATH where ABSOLUTE_PATH
 * is a local file system path.
 * If you want to put it inside of your jar it may be a problem.
 * XLogger helps to overcome this.</p>
 * 
 * <ul>
 * <li>If java.util.logging.config.file is set and it points to local file
 * system file then XLogger will do nothing.
 * <li>If it points to the resource file then it will load it and initialize
 * JUL with it.
 * <li>If java.util.logging.config.file is not set then it will search for resource
 * /logging.properties and initialize JUL with it.
 * <li>Otherwise it will do nothing
 * </ul>
 * 
 * <p>Use it in same way as you would use Logger.</p>
 * <pre>{@code
 * static final Logger LOGGER = XLogger.getLogger(HelloWorld.class);
 * LOGGER.log(Level.FINE, "Publishers: {0}", publishers);
 * }</pre>
 * 
 * <p>This class also enhance Logger with methods accepting varargs
 * instead of Object[].
 * It means that you don't need to provide source class name in each
 * call. Same for info logging methods.</p>
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
        load(System.getProperty("java.util.logging.config.file", "logging.properties"));
    }

    /**
     * Returns Logger with given class name as a logger name.
     */
    public static XLogger getLogger(Class<?> cls) {
        return new XLogger(cls.getName());
    }

    /**
     * Returns Logger with given logger name.
     */
    public static XLogger getLogger(String name) {
        return new XLogger(name);
    }

    /**
     * Returns Logger with given object class name as a logger name.
     * It also stores object hashCode as unique ID and includes
     * it as part of the logging source class name
     */
    public static XLogger getLogger(Object obj) {
        Class<?> cls = obj.getClass();
        if (cls.isAnonymousClass()) cls = cls.getSuperclass();
        return new XLogger(cls.getName(), obj.hashCode());
    }

    /**
     * <p>Initializes JUL using specified property resource.</p>
     * <p>Visible for tests only.</p>
     * @param propertyResource absolute path to resource file
     */
    public static void load(String propertyResource) {
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(propertyResource);
        if (inputStream == null) return;
        Unchecked.run(() -> LogManager.getLogManager().readConfiguration(inputStream));
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
    
    public void entering(String sourceMethod, Object...params) {
        super.entering(className, sourceMethod, params);
    }
    
    public void exiting(String sourceMethod) {
        super.exiting(className, sourceMethod);
    }
    
    /**
     * Unfortunately original Logger method does not convert result to String
     * and requires you to call it explicitly and only then pass String result.
     * This method does it for you and converts the result to String and pass it
     * further to Logger.
     */
    public void exiting(String sourceMethod, Object result) {
        super.exiting(className, sourceMethod, Objects.toString(result));
    }
    
    public void info(String msg) {
        super.log(Level.INFO, msg);
    }
    
    public void info(String msg, Object...param) {
        super.log(Level.INFO, msg, param);
    }
    
    public void warning(String msg, Object...param) {
        super.log(Level.WARNING, msg, param);
    }
    
    public void severe(String msg, Object...param) {
        super.log(Level.SEVERE, msg, param);
    }

    public void severe(String msg, Throwable t) {
        super.log(Level.SEVERE, msg, t);
    }

    public void severe(Throwable t) {
        super.log(Level.SEVERE, t.getMessage(), t);
    }

    public void fine(String msg, Object...param) {
        super.log(Level.FINE, msg, param);
    }
}
