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
package id.xfunction.tests.logging;

import id.xfunction.logging.LoggerNameFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoggerNameFilterTests {

    @Test
    public void test_deny_all() throws IOException {
        LoggerNameFilter filter = new LoggerNameFilter();
        LogRecord record1 = new LogRecord(Level.INFO, "gg");
        Assertions.assertFalse(filter.isLoggable(record1));
        record1.setLoggerName("gg");
        Assertions.assertFalse(filter.isLoggable(record1));
    }

    @Test
    public void test_allow_whitelisted() throws IOException {
        Path config = Paths.get("/tmp/log");
        Files.writeString(
                config, "id.xfunction.logging.filter = id.kk, sun.net", StandardOpenOption.CREATE);
        LogManager.getLogManager().readConfiguration(new FileInputStream(config.toString()));
        LoggerNameFilter filter = new LoggerNameFilter();
        LogRecord record1 = new LogRecord(Level.INFO, "gg");
        record1.setLoggerName("id.kk.ll");
        Assertions.assertTrue(filter.isLoggable(record1));
        record1.setLoggerName("id.k1k.ll");
        Assertions.assertFalse(filter.isLoggable(record1));
    }
}
