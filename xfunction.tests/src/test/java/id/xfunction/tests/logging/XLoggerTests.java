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

import id.xfunction.logging.XLogger;
import id.xfunction.nio.file.XFiles;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XLoggerTests {

    @Test
    public void test_read_config() throws IOException {
        Logger logger = XLogger.getLogger(XLoggerTests.class);
        System.out.println(logger.getName());
        Path file = XFiles.TEMP_FOLDER.get().resolve("l.txt");
        file.toFile().delete();
        logger.info("test");
        Assertions.assertTrue(file.toFile().exists());
        Assertions.assertEquals(
                "java.util.logging.FileHandler",
                LogManager.getLogManager().getProperty("handlers"));
    }

    @Test
    public void test_config_not_found() {
        // no exception
        XLogger.load("ggg");
    }
}
