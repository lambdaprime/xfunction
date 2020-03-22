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

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import id.xfunction.function.Unchecked;

/**
 * Using JUL you have to specify location of logging.properties
 * file using -Djava.util.logging.config.file=ABSOLUTE_PATH
 * If you want to put it inside of your jar it may be a problem.
 * XLogger helps to overcome this. It will search for /logging.properties
 * in your jar and initialize JUL with it.
 */
public class XLogger {

    static {
        load("logging.properties");
    }

    /**
     * Returns Logger with given class name
     */
    public static Logger getLogger(Class<?> cls) {
        return Logger.getLogger(cls.getName());
    }

    static void load(String resource) {
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(resource);
        if (inputStream == null) return;
        Unchecked.run(() -> LogManager.getLogManager().readConfiguration(inputStream));
    }
}
