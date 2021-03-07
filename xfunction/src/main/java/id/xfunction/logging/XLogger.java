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
 * file using -Djava.util.logging.config.file=ABSOLUTE_PATH
 * If you want to put it inside of your jar it may be a problem.
 * XLogger helps to overcome this. It will search for /logging.properties
 * in your jar and initialize JUL with it.</p>
 * 
 * <p>Use it in same way as you would use Logger.</p>
 * <pre>{@code
 * static final Logger LOGGER = XLogger.getLogger(HelloWorld.class);
 * LOGGER.log(Level.FINE, "Publishers: {0}", publishers);
 * }</pre>
 * 
 * <p>This class also enhance Logger with methods accepting varargs
 * instead of Object[] and which use getName as the source class.
 * It means that you don't need to provide source class name in each
 * call. Same for info logging methods.</p>
 */
public class XLogger extends Logger {

    private Logger logger;

    protected XLogger(Logger logger) {
        super(logger.getName(), logger.getResourceBundleName());
        this.logger = logger;
    }

    static {
        load("logging.properties");
        // JUL tries to find source class which sent log record
        // relying on stacktrace. Because we subclass Logger it would always
        // end up on XLogger as a source class. Here we ask it to ignore
        // XLogger package so that source class will be found correctly.
        String propName = "jdk.logger.packages";
        String value = System.getProperty(propName, "");
        if (!value.isEmpty())
            value +=  ", ";
        value += XLogger.class.getCanonicalName();
        System.setProperty(propName, value);
    }

    /**
     * <p>Returns Logger with given class name.</p>
     */
    public static XLogger getLogger(Class<?> cls) {
        XLogger logger = new XLogger(Logger.getLogger(cls.getName()));
        return logger;
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
        logger.log(record);
    }
    
    public void entering(String sourceMethod) {
        super.entering(getName(), sourceMethod);
    }
    
    public void entering(String sourceMethod, Object...params) {
        super.entering(getName(), sourceMethod, params);
    }
    
    public void exiting(String sourceMethod) {
        super.exiting(getName(), sourceMethod);
    }
    
    /**
     * Unfortunately original Logger method does not convert result to String
     * and requires you to call it explicitly and only then pass String result.
     * This method does it for you and converts the result to String and pass it
     * further to Logger.
     */
    public void exiting(String sourceMethod, Object result) {
        super.exiting(getName(), sourceMethod, Objects.toString(result));
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

    public void fine(String msg, Object...param) {
        super.log(Level.FINE, msg, param);
    }
}
