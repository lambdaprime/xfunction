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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.xfunction.cli.CommandLineInterface;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class CommandLineInterfaceTest {

    private static final String NL = System.lineSeparator();

    @Test
    public void test_askConfirm() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream("yes".getBytes());
        CommandLineInterface cli = new CommandLineInterface(bais, baos, baos);
        String msg = "test?";
        boolean actual = cli.askConfirm(msg);
        assertTrue(actual);
        assertEquals(msg + NL + "Please confirm [yes/no]: ", baos.toString());
    }

    @Test
    public void test_askConfirm_wrong_answer() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream("ye\nnoo\nno".getBytes());
        CommandLineInterface cli = new CommandLineInterface(bais, baos, baos);
        String msg = "test?";
        boolean actual = cli.askConfirm(msg);
        assertFalse(actual);
        assertEquals(
                msg
                        + NL
                        + "Please confirm [yes/no]: Please confirm [yes/no]: Please confirm"
                        + " [yes/no]: ",
                baos.toString());
    }

    @Test
    public void test_print() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLineInterface cli = new CommandLineInterface(System.in, baos, baos);
        cli.print(12);
        assertEquals("12" + NL, baos.toString());
    }

    @Test
    public void test_print_string() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLineInterface cli = new CommandLineInterface(System.in, baos, baos);
        cli.print("12");
        assertEquals("12" + NL, baos.toString());
    }

    @Test
    public void test_print_string_with_special_char() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLineInterface cli = new CommandLineInterface(System.in, baos, baos);
        cli.print("12%d");
        assertEquals("12%d" + NL, baos.toString());
    }

    @Test
    public void test_teeToFile() throws IOException {
        Path tempFile = Files.createTempFile("teeToFile", "");
        CommandLineInterface cli = new CommandLineInterface();
        cli.teeToFile(tempFile);
        cli.print("Test");
        cli.printerr("Error");
        assertEquals("Test" + NL + "Error" + NL, Files.readString(tempFile));
    }
}
