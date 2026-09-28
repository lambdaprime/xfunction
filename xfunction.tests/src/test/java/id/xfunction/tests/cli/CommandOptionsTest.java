/*
 * Copyright 2020 lambdaprime
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
package id.xfunction.tests.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.CommandOptions;
import id.xfunction.cli.CommandOptions.Config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CommandOptionsTest {

    @Test
    public void test_collectOptions() {
        assertThrows(
                ArgumentParsingException.class,
                () ->
                        CommandOptions.collectOptions(
                                new String[] {"-k", "value", "arg1", "arg2", "arg3"}));
        assertEquals(
                "{arg3=, k=value}",
                CommandOptions.collectOptions(
                                new Config().withIgnoreParsingExceptions(),
                                new String[] {"-k", "value", "arg1", "arg2", "-arg3"})
                        .toString());

        CommandOptions props =
                CommandOptions.collectOptions(new String[] {"-k1", "value", "--k2", "arg2", "-k3"});
        assertEquals("{k1=value, k2=arg2, k3=}", props.toString());

        props =
                CommandOptions.collectOptions(
                        new String[] {"-k1=value", "--k2==arg2", "-k3=", "-b1", "true", "-b2"});
        assertEquals("{b2=, k1=value, k2==arg2, k3=, b1=true}", props.toString());
        assertEquals(true, props.isOptionTrue("b1"));
        assertEquals(true, props.isOptionTrue("b2"));
    }

    /** Test for reading option value from file */
    @Test
    public void test_getOption_fromFile() throws Exception {
        var tempFile = Files.createTempFile("test_option_value", ".txt");
        Files.writeString(tempFile, "file_value");

        var props =
                CommandOptions.collectOptions(
                        new Config().withFileOptionReading(), new String[] {"-k", "@" + tempFile});
        assertEquals("file_value", props.getOption("k").orElse(null));

        Files.delete(tempFile);
    }

    @ParameterizedTest(
            name =
                    "getOption from file with config enabled={0}, prefix={1}, value={2} ->"
                            + " expected={3}")
    @CsvSource({
        "false, @, @test.txt, @test.txt",
        "true, @, @test.txt, file content",
        "true, %, %test.txt, file content"
    })
    void test_getOption_fromFile_parameterized(
            boolean enabled,
            String prefix,
            String optionValue,
            String expectedValue,
            @TempDir Path tempDir)
            throws IOException {
        var filePath = tempDir.resolve("test.txt");
        Files.writeString(filePath, "file content");

        var config = new CommandOptions.Config().withFileOptionPrefix(prefix);
        if (enabled) {
            config.withFileOptionReading();
        }

        var options = new Properties();
        options.setProperty(
                "optionName",
                enabled && optionValue.startsWith(prefix)
                        ? prefix + filePath.toString()
                        : optionValue);
        var commandOptions = new CommandOptions(options, config);

        var actual = commandOptions.getOption("optionName");
        if (expectedValue.equals("file content")) {
            assertTrue(actual.isPresent());
            assertEquals("file content", actual.get());
        } else {
            assertTrue(actual.isPresent());
            assertEquals(expectedValue, actual.get());
        }
    }
}
